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

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.virtualbox.Preconfiguration;
import org.jclouds.virtualbox.config.VirtualBoxConstants;

import com.google.common.base.Supplier;
import com.google.inject.Singleton;

/**
 * @author Andrea Turli
 */
@Preconfiguration
@Singleton
public class StartJettyIfNotAlreadyRunning implements Supplier<URI> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final URI preconfigurationUrl;
   private final Server jetty;

   @Inject
   public StartJettyIfNotAlreadyRunning(
            @Named(VirtualBoxConstants.VIRTUALBOX_PRECONFIGURATION_URL) String preconfigurationUrl) {
      this(new Server(URI.create(preconfigurationUrl).getPort()), preconfigurationUrl);
   }

   public StartJettyIfNotAlreadyRunning(Server jetty,
            @Named(VirtualBoxConstants.VIRTUALBOX_PRECONFIGURATION_URL) String preconfigurationUrl) {
      this.preconfigurationUrl = URI.create(checkNotNull(preconfigurationUrl, "preconfigurationUrl"));
      this.jetty = jetty;
   }

   @PostConstruct
   public void start() {

      if (jetty.getState().equals(Server.STARTED)) {
         logger.debug("not starting jetty, as existing host is serving %s", preconfigurationUrl);
      } else {
         logger.debug(">> starting jetty to serve %s", preconfigurationUrl);
         ResourceHandler resource_handler = new ResourceHandler();
         resource_handler.setDirectoriesListed(true);
         resource_handler.setWelcomeFiles(new String[] { "index.html" });

         resource_handler.setResourceBase("");
         HandlerList handlers = new HandlerList();
         handlers.setHandlers(new Handler[] { resource_handler, new DefaultHandler() });
         jetty.setHandler(handlers);

         try {
            jetty.start();
         } catch (Exception e) {
            logger.error(e, "Server jetty could not be started for %s", preconfigurationUrl);
         }
         logger.debug("<< serving %s", resource_handler.getBaseResource());
      }

   }

   @PreDestroy()
   public void stop() {
      try {
         jetty.stop();
      } catch (Exception e) {
      }
   }

   @Override
   public URI get() {
      return preconfigurationUrl;
   }
}
