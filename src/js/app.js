import EventBus from "vertx3-eventbus-client";
require("./app.css");

window.addEventListener("load", () => {
    const eb = new EventBus('eventbus');

    eb.onopen = function () {

        const video = document.getElementById("video");
        const videoWrapper = document.getElementById("contentVideo");
        const resultWrapper =  document.getElementById("displayResult");
        const resultImg = document.getElementById("result");
        const pictureInProgress =  document.getElementById("pictureInProgress");

        let currentStream;

        const startVideo = () => {
            var constraints = {
                video: {
                    width: 1280,
                    height: 1080
                }
            };

            navigator.mediaDevices.getUserMedia(constraints)
                .then((mediaStream) => {
                    currentStream = mediaStream;
                    video.srcObject = mediaStream;
                    video.onloadedmetadata = function (e) {
                        video.play();
                    };
                })
                .catch(function (err) {
                    console.log(err.name + ": " + err.message);
                });
        };

        // set a handler to receive a message
        eb.registerHandler('takePicture', function (error, message) {
            console.log('received a message: ' + JSON.stringify(message));
            if (message.body.action === "play") {
                if (message.body.resultPath) {
                    pictureInProgress.style.display = "none";
                    videoWrapper.style.display = "none";
                    resultImg.src = '/picture/'+message.body.resultPath;
                    resultWrapper.style.display = "block";
                    startVideo();
                    window.setTimeout(() => {
                        videoWrapper.style.display = "block";
                        resultWrapper.style.display = "none";
                    }, 10000);
                }
            }
        });

        document.getElementsByTagName("body")[0].addEventListener("click", () => {
            videoWrapper.style.display = "none";
            pictureInProgress.style.display = "block";
            currentStream.getVideoTracks()[0].stop();
            window.setTimeout(() => {
                eb.publish("takePicture", {
                    "action": "takePicture"
                });
            }, 500);
        });

        startVideo();

    }
});
