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
package org.jclouds.ec2;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.jclouds.ec2.services.BaseEC2AsyncClientTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code EC2AsyncClient}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "EC2AsyncClientTest")
public class EC2AsyncClientTest extends BaseEC2AsyncClientTest<EC2AsyncClient> {

   private EC2AsyncClient asyncClient;
   private EC2Client syncClient;

   public void testSync() throws SecurityException, NoSuchMethodException, InterruptedException, ExecutionException {
      assert syncClient.getAMIServices() != null;
      assert syncClient.getAvailabilityZoneAndRegionServices() != null;
      assert syncClient.getElasticBlockStoreServices() != null;
      assert syncClient.getElasticIPAddressServices() != null;
      assert syncClient.getInstanceServices() != null;
      assert syncClient.getKeyPairServices() != null;
      assert syncClient.getSecurityGroupServices() != null;
      assert syncClient.getWindowsServices() != null;

   }

   public void testAsync() throws SecurityException, NoSuchMethodException, InterruptedException, ExecutionException {
      assert asyncClient.getAMIServices() != null;
      assert asyncClient.getAvailabilityZoneAndRegionServices() != null;
      assert asyncClient.getElasticBlockStoreServices() != null;
      assert asyncClient.getElasticIPAddressServices() != null;
      assert asyncClient.getInstanceServices() != null;
      assert asyncClient.getKeyPairServices() != null;
      assert asyncClient.getSecurityGroupServices() != null;
      assert asyncClient.getWindowsServices() != null;
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<EC2AsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<EC2AsyncClient>>() {
      };
   }

   @BeforeClass
   @Override
   protected void setupFactory() throws IOException {
      super.setupFactory();
      asyncClient = injector.getInstance(EC2AsyncClient.class);
      syncClient = injector.getInstance(EC2Client.class);
   }

   @Override
   protected void checkFilters(HttpRequest request) {

   }

}
