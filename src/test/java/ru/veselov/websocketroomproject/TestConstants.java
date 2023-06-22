package ru.veselov.websocketroomproject;

import java.util.UUID;

public class TestConstants {

    public static final String AUTH_HEADER = "Authorization";

    //subject=user1, role=ADMIN
    public static final String BEARER_JWT = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyMSIsInJvbGUiOiJBRE1JTiIsImlhdCI6MTUxNjIzOTAyMn0.v_7iQsYZkDi2I5JTaHm53ZYNNUUBJuomtjttLBcCYKk";

    public static final String COMMAND_HEADER = "stompCommand"; //this is private field in StompHeaderAccessor

    public static final String ROOM_ID_HEADER = "roomId";

    public static String TEST_SESSION_ID = "test";

    public static String TEST_USERNAME = "test";

    public static final String SECRET = "D9D323C5E55F45C206D7880329B1721A4334C00F336E5F2F1E9DAB745FF44837";

    public static final String ROOM_ID = UUID.randomUUID().toString();

}
