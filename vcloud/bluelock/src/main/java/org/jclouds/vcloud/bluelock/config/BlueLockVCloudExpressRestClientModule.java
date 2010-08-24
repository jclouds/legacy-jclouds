/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.vcloud.bluelock.config;

import static com.google.common.base.Preconditions.checkState;
import static org.jclouds.Constants.PROPERTY_IDENTITY;

import java.net.URI;
import java.util.Map;

import javax.inject.Named;

import org.jclouds.http.RequiresHttp;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.vcloud.CommonVCloudClient;
import org.jclouds.vcloud.config.VCloudExpressRestClientModule;
import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.domain.Org;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

/**
 * Configures the VCloud authentication service connection, including logging and http transport.
 * 
 * @author Adrian Cole
 */
@RequiresHttp
@ConfiguresRestClient
public class BlueLockVCloudExpressRestClientModule extends VCloudExpressRestClientModule {

   @Override
   protected URI provideDefaultNetwork(CommonVCloudClient client) {
      org.jclouds.vcloud.domain.VDC vDC = client.findVDCInOrgNamed(null, null);
      Map<String, ReferenceType> networks = vDC.getAvailableNetworks();
      checkState(networks.size() > 0, "No networks present in vDC: " + vDC.getName());
      return Iterables.getOnlyElement(Iterables.filter(networks.values(), new Predicate<ReferenceType>() {

         @Override
         public boolean apply(ReferenceType input) {
            return input.getName().equals("Internal In and Out");
         }

      })).getHref();
   }

   @Override
   protected URI provideCatalog(Org org, @Named(PROPERTY_IDENTITY) final String user) {
      checkState(org.getCatalogs().size() > 0, "No catalogs present in org: " + org.getName());
      return Iterables.getOnlyElement(Iterables.filter(org.getCatalogs().values(), new Predicate<ReferenceType>() {

         @Override
         public boolean apply(ReferenceType input) {
            return input.getName().startsWith(user);
         }

      })).getHref();
   }
}
