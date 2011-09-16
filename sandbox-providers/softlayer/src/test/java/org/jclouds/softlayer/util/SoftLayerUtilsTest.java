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
package org.jclouds.softlayer.util;

import com.google.common.collect.ImmutableSet;
import org.jclouds.softlayer.domain.ProductPackage;
import org.jclouds.softlayer.features.AccountClient;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

/**
 * Tests {@code SoftLayerUtils}
 *
 * @author Jason King
 */
@Test(sequential = true,groups = "unit")
public class SoftLayerUtilsTest {

   private AccountClient accountClient;
   private ProductPackage productPackage;

   @BeforeMethod
   public void setup() {
      accountClient = createMock(AccountClient.class);
      ProductPackage.Builder builder = ProductPackage.builder();
      productPackage = builder.name("product package").id(123L).build();
   }

   @Test
   public void testGetProductPackageIdMissing() {
      expect(accountClient.getActivePackages()).andReturn(ImmutableSet.of(productPackage));
      replay(accountClient);
      Long result = SoftLayerUtils.getProductPackageId(accountClient,"missing package");
      assertNull(result);
      verify(accountClient);
   }

   @Test
   public void testGetProductPackageIdFound() {
      expect(accountClient.getActivePackages()).andReturn(ImmutableSet.of(productPackage));
      replay(accountClient);
      Long result = SoftLayerUtils.getProductPackageId(accountClient,"product package");
      assertEquals(result,new Long(123L));
      verify(accountClient);
   }
}
