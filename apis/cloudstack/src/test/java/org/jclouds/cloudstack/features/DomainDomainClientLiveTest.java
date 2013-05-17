/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.cloudstack.features;

import static com.google.common.collect.Sets.newHashSet;
import static org.jclouds.cloudstack.options.ListDomainChildrenOptions.Builder.parentDomainId;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.NoSuchElementException;
import java.util.Set;

import org.jclouds.cloudstack.domain.Domain;
import org.jclouds.cloudstack.internal.BaseCloudStackClientLiveTest;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

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

      Set<Domain> domains = domainAdminClient.getDomainClient().listDomains();
      for (Domain candidate : domains) {
         checkDomain(candidate);
      }
   }

   private void checkDomain(Domain domain) {
      assertNotNull(domain.getId());
      if (domain.getLevel() == 0 /* global ROOT */) {
         assertNull(domain.getParentDomainName());
         assertNull(domain.getParentDomainId());
      } else {
         assertNotNull(domain.getParentDomainName());
         assertNotNull(domain.getParentDomainId());
      }
   }

   @Test
   public void testListDomainChildren() {
      skipIfNotDomainAdmin();

      Set<Domain> domains = domainAdminClient.getDomainClient().listDomains();
      Domain root = findRootOfVisibleTree(domains);
      if (domains.size() > 1) {
         assertTrue(root.hasChild());
      }

      Set<Domain> children = domainAdminClient.getDomainClient()
         .listDomainChildren(parentDomainId(root.getId()).isRecursive(true));
      assertEquals(domains.size() - 1, children.size());
      assertTrue(Sets.difference(domains, children).contains(root));
   }

   private Domain findRootOfVisibleTree(Set<Domain> domains) {
      final Set<String> names = newHashSet(Iterables.transform(domains,
         new Function<Domain, String>() {
            @Override
            public String apply(Domain domain) {
               return domain.getName();
            }
         }));

      for (Domain candidate : domains) {
         if (candidate.getParentDomainId() == null ||
            !names.contains(candidate.getParentDomainName())) {
            return candidate;
         }
      }
      throw new NoSuchElementException("No root node found in this tree");
   }
}
