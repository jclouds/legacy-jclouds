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
package org.jclouds.openstack.swift;

import static org.jclouds.Constants.PROPERTY_API_VERSION;
import static org.jclouds.Constants.PROPERTY_ENDPOINT;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_REGIONS;

import java.util.Properties;

import javax.inject.Singleton;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.http.HttpRequest;
import org.jclouds.openstack.TestOpenStackAuthenticationModule;
import org.jclouds.openstack.swift.config.BaseSwiftRestClientModule;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.internal.BaseAsyncClientTest;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.Test;

import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code BindSwiftObjectMetadataToRequest}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "SwiftClientTest")
public abstract class CommonSwiftClientTest extends BaseAsyncClientTest<SwiftAsyncClient> {
   
   @Override
   protected TypeLiteral<RestAnnotationProcessor<SwiftAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<SwiftAsyncClient>>() {
      };
   }
   
   @Override
   protected void checkFilters(HttpRequest request) {
   }

   @Override
   protected Module createModule() {
      return new TestSwiftRestClientModule();
   }

   @ConfiguresRestClient
   protected static class TestSwiftRestClientModule extends
            BaseSwiftRestClientModule<SwiftClient, SwiftAsyncClient> {
      private TestSwiftRestClientModule() {
         super(new TestOpenStackAuthenticationModule());
      }
      @Provides
      @Singleton
      CommonSwiftClient provideCommonSwiftClient(SwiftClient in) {
         return in;
      }

      @Provides
      @Singleton
      CommonSwiftAsyncClient provideCommonSwiftClient(SwiftAsyncClient in) {
         return in;
      }
   }

   protected String provider = "swift";

   protected ApiMetadata createApiMetadata() {
      return new SwiftApiMetadata();
   }
   
   @Override
   protected Properties setupProperties() {
      Properties properties = new Properties();
      properties.setProperty(PROPERTY_REGIONS, "US");
      properties.setProperty(PROPERTY_ENDPOINT, "https://auth");
      properties.setProperty(PROPERTY_API_VERSION, "1");
      return properties;
   }

}