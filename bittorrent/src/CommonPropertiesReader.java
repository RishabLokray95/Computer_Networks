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
            System.out.println("File not found. File name : " + Constants.COMMON_CFG + " . Exception : " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO Exception. Exception : " + e.getMessage());
        }


        return CommonConfiguration.CommonConfigurationBuilder.builder()
                .withNumberOfPreferredNeighbors(fileData.get(Constants.PREFERRED_NEIGHBORS))
                .withUnchokingInterval(fileData.get(Constants.UNCHOKING_INTERVAL))
                .withOptimisticUnchokingInterval(fileData.get(Constants.OPTIMISTIC_UNCHOKING_INTERVAL))
                .withFileName(fileData.get(Constants.FILE_NAME))
                .withFileSize(Integer.parseInt(fileData.get(Constants.FILE_SIZE)))
                .withPieceSize(Integer.parseInt(fileData.get(Constants.PIECE_SIZE))).build();
    }

}
