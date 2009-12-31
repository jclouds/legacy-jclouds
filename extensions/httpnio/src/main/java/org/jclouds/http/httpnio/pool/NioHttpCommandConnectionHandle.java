/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.http.httpnio.pool;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;

import org.apache.http.nio.NHttpConnection;
import org.jclouds.http.HttpCommandRendezvous;
import org.jclouds.http.pool.HttpCommandConnectionHandle;

/**
 * // TODO: Adrian: Document this!
 * 
 * @author Adrian Cole
 */
public class NioHttpCommandConnectionHandle extends HttpCommandConnectionHandle<NHttpConnection> {

   public NioHttpCommandConnectionHandle(Semaphore maxConnections,
            BlockingQueue<NHttpConnection> available, URI endPoint,
            HttpCommandRendezvous<?> command, NHttpConnection conn) throws InterruptedException {
      super(maxConnections, available, endPoint, command, conn);
   }

   public void startConnection() {
      conn.getContext().setAttribute("command", command);
      logger.trace("invoking %1$s on connection %2$s", command, conn);
      conn.requestOutput();
   }

   public void shutdownConnection() throws IOException {
      conn.shutdown();
   }

}
