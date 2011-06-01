/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.vcloud.config;

import java.util.Map;

import org.jclouds.http.RequiresHttp;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.vcloud.VCloudAsyncClient;
import org.jclouds.vcloud.VCloudClient;
import org.jclouds.vcloud.features.CatalogAsyncClient;
import org.jclouds.vcloud.features.CatalogClient;
import org.jclouds.vcloud.features.NetworkAsyncClient;
import org.jclouds.vcloud.features.NetworkClient;
import org.jclouds.vcloud.features.OrgAsyncClient;
import org.jclouds.vcloud.features.OrgClient;
import org.jclouds.vcloud.features.TaskAsyncClient;
import org.jclouds.vcloud.features.TaskClient;
import org.jclouds.vcloud.features.VAppAsyncClient;
import org.jclouds.vcloud.features.VAppClient;
import org.jclouds.vcloud.features.VAppTemplateAsyncClient;
import org.jclouds.vcloud.features.VAppTemplateClient;
import org.jclouds.vcloud.features.VDCAsyncClient;
import org.jclouds.vcloud.features.VDCClient;
import org.jclouds.vcloud.features.VmAsyncClient;
import org.jclouds.vcloud.features.VmClient;

import com.google.common.collect.ImmutableMap;

/**
 * Configures the VCloud authentication service connection, including logging and http transport.
 * 
 * @author Adrian Cole
 */
@RequiresHttp
@ConfiguresRestClient
public class VCloudRestClientModule extends BaseVCloudRestClientModule<VCloudClient, VCloudAsyncClient> {

   public static final Map<Class<?>, Class<?>> DELEGATE_MAP = ImmutableMap.<Class<?>, Class<?>> builder()//
            .put(VAppTemplateClient.class, VAppTemplateAsyncClient.class)//
            .put(VAppClient.class, VAppAsyncClient.class)//
            .put(VmClient.class, VmAsyncClient.class)//
            .put(CatalogClient.class, CatalogAsyncClient.class)//
            .put(TaskClient.class, TaskAsyncClient.class)//
            .put(VDCClient.class, VDCAsyncClient.class)//
            .put(NetworkClient.class, NetworkAsyncClient.class)//
            .put(OrgClient.class, OrgAsyncClient.class)//
            .build();

   public VCloudRestClientModule() {
      super(VCloudClient.class, VCloudAsyncClient.class, DELEGATE_MAP);
   }

}
