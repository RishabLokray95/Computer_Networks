package com.group2;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

public class Log {
    public static Logger LOGGER = Logger.getLogger(Log.class.getName());
    private static Integer myPeerId;
    public static Handler fileHandler;


    public static void initialise(Integer peerId) {
        myPeerId = peerId;
        try {
            fileHandler = new FileHandler("./log_file_" + myPeerId + ".log");
            LOGGER.addHandler(fileHandler);
            fileHandler.setLevel(Level.ALL);
            LOGGER.setLevel(Level.ALL);
            Formatter simpleFormatter = new SimpleFormatter();
            fileHandler.setFormatter(simpleFormatter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void setInfo (String comment){
        String timeStamp = new SimpleDateFormat().format(new Date());
        LOGGER.info(timeStamp + " " + comment);
        }
    }


