package rhizome.services.blockchain;

import org.jetbrains.annotations.NotNull;
import lombok.Getter;
import lombok.Setter;

import io.activej.async.service.EventloopService;
import io.activej.eventloop.Eventloop;
import io.activej.promise.Promise;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
public class BlockchainService extends AbstractBlockchain implements EventloopService {

    private Eventloop eventloop;

    public BlockchainService(Eventloop eventloop) {
        this.eventloop = eventloop;
    }

    @Override
    public @NotNull Promise<?> start() {
        throw new UnsupportedOperationException("Unimplemented method 'start'");
    }

    @Override
    public @NotNull Promise<?> stop() {
        throw new UnsupportedOperationException("Unimplemented method 'stop'");
    }

    
}
