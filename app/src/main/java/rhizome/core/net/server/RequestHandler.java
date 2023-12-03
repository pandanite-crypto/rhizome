package rhizome.core.net.server;

import io.activej.promise.Promisable;

@FunctionalInterface
public interface RequestHandler<I, O> {
	Promisable<O> run(I request);
}
