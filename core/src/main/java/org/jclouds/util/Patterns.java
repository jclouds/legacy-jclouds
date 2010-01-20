/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.regex.Pattern;

import com.google.common.base.Function;
import com.google.common.collect.MapMaker;

/**
 * 
 * @author Adrian Cole
 */
public class Patterns {
   public static final Pattern TWO_SPACE_PATTERN = Pattern.compile("  ");
   public static final Pattern URL_ENCODED_PATTERN = Pattern.compile(".*%[a-fA-F0-9][a-fA-F0-9].*");
   public static final Pattern URI_PATTERN = Pattern.compile("([a-z0-9]+)://([^:]*):(.*)@(.*)");
   public static final Pattern PATTERN_THAT_BREAKS_URI = Pattern.compile("[a-z0-9]+://.*/.*@.*");
   public static final Pattern PLUS_PATTERN = Pattern.compile("\\+");
   public static final Pattern STAR_PATTERN = Pattern.compile("\\*");
   public static final Pattern _7E_PATTERN = Pattern.compile("%7E");
   public static final Pattern NEWLINE_PATTERN = Pattern.compile("\r?\n");
   public static final Pattern SLASH_PATTERN = Pattern.compile("[/]");
   public static final Pattern IP_PATTERN = Pattern
            .compile("b(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?).)"
                     + "{3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)b");
   public static final Pattern LEADING_SLASHES = Pattern.compile("^[/]+");
   public static final Pattern TRAILING_SLASHES = Pattern.compile("[/]*$");

   public final static Map<Character, Pattern> CHAR_TO_ENCODED_PATTERN = new MapMaker()
            .makeComputingMap(new Function<Character, Pattern>() {
               public Pattern apply(Character plain) {
                  try {
                     String encoded = URLEncoder.encode(plain + "", "UTF-8");
                     return Pattern.compile(encoded);
                  } catch (UnsupportedEncodingException e) {
                     throw new IllegalStateException("Bad encoding on input: " + plain, e);
                  }
               }
            });

   public final static Map<Character, Pattern> CHAR_TO_PATTERN = new MapMaker()
            .makeComputingMap(new Function<Character, Pattern>() {
               public Pattern apply(Character plain) {
                  return Pattern.compile(plain + "");
               }
            });

   public final static Map<String, Pattern> TOKEN_TO_PATTERN = new MapMaker()
            .makeComputingMap(new Function<String, Pattern>() {
               public Pattern apply(String tokenValue) {
                  return Pattern.compile("\\{" + tokenValue + "\\}");
               }
            });
}
