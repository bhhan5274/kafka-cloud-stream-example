package io.github.bhhan.example.hadoop;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class HdfsSinkApplication {
    private static final String BOOTSTRAP_SERVER = "my-kafka:9092";
    private static final String TOPIC_NAME = "select-color";
    private static final String GROUP_ID = "color-hdfs-save-consumer-group";
    private static final int CONSUMER_COUNT = 2;
    private static final List<ConsumerWorker> workers = new ArrayList<>();

    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new ShutdownThread());

        Properties configs = new Properties();
        configs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        configs.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        ExecutorService executorService = Executors.newCachedThreadPool();

        for(int i = 0; i < CONSUMER_COUNT; i++){
            workers.add(new ConsumerWorker(configs, TOPIC_NAME, i));
        }

        workers.forEach(executorService::execute);
    }

    static class ShutdownThread extends Thread{
        @Override
        public void run() {
            workers.forEach(ConsumerWorker::stopAndWakeup);
        }
    }
}
