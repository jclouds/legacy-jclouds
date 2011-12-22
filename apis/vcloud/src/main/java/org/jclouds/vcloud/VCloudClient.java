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
package org.jclouds.vcloud;

import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.vcloud.features.CatalogClient;
import org.jclouds.vcloud.features.NetworkClient;
import org.jclouds.vcloud.features.OrgClient;
import org.jclouds.vcloud.features.TaskClient;
import org.jclouds.vcloud.features.VAppClient;
import org.jclouds.vcloud.features.VAppTemplateClient;
import org.jclouds.vcloud.features.VDCClient;
import org.jclouds.vcloud.features.VmClient;

/**
 * Provides access to VCloud resources via their REST API.
 * <p/>
 * 
 * @see <a href="http://communities.vmware.com/community/developer/forums/vcloudapi" />
 * @author Adrian Cole
 */
@Timeout(duration = 300, timeUnit = TimeUnit.SECONDS)
public interface VCloudClient {
   /**
    * Provides asynchronous access to VApp Template features.
    * 
    */
   @Delegate
   VAppTemplateClient getVAppTemplateClient();

   /**
    * Provides synchronous access to VApp features.
    */
   @Delegate
   VAppClient getVAppClient();

   /**
    * Provides synchronous access to Vm features.
    */
   @Delegate
   VmClient getVmClient();

   /**
    * Provides synchronous access to Catalog features.
    */
   @Delegate
   CatalogClient getCatalogClient();

   /**
    * Provides synchronous access to Task features.
    */
   @Delegate
   TaskClient getTaskClient();

   /**
    * Provides synchronous access to VDC features.
    */
   @Delegate
   VDCClient getVDCClient();

   /**
    * Provides synchronous access to Network features.
    */
   @Delegate
   NetworkClient getNetworkClient();

   /**
    * Provides synchronous access to Org features.
    */
   @Delegate
   OrgClient getOrgClient();

}
