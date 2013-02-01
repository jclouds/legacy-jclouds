/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, String 2.0 (the
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
package org.jclouds.dynect.v3.functions;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;

/**
 * Zones come back encoded in REST paths, such as
 * {@code /REST/Zone/jclouds.org/}
 * 
 * @author Adrian Cole
 * 
 */
public final class ExtractNames implements Function<FluentIterable<String>, FluentIterable<String>> {
   public FluentIterable<String> apply(FluentIterable<String> in) {
      return in.transform(ExtractNameInPath.INSTANCE);
   }

   static enum ExtractNameInPath implements Function<String, String> {
      INSTANCE;

      final int position = "/REST/Zone/".length();

      public String apply(String in) {
         return in.substring(position, in.length() - 1);
      }
   }
}