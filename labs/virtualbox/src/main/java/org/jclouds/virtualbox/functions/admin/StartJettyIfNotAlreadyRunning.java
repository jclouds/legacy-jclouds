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

import static com.google.common.base.Throwables.propagate;
import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_PRECONFIGURATION_URL;

import java.net.URI;

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
import org.jclouds.virtualbox.domain.IsoSpec;

import com.google.common.cache.CacheLoader;
import com.google.inject.Singleton;

/**
 * @author Andrea Turli
 */
@Singleton
public class StartJettyIfNotAlreadyRunning extends CacheLoader<IsoSpec, URI> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;
   private Server jetty;
   private final String preconfigurationUrl;

   @Inject
   public StartJettyIfNotAlreadyRunning(@Named(VIRTUALBOX_PRECONFIGURATION_URL) String preconfigurationUrl, Server jetty) {
      this.preconfigurationUrl = preconfigurationUrl;
      this.jetty = jetty;
   }

   @Override
      public URI load(IsoSpec isoSpec) throws Exception {
      try {
         start();
      } catch (Exception e) {
         logger.error("Could not connect to host providing ISO " + isoSpec, e);
         propagate(e);
      }
      return URI.create(preconfigurationUrl);
   }

   private void start() {
      if (jetty.getState().equals(Server.STARTED)) {
         logger.debug("not starting jetty, as existing host is serving %s", preconfigurationUrl);
      } else {
         logger.debug(">> starting jetty to serve %s", preconfigurationUrl);
         ResourceHandler resourceHandler = new ResourceHandler();
         resourceHandler.setDirectoriesListed(true);
         resourceHandler.setWelcomeFiles(new String[]{"index.html"});

         resourceHandler.setResourceBase("");
         HandlerList handlers = new HandlerList();
         handlers.setHandlers(new Handler[]{resourceHandler, new DefaultHandler()});
         jetty.setHandler(handlers);

         try {
            jetty.start();
         } catch (Exception e) {
            logger.error(e, "Server jetty could not be started for %s", preconfigurationUrl);
         }
         logger.debug("<< serving %s", resourceHandler.getBaseResource());
      }

   }

   @PreDestroy()
   public void stop() {
      try {
         jetty.stop();
      } catch (Exception e) {
         logger.error("Could not stop jetty.", e);
      }
   }

}
