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

import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_USER;

import java.net.URI;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.inject.Named;

import org.jclouds.http.RequiresHttp;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.vcloud.VCloudAsyncClient;
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
   protected URI provideDefaultNetwork(VCloudAsyncClient client) throws InterruptedException,
            ExecutionException, TimeoutException {
      return Iterables.getOnlyElement(
               Iterables.filter(client.getDefaultVDC().get(180, TimeUnit.SECONDS)
                        .getAvailableNetworks().values(), new Predicate<NamedResource>() {

                  @Override
                  public boolean apply(NamedResource input) {
                     return input.getName().endsWith("Public");
                  }

               })).getLocation();
   }

   @Override
   protected URI provideCatalog(Organization org, @Named(PROPERTY_VCLOUD_USER) final String user) {
      return Iterables.getOnlyElement(
               Iterables.filter(org.getCatalogs().values(), new Predicate<NamedResource>() {

                  @Override
                  public boolean apply(NamedResource input) {
                     return input.getName().startsWith(user);
                  }

               })).getLocation();
   }
}
