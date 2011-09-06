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
package org.jclouds.cloudfiles.config;

import java.net.URI;

import javax.inject.Singleton;

import org.jclouds.cloudfiles.CDNManagement;
import org.jclouds.cloudfiles.CloudFilesAsyncClient;
import org.jclouds.cloudfiles.CloudFilesClient;
import org.jclouds.http.RequiresHttp;
import org.jclouds.openstack.OpenStackAuthAsyncClient.AuthenticationResponse;
import org.jclouds.openstack.reference.AuthHeaders;
import org.jclouds.openstack.swift.CommonSwiftAsyncClient;
import org.jclouds.openstack.swift.CommonSwiftClient;
import org.jclouds.openstack.swift.config.BaseSwiftRestClientModule;
import org.jclouds.rest.ConfiguresRestClient;

import com.google.inject.Provides;

/**
 * 
 * @author Adrian Cole
 */
@ConfiguresRestClient
@RequiresHttp
public class CloudFilesRestClientModule extends BaseSwiftRestClientModule<CloudFilesClient, CloudFilesAsyncClient> {

   public CloudFilesRestClientModule() {
      super(CloudFilesClient.class, CloudFilesAsyncClient.class);
   }

   @Provides
   @Singleton
   CommonSwiftClient provideCommonSwiftClient(CloudFilesClient in) {
      return in;
   }

   @Provides
   @Singleton
   CommonSwiftAsyncClient provideCommonSwiftClient(CloudFilesAsyncClient in) {
      return in;
   }

   @Provides
   @Singleton
   @CDNManagement
   protected URI provideCDNUrl(AuthenticationResponse response) {
      return response.getServices().get(AuthHeaders.CDN_MANAGEMENT_URL);
   }
}
