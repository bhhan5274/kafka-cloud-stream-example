package io.github.bhhan.example.hadoop;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;

import java.time.Duration;
import java.util.*;

@Slf4j
public class ConsumerWorker implements Runnable{
    private static Map<Integer, List<String>> bufferString = new HashMap<>();
    private static Map<Integer, Long> currentFileOffset = new HashMap<>();

    private static final int FLUSH_RECORD_COUNT = 10;
    private Properties prop;
    private String topic;
    private String threadName;
    private KafkaConsumer<String, String> consumer;

    public ConsumerWorker(Properties prop, String topic, int number) {
        this.prop = prop;
        this.topic = topic;
        this.threadName = "consumer-thread-" + number;
    }

    @Override
    public void run() {
        Thread.currentThread().setName(threadName);
        consumer = new KafkaConsumer<>(prop);
        consumer.subscribe(List.of(topic));

        try {
            while(true){
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
                for (ConsumerRecord<String, String> record : records) {
                    addHdfsFileBuffer(record);
                }
                saveBufferToHdfsFile(consumer.assignment());
            }
        }catch (WakeupException e){
            log.info("Wakeup Hadoop Consumer");
        }catch(Exception e){
            log.info("Error: " + e.getMessage());
        }finally {
            consumer.close();
        }
    }

    private void saveBufferToHdfsFile(Set<TopicPartition> partitions) {
        partitions.forEach(p -> checkFlushCount(p.partition()));
    }

    private void checkFlushCount(int partitionNo) {
        if(bufferString.get(partitionNo) != null){
            if(bufferString.get(partitionNo).size() > FLUSH_RECORD_COUNT - 1){
                save(partitionNo);
            }
        }
    }

    private void save(int partitionNo) {
        if(bufferString.get(partitionNo).size() > 0){
            try {
                String fileName = "/data/color-" + partitionNo + "-" + currentFileOffset.get(partitionNo) + ".log";
                Configuration configuration = new Configuration();
                configuration.set("fs.defaultFS", "hdfs://localhost:9000");
                FileSystem hdfsFileSystem = FileSystem.get(configuration);
                FSDataOutputStream fileOutputStream = hdfsFileSystem.create(new Path(fileName));
                fileOutputStream.writeBytes(StringUtils.join(bufferString.get(partitionNo), "\n"));
                fileOutputStream.close();

                bufferString.put(partitionNo, new ArrayList<>());
            }catch(Exception e){
                log.info(e.getMessage());
            }
        }
    }

    private void addHdfsFileBuffer(ConsumerRecord<String, String> record) {
        List<String> buffer = bufferString.getOrDefault(record.partition(), new ArrayList<>());
        buffer.add(record.value());
        bufferString.put(record.partition(), buffer);

        if(buffer.size() == 1){
            currentFileOffset.put(record.partition(), record.offset());
        }
    }

    private void saveRemainBufferToHdfsFile(){
        bufferString.forEach((partitionNo, v) -> this.save(partitionNo));
    }

    public void stopAndWakeup(){
        consumer.wakeup();
        saveRemainBufferToHdfsFile();
    }
}
