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

import org.jclouds.opsource.servers.features.AccountApi;
import org.jclouds.opsource.servers.features.ServerApi;
import org.jclouds.opsource.servers.features.ServerImageApi;
import org.jclouds.rest.annotations.Delegate;

/**
 * Provides synchronous access to OpSourceServers.
 * 
 * @see OpSourceServersAsyncApi
 * @author Adrian Cole
 */
public interface OpSourceServersApi {

   /**
    * @return synchronous access to {@link Account} features
    */
   @Delegate
   AccountApi getAccountApi();

   /**
    * @return synchronous access to {@link ServerImage} features
    */
   @Delegate
   ServerImageApi getServerImageApi();
   
   /**
    * @return synchronous access to server features
    */
   @Delegate
   ServerApi getServerApi();
   
}
