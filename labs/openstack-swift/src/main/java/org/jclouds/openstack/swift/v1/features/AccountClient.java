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
package org.jclouds.openstack.swift.v1.features;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;
import org.jclouds.openstack.swift.v1.domain.AccountMetadata;
import org.jclouds.openstack.swift.v1.domain.ContainerMetadata;
import org.jclouds.openstack.swift.v1.options.ListContainersOptions;

/**
 * Storage Account Services
 * 
 * @see AccountAsyncClient
 * @author Adrian Cole
 * @see <a
 *      href="http://docs.openstack.org/api/openstack-object-storage/1.0/content/storage-account-services.html"
 *      >api doc</a>
 */
@Timeout(duration = 180, timeUnit = TimeUnit.SECONDS)
public interface AccountClient {
   /**
    * Retrieve Account Metadata
    * 
    * @return account metadata including container count and bytes used
    */
   AccountMetadata getAccountMetadata();

   /**
    * @see #listContainers(ListContainersOptions)
    */
   Set<ContainerMetadata> listContainers();

   /**
    * retrieve a list of existing storage containers ordered by name. The sort order for the name is
    * based on a binary comparison, a single built-in collating sequence that compares string data
    * using SQLite's memcmp() function, regardless of text encoding.
    * 
    * @param options
    * @return a list of existing storage containers ordered by name.
    */
   Set<ContainerMetadata> listContainers(ListContainersOptions options);

}
