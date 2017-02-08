package name.hessenberger;

import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.bus.CXFBusFactory;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;

import javax.xml.ws.Endpoint;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Uli Hessenberger on 07.02.17.
 */
public class JettyContainer {

    public void start() {
        try {
            Server webserver = new Server();

            Connector[] connectors = configureConnectors(9080, webserver);
            webserver.setConnectors(connectors);

            // static resource
            ResourceHandler resourceHandler = new ResourceHandler();
            resourceHandler.setWelcomeFiles(new String[] {"index.html"});
            resourceHandler.setResourceBase("src/main/resources");
            ContextHandler resourceContext = new ContextHandler();
            //resourceContext.setContextPath("/");
            resourceContext.setHandler(resourceHandler);

            // web service
            ServletContextHandler serviceContext = new ServletContextHandler(ServletContextHandler.SESSIONS);
            //serviceContext.setContextPath("/");
            serviceContext.setSessionHandler(new SessionHandler());
            configureCxf(serviceContext);

            // context collection
            ContextHandlerCollection contexts = new ContextHandlerCollection();
            contexts.setHandlers(new Handler[] { resourceContext, serviceContext });
            webserver.setHandler(contexts);

            webserver.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Connector[] configureConnectors(int port, Server server) {
        List<Connector> connectors = new ArrayList<>();
        ServerConnector httpConnector = new ServerConnector(server);
        httpConnector.setPort(port);
        connectors.add(httpConnector);
        return connectors.toArray(new Connector[connectors.size()]);
    }

    private void configureCxf(ServletContextHandler applicationContext) {
        System.setProperty(BusFactory.BUS_FACTORY_PROPERTY_NAME, CXFBusFactory.class.getName());
        Bus bus = BusFactory.getDefaultBus(true);
        CXFServlet cxfServlet = new CXFServlet();
        cxfServlet.setBus(bus);
        ServletHolder cxfServletHolder = new ServletHolder(cxfServlet);
        cxfServletHolder.setName("/service");
        cxfServletHolder.setForcedPath("/service");

        applicationContext.addServlet(cxfServletHolder, "/service/*");
        BusFactory.setDefaultBus(bus);

        Hello helloService = new HelloImpl();
        Endpoint.publish("/", helloService);
    }

}
