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
package org.jclouds.aws.binders;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;

import java.util.Map;
import java.util.Map.Entry;

import org.jclouds.http.HttpRequest;
import org.jclouds.rest.Binder;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

/**
 * @author Adrian Cole
 */
public class BindMapToIndexedFormParams implements Binder {

   private final String keyPattern;
   private final String valuePattern;

   protected BindMapToIndexedFormParams(String keyPattern, String valuePattern) {
      this.keyPattern = keyPattern;
      this.valuePattern = valuePattern;
   }

   @SuppressWarnings("unchecked")
   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      if (checkNotNull(input, "input") instanceof Iterable)
         input = Maps.uniqueIndex((Iterable<String>) input, new Function<String, String>() {
            int index = 1;

            @Override
            public String apply(String input) {
               return index++ + "";
            }
         });
      checkArgument(checkNotNull(input, "input") instanceof Map, "this binder is only valid for Map");
      Map<String, String> mapping = (Map<String, String>) input;

      ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
      int amazonOneBasedIndex = 1; // according to docs, counters must start
                                   // with 1
      for (Entry<String, String> entry : mapping.entrySet()) {
         // not null by contract
         builder.put(format(keyPattern, amazonOneBasedIndex), entry.getKey());
         builder.put(format(valuePattern, amazonOneBasedIndex), entry.getValue());
         amazonOneBasedIndex++;
      }
      Multimap<String, String> forms = Multimaps.forMap(builder.build());
      return forms.size() == 0 ? request : (R) request.toBuilder().replaceFormParams(forms).build();
   }

}
