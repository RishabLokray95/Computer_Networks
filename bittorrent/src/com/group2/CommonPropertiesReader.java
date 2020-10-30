package com.group2;

import com.group2.model.CommonConfiguration;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import static com.group2.constants.Constants.*;

public final class CommonPropertiesReader {

    public static CommonConfiguration getConfigurations() {

        Map<String, String> fileData = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(COMMON_CFG)))) {
            while (reader.ready()) {
                String line = reader.readLine();
                String[] data = line.split(" ");
                fileData.put(data[0],data[1]);
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found. File name : " + COMMON_CFG + " . Exception : " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO Exception. Exception : " + e.getMessage());
        }


        return CommonConfiguration.CommonConfigurationBuilder.builder()
                .withNumberOfPreferredNeighbors(fileData.get(PREFERRED_NEIGHBORS))
                .withUnchokingInterval(fileData.get(UNCHOKING_INTERVAL))
                .withFileName(fileData.get(OPTIMISTIC_UNCHOKING_INTERVAL))
                .withFileName(fileData.get(FILE_NAME))
                .withFileSize(Integer.parseInt(fileData.get(FILE_SIZE)))
                .withPieceSize(Integer.parseInt(fileData.get(PIECE_SIZE))).build();
    }

}
