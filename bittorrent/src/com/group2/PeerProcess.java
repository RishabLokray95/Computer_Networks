package com.group2;

import com.group2.model.PeerInfo;
import sun.security.pkcs11.wrapper.Functions;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;


public class PeerProcess {

    public static ConcurrentMap<Integer, Client> clientsMap = new ConcurrentHashMap<>();
    public static ConcurrentMap<Integer, PeerInfo> peerInfoMap = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        if(args.length < 1) {
            System.out.println("Peer Id not found in args");
        }
        Integer peerId = Integer.parseInt(args[0]);
        List<PeerInfo> peers = PeerInfoReader.getConfigurations();
        PeerInfo myInfo = peers.stream().filter(x -> x.getPeerId().equals(peerId)).findFirst().get();

        // Create peerInfo Map
        peerInfoMap = peers.stream().collect(Collectors.toConcurrentMap(PeerInfo::getPeerId, x -> x));

        // Start my server
        Server server = new Server(myInfo);

        // Send handshake requests to other eligible peers
        for(PeerInfo peer : peers) {
            if(peer.getPeerId().equals(peerId)) {
                break;
            }
            Client client = new Client(peerId, peer);
            client.sendHandshakeRequest();
            clientsMap.put(peer.getPeerId(), client);
        }
    }
}
