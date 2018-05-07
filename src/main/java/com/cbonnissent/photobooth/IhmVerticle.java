package com.cbonnissent.photobooth;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import io.vertx.ext.bridge.PermittedOptions;

public class IhmVerticle extends AbstractVerticle {

    @Override
    public void start(Future<Void> future) {

        Router router = Router.router(vertx);

        // Event bus bridge
        SockJSHandler sockJSHandler = SockJSHandler.create(vertx);
        BridgeOptions options = new BridgeOptions();
        options.addInboundPermitted(new PermittedOptions().setAddress(MainVerticle.ADDRESS));
        options.addOutboundPermitted(new PermittedOptions().setAddress(MainVerticle.ADDRESS));
        sockJSHandler.bridge(options);
    
        router.route("/eventbus/*").handler(sockJSHandler);

        //Img result
        StaticHandler imgHandler = StaticHandler.create();
        imgHandler.setCachingEnabled(false);
        imgHandler.setAllowRootFileSystemAccess(true);
        imgHandler.setWebRoot(config().getString("savePath"));

        router.route("/picture/*").handler(imgHandler);

        // Static content
        StaticHandler defaultHandler = StaticHandler.create();
        defaultHandler.setCachingEnabled(false);

        router.route("/*").handler(defaultHandler);

        vertx.createHttpServer().requestHandler(router::accept).listen(config().getInteger("http.port", 8080), result -> {
            if (result.succeeded()) {
                System.out.println("HTTP server started on port "+config().getInteger("http.port", 8080));
                future.complete();
            } else {
                future.fail(result.cause());
            }
        });

    }
}
