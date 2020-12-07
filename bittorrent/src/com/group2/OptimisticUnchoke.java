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
                .filter(x -> peerInfoMap.get(x).isInterested() && clientsMap.containsKey(x))
                .collect(Collectors.toSet());

        interestedPeers.removeAll(preferredPeers);
        ArrayList<Integer> interestedPeers_list = new ArrayList<>(interestedPeers);
        Collections.shuffle(interestedPeers_list);
        System.out.println("In Optimistic, picking from :" + interestedPeers_list.stream().map(x -> x + "").collect(Collectors.joining(",")));
        if (interestedPeers_list.size() > 0) {
            int newOptimisticUnchokedPeer = interestedPeers_list.get(0);
            if (!preferredPeers.contains(optimisticallyUnchokedPeer)
                    && optimisticallyUnchokedPeer != newOptimisticUnchokedPeer
                    && clientsMap.containsKey(optimisticallyUnchokedPeer)) {

                System.out.println("In Optimistic, Choking :" + optimisticallyUnchokedPeer);
                clientsMap.get(optimisticallyUnchokedPeer).sendMessage(CHOKE_MESSAGE);
            }
            if (!preferredPeers.contains(newOptimisticUnchokedPeer) && optimisticallyUnchokedPeer != newOptimisticUnchokedPeer) {
                System.out.println("In Optimistic, UnChoking :" + newOptimisticUnchokedPeer);
                clientsMap.get(newOptimisticUnchokedPeer).sendMessage(UNCHOKE_MESSAGE);
            }
            optimisticallyUnchokedPeer = newOptimisticUnchokedPeer;
        }
    }
}
