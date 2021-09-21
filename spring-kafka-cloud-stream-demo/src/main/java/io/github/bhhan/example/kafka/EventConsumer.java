package io.github.bhhan.example.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.handler.annotation.Payload;

import java.util.Arrays;

@Slf4j
@Configuration
public class EventConsumer {
    @StreamListener(EventStream.INBOUND)
    public void consumeEvent(@Payload Message msg){
        log.info("Inbound message--> id: " + msg.getId() + " name: " + msg.getName() + " Actual message: " + msg.getData() + " bytePayload: " + Arrays.toString(msg.getBytePayload()));
    }
}
