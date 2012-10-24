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

package org.jclouds.abiquo.binders;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.abiquo.model.rest.RESTLink;

/**
 * Unit tests for the {@link BindLinkToPath} class.
 * 
 * @author Ignasi Barrera
 */
@Test(groups = "unit", testName = "BindLinkToPathTest")
public class BindLinkToPathTest {
   @Test(expectedExceptions = NullPointerException.class)
   public void testGetNewEnpointNullInput() {
      BindLinkToPath binder = new BindLinkToPath();
      binder.getNewEndpoint(null, null);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testGetNewEnpointInvalidInput() {
      BindLinkToPath binder = new BindLinkToPath();
      binder.getNewEndpoint(null, new Object());
   }

   public void testGetNewEnpoint() {
      BindLinkToPath binder = new BindLinkToPath();
      assertEquals(binder.getNewEndpoint(null, new RESTLink("edit", "http://foo/bar")), "http://foo/bar");
   }
}
