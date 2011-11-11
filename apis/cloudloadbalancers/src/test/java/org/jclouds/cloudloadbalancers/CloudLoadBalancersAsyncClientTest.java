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
package org.jclouds.cloudloadbalancers;

import static org.jclouds.Constants.PROPERTY_API_VERSION;
import static org.jclouds.location.reference.LocationConstants.PROPERTY_REGIONS;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import org.jclouds.cloudloadbalancers.features.BaseCloudLoadBalancersAsyncClientTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.RestContextFactory;
import org.jclouds.rest.RestContextSpec;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code CloudLoadBalancersAsyncClient}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "CloudLoadBalancersAsyncClientTest")
public class CloudLoadBalancersAsyncClientTest extends BaseCloudLoadBalancersAsyncClientTest<CloudLoadBalancersAsyncClient> {

   private CloudLoadBalancersAsyncClient asyncClient;
   private CloudLoadBalancersClient syncClient;

   public void testSync() throws SecurityException, NoSuchMethodException, InterruptedException, ExecutionException {
      assert syncClient.getLoadBalancerClient("DFW") != null;
   }

   public void testAsync() throws SecurityException, NoSuchMethodException, InterruptedException, ExecutionException {  
      assert asyncClient.getLoadBalancerClient("DFW") != null;
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<CloudLoadBalancersAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<CloudLoadBalancersAsyncClient>>() {
      };
   }

   @BeforeClass
   @Override
   protected void setupFactory() throws IOException {
      super.setupFactory();
      asyncClient = injector.getInstance(CloudLoadBalancersAsyncClient.class);
      syncClient = injector.getInstance(CloudLoadBalancersClient.class);
   }

   @Override
   protected void checkFilters(HttpRequest request) {

   }
   
   protected String provider = "cloudloadbalancers";

   @Override
   public RestContextSpec<CloudLoadBalancersClient, CloudLoadBalancersAsyncClient> createContextSpec() {
      return new RestContextFactory(getProperties()).createContextSpec(provider, "user", "password", new Properties());
   }
   
   @Override
   protected Properties getProperties() {
      Properties overrides = new Properties();
      overrides.setProperty(PROPERTY_REGIONS, "US");
      overrides.setProperty(PROPERTY_API_VERSION, "1");
      overrides.setProperty(provider + ".endpoint", "https://auth");
      overrides.setProperty(provider + ".contextbuilder", CloudLoadBalancersContextBuilder.class.getName());
      return overrides;
   }
}
