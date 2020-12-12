import java.io.IOException;
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
            fileHandler.setLevel(Level.INFO);
            LOGGER.setLevel(Level.INFO);
            Formatter simpleFormatter = new SimpleFormatter();
            fileHandler.setFormatter(simpleFormatter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    public static void setInfo (String comment){
        String timeStamp = new SimpleDateFormat("s").format(new Date());
        LOGGER.info(timeStamp + " " + comment);
        }
    }


