import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.stream.Collectors;


public class PeerProcess {
    public static ConcurrentMap<Integer, Client> clientsMap = new ConcurrentHashMap<>();
    public static ConcurrentMap<Integer, PeerInfo> peerInfoMap = new ConcurrentHashMap<>();
    public static CommonConfiguration commonConfiguration;
    public static Set<Integer> preferredPeers = new HashSet<Integer>();
    public static DownloadRatesFetcher downloadRates = new DownloadRatesFetcher();
    public static Integer optimisticallyUnchokedPeer = 0;
    public static ActualMessage CHOKE_MESSAGE =
            ActualMessage.ActualMessageBuilder.builder()
                    .withMessageType(MessageType.CHOKE.getMessageTypeValue())
                    .build();
    public static ActualMessage UNCHOKE_MESSAGE =
            ActualMessage.ActualMessageBuilder.builder()
                    .withMessageType(MessageType.UNCHOKE.getMessageTypeValue())
                    .build();
    public static ActualMessage.ActualMessageBuilder HAVE_MESSAGE_UNBUILT =
            ActualMessage.ActualMessageBuilder.builder()
                    .withMessageType(MessageType.HAVE.getMessageTypeValue());
    public static byte[] file;

    public static void main(String[] args) throws Exception {
        if(args.length < 1) {
            Log.setInfo("Peer Id not found in args");
        }
        Integer myId = Integer.parseInt(args[0]);
        Log.initialise(myId);
        commonConfiguration = CommonPropertiesReader.getConfigurations();
        List<PeerInfo> peers = PeerInfoReader.getConfigurations();
        PeerInfo myInfo = peers.stream().filter(x -> x.getPeerId().equals(myId)).findFirst().get();
        peerInfoMap = peers.stream().collect(Collectors.toConcurrentMap(PeerInfo::getPeerId, x -> x));

        if(peerInfoMap.get(myId).isHasFile()) {
            Log.setInfo("Peer " + myId + " has set all the bits in bitfield to 1, since it has the file.");
        } else {
            Log.setInfo("Peer " + myId + " has set all the bits in bitfield to 0, since it does not have the file.");
        }

        // Start my server
        ServerSocket listener = new ServerSocket(PeerProcess.peerInfoMap.get(myId).getPort());
        Server server = new Server(myInfo.getPeerId(),listener);
        server.start();

        //Check if has File and populate byte array; else create empty byte array.
        if(myInfo.isHasFile()){
            file = Files.readAllBytes(Paths.get(PeerProcess.commonConfiguration.getFileName()));
        }
        else{
            file = new byte[commonConfiguration.getFileSize()];
        }
        // Send handshake requests to other eligible peers
        for(PeerInfo peer : peers) {
            if(peer.getPeerId().equals(myId)) {
                break;
            }

            Client client = new Client(PeerProcess.peerInfoMap.get(myId), peer);
//            Log.setInfo("TCP connection has been established between peer " + myId + " and peer " + peer.getPeerId());
            client.sendHandshakeRequest();
            clientsMap.put(peer.getPeerId(), client);
            downloadRates.getDownloadRateMap().putIfAbsent(peer.getPeerId(), 0);
        }
        ScheduledExecutorService preferredScheduler = Executors.newSingleThreadScheduledExecutor();
        Runnable preferredUnchoke =  new PreferredUnchoke(myInfo);
        preferredScheduler.scheduleAtFixedRate(preferredUnchoke, 0,
                Long.parseLong(commonConfiguration.getUnchokingInterval()), TimeUnit.SECONDS);

        ScheduledExecutorService optimisticScheduler = Executors.newSingleThreadScheduledExecutor();
        Runnable optimisticUnchoke =  new OptimisticUnchoke(myInfo);
        optimisticScheduler.scheduleAtFixedRate(optimisticUnchoke, 0,
                Long.parseLong(commonConfiguration.getOptimisticUnchokingInterval()), TimeUnit.SECONDS);
        server.join();
    }
}
