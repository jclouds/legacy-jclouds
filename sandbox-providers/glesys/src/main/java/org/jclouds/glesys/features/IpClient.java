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
package org.jclouds.glesys.features;

import org.jclouds.concurrent.Timeout;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Provides synchronous access to IP Addresses.
 * <p/>
 *
 * @author Adrian Cole
 * @see IpAsyncClient
 * @see <a href="https://customer.glesys.com/api.php" />
 */
@Timeout(duration = 30, timeUnit = TimeUnit.SECONDS)
public interface IpClient {


   /**
    * Get a set of all IP addresses that are available and not used on any account or server.
    *
    * @param ipversion  "4" or "6", for IPV4 or IPV6, respectively
    * @param datacenter the datacenter
    * @param platform   the platform
    * @return a set of free IP addresses
    */
   Set<String> listFree(String ipversion, String datacenter, String platform);

}