package com.group2;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.SimpleFormatter;
import java.util.logging.Formatter;

public class Log extends Thread{
    public Logger LOGGER;

    public Log(Integer PeerId) throws IOException {

        this.LOGGER = Logger.getLogger(Log.class.getName());
        Handler fileHandler  = new FileHandler("./log_file_"+PeerId+".log");
        this.LOGGER.addHandler(fileHandler);
        fileHandler.setLevel(Level.ALL);
        this.LOGGER.setLevel(Level.ALL);


        Formatter simpleFormatter  = new SimpleFormatter();
        fileHandler.setFormatter(simpleFormatter);
    }

}
