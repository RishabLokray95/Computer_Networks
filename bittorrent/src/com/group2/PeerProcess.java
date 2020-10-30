package com.group2;

import com.group2.model.CommonConfiguration;
import com.group2.model.PeerInfo;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;


public class PeerProcess {

    public static ConcurrentMap<Integer, Client> clientsMap = new ConcurrentHashMap<>();
    public static ConcurrentMap<Integer, PeerInfo> peerInfoMap = new ConcurrentHashMap<>();
    public final static CommonConfiguration commonConfiguration = CommonPropertiesReader.getConfigurations();


    public static void main(String[] args) throws Exception {
        if(args.length < 1) {
            System.out.println("Peer Id not found in args");
        }
        Integer myId = Integer.parseInt(args[0]);
        List<PeerInfo> peers = PeerInfoReader.getConfigurations();
        PeerInfo myInfo = peers.stream().filter(x -> x.getPeerId().equals(myId)).findFirst().get();


        peerInfoMap = peers.stream().collect(Collectors.toConcurrentMap(PeerInfo::getPeerId, x -> x));

        // Start my server
        Server server = new Server(myInfo.getPeerId());
        server.start();
        // Send handshake requests to other eligible peers
        for(PeerInfo peer : peers) {
            if(peer.getPeerId().equals(myId)) {
                break;
            }
            Client client = new Client(myId, peer);
            client.sendHandshakeRequest();
            clientsMap.put(peer.getPeerId(), client);
        }
        server.join();
    }
}
