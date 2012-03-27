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
package org.jclouds.glesys;

import org.jclouds.glesys.features.ArchiveAsyncClient;
import org.jclouds.glesys.features.DomainAsyncClient;
import org.jclouds.glesys.features.EmailAsyncClient;
import org.jclouds.glesys.features.IpAsyncClient;
import org.jclouds.glesys.features.ServerAsyncClient;
import org.jclouds.rest.annotations.Delegate;

/**
 * Provides asynchronous access to GleSYS via their REST API.
 * <p/>
 * 
 * @see GleSYSClient
 * @see <a href="https://customer.glesys.com/api.php" />
 * @author Adrian Cole
 */
public interface GleSYSAsyncClient {

   /**
    * Provides asynchronous access to Server features.
    */
   @Delegate
   ServerAsyncClient getServerClient();

   /**
    * Provides asynchronous access to Ip Address features.
    */
   @Delegate
   IpAsyncClient getIpClient();

   /**
    * Provides asynchronous access to Archive features.
    */
   @Delegate
   ArchiveAsyncClient getArchiveClient();

   /**
    * Provides asynchronous access to DNS features.
    */
   @Delegate
   DomainAsyncClient getDomainClient();

   /**
    * Provides asynchronous access to E-Mail features.
    */
   @Delegate
   EmailAsyncClient getEmailClient();
   
}
