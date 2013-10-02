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
package org.jclouds.openstack.nova.v2_0.functions.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import javax.inject.Singleton;

import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.json.internal.GsonWrapper;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;
import com.google.inject.TypeLiteral;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class OnlyMetadataValueOrNull implements Function<HttpResponse, String> {
   private final ParseJson<Wrapper> parser;

   private static class Wrapper implements Supplier<String> {
      private Map<String, String> metadata;

      @Override
      public String get() {
         return metadata == null ? null : Iterables.get(metadata.values(), 0, null);
      }

   }

   @Inject
   public OnlyMetadataValueOrNull(GsonWrapper gsonView) {
      this.parser = new ParseJson<Wrapper>(checkNotNull(gsonView, "gsonView"), new TypeLiteral<Wrapper>() {
      });
   }

   public String apply(HttpResponse response) {
      checkNotNull(response, "response");
      return parser.apply(response).get();
   }
}
