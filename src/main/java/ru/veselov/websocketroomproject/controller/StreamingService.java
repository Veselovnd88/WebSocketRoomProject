package ru.veselov.websocketroomproject.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class StreamingService {

    private static final String FORMAT = "classpath:videos/%s.mp4";

    private final ResourceLoader resourceLoader;


    public Mono<Resource> getVideo(String title) throws IOException {
        Resource resource = resourceLoader.getResource(String.format(FORMAT, title));
        byte[] contentAsByteArray = resource.getContentAsByteArray();
        UrlResource url = new UrlResource("https://www.youtube.com/watch?v=lJxjTLU9pGs");
        InputStream inputStream = url.getInputStream();
        byte[] bytes = inputStream.readAllBytes();



        Resource byteArrayResource = new ByteArrayResource(bytes);
        return Mono.just(byteArrayResource);
    }
}