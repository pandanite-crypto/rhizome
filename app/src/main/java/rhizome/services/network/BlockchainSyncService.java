package rhizome.services.network;

import java.util.List;

import io.activej.async.function.AsyncRunnable;
import io.activej.async.function.AsyncRunnables;
import io.activej.eventloop.Eventloop;
import io.activej.promise.Promise;
import lombok.Builder;

public class BlockchainSyncService extends BaseService {
    
    private final List<AsyncRunnable> routines = List.of(
        AsyncRunnables.reuse(this::sync)
    );

    private BlockchainSyncService(Eventloop eventloop, PeerManagerService peerManagerService) {
        super(eventloop, routines);
        this.peerManagerService = peerManagerService;
    }

    public static BlockchainSyncService create(Eventloop eventloop, PeerManagerService peerManagerService) {
        return new BlockchainSyncService(eventloop, peerManagerService);
    }

    private final PeerManagerService peerManagerService;

    private Promise<Void> sync() {
        return Promise.complete();
    }
}
