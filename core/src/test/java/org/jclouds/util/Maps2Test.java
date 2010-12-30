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

package org.jclouds.util;

import static org.testng.Assert.assertEquals;

import java.util.Map;

import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class Maps2Test {
   public void testRenameKeyWhenNotFound() {
      Map<String, String> nothing = ImmutableMap.of();
      assertEquals(Maps2.renameKey(nothing, "foo", "bar"), nothing);
   }

   public void testRenameKeyWhenFound() {
      Map<String, String> nothing = ImmutableMap.of("foo", "bar");
      assertEquals(Maps2.renameKey(nothing, "foo", "bar"), ImmutableMap.of("bar", "bar"));
   }

   public void testTransformKeys() {
      Map<String, String> map = ImmutableMap.of("prefix:foo", "bar");
      assertEquals(Maps2.transformKeys(map, new Function<String, String>() {

         @Override
         public String apply(String arg0) {
            return arg0.replace("prefix:", "");
         }

      }), ImmutableMap.of("foo", "bar"));
   }

}
