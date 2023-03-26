var stompClient = null;
let roomId = Math.floor((Math.random()*1000)+1);
const eventSource = new EventSource('/api/room/sse?roomId='+roomId);
eventSource.onopen = function () {
    console.log("connection is ok")
}
eventSource.onmessage = (e) => {
    console.log(e.data);
};

eventSource.addEventListener('init', (e)=> {
    console.log(e.data);
});

eventSource.addEventListener('USERS_REFRESHED', (e)=> {
    showUsers(e.data)
    console.log(e.data);
});

eventSource.addEventListener('CONNECTED', (e)=> {
    console.log(JSON.parse(e.data).username);
});

eventSource.addEventListener('DISCONNECTED', (e)=> {
    console.log(e.data);
});

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
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
        stompClient.subscribe('/topic/messages/'+roomId, function (greeting) {
            showGreeting(JSON.parse(greeting.body).message.username);
            console.log(greeting)
        });
        stompClient.subscribe('/topic/users/'+roomId, function (users){
            showUsers(JSON.parse(users.body).message);
        }, {roomId: roomId});
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendName() {
    stompClient.send("/app/chat/5", {}, JSON.stringify({'name': $("#name").val()}));
}


function showGreeting(message) {
    $("#greetings").append("<tr><td>" + message + "</td></tr>");
}

function showUsers(message) {

    $("#users").empty();
    $("#users").append("<tr><td>" + message + "</td></tr>");
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { sendName(); });
});