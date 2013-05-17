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
package org.jclouds.ec2.binders;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.aws.util.AWSUtils.indexMapToFormValuesWithPrefix;

import java.util.Map;

import org.jclouds.http.HttpRequest;
import org.jclouds.rest.Binder;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

/**
 * @author Adrian Cole
 */
public class BindTagsToIndexedFormParams implements Binder {

   @SuppressWarnings("unchecked")
   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      checkNotNull(input, "tags");
      Map<String, String> tagValues;
      if (input instanceof Iterable) {
         Builder<String, String> builder = ImmutableMap.<String, String> builder();
         for (String key : (Iterable<String>) input) {
            builder.put(key, "");
         }
         tagValues = builder.build();
      } else if (input instanceof Map) {
         tagValues = Map.class.cast(input);
      } else {
         throw new IllegalArgumentException("This binder is only valid for Map<String,String> or Iterable<String>");
      }
      return indexMapToFormValuesWithPrefix(request, "Tag", "Key", "Value", tagValues);
   }

}
