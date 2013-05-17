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
package org.jclouds.dynect.v3.functions;

import static com.google.common.base.Preconditions.checkState;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

/**
 * Zones, Geo Services, and Geo region groups come back encoded in REST paths, such as
 * {@code /REST/Geo/srv-global/}
 * 
 * @author Adrian Cole
 * 
 */
public final class ExtractLastPathComponent implements Function<FluentIterable<String>, FluentIterable<String>> {
   public FluentIterable<String> apply(FluentIterable<String> in) {
      return in.transform(ExtractNameInPath.INSTANCE);
   }

   static enum ExtractNameInPath implements Function<String, String> {
      INSTANCE;
      public static final Pattern DEFAULT_PATTERN = Pattern.compile("/REST.*/([^/]+)/?$");

      public String apply(String in) {
         Matcher matcher = DEFAULT_PATTERN.matcher(in);
         checkState(matcher.find() && matcher.groupCount() == 1, "%s didn't match %s", in, DEFAULT_PATTERN);
         return matcher.group(1);
      }
   }
}
