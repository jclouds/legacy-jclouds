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

package org.jclouds.savvis.vpdc;

import static org.testng.Assert.assertEquals;

import java.io.IOException;

import org.jclouds.http.HttpRequest;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.jclouds.savvis.vpdc.features.BaseVPDCAsyncClientTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code VPDCAsyncClient}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "VPDCAsyncClientTest")
public class VPDCAsyncClientTest extends BaseVPDCAsyncClientTest<VPDCAsyncClient> {

   private VPDCAsyncClient asyncClient;
   private VPDCClient syncClient;

   public void testSync() {
      assert syncClient.getBrowsingClient() != null;
      assert syncClient.getVMClient() != null;
      assertEquals(syncClient.listOrgs().size(), 1);
      assertEquals(syncClient.listPredefinedOperatingSystems().size(), 3);

   }

   public void testAsync() {
      assert asyncClient.getBrowsingClient() != null;
      assert asyncClient.getVMClient() != null;
      assertEquals(asyncClient.listOrgs().size(), 1);
      assertEquals(asyncClient.listPredefinedOperatingSystems().size(), 3);

   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<VPDCAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<VPDCAsyncClient>>() {
      };
   }

   @BeforeClass
   @Override
   protected void setupFactory() throws IOException {
      super.setupFactory();
      asyncClient = injector.getInstance(VPDCAsyncClient.class);
      syncClient = injector.getInstance(VPDCClient.class);
   }

   @Override
   protected void checkFilters(HttpRequest request) {

   }
}
