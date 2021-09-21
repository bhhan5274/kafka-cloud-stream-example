package io.github.bhhan.example.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/event")
public class EventController {
    private final EventStreamService eventStreamService;

    @PostMapping("/produce")
    public boolean sendEvent(@RequestBody Message msg){
        return eventStreamService.produceEvent(msg);
    }
}
