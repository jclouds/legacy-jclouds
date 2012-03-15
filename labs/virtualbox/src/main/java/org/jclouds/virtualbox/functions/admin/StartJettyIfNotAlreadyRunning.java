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

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Throwables.propagate;
import static org.jclouds.virtualbox.config.VirtualBoxConstants.VIRTUALBOX_PRECONFIGURATION_URL;

import java.io.IOException;
import java.net.URI;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.Resource;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.virtualbox.domain.IsoSpec;

import com.google.common.base.Throwables;
import com.google.common.cache.CacheLoader;
import com.google.inject.Singleton;

/**
 * Sets up jetty so that it can serve the preseed.cfg file to automate master creation.
 * 
 * TODO - Probably we can make this only start jetty. This has not been used to serve isos.
 * 
 * @author Andrea Turli, David Alves
 */
@Singleton
public class StartJettyIfNotAlreadyRunning extends CacheLoader<IsoSpec, URI> {

   @javax.annotation.Resource
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
         try {

            // find the the parent dir inside the jar to serve the file from

            final String preseedFile = IOUtils
                     .toString(Resource.newSystemResource("preseed.cfg").getURL().openStream());

            checkState(preseedFile != null);

            // since we're only serving the preseed.cfg file respond to all requests with it
            jetty.setHandler(new AbstractHandler() {

               @Override
               public void handle(String target, Request baseRequest, HttpServletRequest request,
                        HttpServletResponse response) throws IOException, ServletException {
                  response.setContentType("text/plain;charset=utf-8");
                  response.setStatus(HttpServletResponse.SC_OK);
                  baseRequest.setHandled(true);
                  response.getWriter().println(preseedFile);
               }

            });

            jetty.start();

         } catch (Exception e) {
            logger.error(e, "Server jetty could not be started for %s", preconfigurationUrl);
            throw Throwables.propagate(e);
         }
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
