package com.group2;

import com.group2.model.*;

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
    public final static CommonConfiguration commonConfiguration = CommonPropertiesReader.getConfigurations();
    public static Set<Integer> preferredPeers = new HashSet<Integer>();
    public static  DownloadRatesFetcher downloadRates = new DownloadRatesFetcher();
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
            System.out.println("Peer Id not found in args");
        }
        Integer myId = Integer.parseInt(args[0]);
        List<PeerInfo> peers = PeerInfoReader.getConfigurations();
        PeerInfo myInfo = peers.stream().filter(x -> x.getPeerId().equals(myId)).findFirst().get();
        Log.initialise(myId);

        peerInfoMap = peers.stream().collect(Collectors.toConcurrentMap(PeerInfo::getPeerId, x -> x));

        // Start my server
        Server server = new Server(myInfo.getPeerId());
        server.start();
        ScheduledExecutorService preferredScheduler = Executors.newSingleThreadScheduledExecutor();
        Runnable preferredUnchoke =  new PreferredUnchoke(myInfo);
        preferredScheduler.scheduleAtFixedRate(preferredUnchoke, 0,
                Long.parseLong(commonConfiguration.getUnchokingInterval()), TimeUnit.SECONDS);

        ScheduledExecutorService optimisticScheduler = Executors.newSingleThreadScheduledExecutor();
        Runnable optimisticUnchoke =  new OptimisticUnchoke();
        optimisticScheduler.scheduleAtFixedRate(optimisticUnchoke, 0,
                Long.parseLong(commonConfiguration.getOptimisticUnchokingInterval()), TimeUnit.SECONDS);


        //Check if has File and populate byte array; else create empty byte array.
        if(myInfo.isHasFile()){
            file = Files.readAllBytes(Paths.get("./bittorrent/testFile.pdf"));
        }
        else{
            file = new byte[commonConfiguration.getFileSize()];
        }

        // Send handshake requests to other eligible peers
        for(PeerInfo peer : peers) {
            if(peer.getPeerId().equals(myId)) {
                break;
            }
            Client client = new Client(myId, peer);
            client.sendHandshakeRequest();
            clientsMap.put(peer.getPeerId(), client);
            downloadRates.getDownloadRateMap().putIfAbsent(peer.getPeerId(), 0);
        }
        server.join();
    }
}
