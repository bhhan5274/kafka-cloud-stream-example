package io.github.bhhan.example.producer.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.bhhan.example.producer.web.dto.UserEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/v1/api")
public class ProduceController {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private static final String TOPIC_NAME = "select-color";

    @GetMapping("/select")
    public void selectColor(@RequestHeader("user-agent") String userAgentName,
                            @RequestParam("color") String colorName,
                            @RequestParam("user") String userName) throws JsonProcessingException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZ");

        UserEvent userEvent = UserEvent.builder()
                .userAgent(userAgentName)
                .colorName(colorName)
                .userName(userName)
                .timestamp(dateFormat.format(new Date()))
                .build();

        kafkaTemplate.send(TOPIC_NAME, objectMapper.writeValueAsString(userEvent))
                .addCallback(new ListenableFutureCallback<>() {
                    @Override
                    public void onFailure(Throwable ex) {
                        log.error(ex.getMessage(), ex);
                    }

                    @Override
                    public void onSuccess(SendResult<String, String> result) {
                        log.info(result.toString());
                    }
                });
    }
}

