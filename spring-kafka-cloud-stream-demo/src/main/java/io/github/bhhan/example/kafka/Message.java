package io.github.bhhan.example.kafka;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class Message {
    private Integer id;
    private String name;
    private String data;
    private byte[] bytePayload;
}
