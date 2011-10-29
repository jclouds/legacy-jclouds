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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class KeyboardScancodes {

   // http://www.win.tue.nl/~aeb/linux/kbd/scancodes-1.html

   public static final Map<String, String> NORMAL_KEYBOARD_BUTTON_MAP = createMap();
   public static final Map<String, String> SPECIAL_KEYBOARD_BUTTON_MAP = createSpecialMap();

   private static Map<String, String> createMap() {
      Map<String, String> alphaToHex = new HashMap<String, String>();
      alphaToHex.put("1", "02 82");
      alphaToHex.put("2", "03 83");
      alphaToHex.put("3", "04 84");
      alphaToHex.put("4", "05 85");
      alphaToHex.put("5", "06 86");
      alphaToHex.put("6", "07 87");
      alphaToHex.put("7", "08 88");
      alphaToHex.put("8", "09 89");
      alphaToHex.put("9", "0a 8a");
      alphaToHex.put("0", "0b 8b");
      alphaToHex.put("-", "0c 8c");
      alphaToHex.put("=", "0d 8d");
      alphaToHex.put("Tab", "0f 8f");
      alphaToHex.put("q", "10 90");
      alphaToHex.put("w", "11 91");
      alphaToHex.put("e", "12 92");
      alphaToHex.put("r", "13 93");
      alphaToHex.put("t", "14 94");
      alphaToHex.put("y", "15 95");
      alphaToHex.put("u", "16 96");
      alphaToHex.put("i", "17 97");
      alphaToHex.put("o", "18 98");
      alphaToHex.put("p", "19 99");

      alphaToHex.put("Q", "2a 10 aa");
      alphaToHex.put("W", "2a 11 aa");
      alphaToHex.put("E", "2a 12 aa");
      alphaToHex.put("R", "2a 13 aa");
      alphaToHex.put("T", "2a 14 aa");
      alphaToHex.put("Y", "2a 15 aa");
      alphaToHex.put("U", "2a 16 aa");
      alphaToHex.put("I", "2a 17 aa");
      alphaToHex.put("O", "2a 18 aa");
      alphaToHex.put("P", "2a 19 aa");

      alphaToHex.put("a", "1e 9e");
      alphaToHex.put("s", "1f 9f");
      alphaToHex.put("d", "20 a0");
      alphaToHex.put("f", "21 a1");
      alphaToHex.put("g", "22 a2");
      alphaToHex.put("h", "23 a3");
      alphaToHex.put("j", "24 a4");
      alphaToHex.put("k", "25 a5");
      alphaToHex.put("l", "26 a6");

      alphaToHex.put("A", "2a 1e aa 9e");
      alphaToHex.put("S", "2a 1f aa 9f");
      alphaToHex.put("D", "2a 20 aa a0");
      alphaToHex.put("F", "2a 21 aa a1");
      alphaToHex.put("G", "2a 22 aa a2");
      alphaToHex.put("H", "2a 23 aa a3");
      alphaToHex.put("J", "2a 24 aa a4");
      alphaToHex.put("K", "2a 25 aa a5");
      alphaToHex.put("L", "2a 26 aa a6");

      alphaToHex.put(");", "27 a7");
      alphaToHex.put("\"", "2a 28 aa a8");
      alphaToHex.put("\"", "28 a8");
      alphaToHex.put("\\", "2b ab");
      alphaToHex.put("|", "2a 2b aa 8b");
      alphaToHex.put("[", "1a 9a");
      alphaToHex.put("", "1b 9b");
      alphaToHex.put("<", "2a 33 aa b3");
      alphaToHex.put(">", "2a 34 aa b4");
      alphaToHex.put("$", "2a 05 aa 85");
      alphaToHex.put("+", "2a 0d aa 8d");

      alphaToHex.put("z", "2c ac");
      alphaToHex.put("x", "2d ad");
      alphaToHex.put("c", "2e ae");
      alphaToHex.put("v", "2f af");
      alphaToHex.put("b", "30 b0");
      alphaToHex.put("n", "31 b1");
      alphaToHex.put("m", "32 b2");
      alphaToHex.put("Z", "2a 2c aa ac");
      alphaToHex.put("X", "2a 2d aa ad");
      alphaToHex.put("C", "2a 2e aa ae");
      alphaToHex.put("V", "2a 2f aa af");
      alphaToHex.put("B", "2a 30 aa b0");
      alphaToHex.put("N", "2a 31 aa b1");
      alphaToHex.put("M", "2a 32 aa b2");

      alphaToHex.put(",", "33 b3");
      alphaToHex.put(".", "34 b4");
      alphaToHex.put("/", "35 b5");
      alphaToHex.put(":", "2a 27 aa a7");
      alphaToHex.put("%", "2a 06 aa 86");
      alphaToHex.put("_", "2a 0c aa 8c");
      alphaToHex.put("&", "2a 08 aa 88");
      alphaToHex.put("(", "2a 0a aa 8a");
      alphaToHex.put(")", "2a 0b aa 8b");
      return Collections.unmodifiableMap(alphaToHex);
   }

   private static Map<String, String> createSpecialMap() {
      Map<String, String> special = new HashMap<String, String>();
      special.put("<Enter>", "1c 9c");
      special.put("<Backspace>", "0e 8e");
      special.put("<Spacebar>", "39 b9");
      special.put("<Return>", "1c 9c");
      special.put("<Esc>", "01 81");
      special.put("<Tab>", "0f 8f");
      special.put("<KillX>", "1d 38 0e");
      special.put("<Wait>", "wait");

      special.put("<Up>", "48 c8");
      special.put("<Down>", "50 d0");
      special.put("<PageUp>", "49 c9");
      special.put("<PageDown>", "51 d1");
      special.put("<End>", "4f cf");
      special.put("<Insert>", "52 d2");
      special.put("<Delete>", "53 d3");
      special.put("<Left>", "4b cb");
      special.put("<Right>", "4d cd");
      special.put("<Home>", "47 c7");

      special.put("<F1>", "3b");
      special.put("<F2>", "3c");
      special.put("<F3>", "3d");
      special.put("<F4>", "3e");
      special.put("<F5>", "3f");
      special.put("<F6>", "40");
      special.put("<F7>", "41");
      special.put("<F8>", "42");
      special.put("<F9>", "43");
      special.put("<F10>", "44");
      return Collections.unmodifiableMap(special);
   }
}
