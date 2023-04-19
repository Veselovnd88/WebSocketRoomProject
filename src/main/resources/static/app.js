var stompClient = null;
//let roomId = Math.floor((Math.random() * 1000) + 1);
let roomId = "5";
let eventSource = null;
const tz = Intl.DateTimeFormat().resolvedOptions().timeZone;

const reader = new FileReader();

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
        stompClient.subscribe('/topic/messages/' + roomId, function (greeting) {

            showGreeting(JSON.parse(greeting.body).sent + ": " +
                JSON.parse(greeting.body).sentFrom + ": " + JSON.parse(greeting.body).content);
            console.log(greeting);
            // createImage(greeting);
           // addVideoLink(greeting);
        });
        stompClient.subscribe('/user/queue/private', function (greeting) {
            showGreeting(JSON.parse(greeting.body).sent + ": " +
                JSON.parse(greeting.body).sentFrom + ": " + JSON.parse(greeting.body).content);
            console.log(greeting);
        })

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
    stompClient.send("/app/chat/" + roomId, {"content-type": "application/json"}, JSON.stringify({
        'content': $("#name").val(), 'zoneId': tz
    }));
}

function sendToUser() {
    stompClient.send("/app/chat-private", {"content-type": "application/json"}, JSON.stringify({
        'content': $("#name").val(),
        'sendTo': "user1",
        'zoneId': tz
    }));
}


function showGreeting(message) {
    $("#greetings").append("<tr><td>" + message + "</td></tr>");
}

function createImage(message) {
    console.log()
    let img = new Image();
    //let blob = new Blob(message, {type: "image/png"});
    //let url = URL.createObjectURL(blob);
    console.log("I am here tryin to convert image")
    img.src = JSON.parse((message.body).content);
    console.log(img.src);
    document.getElementById("loadImage").src = img.src;
}

function addVideoLink(message) {
    console.log("link for video added");
    document.getElementById("videoSource").src = "/api/room/" + roomId;
}

function showServerMessage(message) {
    $("#serverMessage").append("<tr><td>" + message + "</td></tr>");
}

function sendMyImage() {
    let fileInput = document.getElementById('file');
    let file = fileInput.files[0];

    reader.readAsDataURL(file);


    reader.onloadend = function () {
        let message = reader.result;
        console.log("Send Image as dataUrl")
        console.log(message);
        stompClient.send("/app/chat/" + roomId, {},
            JSON.stringify({
                'content': message
            }));
    }

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
        //sendName();
        sendToUser();
    });
    $("#sendImage").click(function () {
        sendMyImage();
    });
});