
    var tag = document.createElement('script');
    tag.src = "https://www.youtube.com/player_api";
    var firstScriptTag = document.getElementsByTagName('script')[0];
    firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);

    // Replace the 'ytplayer' element with an <iframe> and
    // YouTube player after the API code downloads.
    var player;

    function onYouTubePlayerAPIReady() {
        player = new YT.Player('ytplayer', {
            height: '360',
            width: '640',
            videoId: '_PEPaWFs064',
            playerVars: { 'autoplay': 1, 'controls': 1 },
            events: {
                'onReady': onPlayerReady,
                'onStateChange': onPlayerStateChange

            }
        });
    }



    function onPlayerReady(event) {
        event.target.playVideo();
    }

    // 5. The API calls this function when the player's state changes.
    //    The function indicates that when playing a video (state=1),
    //    the player should play for six seconds and then stop.
    var done = false;
    function onPlayerStateChange(event) {
        console.log(player.getCurrentTime());
        if (event.data === YT.PlayerState.PAUSED) {
            fetch('/api/room/5')
            console.log("send get query to endpoint")
        }
    }

/*    player.addEventListener('scroll', function(event) {
        // Get current scroll position
        const scrollPosition = event.target.scrollTop;

        // Send scroll position to backend
        fetch('/api/room/5?scroll=' + scrollPosition)
            .then(response => {
                // Handle response
            })
            .catch(error => {
                // Handle error
            });
    });*/
    function stopVideo() {
        player.stopVideo();
    }
