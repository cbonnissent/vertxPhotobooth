//cat ./pictures.jpg | jpegtopnm | pnmtops -width 4 -height 6 | lp
package com.cbonnissent.photobooth;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;

import java.io.FileInputStream;
import java.io.IOException;

import io.vertx.core.AbstractVerticle;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

public class PrinterVerticle extends AbstractVerticle {

    @Override
    public void start(Future<Void> future) {
        vertx.eventBus().<JsonObject>consumer(MainVerticle.ADDRESS).handler(message -> {
            if (message.body().getValue("action").equals("print") && message.body().getValue("path") != null) {
                //print(message.body().getValue("path").toString());
            }
        });
    }

    protected void print(String photoPath) {

        try {
            PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();
            pras.add(new Copies(1));
            PrintService pss[] = PrintServiceLookup.lookupPrintServices(DocFlavor.INPUT_STREAM.JPEG, pras);
            if (pss.length == 0) {
                throw new RuntimeException("No printer services available.");
            }
            PrintService ps = null;
            for (PrintService currentPrinter : pss) {
                if (currentPrinter.getName().equals(config().getValue("printer"))) {
                    ps = currentPrinter;
                }
            }
            if (ps == null) {
                throw new Exception("unable to find printer");
            }
            System.out.println("Printing to " + ps);
            DocPrintJob job = ps.createPrintJob();
            FileInputStream fin = new FileInputStream(photoPath);
            Doc doc = new SimpleDoc(fin, DocFlavor.INPUT_STREAM.JPEG, null);
            job.print(doc, pras);
            fin.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}