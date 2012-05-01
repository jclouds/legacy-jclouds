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
package org.jclouds.util;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class ProvidersTest {

   @Test
   @Deprecated
   public void testSupportedProviders() {
      Iterable<String> providers = org.jclouds.rest.Providers.getSupportedProviders();
      assertEquals(Sets.newLinkedHashSet(providers), ImmutableSet.<String> of("test-blobstore-api", "test-compute-api",
            "test-yet-another-compute-api", "test-yet-another-compute-provider"));
   }

}
