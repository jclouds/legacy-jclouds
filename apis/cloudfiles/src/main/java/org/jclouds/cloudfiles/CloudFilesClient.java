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

   /**
    * See http://docs.rackspace.com/files/api/v1/cf-devguide/content/List_CDN-Enabled_Containers-d1e2414.html
    */
   Set<ContainerCDNMetadata> listCDNContainers(ListCdnContainerOptions... options);

   /**
    * See http://docs.rackspace.com/files/api/v1/cf-devguide/content/List_CDN-Enabled_Container_Metadata-d1e2711.html
    */
   ContainerCDNMetadata getCDNMetadata(String container);

   /**
    * See http://docs.rackspace.com/files/api/v1/cf-devguide/content/CDN-Enabled_Container-d1e2665.html
    */
   URI enableCDN(String container, long ttl, boolean logRetention);

   /**
    * See http://docs.rackspace.com/files/api/v1/cf-devguide/content/CDN-Enabled_Container-d1e2665.html
    */
   URI enableCDN(String container, long ttl);
   
   /**
    * See http://docs.rackspace.com/files/api/v1/cf-devguide/content/CDN-Enabled_Container-d1e2665.html
    */
   URI enableCDN(String container);
   
   /**
    * See http://docs.rackspace.com/files/api/v1/cf-devguide/content/Update_CDN-Enabled_Container_Metadata-d1e2787.html
    */
   URI updateCDN(String container, long ttl, boolean logRetention);

   /**
    * See http://docs.rackspace.com/files/api/v1/cf-devguide/content/Update_CDN-Enabled_Container_Metadata-d1e2787.html
    */
   URI updateCDN(String container, boolean logRetention);

   /**
    * See http://docs.rackspace.com/files/api/v1/cf-devguide/content/Update_CDN-Enabled_Container_Metadata-d1e2787.html
    */
   URI updateCDN(String container, long ttl);

   /**
    * See http://docs.rackspace.com/files/api/v1/cf-devguide/content/CDN-Enabled_Container-d1e2665.html
    */
   boolean disableCDN(String container);
   
   /**
    * See http://docs.rackspace.com/files/api/v1/cf-devguide/content/Purge_CDN-Enabled_Objects-d1e3858.html
    */
   boolean purgeCDNObject(String container, String object, Iterable<String> emails);
   
   /**
    * See http://docs.rackspace.com/files/api/v1/cf-devguide/content/Purge_CDN-Enabled_Objects-d1e3858.html
    */
   boolean purgeCDNObject(String container, String object);

   /**
    * http://docs.rackspace.com/files/api/v1/cf-devguide/content/Create_Static_Website-dle4000.html
    */
   boolean setCDNStaticWebsiteIndex(String container, String index);

   /*
    * http://docs.rackspace.com/files/api/v1/cf-devguide/content/Set_Error_Pages_for_Static_Website-dle4005.html
    */
   boolean setCDNStaticWebsiteError(String container, String error);
}
