/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.util;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.io.Closeables.closeQuietly;
import static org.jclouds.util.Patterns.TOKEN_TO_PATTERN;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Charsets;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Multimap;
import com.google.common.io.CharStreams;
import com.google.common.io.InputSupplier;
import com.google.common.primitives.Chars;

/**
 * 
 * 
 * @author Adrian Cole
 */
public class Strings2 {

   /**
    * Web browsers do not always handle '+' characters well, use the well-supported '%20' instead.
    */
   public static String urlEncode(String in, char... skipEncode) {
      return urlEncode(in, Chars.asList(skipEncode));
   }

   public static String urlEncode(String in, Iterable<Character> skipEncode) {
      if (isUrlEncoded(in))
         return in;
      try {
         String returnVal = URLEncoder.encode(in, "UTF-8");
         returnVal = returnVal.replace("+", "%20");
         returnVal = returnVal.replace("*", "%2A");
         for (char c : skipEncode) {
            returnVal = returnVal.replace(CHAR_TO_ENCODED.get(c), c + "");
         }
         return returnVal;
      } catch (UnsupportedEncodingException e) {
         throw new IllegalStateException("Bad encoding on input: " + in, e);
      } catch (ExecutionException e) {
         throw new IllegalStateException("error creating pattern: " + in, e);
      }
   }
   
   private static final LoadingCache<Character, String> CHAR_TO_ENCODED = CacheBuilder.newBuilder()
         .<Character, String> build(new CacheLoader<Character, String>() {
            @Override
            public String load(Character plain) throws ExecutionException {
               try {
                  return URLEncoder.encode(plain + "", "UTF-8");
               } catch (UnsupportedEncodingException e) {
                  throw new ExecutionException("Bad encoding on input: " + plain, e);
               }
            }
         });
   
   private static final String IP_ADDRESS = "(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})";
   private static final String SLASH_FORMAT = IP_ADDRESS + "/(\\d{1,3})";
   private static final Pattern ADDRESS_PATTERN = Pattern.compile(IP_ADDRESS);
   private static final Pattern CIDR_PATTERN = Pattern.compile(SLASH_FORMAT);

   public static boolean isCidrFormat(String in) {
      return CIDR_PATTERN.matcher(in).matches();
   }
      
   private static final Pattern URL_ENCODED_PATTERN = Pattern.compile(".*%[a-fA-F0-9][a-fA-F0-9].*");

   public static boolean isUrlEncoded(String in) {
      return URL_ENCODED_PATTERN.matcher(in).matches();
   }

   /**
    * url decodes the input param, if set.
    * 
    * @param in
    *           nullable
    * @return null if input was null
    * @throws IllegalStateException
    *            if encoding isn't {@code UTF-8}
    */
   public static String urlDecode(@Nullable String in) {
      if (in == null)
         return null;
      String input = in.toString();
      // Don't double decode
      if (!isUrlEncoded(input)) {
         return input;
      }
      try {
         return URLDecoder.decode(input, "UTF-8");
      } catch (UnsupportedEncodingException e) {
         throw new IllegalStateException("Bad encoding on input: " + in, e);
      }
   }

   public static String toString(InputSupplier<? extends InputStream> supplier)
         throws IOException {
      return CharStreams.toString(CharStreams.newReaderSupplier(supplier,
         Charsets.UTF_8));
   }

   public static String toStringAndClose(InputStream input) throws IOException {
      checkNotNull(input, "input");
      try {
         return CharStreams.toString(new InputStreamReader(input, Charsets.UTF_8));
      } finally {
         closeQuietly(input);
      }
   }

   public static InputStream toInputStream(String in) {
      return new ByteArrayInputStream(in.getBytes(Charsets.UTF_8));
   }

   /**
    * replaces tokens that are expressed as <code>{token}</code>
    * 
    * <p/>
    * ex. if input is "hello {where}"<br/>
    * and replacements is "where" -> "world" <br/>
    * then replaceTokens returns "hello world"
    * 
    * @param input
    *           source to replace
    * @param replacements
    *           token/value pairs
    */
   public static String replaceTokens(String input, Map<String, String> replacements) {
      Matcher matcher = TOKEN_PATTERN.matcher(input);
      StringBuilder builder = new StringBuilder();
      int i = 0;
      while (matcher.find()) {
         String replacement = replacements.get(matcher.group(1));
         builder.append(input.substring(i, matcher.start()));
         if (replacement == null)
            builder.append(matcher.group(0));
         else
            builder.append(replacement);
         i = matcher.end();
      }
      builder.append(input.substring(i, input.length()));
      return builder.toString();
   }
   
   private static final Pattern TOKEN_PATTERN = Pattern.compile("\\{(.+?)\\}");

   public static String replaceTokens(String input, Multimap<String, ?> tokenValues) {
      for (Entry<String, ?> tokenValue : tokenValues.entries()) {
         Pattern pattern = TOKEN_TO_PATTERN.getUnchecked(tokenValue.getKey());
         input = pattern.matcher(input).replaceAll(tokenValue.getValue().toString());
      }
      return input;
   }
}
