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
import static org.jclouds.cloudstack.options.ListDomainChildrenOptions.Builder.parentDomainId;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.Set;

import javax.annotation.Nullable;

import org.jclouds.cloudstack.domain.Domain;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;

/**
 * Tests behavior of {@code DomainDomainClient}
 *
 * @author Andrei Savu
 */
@Test(groups = "live", singleThreaded = true, testName = "DomainDomainClientLiveTest")
public class DomainDomainClientLiveTest extends BaseCloudStackClientLiveTest {

   @Test
   public void testListDomains() {
      skipIfNotDomainAdmin();

      Set<Domain> allDomains = domainAdminClient.getDomainClient().listDomains();

      Domain root = find(allDomains, withName("ROOT"));
      assertEquals(root, domainAdminClient.getDomainClient().getDomainById(root.getId()));
      assertEquals(root.getLevel(), 0);
      assertEquals(root.getParentDomainId(), 0);
      assertNull(root.getParentDomainName());
      if (allDomains.size() > 1) {
         assertTrue(root.hasChild());
      }

      for (Domain domain : allDomains) {
         checkDomain(domain, allDomains);
      }
   }

   @Test
   public void testListDomainChildren() {
      skipIfNotDomainAdmin();

      Set<Domain> allDomains = domainAdminClient.getDomainClient().listDomains();
      Domain root = find(allDomains, withName("ROOT"));

      Set<Domain> children = domainAdminClient.getDomainClient()
         .listDomainChildren(parentDomainId(root.getId()).isRecursive(true));
      assertEquals(allDomains.size() - 1, children.size());

      for (Domain domain : children) {
         checkDomain(domain, allDomains);
      }
   }

   private Predicate<Domain> withName(final String name) {
      return new Predicate<Domain>() {
         @Override
         public boolean apply(@Nullable Domain domain) {
            return domain != null && domain.getName().equals(name);
         }
      };
   }

   private void checkDomain(Domain domain, Set<Domain> allDomains) {
      assert domain.getId() != null : domain;
      if (domain.getParentDomainName() != null) {
         Domain parent = find(allDomains, withName(domain.getParentDomainName()));
         assertEquals(parent.getId(), domain.getParentDomainId());
         assertTrue(parent.hasChild());
      }
   }
}
