package io.github.bhhan.example.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;

@Service
@RequiredArgsConstructor
public class EventStreamService {
    private final EventStream eventStream;

    public boolean produceEvent(Message msg){
        msg.setBytePayload(msg.getData().getBytes());
        MessageChannel messageChannel = eventStream.producer();
        return messageChannel.send(MessageBuilder.withPayload(msg)
                .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
                .build());
    }
}
