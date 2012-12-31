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
package org.jclouds.virtualbox.functions;

import java.util.Collection;
import java.util.List;

import org.jclouds.virtualbox.settings.KeyboardScancodes;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

public class StringToKeyCode implements Function<String, List<Integer>> {

   @Override
   public List<Integer> apply(String subsequence) {
	   return stringToKeycode(subsequence);
   }
   
   private List<Integer> stringToKeycode(String s) {
      if (containsSpecialCharacter(s)) {
         return transformSpecialCharIntoKeycodes(s);
      } else {
         return transformStandardCharacterIntoKeycodes(s);
      }
   }
   
   private List<Integer> transformStandardCharacterIntoKeycodes(String s) {
      List<Integer> values = Lists.newArrayList();
      for (String digit : Splitter.fixedLength(1).split(s)) {
         Collection<Integer> hex = KeyboardScancodes.NORMAL_KEYBOARD_BUTTON_MAP_LIST.get(digit);
         if (hex != null)
            values.addAll(hex);
      }
      values.addAll(KeyboardScancodes.SPECIAL_KEYBOARD_BUTTON_MAP_LIST.get("<Spacebar>"));
      return values;
   }

   private List<Integer> transformSpecialCharIntoKeycodes(String s) {
      List<Integer> values = Lists.newArrayList();
      for (String special : s.split("<")) {
         Collection<Integer> value = KeyboardScancodes.SPECIAL_KEYBOARD_BUTTON_MAP_LIST.get("<" + special);
         if (value != null)
            values.addAll(value);
      }
      return values;
   }

   private boolean containsSpecialCharacter(String s) {
      return s.startsWith("<");
   }
}
