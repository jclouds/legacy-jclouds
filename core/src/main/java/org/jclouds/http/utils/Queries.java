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
package org.jclouds.http.utils;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.util.Strings2;

import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

/**
 * 
 * @author Adrian Cole
 */
public class Queries {

   public static Multimap<String, String> parseQueryToMap(String in) {
      Multimap<String, String> map = LinkedListMultimap.create();
      if (in == null) {
      } else if (in.indexOf('&') == -1) {
         if (in.contains("="))
            parseKeyValueFromStringToMap(in, map);
         else
            map.put(in, null);
      } else {
         String[] parts = in.split("&");
         for (String part : parts) {
            parseKeyValueFromStringToMap(part, map);
         }
      }
      return map;
   }

   public static void parseKeyValueFromStringToMap(String stringToParse, Multimap<String, String> map) {
      // note that '=' can be a valid part of the value
      int indexOfFirstEquals = stringToParse.indexOf('=');
      String key = indexOfFirstEquals == -1 ? stringToParse : stringToParse.substring(0, indexOfFirstEquals);
      String value = indexOfFirstEquals == -1 ? null : stringToParse.substring(indexOfFirstEquals + 1);
      map.put(Strings2.urlDecode(key), Strings2.urlDecode(value));
   }

   public static String makeQueryLine(Multimap<String, String> params,
            @Nullable Comparator<Map.Entry<String, String>> sorter, char... skips) {
      Iterator<Map.Entry<String, String>> pairs = ((sorter == null) ? params.entries() : ImmutableSortedSet.copyOf(
               sorter, params.entries())).iterator();
      StringBuilder formBuilder = new StringBuilder();
      while (pairs.hasNext()) {
         Map.Entry<String, String> pair = pairs.next();
         formBuilder.append(Strings2.urlEncode(pair.getKey(), skips));
         if (pair.getValue() != null)
            formBuilder.append("=");
         if (pair.getValue() != null && !pair.getValue().equals("")) {
            formBuilder.append(Strings2.urlEncode(pair.getValue(), skips));
         }
         if (pairs.hasNext())
            formBuilder.append("&");
      }
      return formBuilder.toString();
   }
}
