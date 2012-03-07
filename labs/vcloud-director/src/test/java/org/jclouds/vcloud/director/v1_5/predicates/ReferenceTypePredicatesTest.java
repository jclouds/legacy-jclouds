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
package org.jclouds.vcloud.director.v1_5.predicates;

import java.net.URI;

import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;
import org.jclouds.vcloud.director.v1_5.domain.Reference;
import org.jclouds.vcloud.director.v1_5.predicates.ReferenceTypePredicates;
import org.testng.annotations.Test;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ReferenceTypePredicatesTest")
public class ReferenceTypePredicatesTest {
   Reference ref = Reference.builder().type("application/vnd.vmware.vcloud.catalogItem+xml").name("image").href(
            URI.create("https://vcloudbeta.bluelock.com/api/catalogItem/67a469a1-aafe-4b5b-bb31-a6202ad8961f")).build();

   @Test
   public void testNameEqualsWhenEqual() {
      assert ReferenceTypePredicates.<Reference> nameEquals("image").apply(ref);
   }

   @Test
   public void testNameEqualsWhenNotEqual() {
      assert !ReferenceTypePredicates.<Reference> nameEquals("foo").apply(ref);
   }

   @Test
   public void testTypeEqualsWhenEqual() {
      assert ReferenceTypePredicates.<Reference> typeEquals(VCloudDirectorMediaType.CATALOG_ITEM).apply(ref);
   }

   @Test
   public void testTypeEqualsWhenNotEqual() {
      assert !ReferenceTypePredicates.<Reference> typeEquals("foo").apply(ref);
   }
}
