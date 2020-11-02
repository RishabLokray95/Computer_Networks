package com.group2;
import com.group2.model.PeerInfo;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.SimpleFormatter;
import java.util.logging.Formatter;
import java.text.SimpleDateFormat;
import java.util.Date;

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


