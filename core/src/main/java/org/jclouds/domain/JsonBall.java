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
package org.jclouds.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.regex.Pattern;

/**
 * 
 * As String is final, using a different marker to imply this is a json object
 * 
 * @author Adrian Cole
 * @see <a href="http://code.google.com/p/google-gson/issues/detail?id=326"/>
 */
public class JsonBall implements Comparable<String>, CharSequence {
   
   public static final Pattern JSON_STRING_PATTERN = Pattern.compile("^[^\"\\{\\[].*[^\\{\\[\"]$");
   public static final Pattern JSON_NUMBER_PATTERN = Pattern.compile("^[0-9]*\\.?[0-9]*$");
   public static final Pattern JSON_BOOLEAN_PATTERN = Pattern.compile("^(true|false)$");
   
   private final String value;

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      JsonBall other = (JsonBall) obj;
      if (value == null) {
         if (other.value != null)
            return false;
      } else if (!value.equals(other.value))
         return false;
      return true;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((value == null) ? 0 : value.hashCode());
      return result;
   }

   @Override
   public String toString() {
      return value;
   }

   public JsonBall(double value) {
      this.value = value + "";
   }

   public JsonBall(int value) {
      this.value = value + "";
   }

   public JsonBall(long value) {
      this.value = value + "";
   }
   
   public JsonBall(boolean value) {
      this.value = value + "";
   }

   public JsonBall(String value) {
      this.value = quoteStringIfNotNumberOrBoolean(checkNotNull(value, "value"));
   }

   private static String quoteStringIfNotNumberOrBoolean(String in) {
      if (JSON_STRING_PATTERN.matcher(in).find() && !JSON_NUMBER_PATTERN.matcher(in).find()
            && !JSON_BOOLEAN_PATTERN.matcher(in).find()) {
         return "\"" + in + "\"";
      }
      return in;
   }

   @Override
   public char charAt(int index) {
      return value.charAt(index);
   }

   @Override
   public int length() {
      return value.length();
   }

   @Override
   public CharSequence subSequence(int start, int end) {
      return value.subSequence(start, end);
   }

   @Override
   public int compareTo(String o) {
      return value.compareTo(o);
   }

}
