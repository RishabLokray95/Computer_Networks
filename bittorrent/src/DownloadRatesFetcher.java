import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class DownloadRatesFetcher {
    private static final ConcurrentMap<Integer, Integer> downloadRateMap = new ConcurrentHashMap<>();
    public void addPeerDownloadDetails(Integer ConnectedPeerId,Integer payloadLength) {
        if(downloadRateMap.containsKey(ConnectedPeerId)){
            downloadRateMap.put(ConnectedPeerId,downloadRateMap.get(ConnectedPeerId)+payloadLength);
        }
        else{
            downloadRateMap.put(ConnectedPeerId,payloadLength);
        }

    }
    public ConcurrentMap<Integer, Integer> getDownloadRateMap() {
        return downloadRateMap;
    }
}
