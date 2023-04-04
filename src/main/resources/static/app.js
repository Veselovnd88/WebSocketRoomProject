var stompClient = null;
//let roomId = Math.floor((Math.random() * 1000) + 1);
let roomId = "5";
let eventSource = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    } else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function connect() {
    var socket = new SockJS('/api/room/chat');
    stompClient = Stomp.over(socket);
    stompClient.connect({roomId: roomId}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        /*stompClient.subscribe('/topic/messages/9', function (greeting) {
            showGreeting(JSON.parse(greeting.body).username);

        });*/
        stompClient.subscribe('/topic/messages/' + roomId, function (greeting) {
            showGreeting(JSON.parse(greeting.body).sentFrom);
            console.log(greeting)
        });
    });
    eventSource = new EventSource('/api/room?roomId=' + roomId);
    eventSource.onopen = function () {
        console.log("connection is ok")
    }
    eventSource.onmessage = (e) => {
        console.log(e.data);
    };

    eventSource.addEventListener('init', (e) => {
        console.log(e.data);
    });

    eventSource.addEventListener('USERS_REFRESHED', (e) => {
        showUsers(e.data);
        console.log(e.data);
    });

    eventSource.addEventListener('CONNECTED', (e) => {
        showServerMessage("Connected " + JSON.parse(e.data).username);
        console.log(e.data);
    });

    eventSource.addEventListener('DISCONNECTED', (e) => {
        showServerMessage("Disconnected " + JSON.parse(e.data).username);
        console.log(e.data);
    });


}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
        eventSource.close();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendName() {
    stompClient.send("/app/chat/"+roomId, {}, JSON.stringify({'sentFrom': $("#name").val()}));
}


function showGreeting(message) {
    $("#greetings").append("<tr><td>" + message + "</td></tr>");
}

function showServerMessage(message) {
    $("#serverMessage").append("<tr><td>" + message + "</td></tr>");
}

function showUsers(message) {

    $("#users").empty();
    $("#users").append("<tr><td>" + message + "</td></tr>");
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $("#connect").click(function () {
        connect();
    });
    $("#disconnect").click(function () {
        disconnect();
    });
    $("#send").click(function () {
        sendName();
    });
});