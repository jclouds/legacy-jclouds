/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.ssh.jsch.predicates;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.logging.Logger;
import org.jclouds.net.IPSocket;
import org.jclouds.predicates.SocketOpen;

import com.google.inject.Inject;

/**
 * 
 * Tests to see if a socket is open.
 * 
 * @author Adrian Cole
 */
@Singleton
public class InetSocketAddressConnect implements SocketOpen {

   @Resource
   protected Logger logger = Logger.NULL;

   @Inject(optional = true)
   @Named("org.jclouds.socket_timeout")
   private int timeout = 2000;

   @Override
   public boolean apply(IPSocket socketA) {
      InetSocketAddress socketAddress = new InetSocketAddress(socketA.getAddress(), socketA
               .getPort());
      Socket socket = null;
      try {
         logger.trace("testing socket %s", socketAddress);
         socket = new Socket();
         socket.setReuseAddress(false);
         socket.setSoLinger(false, 1);
         socket.setSoTimeout(timeout);
         socket.connect(socketAddress, timeout);
      } catch (IOException e) {
         return false;
      } finally {
         if (socket != null) {
            try {
               socket.close();
            } catch (IOException ioe) {
               // no work to do
            }
         }
      }
      return true;
   }

}
