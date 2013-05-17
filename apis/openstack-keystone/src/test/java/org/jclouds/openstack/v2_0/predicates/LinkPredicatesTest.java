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
package org.jclouds.openstack.v2_0.predicates;

import static org.jclouds.openstack.v2_0.predicates.LinkPredicates.hrefEquals;
import static org.jclouds.openstack.v2_0.predicates.LinkPredicates.relationEquals;
import static org.jclouds.openstack.v2_0.predicates.LinkPredicates.typeEquals;

import java.net.URI;

import org.jclouds.openstack.v2_0.domain.Link;
import org.jclouds.openstack.v2_0.domain.Link.Relation;
import org.testng.annotations.Test;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "LinkPredicatesTest")
public class LinkPredicatesTest {
   Link ref = Link.builder().type("application/pdf").relation(Relation.DESCRIBEDBY).href(
            URI.create("http://docs.openstack.org/ext/keypairs/api/v1.1")).build();

   @Test
   public void testRelationEqualsWhenEqual() {
      assert relationEquals(Relation.DESCRIBEDBY).apply(ref);
   }

   @Test
   public void testRelationEqualsWhenNotEqual() {
      assert !relationEquals(Relation.UNRECOGNIZED).apply(ref);
   }

   @Test
   public void testTypeEqualsWhenEqual() {
      assert typeEquals("application/pdf").apply(ref);
   }

   @Test
   public void testTypeEqualsWhenNotEqual() {
      assert !typeEquals("foo").apply(ref);
   }

   @Test
   public void testHrefEqualsWhenEqual() {
      assert hrefEquals(URI.create("http://docs.openstack.org/ext/keypairs/api/v1.1")).apply(ref);
   }

   @Test
   public void testHrefEqualsWhenNotEqual() {
      assert !hrefEquals(URI.create("foo")).apply(ref);
   }
}
