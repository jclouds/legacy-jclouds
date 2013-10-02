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

import java.util.Map;

import org.xml.sax.Attributes;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

/**
 * 
 * @author Adrian Cole
 */
public class SaxUtils {

   public static boolean equalsOrSuffix(String val, String expected) {
      return expected.equals(val) || val.endsWith(":" + expected);
   }

   public static Map<String, String> cleanseAttributes(Attributes in) {
      Builder<String, String> attrs = ImmutableMap.builder();
      for (int i = 0; i < in.getLength(); i++) {
         String name = in.getQName(i);
         if (name.indexOf(':') != -1)
            name = name.substring(name.indexOf(':') + 1);
         attrs.put(name, in.getValue(i));
      }
      return attrs.build();
   }

   public static String currentOrNull(StringBuilder currentText) {
      String returnVal = currentText.toString().trim();
      return returnVal.equals("") ? null : returnVal;
   }

}
