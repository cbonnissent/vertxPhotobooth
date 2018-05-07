package com.cbonnissent.photobooth;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;

public class MainVerticle extends AbstractVerticle {

    public static final String ADDRESS = "takePicture";

    @Override
    public void start(Future<Void> future) {

        vertx.deployVerticle(IhmVerticle.class.getName(), new DeploymentOptions().setConfig(config()));
        vertx.deployVerticle(PictureVerticle.class.getName(), new DeploymentOptions().setConfig(config()));
        vertx.deployVerticle(PrinterVerticle.class.getName(), new DeploymentOptions().setConfig(config()));
    }
}
