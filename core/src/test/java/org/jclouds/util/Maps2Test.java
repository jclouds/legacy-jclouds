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

import static com.google.common.base.Functions.constant;
import static com.google.common.base.Functions.identity;
import static com.google.common.collect.Sets.newHashSet;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Map;

import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

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

   public void testFromKeysEmptyKeys() {
       assertTrue(Maps2.fromKeys(ImmutableSet.of(), identity()).isEmpty(),
               "Expected returned map to be empty");
   }
   
   @Test(expectedExceptions = { NullPointerException.class })
   public void testFromKeysNullKey() {
       Maps2.fromKeys(newHashSet((Object) null), constant("const"));
   }
   
   public void testFromKeys() {
       // ImmutableMap doesn't support null values
       Map<String, String> expected = Maps.newHashMap();
       expected.put("foo", "foo");
       expected.put("bar", "foo");
       expected.put("baz", null);

       assertEquals(Maps2.fromKeys(ImmutableSet.of("foo", "bar", "baz"),
           new Function<String, String>() {
                @Override
                public String apply(String input) {
                    return (input.equals("baz") ? null : "foo");
                }
            }), expected);
   }
   
}
