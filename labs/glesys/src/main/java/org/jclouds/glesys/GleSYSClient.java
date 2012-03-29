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

import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;
import org.jclouds.glesys.features.ArchiveClient;
import org.jclouds.glesys.features.DomainClient;
import org.jclouds.glesys.features.EmailClient;
import org.jclouds.glesys.features.IpClient;
import org.jclouds.glesys.features.ServerClient;
import org.jclouds.rest.annotations.Delegate;

/**
 * Provides synchronous access to GleSYS.
 * <p/>
 * 
 * @see GleSYSAsyncClient
 * @see <a href="https://customer.glesys.com/api.php" />
 * @author Adrian Cole
 */
@Timeout(duration = 60, timeUnit = TimeUnit.SECONDS)
public interface GleSYSClient {

   /**
    * Provides synchronous access to Server features.
    */
   @Delegate
   ServerClient getServerClient();

   /**
    * Provides synchronous access to Ip Address features.
    */
   @Delegate
   IpClient getIpClient();

   /**
    * Provides synchronous access to Archive features.
    */
   @Delegate
   ArchiveClient getArchiveClient();

   /**
    * Provides synchronous access to DNS features.
    */
   @Delegate
   DomainClient getDomainClient();

   /**
    * Provides synchronous access to E-Mail features.
    */
   @Delegate
   EmailClient getEmailClient();

}
