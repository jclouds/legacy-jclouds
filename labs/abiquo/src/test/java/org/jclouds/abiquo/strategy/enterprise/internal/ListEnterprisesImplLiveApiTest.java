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

package org.jclouds.abiquo.strategy.enterprise.internal;

import static com.google.common.collect.Iterables.size;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import org.jclouds.abiquo.domain.enterprise.Enterprise;
import org.jclouds.abiquo.predicates.enterprise.EnterprisePredicates;
import org.jclouds.abiquo.strategy.BaseAbiquoStrategyLiveApiTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Live tests for the {@link ListEnterprisesImpl} strategy.
 * 
 * @author Ignasi Barrera
 */
@Test(groups = "api", testName = "ListEnterprisesImplLiveApiTest")
public class ListEnterprisesImplLiveApiTest extends BaseAbiquoStrategyLiveApiTest {
   private ListEnterprisesImpl strategy;

   @Override
   @BeforeClass(groups = "api")
   protected void setupStrategy() {
      this.strategy = env.context.getUtils().getInjector().getInstance(ListEnterprisesImpl.class);
   }

   public void testExecute() {
      Iterable<Enterprise> enterprises = strategy.execute();
      assertNotNull(enterprises);
      assertTrue(size(enterprises) > 0);
   }

   public void testExecutePredicateWithoutResults() {
      Iterable<Enterprise> enterprises = strategy.execute(EnterprisePredicates.name("UNEXISTING"));
      assertNotNull(enterprises);
      assertEquals(size(enterprises), 0);
   }

   public void testExecutePredicateWithResults() {
      Iterable<Enterprise> enterprises = strategy.execute(EnterprisePredicates.name(env.enterprise.getName()));
      assertNotNull(enterprises);
      assertEquals(size(enterprises), 1);
   }
}
