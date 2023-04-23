var stompClient = null;
//let roomId = Math.floor((Math.random() * 1000) + 1);
let roomId = "5";
let jwt = eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwidXNlcm5hbWUiOiJ1c2VyMSIsInJvbGUiOiJhZG1pbiJ9.vDluIRzAjSOxbq8I4tLPUR_koUl7GPkAq34xjsuA1Ds;
fetch("/api/room/getInfo/" + roomId, {
    headers: {
        Authorization : 'Bearer '+jwt
    }}
    ).then(r =>
    r.json())
    .then(data => {
            console.log(data)
            owner = data.owner;
            username = data.username;
            console.log(owner)
        }
    )

let owner = null;
let username = null;
var tag = document.createElement('script');
tag.src = "https://www.youtube.com/player_api";
var firstScriptTag = document.getElementsByTagName('script')[0];
firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);

const tz = Intl.DateTimeFormat().resolvedOptions().timeZone;
addVideoLink();

function addVideoLink() {
    console.log("link for video added");
    document.getElementById("ytplayer").src = "https://www.youtube.com/embed/_PEPaWFs064";
    //console.log(document.getElementById("loadImage"));

}

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
        console.log(socket._transport.url);
        var sessionId = /\/([^\/]+)\/websocket/.exec(socket._transport.url)[1];
        console.log(sessionId)
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
        });

        stompClient.subscribe('/topic/youtube/' + roomId, function (message) {
            console.log(message);

            let state = JSON.parse(message.body).playerState;
            let time = JSON.parse(message.body).currentTime;
            let quality = JSON.parse(message.body).playbackQuality;
            let rate = JSON.parse(message.body).playbackRate;
            console.log("go action for" + username)
            if (username !== owner) {
                player.seekTo(time, true);
                if (state === 1) {
                    player.playVideo();
                }
                if (state === 2) {
                    player.pauseVideo();
                }
                if (quality != null) {
                    player.playbackQuality = quality;
                }
                if (rate != null) {
                    player.playbackRate = rate;
                }
            }

        });
    });
}

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


// Replace the 'ytplayer' element with an <iframe> and
// YouTube player after the API code downloads.
var player;

function onYouTubePlayerAPIReady() {
    player = new YT.Player('ytplayer', {
        height: '360',
        width: '640',
        videoId: 'cY9nFsx4q0I',
        playerVars: {'autoplay': 0, 'controls': 1},
        events: {
            'onReady': onPlayerReady,
            'onStateChange': onPlayerStateChange,
            'onPlaybackQualityChange': onPlaybackQualityChange,
            'onPlaybackRateChange': onPlaybackRateChange,
        }
    });
    if (username !== owner) {
        player.controls = 0;
    }
}


function onPlayerReady(event) {
    /*stompClient.subscribe('/topic/youtube/' + roomId, function (greeting) {
        console.log(greeting);*/
    // createImage(greeting);
    // addVideoLink(greeting);
}


// 5. The API calls this function when the player's state changes.
//    The function indicates that when playing a video (state=1),
//    the player should play for six seconds and then stop.
var done = false;

function onPlayerStateChange(event) {
    console.log(username + " make action ")
    if (username === owner) {
        console.log(player.getCurrentTime());
        let state = event.data;
        if (state === YT.PlayerState.PAUSED || state === YT.PlayerState.PLAYING)
            stompClient.send("/app/youtube/" + roomId, {"content-type": "application/json"}, JSON.stringify({
                'playerState': state,
                'currentTime': player.getCurrentTime()
            }));

        console.log("send get query to endpoint")
    }
}

function onPlaybackQualityChange(event) {
    if (username === owner) {
        console.log(player.getCurrentTime());
        let quality = event.data;
        stompClient.send("/app/youtube/" + roomId, {"content-type": "application/json"}, JSON.stringify({
            'playbackQuality': quality
        }));
        console.log("send get query to endpoint")
    }
}

function onPlaybackRateChange(event) {
    if (username === owner) {
        let rate = event.data;
        console.log(rate);
        stompClient.send("/app/youtube/" + roomId, {"content-type": "application/json"}, JSON.stringify({
            'playbackRate': rate
        }));
        console.log("send get query to endpoint")
    }
}

function stopVideo() {
    player.stopVideo();
}
