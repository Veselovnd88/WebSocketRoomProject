package ru.veselov.websocketroomproject;

public class TestConstants {

    public static final String AUTH_HEADER = "Authorization";

    public static final String BEARER_JWT = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyMSIsI" +
            "nJvbGUiOiJhZG1pbiJ9.WX64wPxrQ9JJfRxbYGqOzfGEi6hooEzOkKd0CtQDztU";

    public static final String COMMAND_HEADER = "stompCommand"; //this is private field in StompHeaderAccessor

    public static final String ROOM_ID_HEADER = "roomId";

    public static String TEST_SESSION_ID = "test";

    public static String TEST_USERNAME = "test";
}
