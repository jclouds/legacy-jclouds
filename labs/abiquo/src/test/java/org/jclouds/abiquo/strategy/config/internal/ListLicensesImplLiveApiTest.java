/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.abiquo.strategy.config.internal;

import static com.google.common.collect.Iterables.size;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import org.jclouds.abiquo.domain.config.License;
import org.jclouds.abiquo.domain.config.options.LicenseOptions;
import org.jclouds.abiquo.predicates.config.LicensePredicates;
import org.jclouds.abiquo.strategy.BaseAbiquoStrategyLiveApiTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Live tests for the {@link ListLicenseImpl} strategy.
 * 
 * @author Ignasi Barrera
 */
@Test(groups = "api", testName = "ListLicensesImplLiveApiTest")
public class ListLicensesImplLiveApiTest extends BaseAbiquoStrategyLiveApiTest {
   private ListLicensesImpl strategy;

   @Override
   @BeforeClass(groups = "api")
   protected void setupStrategy() {
      this.strategy = env.context.getUtils().getInjector().getInstance(ListLicensesImpl.class);
   }

   public void testExecute() {
      Iterable<License> licenses = strategy.execute();
      assertNotNull(licenses);
      assertTrue(size(licenses) > 0);
   }

   public void testExecuteInactive() {
      LicenseOptions options = LicenseOptions.builder().inactive(true).build();

      Iterable<License> licenses = strategy.execute(options);
      assertNotNull(licenses);
      assertTrue(size(licenses) == 1);
   }

   public void testExecuteActive() {
      LicenseOptions options = LicenseOptions.builder().active(true).build();

      Iterable<License> licenses = strategy.execute(options);
      assertNotNull(licenses);
      assertTrue(size(licenses) >= 1);
   }

   public void testExecutePredicateWithoutResults() {
      Iterable<License> licenses = strategy.execute(LicensePredicates.customer("FAIL"));
      assertNotNull(licenses);
      assertEquals(size(licenses), 0);
   }

   public void testExecutePredicateWithResults() {
      Iterable<License> licenses = strategy.execute(LicensePredicates.code(env.license.getCode()));
      assertNotNull(licenses);
      assertEquals(size(licenses), 1);
   }
}
