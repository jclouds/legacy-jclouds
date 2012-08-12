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
package org.jclouds.cloudstack.features;

import static com.google.common.collect.Iterables.find;
import static org.jclouds.cloudstack.options.UpdateDomainOptions.Builder.name;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import org.jclouds.cloudstack.domain.Domain;
import org.jclouds.cloudstack.internal.BaseCloudStackClientLiveTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;

/**
 * Tests behavior of {@code GlobalDomainClient}
 *
 * @author Andrei Savu
 */
@Test(groups = "live", singleThreaded = true, testName = "GlobalDomainClientLiveTest")
public class GlobalDomainClientLiveTest extends BaseCloudStackClientLiveTest {

   private GlobalDomainClient domainClient;
   private Domain rootDomain;

   @BeforeMethod
   public void before() {
      domainClient = globalAdminClient.getDomainClient();
      rootDomain = find(domainClient.listDomains(), new Predicate<Domain>() {
         @Override
         public boolean apply(Domain domain) {
            return domain != null && domain.getName().equals("ROOT");
         }
      });
   }

   @Test
   public void testCreateUpdateDeleteDomain() {
      skipIfNotDomainAdmin();

      Domain domain = null;
      try {
         domain = domainClient.createDomain(prefix + "-domain");
         checkDomain(domain, rootDomain, prefix + "-domain");

         Domain updated = domainClient.updateDomain(domain.getId(), name(prefix + "-domain-2"));
         checkDomain(updated, rootDomain, prefix + "-domain-2");
         assertEquals(updated.getId(), domain.getId());

      } finally {
         if (domain != null) {
            domainClient.deleteDomainAndAttachedResources(domain.getId());
         }
      }
      assertNull(domainClient.getDomainById(domain.getId()));
   }

   private void checkDomain(Domain domain, Domain rootDomain, String expectedName) {
      assertEquals(domain.getParentDomainId(), rootDomain.getId());
      assertEquals(domain.getName(), expectedName);
      assertEquals(domain.getParentDomainName(), rootDomain.getName());
   }
}
