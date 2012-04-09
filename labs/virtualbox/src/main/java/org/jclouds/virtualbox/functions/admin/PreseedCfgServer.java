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

import java.io.IOException;
import java.net.URI;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

import com.google.common.base.Throwables;

/**
 * Sets up jetty so that it can serve the preseed.cfg file to automate master creation.
 * 
 * @author Andrea Turli, David Alves
 */
public class PreseedCfgServer {

   private Server jetty;

   public void start(String preconfigurationUrl, final String preseedCfg) {
      this.jetty = new Server(URI.create(preconfigurationUrl).getPort());
      try {
         // since we're only serving the preseed.cfg file respond to all requests with it
         jetty.setHandler(new AbstractHandler() {
            @Override
            public void handle(String target, Request baseRequest, HttpServletRequest request,
                     HttpServletResponse response) throws IOException, ServletException {
               response.setContentType("text/plain;charset=utf-8");
               response.setStatus(HttpServletResponse.SC_OK);
               baseRequest.setHandled(true);
               response.getWriter().println(preseedCfg);
            }
         });
         jetty.start();
      } catch (Exception e) {
         throw Throwables.propagate(e);
      }
   }

   public void stop() {
      try {
         if (jetty != null) {
            jetty.stop();
         }
      } catch (Exception e) {
         throw Throwables.propagate(e);
      }
   }

}
