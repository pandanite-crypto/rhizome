package rhizome.services.network;

import org.jetbrains.annotations.NotNull;

import io.activej.async.function.AsyncRunnable;
import io.activej.async.function.AsyncRunnables;
import io.activej.async.service.EventloopService;
import io.activej.eventloop.Eventloop;
import io.activej.promise.Promise;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import rhizome.services.network.discovery.PeerDiscoveryListener;
import rhizome.services.network.discovery.PeerDiscoveryService.DiscoveryPeer;

@Slf4j
@Getter
@Setter
public class PeerManagerService implements EventloopService, PeerDiscoveryListener {

    private Eventloop eventloop;
    private final AsyncRunnable refresh;

    public PeerManagerService(Eventloop eventloop, PeerManager manager) {
        this.eventloop = eventloop;
        this.refresh = AsyncRunnables.reuse(() -> doRefresh(manager));
    }

	public Promise<Void> refresh() {
		return refresh.run();
	}

    @Override
    public @NotNull Promise<?> start() {
        log.info("|PEER MANAGER STARTING|");
        return refresh();    
    }

    @Override
    public @NotNull Promise<?> stop() {
        log.info("|PEER MANAGER STARTING|");
        return Promise.complete().whenResult(() -> log.info("|PEER MANAGER STOPPED|"));    
    }

    private Promise<Void> doRefresh(PeerManager manager) {
        // TODO Auto-generated method stub
        return Promise.complete();
    }

    @Override
    public void onNewPeerDiscovered(DiscoveryPeer peer) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'onNewPeerDiscovered'");
    }
}
