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
package org.jclouds.opsource.servers;

import org.jclouds.opsource.servers.domain.Account;
import org.jclouds.opsource.servers.domain.ServerImage;
import org.jclouds.opsource.servers.features.AccountAsyncClient;
import org.jclouds.opsource.servers.features.ServerAsyncClient;
import org.jclouds.opsource.servers.features.ServerImageAsyncClient;
import org.jclouds.rest.annotations.Delegate;

/**
 * Provides asynchronous access to OpSourceServers via their REST API.
 * 
 * @see OpSourceServersClient
 * @author Adrian Cole
 */
public interface OpSourceServersAsyncClient {

   /**
    * @return asynchronous access to {@link Account} features
    */
   @Delegate
   AccountAsyncClient getAccountClient();
   
   /**
    * @return asynchronous access to {@link ServerImage} features
    */
   @Delegate
   ServerImageAsyncClient getServerImageClient();
   
   /**
    * @return asynchronous access to server features
    */
   @Delegate
   ServerAsyncClient getServerClient();

}
