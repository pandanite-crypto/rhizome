package rhizome.services.network;

import java.util.ArrayList;
import java.util.List;

import io.activej.rpc.server.RpcRequestHandler;

public class MessageProcessorService {

    List<RpcRequestHandler<?, ?>> handlers = new ArrayList<>();
    
}
