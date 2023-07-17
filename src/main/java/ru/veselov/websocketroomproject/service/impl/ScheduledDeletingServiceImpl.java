package ru.veselov.websocketroomproject.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.veselov.websocketroomproject.repository.RoomRepository;
import ru.veselov.websocketroomproject.service.ScheduledDeletingService;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduledDeletingServiceImpl implements ScheduledDeletingService {

    private final RoomRepository roomRepository;

    @Scheduled(cron = "${room.delete-empty-room-period-cron}")
    @Override
    @Transactional
    public void deleteEmptyRooms() {
        roomRepository.deleteEmptyRooms();
        log.info("Empty rooms deleted by schedule");
    }

}
