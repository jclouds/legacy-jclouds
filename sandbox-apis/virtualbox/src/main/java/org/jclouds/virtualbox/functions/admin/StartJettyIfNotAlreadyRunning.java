/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.virtualbox.functions.admin;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.logging.Logger;
import org.jclouds.net.IPSocket;
import org.jclouds.predicates.InetSocketAddressConnect;
import org.jclouds.virtualbox.config.VirtualBoxConstants;

import com.google.common.base.Function;
import com.google.inject.Singleton;

/**
 * @author Andrea Turli
 */
public class StartJettyIfNotAlreadyRunning implements Function<String, Server> {

	@Resource
	@Named(ComputeServiceConstants.COMPUTE_LOGGER)
	protected Logger logger = Logger.NULL;

	private final int port;

	@Inject
	public StartJettyIfNotAlreadyRunning(
			@Named(VirtualBoxConstants.VIRTUALBOX_JETTY_PORT) final String port) {
		this.port = Integer.parseInt(port);
	}

	@Override
	public Server apply(@Nullable String baseResource) {
		final Server server = ServerJetty.getInstance().getServer();

		if (!server.getState().equals(Server.STARTED) && !new InetSocketAddressConnect().apply(new IPSocket("localhost", port))) {
			ResourceHandler resource_handler = new ResourceHandler();
			resource_handler.setDirectoriesListed(true);
			resource_handler.setWelcomeFiles(new String[] { "index.html" });

			resource_handler.setResourceBase(baseResource);
			logger.info("serving " + resource_handler.getBaseResource());

			HandlerList handlers = new HandlerList();
			handlers.setHandlers(new Handler[] { resource_handler, new DefaultHandler() });
			server.setHandler(handlers);

			try {
				server.start();
			} catch (Exception e) {
				logger.error(e, "Server jetty could not be started at this %s", baseResource);
			}
			return server;
		} else {
			logger.debug("Server jetty serving %s already running. Skipping start", baseResource);
			return server;
		}

	}
	
	@Singleton
	private static class ServerJetty {
		private static ServerJetty instance;
		private Server server;
		private String port = System.getProperty(VirtualBoxConstants.VIRTUALBOX_JETTY_PORT, "8080");

		private ServerJetty() {
			this.server = new Server(Integer.parseInt(port));
		}

		public static ServerJetty getInstance() {
			if (instance == null)
				instance = new ServerJetty();
			return instance;
		}

		public Server getServer() {
			return server;
		}
	}
}
