/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.ssh;

import org.jclouds.compute.domain.ExecChannel;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.io.Payload;

import com.google.common.net.HostAndPort;

/**
 * @author Adrian Cole
 */
public interface SshClient {

   interface Factory {

      SshClient create(HostAndPort socket, LoginCredentials credentials);

   }

   String getUsername();

   String getHostAddress();

   void put(String path, Payload contents);

   Payload get(String path);

   /**
    * Execute a process and block until it is complete
    * 
    * @param command
    *           command line to invoke
    * @return output of the command
    */
   ExecResponse exec(String command);

   /**
    * Execute a process and allow the user to interact with it. Note that this will allow the
    * session to exist indefinitely, and its connection is not closed when {@link #disconnect()} is
    * called.
    * 
    * @param command
    *           command line to invoke
    * @return reference to the running process
    * @since 1.5.0
    */
   ExecChannel execChannel(String command);

   void connect();

   void disconnect();

   void put(String path, String contents);

}
