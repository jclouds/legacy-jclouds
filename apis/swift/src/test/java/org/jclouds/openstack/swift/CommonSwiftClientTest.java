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
package org.jclouds.openstack.swift;

import static org.jclouds.Constants.PROPERTY_API_VERSION;
import static org.jclouds.Constants.PROPERTY_ENDPOINT;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_REGIONS;

import java.net.URI;
import java.util.Properties;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.RequiresHttp;
import org.jclouds.openstack.TestOpenStackAuthenticationModule;
import org.jclouds.openstack.OpenStackAuthAsyncClient.AuthenticationResponse;
import org.jclouds.openstack.swift.CommonSwiftAsyncClient;
import org.jclouds.openstack.swift.CommonSwiftClient;
import org.jclouds.openstack.swift.config.BaseSwiftRestClientModule;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.RestClientTest;
import org.jclouds.rest.RestContextFactory;
import org.jclouds.rest.RestContextSpec;
import org.testng.annotations.Test;

import com.google.inject.Module;

/**
 * Tests behavior of {@code BindSwiftObjectMetadataToRequest}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "SwiftClientTest")
public abstract class CommonSwiftClientTest<A extends CommonSwiftAsyncClient> extends RestClientTest<A> {

   @Override
   protected void checkFilters(HttpRequest request) {
   }

   @Override
   protected Module createModule() {
      return new TestSwiftRestClientModule();
   }

   @ConfiguresRestClient
   @RequiresHttp
   protected static class TestSwiftRestClientModule extends
            BaseSwiftRestClientModule<CommonSwiftClient, CommonSwiftAsyncClient> {
      private TestSwiftRestClientModule() {
         super(new TestOpenStackAuthenticationModule(), CommonSwiftClient.class, CommonSwiftAsyncClient.class);
      }

      @Override
      protected URI provideStorageUrl(AuthenticationResponse response) {
         return URI.create("http://storage");
      }

   }

   protected String provider = "swift";

   @Override
   public RestContextSpec<CommonSwiftClient, CommonSwiftAsyncClient> createContextSpec() {
      return new RestContextFactory().createContextSpec(provider, "user", "password", new Properties());
   }

   @Override
   protected Properties getProperties() {
      Properties properties = new Properties();
      properties.setProperty(PROPERTY_REGIONS, "US");
      properties.setProperty(PROPERTY_ENDPOINT, "https://auth");
      properties.setProperty(PROPERTY_API_VERSION, "1");
      return properties;
   }

}