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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * 
 * @author Adrian Cole
 */
public class Patterns {
   public static final Pattern TOKEN_PATTERN = Pattern.compile("\\{(.+?)\\}");
   public static final Pattern TWO_SPACE_PATTERN = Pattern.compile("  ");
   public static final Pattern URL_ENCODED_PATTERN = Pattern.compile(".*%[a-fA-F0-9][a-fA-F0-9].*");
   public static final Pattern URI_PATTERN = Pattern.compile("([a-z0-9]+)://([^:]*):(.*)@(.*)");
   public static final Pattern PATTERN_THAT_BREAKS_URI = Pattern.compile("[a-z0-9]+://.*/.*@.*");
   public static final Pattern JSON_STRING_PATTERN = Pattern.compile("^[^\"\\{\\[].*[^\\{\\[\"]$");
   public static final Pattern JSON_NUMBER_PATTERN = Pattern.compile("^[0-9]*\\.?[0-9]*$");
   public static final Pattern JSON_BOOLEAN_PATTERN = Pattern.compile("^(true|false)$");
   public static final Pattern PLUS_PATTERN = Pattern.compile("\\+");
   public static final Pattern STAR_PATTERN = Pattern.compile("\\*");
   public static final Pattern _7E_PATTERN = Pattern.compile("%7E");
   public static final Pattern NEWLINE_PATTERN = Pattern.compile("\r?\n");
   public static final Pattern SLASH_PATTERN = Pattern.compile("[/]");
   public static final Pattern IP_PATTERN = Pattern.compile("b(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?).)"
         + "{3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)b");
   public static final Pattern LEADING_SLASHES = Pattern.compile("^[/]+");
   public static final Pattern TRAILING_SLASHES = Pattern.compile("[/]*$");
   public static final Pattern REST_CONTEXT_BUILDER = Pattern.compile("(.*ContextBuilder)<([^,]+), ?([^>]+)>");

   public static final LoadingCache<Character, Pattern> CHAR_TO_ENCODED_PATTERN = CacheBuilder.newBuilder()
         .<Character, Pattern> build(new CacheLoader<Character, Pattern>() {
            @Override
            public Pattern load(Character plain) throws ExecutionException {
               try {
                  String encoded = URLEncoder.encode(plain + "", "UTF-8");
                  return Pattern.compile(encoded);
               } catch (UnsupportedEncodingException e) {
                  throw new ExecutionException("Bad encoding on input: " + plain, e);
               }
            }
         });

   public static final LoadingCache<Character, Pattern> CHAR_TO_PATTERN = CacheBuilder.newBuilder()
         .<Character, Pattern> build(new CacheLoader<Character, Pattern>() {
            @Override
            public Pattern load(Character plain) {
               return Pattern.compile(plain + "");
            }
         });

   public static final LoadingCache<String, Pattern> TOKEN_TO_PATTERN = CacheBuilder.newBuilder()
         .<String, Pattern> build(new CacheLoader<String, Pattern>() {
            @Override
            public Pattern load(String tokenValue) {
               return Pattern.compile("\\{" + tokenValue + "\\}");
            }
         });
}
