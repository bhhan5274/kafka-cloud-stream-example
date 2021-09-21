package io.github.bhhan.example.kafka;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

public interface EventStream {
    String INBOUND = "event-consumer";
    String OUTBOUND = "event-producer";

    @Input(EventStream.INBOUND)
    SubscribableChannel consumer();

    @Output(EventStream.OUTBOUND)
    MessageChannel producer();
}
