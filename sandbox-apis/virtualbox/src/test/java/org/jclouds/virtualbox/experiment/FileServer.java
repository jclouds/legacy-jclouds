/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.virtualbox.experiment;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.log.Log;

public class FileServer {
   public static void main(String[] args) throws Exception {
      Server server = new Server(args.length == 0 ? 8080 : Integer.parseInt(args[0]));

      ResourceHandler resource_handler = new ResourceHandler();
      resource_handler.setDirectoriesListed(true);
      resource_handler.setWelcomeFiles(new String[] { "index.html" });

      resource_handler.setResourceBase(args.length == 2 ? args[1] : ".");
      Log.info("serving " + resource_handler.getBaseResource());

      HandlerList handlers = new HandlerList();
      handlers.setHandlers(new Handler[] { resource_handler, new DefaultHandler() });
      server.setHandler(handlers);

      server.start();
      server.join();
   }

}