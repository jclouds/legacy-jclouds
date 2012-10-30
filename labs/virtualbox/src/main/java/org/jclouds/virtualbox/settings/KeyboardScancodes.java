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

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

/**
 * 
 * @see <a href="http://www.win.tue.nl/~aeb/linux/kbd/scancodes-1.html" />
 *
 */
public class KeyboardScancodes {

   public static final Multimap<String, Integer> NORMAL_KEYBOARD_BUTTON_MAP_LIST = ImmutableMultimap
         .<String, Integer> builder()
         
            .putAll("1", 0x02, 0x82)
            .putAll("2", 0x03, 0x83)
            .putAll("3", 0x04, 0x84)
            .putAll("4", 0x05, 0x85)
            .putAll("5", 0x06, 0x86)
            .putAll("6", 0x07, 0x87)
            .putAll("7", 0x08, 0x88)
            .putAll("8", 0x09, 0x89)
            .putAll("9", 0x0a, 0x8a)
            .putAll("0", 0x0b, 0x8b)

            .putAll("-", 0x0c, 0x8c)
            .putAll("=", 0x0d, 0x8d)
            .putAll("Tab", 0x0f, 0x8f)
            .putAll("q", 0x10, 0x90)
            .putAll("w", 0x11, 0x91)
            .putAll("e", 0x12, 0x92)
            .putAll("r", 0x13, 0x93)
            .putAll("t", 0x14, 0x94)
            .putAll("y", 0x15, 0x95)
            .putAll("u", 0x16, 0x96)
            .putAll("i", 0x17, 0x97)
            .putAll("o", 0x18, 0x98)
            .putAll("p", 0x19, 0x99)

            .putAll("Q", 0x2a, 0x10, 0xaa)
            .putAll("W", 0x2a, 0x11, 0xaa)
            .putAll("E", 0x2a, 0x12, 0xaa)
            .putAll("R", 0x2a, 0x13, 0xaa)
            .putAll("T", 0x2a, 0x14, 0xaa)
            .putAll("Y", 0x2a, 0x15, 0xaa)
            .putAll("U", 0x2a, 0x16, 0xaa)
            .putAll("I", 0x2a, 0x17, 0xaa)
            .putAll("O", 0x2a, 0x18, 0xaa)
            .putAll("P", 0x2a, 0x19, 0xaa)

            .putAll("a", 0x1e, 0x9e)
            .putAll("s", 0x1f, 0x9f)
            .putAll("d", 0x20, 0xa0)
            .putAll("f", 0x21, 0xa1)
            .putAll("g", 0x22, 0xa2)
            .putAll("h", 0x23, 0xa3)
            .putAll("j", 0x24, 0xa4)
            .putAll("k", 0x25, 0xa5)
            .putAll("l", 0x26, 0xa6)

            .putAll("A", 0x2a, 0x1e, 0xaa, 0x9e)
            .putAll("S", 0x2a, 0x1f, 0xaa, 0x9f)
            .putAll("D", 0x2a, 0x20, 0xaa, 0xa0)
            .putAll("F", 0x2a, 0x21, 0xaa, 0xa1)
            .putAll("G", 0x2a, 0x22, 0xaa, 0xa2)
            .putAll("H", 0x2a, 0x23, 0xaa, 0xa3)
            .putAll("J", 0x2a, 0x24, 0xaa, 0xa4)
            .putAll("K", 0x2a, 0x25, 0xaa, 0xa5)
            .putAll("L", 0x2a, 0x26, 0xaa, 0xa6)

            .putAll(") ", 0x27, 0xa7)
            .putAll("\"", 0x2a, 0x28, 0xaa, 0xa8)
            .putAll("\"", 0x28, 0xa8)
            .putAll("\\", 0x2b, 0xab)
            .putAll("|", 0x2a, 0x2b, 0xaa, 0x8b)
            .putAll("[", 0x1a, 0x9a)
            .putAll("", 0x1b, 0x9b)
            .putAll("<", 0x2a, 0x33, 0xaa, 0xb3)
            .putAll(">", 0x2a, 0x34, 0xaa, 0xb4)
            .putAll("$", 0x2a, 0x05, 0xaa, 0x85)
            .putAll("+", 0x2a, 0x0d, 0xaa, 0x8d)
            
            .putAll("z", 0x2c, 0xac)
            .putAll("x", 0x2d, 0xad)
            .putAll("c", 0x2e, 0xae)
            .putAll("v", 0x2f, 0xaf)
            .putAll("b", 0x30, 0xb0)
            .putAll("n", 0x31, 0xb1)
            .putAll("m", 0x32, 0xb2)
            .putAll("Z", 0x2a, 0x2c, 0xaa, 0xac)
            .putAll("X", 0x2a, 0x2d, 0xaa, 0xad)
            .putAll("C", 0x2a, 0x2e, 0xaa, 0xae)
            .putAll("V", 0x2a, 0x2f, 0xaa, 0xaf)
            .putAll("B", 0x2a, 0x30, 0xaa, 0xb0)
            .putAll("N", 0x2a, 0x31, 0xaa, 0xb1)
            .putAll("M", 0x2a, 0x32, 0xaa, 0xb2)

            .putAll(",", 0x33, 0xb3)
            .putAll(".", 0x34, 0xb4)
            .putAll("/", 0x35, 0xb5)
            .putAll(":", 0x2a, 0x27, 0xaa, 0xa7)
            .putAll("%", 0x2a, 0x06, 0xaa, 0x86)
            .putAll("_", 0x2a, 0x0c, 0xaa, 0x8c)
            .putAll("&", 0x2a, 0x08, 0xaa, 0x88)
            .putAll("(", 0x2a, 0x0a, 0xaa, 0x8a)
            .putAll(")", 0x2a, 0x0b, 0xaa, 0x8b)
            .putAll("#", 0x2a, 0x04, 0xaa, 0x85)
            
            .build();


   public static final Multimap<String, Integer> SPECIAL_KEYBOARD_BUTTON_MAP_LIST = ImmutableMultimap
         .<String, Integer> builder()
         
            .putAll("<Enter>", 0x1c, 0x9c)
            .putAll("<Backspace>", 0x0e, 0x8e)
            .putAll("<Spacebar>", 0x39, 0xb9)
            .putAll("<Return>", 0x1c, 0x9c)
            .putAll("<Esc>", 0x01, 0x81)
            .putAll("<Tab>", 0x0f, 0x8f)
            .putAll("<KillX>", 0x1d, 0x38, 0x0e)

            .putAll("<Up>", 0x48, 0xc8)
            .putAll("<Down>", 0x50, 0xd0)
            .putAll("<PageUp>", 0x49, 0xc9)
            .putAll("<PageDown>", 0x51, 0xd1)
            .putAll("<End>", 0x4f, 0xcf)
            .putAll("<Insert>", 0x52, 0xd2)
            .putAll("<Delete>", 0x53, 0xd3)
            .putAll("<Left>", 0x4b, 0xcb)
            .putAll("<Right>", 0x4d, 0xcd)
            .putAll("<Home>", 0x47, 0xc7)

            .putAll("<F1>", 0x3b)
            .putAll("<F2>", 0x3c)
            .putAll("<F3>", 0x3d)
            .putAll("<F4>", 0x3e)
            .putAll("<F5>", 0x3f)
            .putAll("<F6>", 0x40)
            .putAll("<F7>", 0x41)
            .putAll("<F8>", 0x42)
            .putAll("<F9>", 0x43)
            .putAll("<F10>", 0x44)
            .build();

}
