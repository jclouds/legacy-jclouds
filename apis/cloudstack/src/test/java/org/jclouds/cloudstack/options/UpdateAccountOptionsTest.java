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
package org.jclouds.cloudstack.options;

import com.google.common.collect.ImmutableSet;
import org.testng.annotations.Test;

import static org.jclouds.cloudstack.options.UpdateAccountOptions.Builder.networkDomain;
import static org.testng.Assert.assertEquals;

/**
 * Tests behavior of {@code UpdateAccountOptions}
 *
 * @author Andrei Savu
 */
@Test(groups = "unit")
public class UpdateAccountOptionsTest {

   public void testNetworkDomain() {
      UpdateAccountOptions options = new UpdateAccountOptions().networkDomain("net");
      assertEquals(ImmutableSet.of("net"), options.buildQueryParameters().get("networkdomain"));
   }

   public void testNameStatic() {
      UpdateAccountOptions options = networkDomain("net");
      assertEquals(ImmutableSet.of("net"), options.buildQueryParameters().get("networkdomain"));
   }
}
