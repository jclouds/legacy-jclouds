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

package org.jclouds.cloudsigma.functions;

import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ReturnStringIf2xx;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class SplitNewlinesAndReturnSecondField extends SplitNewlines {

   @Inject
   SplitNewlinesAndReturnSecondField(ReturnStringIf2xx returnStringIf200) {
      super(returnStringIf200);
   }

   @Override
   public Set<String> apply(HttpResponse response) {
      return ImmutableSet.copyOf(Iterables.filter(
            Iterables.transform(super.apply(response), new Function<String, String>() {

               @Override
               public String apply(String arg0) {
                  if (arg0 == null)
                     return null;
                  Iterable<String> parts = Splitter.on(' ').split(arg0);
                  if (Iterables.size(parts) == 2)
                     return Iterables.get(parts, 1);
                  else if (Iterables.size(parts) == 1)
                     return Iterables.get(parts, 0);
                  return null;
               }

            }), Predicates.notNull()));
   }
}