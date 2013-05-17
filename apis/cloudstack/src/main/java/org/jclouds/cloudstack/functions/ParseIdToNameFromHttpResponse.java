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

import java.util.Map;
import java.util.Set;

import javax.inject.Singleton;

import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseFirstJsonValueNamed;
import org.jclouds.json.internal.GsonWrapper;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.inject.Inject;
import com.google.inject.TypeLiteral;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class ParseIdToNameFromHttpResponse implements Function<HttpResponse, Map<String, String>> {
   private final ParseFirstJsonValueNamed<Set<IdName>> parser;

   private static class IdName {
      private String id;
      private String name;

       @Override
       public boolean equals(Object o) {
           if (this == o) return true;
           if (o == null || getClass() != o.getClass()) return false;
           
           IdName that = (IdName) o;
           
           if (!Objects.equal(id, that.id)) return false;
           if (!Objects.equal(name, that.name)) return false;
           
           return true;
       }
       
       @Override
       public int hashCode() {
           return Objects.hashCode(id, name);
       }
   }

   @Inject
   public ParseIdToNameFromHttpResponse(GsonWrapper gsonView) {
      this.parser = new ParseFirstJsonValueNamed<Set<IdName>>(checkNotNull(gsonView, "gsonView"),
            new TypeLiteral<Set<IdName>>() {
            }, "oscategory");
   }

   public Map<String, String> apply(HttpResponse response) {
      checkNotNull(response, "response");
      Set<IdName> toParse = parser.apply(response);
      checkNotNull(toParse, "parsed result from %s", response);
      Builder<String, String> builder = ImmutableSortedMap.naturalOrder();
      for (IdName entry : toParse)
         builder.put(entry.id, entry.name);
      return builder.build();
   }
}
