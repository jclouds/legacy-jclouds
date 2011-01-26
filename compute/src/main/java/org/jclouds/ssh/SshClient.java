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

package org.jclouds.ssh;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.domain.Credentials;
import org.jclouds.io.Payload;
import org.jclouds.net.IPSocket;

/**
 * @author Adrian Cole
 */
public interface SshClient {

   interface Factory {
      /**
       * please use {@link Factory#create(IPSocket, Credentials)}
       * 
       * @return
       */
      @Deprecated
      SshClient create(IPSocket socket, String username, String password);

      /**
       * please use {@link Factory#create(IPSocket, Credentials)}
       * 
       * @return
       */
      @Deprecated
      SshClient create(IPSocket socket, String username, byte[] privateKey);

      SshClient create(IPSocket socket, Credentials credentials);

   }

   String getUsername();

   String getHostAddress();

   void put(String path, Payload contents);

   Payload get(String path);

   ExecResponse exec(String command);

   @PostConstruct
   void connect();

   @PreDestroy
   void disconnect();

   void put(String path, String contents);

}