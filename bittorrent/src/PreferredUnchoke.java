import java.util.*;
import java.util.stream.Collectors;

import static java.util.Map.Entry.comparingByValue;

public class PreferredUnchoke implements Runnable {
    private final PeerInfo myInfo;
    private final HashSet<Integer> newNeighbours = new HashSet<>();

    public PreferredUnchoke(PeerInfo myInfo) {
        this.myInfo = myInfo;
    }

    @Override
    public void run() {
        // calculate new preferred list
        this.newNeighbours();
        // check the diff of old and new" list
        HashSet<Integer> oldPreferred = new HashSet<>(PeerProcess.preferredPeers);
        oldPreferred.removeAll(newNeighbours);
        // send choke
        for (Integer peerId : oldPreferred) {
            System.out.println("In Preferred, Choking :" + peerId);
            PeerProcess.clientsMap.get(peerId).sendMessage(PeerProcess.CHOKE_MESSAGE);
        }
        // check the diff of new and old list
        HashSet<Integer> newPreferred = new HashSet<>(newNeighbours);
        newPreferred.removeAll(PeerProcess.preferredPeers);
        // send unchoke
        for (Integer peerId : newPreferred) {
            System.out.println("In Preferred, Unchoking :" + peerId);
            PeerProcess.clientsMap.get(peerId).sendMessage(PeerProcess.UNCHOKE_MESSAGE);
        }
        PeerProcess.preferredPeers = new HashSet<>(newNeighbours);

    }

    void newNeighbours() {
        newNeighbours.clear();

        if (myInfo.isHasFile()) {
            //Select neighbours that are interested.

            List<Integer> interestedPeers = PeerProcess.peerInfoMap
                    .values()
                    .stream()
                    .filter(x -> x.isInterested() && PeerProcess.clientsMap.containsKey(x.getPeerId()))
                    .map(PeerInfo::getPeerId).distinct()
                    .collect(Collectors.toList());
            Collections.shuffle(interestedPeers);
            if (interestedPeers.size() <= PeerProcess.commonConfiguration.getNumberOfPreferredNeighbors())
                newNeighbours.addAll(interestedPeers);
            else {
                newNeighbours.addAll(interestedPeers.subList(0, PeerProcess.commonConfiguration.getNumberOfPreferredNeighbors()));
            }

        } else {
            //Check download rates and then set newNeighbours
            // Copy the map
            Set<Integer> sorted = PeerProcess.downloadRates.getDownloadRateMap()
                    .entrySet()
                    .stream()
                    .sequential()
                    .filter(x -> PeerProcess.peerInfoMap.get(x.getKey()).isInterested() && PeerProcess.clientsMap.containsKey(x.getKey()))
                    .sorted(comparingByValue())
                    .limit(PeerProcess.commonConfiguration.getNumberOfPreferredNeighbors())
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toSet());

            System.out.println("--------------Pick from these " +
                    sorted.stream().map(x -> x + "").reduce("", (x, y) -> x + "," + y));
            //Flush download rates map
            PeerProcess.downloadRates.getDownloadRateMap().replaceAll((k, v) -> 0);

            //Add the top k neighbours with highest download rates to newNeighbours set.
            newNeighbours.addAll(sorted);
        }

    }
}
