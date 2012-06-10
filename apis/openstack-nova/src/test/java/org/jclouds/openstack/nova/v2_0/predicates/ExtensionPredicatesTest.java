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
package org.jclouds.openstack.nova.v2_0.predicates;

import static org.jclouds.openstack.nova.v2_0.predicates.ExtensionPredicates.aliasEquals;
import static org.jclouds.openstack.nova.v2_0.predicates.ExtensionPredicates.namespaceEquals;

import java.net.URI;

import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.openstack.nova.v2_0.domain.Extension;
import org.testng.annotations.Test;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ExtensionPredicatesTest")
public class ExtensionPredicatesTest {
   Extension ref = Extension.builder().alias("os-keypairs").name("Keypairs").namespace(
            URI.create("http://docs.openstack.org/ext/keypairs/api/v1.1")).updated(
            new SimpleDateFormatDateService().iso8601SecondsDateParse("2011-08-08T00:00:00+00:00")).description(
            "Keypair Support").build();

   @Test
   public void testAliasEqualsWhenEqual() {
      assert aliasEquals("os-keypairs").apply(ref);
   }

   @Test
   public void testAliasEqualsWhenNotEqual() {
      assert !aliasEquals("foo").apply(ref);
   }

   @Test
   public void testNamespaceEqualsWhenEqual() {
      assert namespaceEquals(URI.create("http://docs.openstack.org/ext/keypairs/api/v1.1")).apply(ref);
   }

   @Test
   public void testNamespaceEqualsWhenEqualEvenOnInputHttps() {
      assert namespaceEquals(URI.create("http://docs.openstack.org/ext/keypairs/api/v1.1")).apply(
               ref.toBuilder().namespace(URI.create("https://docs.openstack.org/ext/keypairs/api/v1.1")).build());
   }

   @Test
   public void testNamespaceEqualsWhenNotEqual() {
      assert !namespaceEquals(URI.create("foo")).apply(ref);
   }
}
