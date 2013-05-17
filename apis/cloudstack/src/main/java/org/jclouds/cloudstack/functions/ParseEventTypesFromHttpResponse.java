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
package org.jclouds.cloudstack.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import javax.inject.Singleton;

import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseFirstJsonValueNamed;
import org.jclouds.json.internal.GsonWrapper;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.inject.Inject;
import com.google.inject.TypeLiteral;

/**
 * @author Vijay Kiran
 */
@Singleton
public class ParseEventTypesFromHttpResponse implements Function<HttpResponse, Set<String>> {
   private final ParseFirstJsonValueNamed<Set<EventType>> parser;

   private static class EventType {
      private String name;

       @Override
       public boolean equals(Object o) {
           if (this == o) return true;
           if (o == null || getClass() != o.getClass()) return false;
           
           EventType that = (EventType) o;
           
           if (!Objects.equal(name, that.name)) return false;
           
           return true;
       }
       
       @Override
       public int hashCode() {
           return Objects.hashCode(name);
       }
   }

   @Inject
   public ParseEventTypesFromHttpResponse(GsonWrapper gsonView) {
      this.parser = new ParseFirstJsonValueNamed<Set<EventType>>(checkNotNull(gsonView, "gsonView"),
            new TypeLiteral<Set<EventType>>() {
            }, "eventtype");
   }

   public Set<String> apply(HttpResponse response) {
      checkNotNull(response, "response");
      Set<EventType> toParse = parser.apply(response);
      checkNotNull(toParse, "parsed result from %s", response);
      Builder<String> builder = ImmutableSet.builder();
      for (EventType entry : toParse)
         builder.add(entry.name);
      return builder.build();
   }
}


