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

package org.jclouds.abiquo.fallbacks;

import static org.testng.Assert.assertEquals;

import org.jclouds.abiquo.AbiquoAsyncApi;
import org.jclouds.abiquo.functions.AppendApiVersionToAbiquoMimeType;
import org.testng.annotations.Test;

import com.google.common.base.Function;

/**
 * Unit tests for the {@link AppendApiVersionToAbiquoMimeType} function.
 * 
 * @author Ignasi Barrera
 */
@Test(groups = "unit", testName = "AppendApiVersionToAbiquoMimeTypeTest")
public class AppendApiVersionToAbiquoMimeTypeTest {
   @Test(expectedExceptions = NullPointerException.class)
   public void testApplyWithNullInput() {
      Function<String, String> function = new AppendApiVersionToAbiquoMimeType(AbiquoAsyncApi.API_VERSION);
      function.apply(null);
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testApplyWithInvalidMediaType() {
      Function<String, String> function = new AppendApiVersionToAbiquoMimeType(AbiquoAsyncApi.API_VERSION);
      function.apply("foo");
   }

   public void testApplyToStandardMediaType() {
      Function<String, String> function = new AppendApiVersionToAbiquoMimeType(AbiquoAsyncApi.API_VERSION);
      assertEquals(function.apply("application/xml"), "application/xml");
   }

   public void testApplyToAbiquoMediaTypeWithVersion() {
      Function<String, String> function = new AppendApiVersionToAbiquoMimeType(AbiquoAsyncApi.API_VERSION);
      assertEquals(function.apply("application/vnd.abiquo.datacenters+xml;version=1.8.5"),
            "application/vnd.abiquo.datacenters+xml;version=1.8.5");
   }

   public void testApplyToAbiquoMediaTypeWithoutVersion() {
      Function<String, String> function = new AppendApiVersionToAbiquoMimeType(AbiquoAsyncApi.API_VERSION);
      assertEquals(function.apply("application/vnd.abiquo.datacenters+xml"),
            "application/vnd.abiquo.datacenters+xml;version=" + AbiquoAsyncApi.API_VERSION);
   }
}
