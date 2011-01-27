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

import org.jclouds.cloudsigma.domain.ProfileInfo;
import org.jclouds.cloudsigma.domain.ProfileType;
import org.jclouds.util.Strings2;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = { "unit" })
public class MapToProfileInfoTest {
   public static ProfileInfo ONE = new ProfileInfo.Builder().type(ProfileType.REGULAR)//
         .uuid("58ca3c1f-7629-4771-9b71-863f40153ba4")//
         .email("adrian@jclouds.org").firstName("Adrian").lastName("Cole").nickName("jclouds").build();

   private static final MapToProfileInfo MAP_TO_PROFILE = new MapToProfileInfo();

   public void testEmptyMapReturnsNull() {
      assertEquals(MAP_TO_PROFILE.apply(ImmutableMap.<String, String> of()), null);
   }

   public void test() throws IOException {

      Map<String, String> input = new ListOfKeyValuesDelimitedByBlankLinesToListOfMaps().apply(
            Strings2.toStringAndClose(MapToProfileInfoTest.class.getResourceAsStream("/profile.txt"))).get(0);

      assertEquals(MAP_TO_PROFILE.apply(input), ONE);

   }
}
