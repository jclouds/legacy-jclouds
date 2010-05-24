/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_USER;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import javax.inject.Named;

import org.jclouds.http.RequiresHttp;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.vcloud.VCloudClient;
import org.jclouds.vcloud.config.VCloudRestClientModule;
import org.jclouds.vcloud.domain.NamedResource;
import org.jclouds.vcloud.domain.Organization;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

/**
 * Configures the VCloud authentication service connection, including logging and http transport.
 * 
 * @author Adrian Cole
 */
@RequiresHttp
@ConfiguresRestClient
public class BlueLockVCloudRestClientModule extends VCloudRestClientModule {

   @Override
   protected URI provideDefaultNetwork(VCloudClient client) throws InterruptedException,
            ExecutionException, TimeoutException {
      org.jclouds.vcloud.domain.VDC vDC = client.getDefaultVDC();
      Map<String, NamedResource> networks = vDC.getAvailableNetworks();
      checkState(networks.size() > 0, "No networks present in vDC: " + vDC.getName());
      return Iterables.getOnlyElement(
               Iterables.filter(networks.values(), new Predicate<NamedResource>() {

                  @Override
                  public boolean apply(NamedResource input) {
                     return input.getName().equals("Internal In and Out");
                  }

               })).getLocation();
   }

   @Override
   protected URI provideCatalog(Organization org, @Named(PROPERTY_VCLOUD_USER) final String user) {
      checkState(org.getCatalogs().size() > 0, "No catalogs present in org: " + org.getName());
      return Iterables.getOnlyElement(
               Iterables.filter(org.getCatalogs().values(), new Predicate<NamedResource>() {

                  @Override
                  public boolean apply(NamedResource input) {
                     return input.getName().startsWith(user);
                  }

               })).getLocation();
   }
}
