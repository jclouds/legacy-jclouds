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

package org.jclouds.virtualbox.settings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KeyboardScancodes {

   // http://www.win.tue.nl/~aeb/linux/kbd/scancodes-1.html
   public static final Map<String, List<Integer>> SPECIAL_KEYBOARD_BUTTON_MAP_LIST = createSpecialCodeMap();
   public static final Map<String, List<Integer>> NORMAL_KEYBOARD_BUTTON_MAP_LIST = createNormalCodeMap();

   public static AlphaBuilder builder() {
      return new AlphaBuilder();
   }

   private static Map<String, List<Integer>> createNormalCodeMap() {
      Map<String, List<Integer>> alphaToHex = KeyboardScancodes.builder()
            .put("1", 0x02, 0x82)
            .put("2", 0x03, 0x83)
            .put("3", 0x04, 0x84)
            .put("4", 0x05, 0x85)
            .put("5", 0x06, 0x86)
            .put("6", 0x07, 0x87)
            .put("7", 0x08, 0x88)
            .put("8", 0x09, 0x89)
            .put("9", 0x0a, 0x8a)
            .put("0", 0x0b, 0x8b)

            .put("-", 0x0c, 0x8c)
            .put("=", 0x0d, 0x8d)
            .put("Tab", 0x0f, 0x8f)
            .put("q", 0x10, 0x90)
            .put("w", 0x11, 0x91)
            .put("e", 0x12, 0x92)
            .put("r", 0x13, 0x93)
            .put("t", 0x14, 0x94)
            .put("y", 0x15, 0x95)
            .put("u", 0x16, 0x96)
            .put("i", 0x17, 0x97)
            .put("o", 0x18, 0x98)
            .put("p", 0x19, 0x99)

            .put("Q", 0x2a, 0x10, 0xaa)
            .put("W", 0x2a, 0x11, 0xaa)
            .put("E", 0x2a, 0x12, 0xaa)
            .put("R", 0x2a, 0x13, 0xaa)
            .put("T", 0x2a, 0x14, 0xaa)
            .put("Y", 0x2a, 0x15, 0xaa)
            .put("U", 0x2a, 0x16, 0xaa)
            .put("I", 0x2a, 0x17, 0xaa)
            .put("O", 0x2a, 0x18, 0xaa)
            .put("P", 0x2a, 0x19, 0xaa)

            .put("a", 0x1e, 0x9e)
            .put("s", 0x1f, 0x9f)
            .put("d", 0x20, 0xa0)
            .put("f", 0x21, 0xa1)
            .put("g", 0x22, 0xa2)
            .put("h", 0x23, 0xa3)
            .put("j", 0x24, 0xa4)
            .put("k", 0x25, 0xa5)
            .put("l", 0x26, 0xa6)

            .put("A", 0x2a, 0x1e, 0xaa, 0x9e)
            .put("S", 0x2a, 0x1f, 0xaa, 0x9f)
            .put("D", 0x2a, 0x20, 0xaa, 0xa0)
            .put("F", 0x2a, 0x21, 0xaa, 0xa1)
            .put("G", 0x2a, 0x22, 0xaa, 0xa2)
            .put("H", 0x2a, 0x23, 0xaa, 0xa3)
            .put("J", 0x2a, 0x24, 0xaa, 0xa4)
            .put("K", 0x2a, 0x25, 0xaa, 0xa5)
            .put("L", 0x2a, 0x26, 0xaa, 0xa6)

            .put(") ", 0x27, 0xa7)
            .put("\"", 0x2a, 0x28, 0xaa, 0xa8)
            .put("\"", 0x28, 0xa8)
            .put("\\", 0x2b, 0xab)
            .put("|", 0x2a, 0x2b, 0xaa, 0x8b)
            .put("[", 0x1a, 0x9a)
            .put("", 0x1b, 0x9b)
            .put("<", 0x2a, 0x33, 0xaa, 0xb3)
            .put(">", 0x2a, 0x34, 0xaa, 0xb4)
            .put("$", 0x2a, 0x05, 0xaa, 0x85)
            .put("+", 0x2a, 0x0d, 0xaa, 0x8d)
            
            .put("z", 0x2c, 0xac)
            .put("x", 0x2d, 0xad)
            .put("c", 0x2e, 0xae)
            .put("v", 0x2f, 0xaf)
            .put("b", 0x30, 0xb0)
            .put("n", 0x31, 0xb1)
            .put("m", 0x32, 0xb2)
            .put("Z", 0x2a, 0x2c, 0xaa, 0xac)
            .put("X", 0x2a, 0x2d, 0xaa, 0xad)
            .put("C", 0x2a, 0x2e, 0xaa, 0xae)
            .put("V", 0x2a, 0x2f, 0xaa, 0xaf)
            .put("B", 0x2a, 0x30, 0xaa, 0xb0)
            .put("N", 0x2a, 0x31, 0xaa, 0xb1)
            .put("M", 0x2a, 0x32, 0xaa, 0xb2)

            .put(",", 0x33, 0xb3)
            .put(".", 0x34, 0xb4)
            .put("/", 0x35, 0xb5)
            .put(":", 0x2a, 0x27, 0xaa, 0xa7)
            .put("%", 0x2a, 0x06, 0xaa, 0x86)
            .put("_", 0x2a, 0x0c, 0xaa, 0x8c)
            .put("&", 0x2a, 0x08, 0xaa, 0x88)
            .put("(", 0x2a, 0x0a, 0xaa, 0x8a)
            .put(")", 0x2a, 0x0b, 0xaa, 0x8b)

            .build();

      return Collections.unmodifiableMap(alphaToHex);
   }

   private static Map<String, List<Integer>> createSpecialCodeMap() {
      Map<String, List<Integer>> special = KeyboardScancodes
            .builder()
            .put("<Enter>", 0x1c, 0x9c)
            .put("<Backspace>", 0x0e, 0x8e)
            .put("<Spacebar>", 0x39, 0xb9)
            .put("<Return>", 0x1c, 0x9c)
            .put("<Esc>", 0x01, 0x81)
            .put("<Tab>", 0x0f, 0x8f)
            .put("<KillX>", 0x1d, 0x38, 0x0e)

            .put("<Up>", 0x48, 0xc8)
            .put("<Down>", 0x50, 0xd0)
            .put("<PageUp>", 0x49, 0xc9)
            .put("<PageDown>", 0x51, 0xd1)
            .put("<End>", 0x4f, 0xcf)
            .put("<Insert>", 0x52, 0xd2)
            .put("<Delete>", 0x53, 0xd3)
            .put("<Left>", 0x4b, 0xcb)
            .put("<Right>", 0x4d, 0xcd)
            .put("<Home>", 0x47, 0xc7)

            .put("<F1>", 0x3b)
            .put("<F2>", 0x3c)
            .put("<F3>", 0x3d)
            .put("<F4>", 0x3e)
            .put("<F5>", 0x3f)
            .put("<F6>", 0x40)
            .put("<F7>", 0x41)
            .put("<F8>", 0x42)
            .put("<F9>", 0x43)
            .put("<F10>", 0x44)
            .build();
      return Collections.unmodifiableMap(special);
   }

   public static class AlphaBuilder {

      private Map<String, List<Integer>> mappings = new HashMap<String, List<Integer>>();

      public AlphaBuilder put(String str, int... mapping) {
         List<Integer> arrayList = new ArrayList<Integer>();
         for (int i : mapping) {
            arrayList.add(i);
         }
         mappings.put(str, arrayList);
         return this;
      }

      public Map<String, List<Integer>> build() {
         return mappings;
      }

   }
}
