package ru.veselov.websocketroomproject.event.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import ru.veselov.websocketroomproject.repository.RoomRepository;

import java.util.UUID;

@Slf4j
public class DeleteRoomTask implements Runnable {

    private final String roomId;

    private final RoomRepository roomRepository;

    public DeleteRoomTask(String roomId, RoomRepository roomRepository) {
        this.roomId = roomId;
        this.roomRepository = roomRepository;
    }

    @Transactional
    public void run() {
        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            log.error(e.getMessage());
            Thread.currentThread().interrupt();
        }
        roomRepository.deleteById(UUID.fromString(roomId));
        log.info("Room deleted [{}]", roomId);
    }
}
