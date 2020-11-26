package com.group2;

import com.group2.model.ActualMessage;
import com.group2.model.MessageType;

import java.util.HashSet;

import static com.group2.PeerProcess.*;

public class PreferredUnchoke implements Runnable {
    private HashSet<Integer> newNeighbours = new HashSet<>();

    @Override
    public void run() {
        // calculate new preferred list
        this.newNeighbours();
        // check the diff of old and new list
        HashSet<Integer> oldPreferred = new HashSet<>(preferredPeers);
        oldPreferred.removeAll(newNeighbours);
        // send choke
        ActualMessage chokeMessage =
                ActualMessage.ActualMessageBuilder.builder()
                        .withMessageType(MessageType.CHOKE.getMessageTypeValue())
                        .build();
        for (Integer peerId: oldPreferred) {
            clientsMap.get(peerId).sendMessage(chokeMessage);
        }

        // check the diff of new and old list
        HashSet<Integer> newPreferred = new HashSet<>(newNeighbours);
        newPreferred.removeAll(preferredPeers);
        // send unchoke
        ActualMessage unchokeMessage =
                ActualMessage.ActualMessageBuilder.builder()
                        .withMessageType(MessageType.UNCHOKE.getMessageTypeValue())
                        .build();
        for (Integer peerId: newPreferred) {
            clientsMap.get(peerId).sendMessage(unchokeMessage);
        }
        preferredPeers = newPreferred;

    }

    void newNeighbours(){
        newNeighbours.clear();
        for (Integer peerId : peerInfoMap.keySet()) {
            if(newNeighbours.size() >= Integer.parseInt(commonConfiguration.getNumberOfPreferredNeighbors())){
                break;
            }
            if(peerInfoMap.get(peerId).isInterested()){
                newNeighbours.add(peerId);
            }
        }
    }
}
