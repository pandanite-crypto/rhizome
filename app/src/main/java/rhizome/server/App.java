package rhizome.server;

import java.util.concurrent.Executor;

import io.activej.eventloop.Eventloop;
import io.activej.http.AsyncHttpServer;
import io.activej.http.AsyncServlet;
import io.activej.http.HttpResponse;
import io.activej.http.RoutingServlet;
import io.activej.http.StaticServlet;
import io.activej.inject.annotation.Eager;
import io.activej.inject.annotation.Provides;
import io.activej.inject.module.Module;
import io.activej.launcher.Launcher;
import io.activej.service.ServiceGraphModule;

import static io.activej.http.HttpMethod.*;
import static java.util.concurrent.Executors.newCachedThreadPool;

public final class App extends Launcher {
    private static final int PORT = 8080;
    private static final String RESOURCE_DIR = "static/query";

    @Provides
    Eventloop eventloop() {
        return Eventloop.create();
    }

    @Provides
	Executor executor() {
		return newCachedThreadPool();
	}


    @Provides
    AsyncServlet servlet(Executor executor) {
        return RoutingServlet.create()
            .map(POST, "/hello", request -> request.loadBody()
                .map($ -> {
                    String name = request.getPostParameters().get("name");
                    return HttpResponse.ok200()
                        .withHtml("<h1><center>Hello from POST, " + name + "!</center></h1>");
                }))
            .map(GET, "/hello", request -> {
                String name = request.getQueryParameter("name");
                return HttpResponse.ok200()
                    .withHtml("<h1><center>Hello from GET, " + name + "!</center></h1>");
            })
            .map("/*", StaticServlet.ofClassPath(executor, RESOURCE_DIR)
                .withIndexHtml());
    }

    @Provides
    @Eager
    AsyncHttpServer server(Eventloop eventloop, AsyncServlet servlet) {
        return AsyncHttpServer.create(eventloop, servlet).withListenPort(PORT);
    }

    @Override
    protected Module getModule() {
        return ServiceGraphModule.create();
    }

    @Override
    protected void run() throws Exception {
        logger.info("HTTP Server is now available at http://localhost:" + PORT);
        awaitShutdown();
    }

    public static void main(String[] args) throws Exception {
        Launcher launcher = new App();
        launcher.launch(args);
    }
}