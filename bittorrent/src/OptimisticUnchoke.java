import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public class OptimisticUnchoke implements Runnable {
    private final PeerInfo myInfo;
    public OptimisticUnchoke(PeerInfo myInfo) {
        this.myInfo = myInfo;
    }

    @Override
    public void run() {
        Set<Integer> interestedPeers = PeerProcess.peerInfoMap
                .keySet()
                .stream()
                .filter(x -> PeerProcess.peerInfoMap.get(x).isInterested() && PeerProcess.clientsMap.containsKey(x))
                .collect(Collectors.toSet());

        interestedPeers.removeAll(PeerProcess.preferredPeers);
        ArrayList<Integer> interestedPeers_list = new ArrayList<>(interestedPeers);
        Collections.shuffle(interestedPeers_list);
        Log.setInfo("Trying to optimistically unchoke peers, choosing from the following interested neighbors :" + interestedPeers_list.stream().map(x -> x + "").collect(Collectors.joining(",")));
        if (interestedPeers_list.size() > 0) {
            int newOptimisticUnchokedPeer = interestedPeers_list.get(0);
            if (!PeerProcess.preferredPeers.contains(PeerProcess.optimisticallyUnchokedPeer)
                    && PeerProcess.optimisticallyUnchokedPeer != newOptimisticUnchokedPeer
                    && PeerProcess.clientsMap.containsKey(PeerProcess.optimisticallyUnchokedPeer)) {

                Log.setInfo("Choking :" + PeerProcess.optimisticallyUnchokedPeer);
                PeerProcess.clientsMap.get(PeerProcess.optimisticallyUnchokedPeer).sendMessage(PeerProcess.CHOKE_MESSAGE);
            }
            if (!PeerProcess.preferredPeers.contains(newOptimisticUnchokedPeer) && PeerProcess.optimisticallyUnchokedPeer != newOptimisticUnchokedPeer) {
                Log.setInfo("UnChoking :" + newOptimisticUnchokedPeer);
                PeerProcess.clientsMap.get(newOptimisticUnchokedPeer).sendMessage(PeerProcess.UNCHOKE_MESSAGE);
            }
            PeerProcess.optimisticallyUnchokedPeer = newOptimisticUnchokedPeer;

            Log.setInfo("Peer " + myInfo.getPeerId() + " has the optimistically unchoked neighbor " +
                    PeerProcess.optimisticallyUnchokedPeer + ".");
        } else {
            Log.setInfo("No peers to choose from because none is interested");
        }
    }
}
