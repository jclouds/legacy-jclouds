/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.cloudstack.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import javax.inject.Singleton;

import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.UnwrapOnlyNestedJsonValue;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.inject.Inject;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class ParseNamesFromHttpResponse implements Function<HttpResponse, Set<String>> {
   private final UnwrapOnlyNestedJsonValue<Set<Name>> parser;

   private static class Name {
      private String name;

      @Override
      public int hashCode() {
         final int prime = 31;
         int result = 1;
         result = prime * result + ((name == null) ? 0 : name.hashCode());
         return result;
      }

      @Override
      public boolean equals(Object obj) {
         if (this == obj)
            return true;
         if (obj == null)
            return false;
         if (getClass() != obj.getClass())
            return false;
         Name other = (Name) obj;
         if (name == null) {
            if (other.name != null)
               return false;
         } else if (!name.equals(other.name))
            return false;
         return true;
      }

   }

   @Inject
   public ParseNamesFromHttpResponse(UnwrapOnlyNestedJsonValue<Set<Name>> parser) {
      this.parser = checkNotNull(parser, "parser");
   }

   public Set<String> apply(HttpResponse response) {
      checkNotNull(response, "response");
      Set<Name> toParse = parser.apply(response);
      checkNotNull(toParse, "parsed result from %s", response);
      Builder<String> builder = ImmutableSet.<String> builder();
      for (Name entry : toParse)
         builder.add(entry.name);
      return builder.build();
   }
}
