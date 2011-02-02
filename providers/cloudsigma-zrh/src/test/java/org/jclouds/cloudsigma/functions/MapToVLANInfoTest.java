/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.cloudsigma.functions;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.util.Map;

import org.jclouds.cloudsigma.domain.VLANInfo;
import org.jclouds.util.Strings2;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = { "unit" })
public class MapToVLANInfoTest {
   public static VLANInfo ONE = new VLANInfo.Builder()//
         .uuid("6e2d1f6a-03c8-422b-bc8e-d744612cf46a")//
         .name("My VLAN1").user("f2e19d5c-eaa1-44e5-94aa-dc194594bd7b").build();
   private static final MapToVLANInfo MAP_TO_VLAN = new MapToVLANInfo();

   public void testEmptyMapReturnsNull() {
      assertEquals(MAP_TO_VLAN.apply(ImmutableMap.<String, String> of()), null);
   }

   public void test() throws IOException {

      Map<String, String> input = new ListOfKeyValuesDelimitedByBlankLinesToListOfMaps().apply(
            Strings2.toStringAndClose(MapToVLANInfoTest.class.getResourceAsStream("/vlan.txt"))).get(0);

      assertEquals(MAP_TO_VLAN.apply(input), ONE);

   }
}
