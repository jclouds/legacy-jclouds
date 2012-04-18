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
package org.jclouds.savvis.vpdc.features;

import static org.jclouds.Constants.PROPERTY_SESSION_INTERVAL;
import static org.testng.Assert.assertEquals;

import java.net.URI;
import java.util.Properties;
import java.util.Set;

import javax.inject.Named;

import org.jclouds.http.HttpRequest;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.internal.BaseAsyncClientTest;
import org.jclouds.savvis.vpdc.VPDCProviderMetadata;
import org.jclouds.savvis.vpdc.config.VPDCRestClientModule;
import org.jclouds.savvis.vpdc.domain.Resource;
import org.jclouds.savvis.vpdc.domain.ResourceImpl;
import org.jclouds.savvis.vpdc.domain.internal.VCloudSession;
import org.jclouds.savvis.vpdc.filters.SetVCloudTokenCookie;
import org.jclouds.savvis.vpdc.internal.LoginClient;
import org.jclouds.savvis.vpdc.reference.VCloudMediaType;
import org.jclouds.savvis.vpdc.reference.VPDCConstants;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * @author Adrian Cole
 */
public abstract class BaseVPDCAsyncClientTest<T> extends BaseAsyncClientTest<T> {

   @Override
   public ProviderMetadata createProviderMetadata() {
      return new VPDCProviderMetadata();
   }

   @Override
   protected void checkFilters(HttpRequest request) {
      assertEquals(request.getFilters().size(), 1);
      assertEquals(request.getFilters().get(0).getClass(), SetVCloudTokenCookie.class);
   }

   @Override
   protected Module createModule() {
      return new VPDCRestClientModuleExtension();
   }

   @ConfiguresRestClient
   public static class VPDCRestClientModuleExtension extends VPDCRestClientModule {

      @Override
      protected Supplier<VCloudSession> provideVCloudTokenCache(@Named(PROPERTY_SESSION_INTERVAL) long seconds,
               final LoginClient login) {
         return Suppliers.<VCloudSession> ofInstance(new VCloudSession() {

            @Override
            public Set<Resource> getOrgs() {
               return ImmutableSet.<Resource> of(new ResourceImpl("1", "org", VCloudMediaType.ORG_XML, URI
                        .create("https://api.savvis.net/rest/api/v0.8/org/1")));
            }

            @Override
            public String getVCloudToken() {
               return "token";
            }

         });

      }

   }

   @Override
   protected Properties setupProperties() {
      Properties props = super.setupProperties();
      props.setProperty(VPDCConstants.PROPERTY_VPDC_VDC_EMAIL, "test");
      return props;
   }

}