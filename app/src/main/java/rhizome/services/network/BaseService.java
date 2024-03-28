package rhizome.services.network;

import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import io.activej.async.function.AsyncRunnable;
import io.activej.async.function.AsyncRunnables;
import io.activej.async.service.EventloopService;
import io.activej.eventloop.Eventloop;
import io.activej.promise.Promise;
import lombok.extern.slf4j.Slf4j;
import rhizome.net.p2p.PeerSystem;

@Slf4j
public abstract class BaseService implements EventloopService {

    private Eventloop eventloop;
    private final List<AsyncRunnable> routines;

    protected BaseService(Eventloop eventloop, PeerSystem peerSystem) {
        this.eventloop = eventloop;
        this.routines = Collections.singletonList(AsyncRunnables.reuse(() -> doRefresh(peerSystem)));
    }

    protected Promise<Void> refresh() {
        return routines.get(0).run();
    }

    @Override
    public @NotNull Promise<?> start() {
        log.info("|SERVICE STARTING|");
        return refresh();
    }

    @Override
    public @NotNull Promise<?> stop() {
        log.info("|SERVICE STOPPING|");
        return Promise.complete().whenResult(() -> log.info("|SERVICE STOPPED|"));
    }

    private Promise<Void> doRefresh(PeerSystem peerSystem) {
        return Promise.complete();
    }

    @Override
    public @NotNull Eventloop getEventloop() {
        return eventloop;
    }
}
