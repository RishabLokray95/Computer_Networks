package com.group2;

import com.group2.model.ActualMessage;
import com.group2.model.MessageType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import static com.group2.PeerProcess.*;

public class OptimisticUnchoke implements Runnable {
    @Override
    public void run() {
        Set<Integer> interestedPeers = peerInfoMap
                .keySet()
                .stream()
                .filter(x -> peerInfoMap.get(x).isInterested())
                .collect(Collectors.toSet());

        interestedPeers.removeAll(preferredPeers);
        ArrayList<Integer> interestedPeers_list = new ArrayList<>(interestedPeers);
        Collections.shuffle(interestedPeers_list);
        int newOptimisticUnchokedPeer = interestedPeers_list.get(0);

        //send unchoke to this new
        // send unchoke
        ActualMessage unchokeMessage =
                ActualMessage.ActualMessageBuilder.builder()
                        .withMessageType(MessageType.UNCHOKE.getMessageTypeValue())
                        .build();

        clientsMap.get(newOptimisticUnchokedPeer).sendMessage(unchokeMessage);

        if(!preferredPeers.contains(optimisticallyUnchokedPeer)){
            ActualMessage chokeMessage =
                    ActualMessage.ActualMessageBuilder.builder()
                            .withMessageType(MessageType.CHOKE.getMessageTypeValue())
                            .build();

            clientsMap.get(newOptimisticUnchokedPeer).sendMessage(chokeMessage);
        }
        optimisticallyUnchokedPeer = newOptimisticUnchokedPeer;
    }
}
