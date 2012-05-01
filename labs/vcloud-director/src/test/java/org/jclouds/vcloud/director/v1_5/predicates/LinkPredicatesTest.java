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

import static org.jclouds.vcloud.director.v1_5.predicates.LinkPredicates.relEquals;
import static org.jclouds.vcloud.director.v1_5.predicates.LinkPredicates.typeEquals;

import java.net.URI;

import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;
import org.jclouds.vcloud.director.v1_5.domain.Link;
import org.testng.annotations.Test;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "LinkPredicatesTest")
public class LinkPredicatesTest {
   Link ref = Link.builder().type("application/vnd.vmware.vcloud.media+xml").rel("add").href(
            URI.create("https://mycloud.greenhousedata.com/api/vdc/e9cd3387-ac57-4d27-a481-9bee75e0690f/media"))
            .build();

   @Test
   public void testRelEqualsWhenEqual() {
      assert relEquals(Link.Rel.ADD).apply(ref);
   }

   @Test
   public void testRelEqualsWhenEqualString() {
      assert relEquals("add").apply(ref);
   }

   @Test
   public void testRelEqualsWhenNotEqual() {
      assert !relEquals("foo").apply(ref);
   }

   @Test
   public void testTypeEqualsWhenEqual() {
      assert typeEquals(VCloudDirectorMediaType.MEDIA).apply(ref);
   }

   @Test
   public void testTypeEqualsWhenNotEqual() {
      assert !typeEquals("foo").apply(ref);
   }
}
