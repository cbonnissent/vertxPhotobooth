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
        const takePicture = document.getElementById("takePicture");
        const mainContent = document.getElementById("mainContent");
        let photoInProgress = false;

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
                    mainContent.classList.remove("flash");
                    takePicture.classList.remove("pulse");
                    pictureInProgress.style.display = "none";
                    videoWrapper.style.display = "none";
                    resultImg.src = '/picture/'+message.body.resultPath;
                    resultWrapper.style.display = "block";
                    startVideo();
                    window.setTimeout(() => {
                        photoInProgress = false;
                        videoWrapper.style.display = "block";
                        resultWrapper.style.display = "none";
                    }, 10000);
                }
            }
            if (message.body.action === "endPicture") {
                mainContent.classList.remove("flash");
                takePicture.classList.remove("pulse");
                takePicture.textContent = `Je présente tout ça.... Attendez...`;
            }
            if (message.body.action === "flash") {
                mainContent.classList.add("flash");
                takePicture.classList.add("pulse");
                takePicture.textContent = `Attention !! Attention !! Je prend la photo ${Math.abs(5-message.body.nbPicture)}/4`;
            }
            if (message.body.action === "wait") {
                takePicture.classList.remove("pulse");
                mainContent.classList.remove("flash");
                if (message.body.nbPicture === 3) {
                    takePicture.textContent = `Souriez`;
                }
                if (message.body.nbPicture === 2) {
                    takePicture.textContent = `Encore un effort`;
                }
                if (message.body.nbPicture === 1) {
                    takePicture.textContent = `C'est la dernière`;
                }
            }
        });

        document.getElementsByTagName("body")[0].addEventListener("click", () => {
            if (photoInProgress) {
                return;
            }
            videoWrapper.style.display = "none";
            pictureInProgress.style.display = "block";
            photoInProgress = true;
            takePicture.textContent = "Préparez vous, le petit oiseau va sortir ! Je vais prendre 4 photos !";
            currentStream.getVideoTracks()[0].stop();
            window.setTimeout(() => {
                eb.publish("takePicture", {
                    "action": "takePicture"
                });
            }, 1500);
        });

        startVideo();

    }
});
