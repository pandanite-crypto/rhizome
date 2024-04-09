package rhizome.services;

import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import io.activej.async.function.AsyncRunnable;
import io.activej.async.function.AsyncRunnables;
import io.activej.async.service.EventloopService;
import io.activej.eventloop.Eventloop;
import io.activej.promise.Promise;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BaseService implements EventloopService {

    private Eventloop eventloop;
    private final List<AsyncRunnable> routines;

    protected BaseService(Eventloop eventloop, List<AsyncRunnable> routines) {
        this.eventloop = eventloop;
        this.routines = Collections.unmodifiableList(routines.stream().map(AsyncRunnables::reuse).toList());
    }

    public Promise<Void> refresh() {
        routines.forEach(AsyncRunnable::run);
        return Promise.complete();
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

    @Override
    public @NotNull Eventloop getEventloop() {
        return eventloop;
    }
}
