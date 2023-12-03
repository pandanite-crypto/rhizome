package rhizome.core.peer;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import rhizome.core.net.server.RequestHandler;

public class Protocol {

	private Map<Class<?>, RequestHandler<?, ?>> handlers = new LinkedHashMap<>();
    private List<Class<?>> messageTypes;
    
}
