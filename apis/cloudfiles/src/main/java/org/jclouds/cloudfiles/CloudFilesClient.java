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
package org.jclouds.cloudfiles;

import java.net.URI;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.jclouds.cloudfiles.domain.ContainerCDNMetadata;
import org.jclouds.cloudfiles.options.ListCdnContainerOptions;
import org.jclouds.concurrent.Timeout;
import org.jclouds.openstack.swift.CommonSwiftClient;

/**
 * Provides access to Cloud Files via their REST API.
 * <p/>
 * All commands return a Future of the result from Cloud Files. Any exceptions incurred during
 * processing will be backend in an {@link ExecutionException} as documented in {@link Future#get()}.
 *
 * @author Adrian Cole
 * @see <a href="http://www.rackspacecloud.com/cf-devguide-20090812.pdf" />
 */
@Timeout(duration = 120, timeUnit = TimeUnit.SECONDS)
public interface CloudFilesClient extends CommonSwiftClient {
   Set<ContainerCDNMetadata> listCDNContainers(ListCdnContainerOptions... options);

   ContainerCDNMetadata getCDNMetadata(String container);

   URI enableCDN(String container, long ttl);

   URI enableCDN(String container);

   URI updateCDN(String container, long ttl);

   boolean disableCDN(String container);

   /**
    * Retrieve the key used to generate Temporary object access URLs
    *
    * @see <a href="http://docs.rackspace.com/files/api/v1/cf-devguide/content/Set_Account_Metadata-d1a4460.html" />
    * @return shared secret key
    */
   String getTemporaryUrlKey();

   /**
    * To create a Temporary URL you must first set a key as account metadata.
    *
    * Once the key is set, you should not change it while you still want others to be
    * able to access your temporary URL. If you change it, the TempURL becomes invalid
    * (within 60 seconds, which is the cache time for a key) and others will not be allowed
    * to access it.
    *
    * @see <a href="http://docs.rackspace.com/files/api/v1/cf-devguide/content/Set_Account_Metadata-d1a4460.html" />
    * @param temporaryUrlKey
    */
   void setTemporaryUrlKey(String temporaryUrlKey);
}
