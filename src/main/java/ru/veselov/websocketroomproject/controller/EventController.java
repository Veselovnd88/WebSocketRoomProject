package ru.veselov.websocketroomproject.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import ru.veselov.websocketroomproject.service.SubscriptionSSEService;

import java.io.IOException;

@RestController
@RequestMapping("/")
@Slf4j
@RequiredArgsConstructor
public class EventController {

    private final SubscriptionSSEService subscriptionSSEService;

    @GetMapping(value = "/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@RequestParam String roomId) {
        SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);
        sentInitEvent(sseEmitter);
        subscriptionSSEService.saveSubscription(roomId, sseEmitter);
        sseEmitter.onCompletion(() -> subscriptionSSEService.removeEmitter(roomId, sseEmitter));
        sseEmitter.onTimeout(() -> subscriptionSSEService.removeEmitter(roomId, sseEmitter));
        sseEmitter.onError((e) -> subscriptionSSEService.removeEmitter(roomId, sseEmitter));
        return sseEmitter;
    }

    private void sentInitEvent(SseEmitter sseEmitter) {
        try {
            sseEmitter.send(SseEmitter.event().name("event").data("vasya"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
