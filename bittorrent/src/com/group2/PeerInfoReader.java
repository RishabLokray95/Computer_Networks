package com.group2;

import com.group2.model.PeerInfo;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static com.group2.constants.Constants.PEER_INFO_CFG;

public final class PeerInfoReader {

    public static List<PeerInfo> getConfigurations() {
        List<PeerInfo> peers = new ArrayList<>();
        Integer bitFieldSize = ((Double) Math.ceil(
                PeerProcess.commonConfiguration.getFileSize() /
                        (PeerProcess.commonConfiguration.getPieceSize() * 1.0D))).intValue();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(PEER_INFO_CFG)))) {
            while (reader.ready()) {
                String line = reader.readLine();
                String[] data = line.split(" ");
                Integer peerId = Integer.parseInt(data[0]);
                String peerHost = data[1].trim();
                Integer port = Integer.parseInt(data[2]);
                boolean hasFile = Boolean.parseBoolean(data[3]);
                PeerInfo peerInfo = new PeerInfo(peerId, peerHost, port, hasFile, bitFieldSize);
                peers.add(peerInfo);
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found. File name : " + PEER_INFO_CFG + " . Exception : " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO Exception. Exception : " + e.getMessage());
        }
        return peers;
    }

}
