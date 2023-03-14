package ru.veselov.websocketroomproject.service;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.veselov.websocketroomproject.dto.RoomInfoDTO;
import ru.veselov.websocketroomproject.exception.NoRoomFoundException;
import ru.veselov.websocketroomproject.model.RoomModel;
import ru.veselov.websocketroomproject.repository.RoomRepository;

import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class RoomService {

    private final RoomRepository repository;


    public RoomModel getRoom(){
        log.info("Получение комнаты {}", 5);//FIXME
     return repository.find();
    }

    public RoomModel findRoomById(Integer id) throws NoRoomFoundException {
        log.info("Retrieving room #{} from db", id);
       return repository.findById(id).orElseThrow(NoRoomFoundException::new);
    }



    public static RoomInfoDTO convertToConfigDTO(RoomModel model){
        return RoomInfoDTO.builder()
                .url(model.getSourceUrl())
                .name(model.getName())
                .isPublic(model.getIsPublic())
                .id(model.getId())
                .ownerName(model.getOwner().getUsername())
                .build();
    }

}
