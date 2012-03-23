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
package org.jclouds.openstack.nova.v1_1.predicates;

import static org.jclouds.openstack.nova.v1_1.predicates.ImagePredicates.statusEquals;

import org.jclouds.openstack.nova.v1_1.domain.Image;
import org.jclouds.openstack.nova.v1_1.domain.Image.Status;
import org.jclouds.openstack.nova.v1_1.parse.ParseImageTest;
import org.testng.annotations.Test;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ImagePredicatesTest")
public class ImagePredicatesTest {
   Image ref = new ParseImageTest().expected();

   @Test
   public void teststatusEqualsWhenEqual() {
      assert statusEquals(Status.SAVING).apply(ref);
   }

   @Test
   public void teststatusEqualsWhenNotEqual() {
      assert !statusEquals(Status.DELETED).apply(ref);
   }

}
