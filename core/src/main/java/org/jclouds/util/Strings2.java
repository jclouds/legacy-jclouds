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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.io.ByteStreams.toByteArray;
import static com.google.common.io.Closeables.closeQuietly;
import static org.jclouds.util.Patterns.CHAR_TO_ENCODED_PATTERN;
import static org.jclouds.util.Patterns.CHAR_TO_PATTERN;
import static org.jclouds.util.Patterns.PLUS_PATTERN;
import static org.jclouds.util.Patterns.STAR_PATTERN;
import static org.jclouds.util.Patterns.TOKEN_TO_PATTERN;
import static org.jclouds.util.Patterns.URL_ENCODED_PATTERN;
import static org.jclouds.util.Patterns._7E_PATTERN;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.common.io.InputSupplier;

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
      if (isUrlEncoded(in))
         return in;
      try {
         String returnVal = URLEncoder.encode(in, "UTF-8");
         returnVal = Strings2.replaceAll(returnVal, '+', PLUS_PATTERN, "%20");
         returnVal = Strings2.replaceAll(returnVal, '*', STAR_PATTERN, "%2A");
         returnVal = Strings2.replaceAll(returnVal, _7E_PATTERN, "~");
         for (char c : skipEncode) {
            returnVal = Strings2.replaceAll(returnVal, CHAR_TO_ENCODED_PATTERN.get(c), c + "");
         }
         return returnVal;
      } catch (UnsupportedEncodingException e) {
         throw new IllegalStateException("Bad encoding on input: " + in, e);
      } catch (ExecutionException e) {
         throw new IllegalStateException("error creating pattern: " + in, e);
      }
   }

   public static boolean isUrlEncoded(String in) {
      return URL_ENCODED_PATTERN.matcher(in).matches();
   }

   public static String urlDecode(String in) {
      try {
         return URLDecoder.decode(in, "UTF-8");
      } catch (UnsupportedEncodingException e) {
         throw new IllegalStateException("Bad encoding on input: " + in, e);
      }
   }

   public static String replaceTokens(String value, Iterable<Entry<String, String>> tokenValues) {
      for (Entry<String, String> tokenValue : tokenValues) {
         try {
            value = Strings2.replaceAll(value, TOKEN_TO_PATTERN.get(tokenValue.getKey()), tokenValue.getValue());
         } catch (ExecutionException e) {
            throw new IllegalStateException("error creating pattern: " + tokenValue.getKey(), e);
         }
      }
      return value;
   }

   public static String replaceAll(String returnVal, Pattern pattern, String replace) {
      Matcher m = pattern.matcher(returnVal);
      returnVal = m.replaceAll(replace);
      return returnVal;
   }

   public static String replaceAll(String input, char ifMatch, Pattern pattern, String replacement) {
      if (input.indexOf(ifMatch) != -1) {
         input = pattern.matcher(input).replaceAll(replacement);
      }
      return input;
   }

   public static String replaceAll(String input, char match, String replacement) {
      if (input.indexOf(match) != -1) {
         try {
            input = CHAR_TO_PATTERN.get(match).matcher(input).replaceAll(replacement);
         } catch (ExecutionException e) {
            throw new IllegalStateException("error creating pattern: " + match, e);
         }
      }
      return input;
   }

   public static String toString(InputSupplier<? extends InputStream> supplier)
         throws IOException {
      return CharStreams.toString(CharStreams.newReaderSupplier(supplier,
         Charsets.UTF_8));
   }

   public static String toStringAndClose(InputStream input) throws IOException {
      checkNotNull(input, "input");
      try {
         return new String(toByteArray(input), Charsets.UTF_8);
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
      Matcher matcher = Patterns.TOKEN_PATTERN.matcher(input);
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

}
