import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.logging.*;

public class Log {
    public static Logger LOGGER = Logger.getGlobal();
    private static Integer myPeerId;
    public static StreamHandler fileHandler;

    private static final String PATTERN = "MM-dd-yyyy HH:mm:ss.SSS";
    public static void initialise(Integer peerId) {
        myPeerId = peerId;
        try {
            fileHandler = new FileHandler("../log_peer_" + myPeerId + ".log");
            Formatter simpleFormatter = new Formatter() {
                @Override
                public String format(LogRecord logRecord) {
                    return String.format(
                            "%1$s %2$s\n",
                            new SimpleDateFormat(PATTERN).format(
                                    new Date(logRecord.getMillis())), formatMessage(logRecord));
                }
            };
            fileHandler.setFormatter(simpleFormatter);
            LOGGER.addHandler(fileHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setInfo (String comment){
        LOGGER.info(comment);
        }
    }


