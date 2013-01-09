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
package org.jclouds.savvis.vpdc;

import java.util.Set;

import org.jclouds.compute.domain.CIMOperatingSystem;
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.savvis.vpdc.domain.Resource;
import org.jclouds.savvis.vpdc.features.BrowsingApi;
import org.jclouds.savvis.vpdc.features.FirewallApi;
import org.jclouds.savvis.vpdc.features.ServiceManagementApi;
import org.jclouds.savvis.vpdc.features.VMApi;
import org.jclouds.savvis.vpdc.internal.Org;

import com.google.inject.Provides;

/**
 * Provides synchronous access to VPDC.
 * <p/>
 * 
 * @see VPDCAsyncApi
 * @see <a href="https://api.sandbox.savvis.net/doc/spec/api/index.html" />
 * @author Adrian Cole
 */
public interface VPDCApi {

   /**
    * Provides synchronous access to Browsing features.
    */
   @Delegate
   BrowsingApi getBrowsingApi();

   /**
    * Provides synchronous access to VM Operation features.
    */
   @Delegate
   VMApi getVMApi();

   /**
    * Provides synchronous access to Firewall Operation features.
    */
   @Delegate
   FirewallApi getFirewallApi();

   /**
    * Provides synchronous access to ServiceManagement Operation features.
    */
   @Delegate
   ServiceManagementApi getServiceManagementApi();

   /**
    * 
    * @return a listing of all orgs that the current user has access to.
    */
   @Provides
   @Org
   Set<Resource> listOrgs();

   /**
    * predefined by default in the classpath resource {@code
    * /savvis-symphonyvpdc/predefined_operatingsystems.json}
    * 
    * @return the operating systems that are predefined in the provider
    * @see <a href="https://api.sandbox.savvis.net/doc/spec/api/addSingleVM.html" />
    */
   @Provides
   Set<CIMOperatingSystem> listPredefinedOperatingSystems();
}
