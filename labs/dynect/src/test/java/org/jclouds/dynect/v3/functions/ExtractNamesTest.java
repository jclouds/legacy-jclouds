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

package org.jclouds.dynect.v3.functions;

import static org.testng.Assert.assertEquals;

import org.jclouds.dynect.v3.functions.ExtractNames.ExtractNameInPath;
import org.testng.annotations.Test;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class ExtractNamesTest {
   ExtractNames fn = new ExtractNames();

   public void testExtractNameInPath() {
      assertEquals(ExtractNameInPath.INSTANCE.apply("/REST/Zone/jclouds.org/"), "jclouds.org");
   }

   public void testExtractNames() {
      assertEquals(fn.apply(FluentIterable.from(ImmutableSet.of("/REST/Zone/jclouds.org/"))).toSet(),
            ImmutableSet.of("jclouds.org"));
   }
}
