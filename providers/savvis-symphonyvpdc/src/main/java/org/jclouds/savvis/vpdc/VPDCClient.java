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
package org.jclouds.savvis.vpdc;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jclouds.compute.domain.CIMOperatingSystem;
import org.jclouds.concurrent.Timeout;
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.savvis.vpdc.domain.Resource;
import org.jclouds.savvis.vpdc.features.BrowsingClient;
import org.jclouds.savvis.vpdc.features.FirewallClient;
import org.jclouds.savvis.vpdc.features.ServiceManagementClient;
import org.jclouds.savvis.vpdc.features.VMClient;

/**
 * Provides synchronous access to VPDC.
 * <p/>
 * 
 * @see VPDCAsyncClient
 * @see <a href="https://api.sandbox.symphonyvpdc.savvis.net/doc/spec/api/index.html" />
 * @author Adrian Cole
 */
@Timeout(duration = 60, timeUnit = TimeUnit.SECONDS)
public interface VPDCClient {

   /**
    * Provides synchronous access to Browsing features.
    */
   @Delegate
   BrowsingClient getBrowsingClient();

   /**
    * Provides synchronous access to VM Operation features.
    */
   @Delegate
   VMClient getVMClient();

   /**
    * Provides synchronous access to Firewall Operation features.
    */
   @Delegate
   FirewallClient getFirewallClient();

   /**
    * Provides synchronous access to ServiceManagement Operation features.
    */
   @Delegate
   ServiceManagementClient getServiceManagementClient();

   /**
    * 
    * @return a listing of all orgs that the current user has access to.
    */
   Set<Resource> listOrgs();

   /**
    * predefined by default in the classpath resource {@code
    * /savvis-symphonyvpdc/predefined_operatingsystems.json}
    * 
    * @return the operating systems that are predefined in the provider
    * @see <a href="https://api.sandbox.symphonyvpdc.savvis.net/doc/spec/api/addSingleVM.html" />
    */
   Set<CIMOperatingSystem> listPredefinedOperatingSystems();
}
