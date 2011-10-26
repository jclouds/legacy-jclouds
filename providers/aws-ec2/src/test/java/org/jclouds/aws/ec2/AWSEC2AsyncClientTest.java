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
package org.jclouds.aws.ec2;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.jclouds.aws.ec2.services.BaseAWSEC2AsyncClientTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code AWSEC2AsyncClient}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "AWSEC2AsyncClientTest")
public class AWSEC2AsyncClientTest extends BaseAWSEC2AsyncClientTest<AWSEC2AsyncClient> {

   private AWSEC2AsyncClient asyncClient;
   private AWSEC2Client syncClient;

   public void testSync() throws SecurityException, NoSuchMethodException, InterruptedException, ExecutionException {
      assert syncClient.getAMIServices() != null;
      assert syncClient.getAvailabilityZoneAndRegionServices() != null;
      assert syncClient.getElasticBlockStoreServices() != null;
      assert syncClient.getElasticIPAddressServices() != null;
      assert syncClient.getInstanceServices() != null;
      assert syncClient.getKeyPairServices() != null;
      assert syncClient.getMonitoringServices() != null;
      assert syncClient.getSecurityGroupServices() != null;
      assert syncClient.getPlacementGroupServices() != null;
      assert syncClient.getWindowsServices() != null;
      assert syncClient.getTagServices() != null;

   }

   public void testAsync() throws SecurityException, NoSuchMethodException, InterruptedException, ExecutionException {
      assert asyncClient.getAMIServices() != null;
      assert asyncClient.getAvailabilityZoneAndRegionServices() != null;
      assert asyncClient.getElasticBlockStoreServices() != null;
      assert asyncClient.getElasticIPAddressServices() != null;
      assert asyncClient.getInstanceServices() != null;
      assert asyncClient.getKeyPairServices() != null;
      assert asyncClient.getMonitoringServices() != null;
      assert asyncClient.getSecurityGroupServices() != null;
      assert asyncClient.getPlacementGroupServices() != null;
      assert asyncClient.getWindowsServices() != null;
      assert asyncClient.getTagServices() != null;
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<AWSEC2AsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<AWSEC2AsyncClient>>() {
      };
   }

   @BeforeClass
   @Override
   protected void setupFactory() throws IOException {
      super.setupFactory();
      asyncClient = injector.getInstance(AWSEC2AsyncClient.class);
      syncClient = injector.getInstance(AWSEC2Client.class);
   }

   @Override
   protected void checkFilters(HttpRequest request) {

   }

}
