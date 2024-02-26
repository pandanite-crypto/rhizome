package rhizome.server;

import java.util.Map;
import java.util.HashMap;
import java.net.InetSocketAddress;

import io.activej.config.Config;
import io.activej.config.converter.ConfigConverters;
import io.activej.eventloop.Eventloop;
import io.activej.inject.annotation.Eager;
import io.activej.inject.annotation.Provides;
import io.activej.inject.module.AbstractModule;
import rhizome.net.NetworkUtils;
import rhizome.net.p2p.PeerSystem;
import rhizome.net.p2p.gossip.GossipSystem;
import rhizome.net.p2p.peer.Peer;
import rhizome.services.network.PeerManager;
import rhizome.services.network.PeerManagerService;
import rhizome.services.network.discovery.DiscoveryService;
import rhizome.services.network.discovery.PeerDiscoveryService;

import static io.activej.config.converter.ConfigConverters.ofList;
import static io.activej.config.converter.ConfigConverters.ofString;

public class NetworkModule extends AbstractModule {

    public static NetworkModule create() {
        return new NetworkModule();
    }

    @Provides
    @Eager
    public PeerManager peerManager() {
        return new PeerManager();
    }

    @Provides
    @Eager
    public PeerManagerService peerManagerService(Eventloop eventloop, PeerSystem peerSystem) {
        return new PeerManagerService(eventloop, peerSystem);
    }

    @Provides
    @Eager
    public PeerDiscoveryService peerDiscoveryService(Eventloop eventloop, Map<Object, Peer> seeders, PeerSystem peerSystem) {
        return PeerDiscoveryService.create(eventloop, DiscoveryService.constant(seeders), peerSystem);
    }

    @Provides
    @Eager
    public PeerSystem peerSystem() {
        return GossipSystem.builder().build();
    }
    
    @Provides
    Map<Object, Peer> seeders(Config config) {
        Map<Object, Peer> peers = new HashMap<>();
        config.get(ofList(ConfigConverters.ofString(), ";"), "seeders").stream()
                .map(ipAddress -> Peer.fromAddress(config.get(ofString(), "cluster"), new InetSocketAddress(ipAddress, 8080)))
                .forEach(peer -> peers.put(peer.address(), peer));

        config.get(ofList(ConfigConverters.ofString(), ";"), "dns").stream()
                .flatMap(hostname -> NetworkUtils.getIPAddresses(hostname).stream())
                .map(ipAddress -> Peer.fromAddress(config.get(ofString(), "cluster"), new InetSocketAddress(ipAddress, 8080))) 
                .forEach(peer -> peers.put(peer.address(), peer));
        return peers;
    }
}