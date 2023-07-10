package ru.veselov.websocketroomproject.config.openapi;

public class OpenApiExampleConstants {

    public static final String SORT = "sort";

    public static final String ORDER = "order";

    public static final String PAGE = "page";

    public static final String ROOM_UUID = "1bd7c828-3a5c-4fd9-a2af-78b6a127459f";

    public static final String SORT_FIELD_DESCRIPTION = "Sorting field, createdAt: by default," +
            " available fields: name, ownerName, changedAt, playerType, createdAt";

    public static final String SORT_ORDER_DESCRIPTION = "Sorting order, desc: by default, available orders: asc, desc";

    public static final String ERROR_CONFLICT_MESSAGE = """
            {
              "error": "ERROR_CONFLICT",
              "code": 409,
              "message" : "Error message"
            }""";

    public static final String ERROR_NOT_ROOM_OWNER_MESSAGE = """
            {
              "error": "ERROR_NOT_ROOM_OWNER",
              "code": 403,
              "message": "You are not owner of room"
            }""";

    public static final String CREATED_ROOM = """
            {  "name": "newRoomName",
            "tags" : ["Movie","Other", "Anime"],
            "playerType" :"YOUTUBE"}""";

    private OpenApiExampleConstants() {
    }
}
