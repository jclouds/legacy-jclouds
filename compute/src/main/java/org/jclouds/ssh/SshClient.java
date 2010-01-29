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
package org.jclouds.ssh;

import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * @author Adrian Cole
 */
public interface SshClient {

   interface Factory {
      SshClient create(InetSocketAddress socket, String username, String password);

      SshClient create(InetSocketAddress socket, String username, byte[] privateKey);

      Map<String, String> generateRSAKeyPair(String comment, String passphrase);
   }

   String getUsername();

   String getHostAddress();

   void put(String path, InputStream contents);

   InputStream get(String path);

   ExecResponse exec(String command);

   @PostConstruct
   void connect();

   @PreDestroy
   void disconnect();

}