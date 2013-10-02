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
package org.jclouds.http.utils;

import static org.jclouds.util.Strings2.urlDecode;
import static org.jclouds.util.Strings2.urlEncode;

import java.util.Map.Entry;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

/**
 * 
 * @author Adrian Cole
 */
public class Queries {

   private static final Function<String, Multimap<String, String>> parseQueryToDecodedMap = new Function<String, Multimap<String, String>>() {
      @Override
      public Multimap<String, String> apply(String in) {
         // some query values are null, which aren't permitted by Immutable*
         Multimap<String, String> map = LinkedListMultimap.create();
         if (in == null) {
         } else if (in.indexOf('&') == -1) {
            if (in.indexOf('=') != -1)
               parseKeyValueFromStringToDecodedMap(in, map);
            else
               map.put(in, null);
         } else {
            for (String part : Splitter.on('&').split(in)) {
               parseKeyValueFromStringToDecodedMap(part, map);
            }
         }
         return map;
      }
   };

   public static Function<String, Multimap<String, String>> queryParser() {
      return parseQueryToDecodedMap;
   }

   private static void parseKeyValueFromStringToDecodedMap(String stringToParse, Multimap<String, String> map) {
      // note that '=' can be a valid part of the value
      int indexOfFirstEquals = stringToParse.indexOf('=');
      String key = indexOfFirstEquals == -1 ? stringToParse : stringToParse.substring(0, indexOfFirstEquals);
      String value = indexOfFirstEquals == -1 ? null : stringToParse.substring(indexOfFirstEquals + 1);
      map.put(urlDecode(key), urlDecode(value));
   }

   /**
    * percent encodes the query parameters, excep {@code /} and {@code ,} characters.
    * 
    * @param queryParams
    * @return percent encoded line or null if no queryParams present
    */
   public static String encodeQueryLine(Multimap<String, ?> queryParams) {
      if (queryParams.isEmpty())
         return null;
      return buildQueryLine(queryParams, new EncodeAndAppendParam());
   }
   
   /**
    * percent encodes the query parameters according except characters specified in the {@code skips} argument.
    * 
    * @param queryParams
    * @return percent encoded line or null if no queryParams present
    */
   public static String encodeQueryLine(Multimap<String, ?> queryParams, Iterable<Character> skips) {
      if (queryParams.isEmpty())
         return null;
      return buildQueryLine(queryParams, new EncodeAndAppendParam(skips));
   }

   public static String buildQueryLine(Multimap<String, ?> queryParams) {
      if (queryParams.isEmpty())
         return null;
      return buildQueryLine(queryParams, new AppendParam());
   }

   private static String buildQueryLine(Multimap<String, ?> queryParams, AppendParam appendParam) {
      StringBuilder queryBuilder = appendParam.b;
      for (Entry<String, ?> pair : queryParams.entries()) {
         queryBuilder.append('&');
         appendParam.appendKey(pair.getKey());
         if (pair.getValue() != null)
            queryBuilder.append('=');
         if (pair.getValue() != null && !pair.getValue().equals("")) {
            appendParam.appendValue(pair.getValue());
         }
      }
      queryBuilder.deleteCharAt(0);
      return queryBuilder.toString();
   }

   private static class AppendParam {
      final StringBuilder b;

      private AppendParam() {
         this.b = new StringBuilder();
      }

      private void appendKey(String key) {
         append(key);
      }

      private void appendValue(Object val) {
         append(val);
      }

      void append(Object in) {
         b.append(in.toString());
      }
   }

   private static class EncodeAndAppendParam extends AppendParam {
      private Iterable<Character> skips;

      private EncodeAndAppendParam() {
         this(ImmutableList.of('/', ','));
      }

      private EncodeAndAppendParam(Iterable<Character> skips) {
         this.skips = skips;
      }

      @Override
      void append(Object in) {
         super.append(urlEncode(in.toString(), skips));
      }
   }

}
