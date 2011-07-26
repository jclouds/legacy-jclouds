package org.jclouds.virtualbox.experiment;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.log.Log;

public class FileServer {
	public static void main(String[] args) throws Exception {
		Server server = new Server(args.length == 0?8080:Integer.parseInt(args[0]));

		ResourceHandler resource_handler = new ResourceHandler();
		resource_handler.setDirectoriesListed(true);
		resource_handler.setWelcomeFiles(new String[]{ "index.html" });

		resource_handler.setResourceBase(args.length == 2?args[1]:".");
		Log.info("serving " + resource_handler.getBaseResource());

		HandlerList handlers = new HandlerList();
		handlers.setHandlers(new Handler[] { resource_handler, new DefaultHandler() });
		server.setHandler(handlers);

		server.start();
		server.join();
	}

}