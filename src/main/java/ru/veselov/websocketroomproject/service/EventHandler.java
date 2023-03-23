package ru.veselov.websocketroomproject.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

@Service
@Slf4j
public class EventHandler {

    private final List<Consumer<String>> listeners = new CopyOnWriteArrayList<>();

    public void subscribe(Consumer<String> listener){
        listeners.add(listener);
        log.info("New consumer added");
    }

    public void publish(String event){
        listeners.forEach(listener-> listener.accept(event));
    }
}
