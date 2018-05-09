package com.cbonnissent.photobooth;

import java.util.concurrent.TimeUnit;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

public class PictureVerticle extends AbstractVerticle {

    @Override
    public void start(Future<Void> future) {
        String photoPath = config().getString("savePath");
        vertx.eventBus().<JsonObject>consumer(MainVerticle.ADDRESS).handler(message -> {
            if (message.body().getValue("action").equals("takePicture")) {
                // Take picture
                Long dateElement = System.currentTimeMillis();
                String datePath = photoPath + dateElement;
                takePicture(datePath, config().getInteger("nbPhoto", 4)).setHandler(handler -> {
                    vertx.eventBus().publish(MainVerticle.ADDRESS,
                    new JsonObject().put("action", "endPicture"));
                    composePicture(datePath).setHandler(result -> {
                        vertx.eventBus().publish(MainVerticle.ADDRESS,
                                new JsonObject().put("action", "print").put("path", result.result()));
                    });
                    makeGif(datePath).setHandler(result -> {
                        vertx.eventBus().publish(MainVerticle.ADDRESS,
                        new JsonObject().put("action", "play").put("resultPath", dateElement + "-result.gif"));
                    });
                });
            }
        });
    }

    protected Future<Void> takePicture(String photoPath, Integer nbPicture) {
        Future<Void> resultFutur = Future.future();
        vertx.executeBlocking(takePictureFutur -> {
            Process exec;
            try {
                vertx.eventBus().publish(MainVerticle.ADDRESS,
                        new JsonObject().put("action", "flash").put("nbPicture", nbPicture));
                exec = Runtime.getRuntime().exec("streamer -c "+config().getString("webCam", "/dev/video0")+" -s 2304x1536 -f jpeg -o " + photoPath
                        + "-photo-" + nbPicture + ".jpeg");
                exec.waitFor();
                Integer nextNbPicture = nbPicture - 1;
                if (nextNbPicture > 0) {
                    vertx.eventBus().publish(MainVerticle.ADDRESS,
                        new JsonObject().put("action", "wait").put("nbPicture", nextNbPicture));
                    TimeUnit.SECONDS.sleep(3);
                    takePicture(photoPath, nextNbPicture).setHandler(ar -> {
                        takePictureFutur.complete();
                    });
                } else {
                    takePictureFutur.complete();
                }
            } catch (Exception e) {
                e.printStackTrace();
                takePictureFutur.fail(e.getMessage());
            }

        }, res -> {
            if (res.result() == null) {
                resultFutur.fail(res.toString());
            } else {
                resultFutur.complete();
            }
        });
        return resultFutur;
    }

    protected Future<String> composePicture(String photoPath) {
        Future<String> resultFutur = Future.future();
        vertx.executeBlocking(futur -> {
            Process exec;
            try {
                exec = Runtime.getRuntime().exec("montage -background \"#FFFFFF\" -geometry +4+4 " + photoPath
                        + "-photo-[1-4].jpeg " + photoPath + "-result.jpg");
                exec.waitFor();
                futur.complete(photoPath + "-result.jpg");
            } catch (Exception e) {
                e.printStackTrace();
                futur.fail(e.getMessage());
            }
        }, res -> {
            if (res.result() == null) {
                resultFutur.fail(res.toString());
            } else {
                resultFutur.complete(res.result().toString());
            }
        });

        return resultFutur;
    }

    protected Future<String> makeGif(String photoPath) {
        Future<String> resultFutur = Future.future();
        vertx.executeBlocking(futur -> {
            Process exec;
            try {
                exec = Runtime.getRuntime().exec("convert -delay 20 -loop 0 " + photoPath
                        + "-photo-[1-4].jpeg " + photoPath + "-result.gif");
                exec.waitFor();
                futur.complete(photoPath + "-result.gif");
            } catch (Exception e) {
                e.printStackTrace();
                futur.fail(e.getMessage());
            }
        }, res -> {
            if (res.result() == null) {
                resultFutur.fail(res.toString());
            } else {
                resultFutur.complete(res.result().toString());
            }
        });

        return resultFutur;
    }
}
