import java.io.*;
import java.util.HashMap;
import java.util.Map;

public final class CommonPropertiesReader {

    public static CommonConfiguration getConfigurations() {

        Map<String, String> fileData = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(Constants.COMMON_CFG)))) {
            while (reader.ready()) {
                String line = reader.readLine();
                String[] data = line.split(" ");
                fileData.put(data[0],data[1]);
            }

        } catch (FileNotFoundException e) {
            Log.setInfo("File not found. File name : " + Constants.COMMON_CFG + " . Exception : " + e.getMessage());
        } catch (IOException e) {
            Log.setInfo("IO Exception. Exception : " + e.getMessage());
        }


        CommonConfiguration.CommonConfigurationBuilder builder =
            CommonConfiguration.CommonConfigurationBuilder.builder()
                .withNumberOfPreferredNeighbors(fileData.get(Constants.PREFERRED_NEIGHBORS))
                .withUnchokingInterval(fileData.get(Constants.UNCHOKING_INTERVAL))
                .withOptimisticUnchokingInterval(fileData.get(Constants.OPTIMISTIC_UNCHOKING_INTERVAL))
                .withFileName(fileData.get(Constants.FILE_NAME))
                .withFileSize(Integer.parseInt(fileData.get(Constants.FILE_SIZE)))
                .withPieceSize(Integer.parseInt(fileData.get(Constants.PIECE_SIZE)));
        Log.setInfo("----Read the 'Common.cfg' file and below values are set----");
        Log.setInfo("Number of preferred neighbors : " + fileData.get(Constants.PREFERRED_NEIGHBORS));
        Log.setInfo("Unchoking Interval : " + fileData.get(Constants.UNCHOKING_INTERVAL));
        Log.setInfo("Optimistically Unchoking Interval : " + fileData.get(Constants.OPTIMISTIC_UNCHOKING_INTERVAL));
        Log.setInfo("Data file name : " + fileData.get(Constants.FILE_NAME));
        Log.setInfo("File size : " + fileData.get(Constants.FILE_SIZE));
        Log.setInfo("Piece Size : " + fileData.get(Constants.PIECE_SIZE));
        return builder.build();
    }

}
