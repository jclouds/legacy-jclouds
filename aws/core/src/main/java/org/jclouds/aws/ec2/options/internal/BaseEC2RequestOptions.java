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

package org.jclouds.aws.ec2.options.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import org.jclouds.http.options.BaseHttpRequestOptions;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * 
 * @author Adrian Cole
 */
public class BaseEC2RequestOptions extends BaseHttpRequestOptions {

   @Override
   public String toString() {
      return "[formParameters=" + formParameters + "]";
   }

   protected void indexFormValuesWithPrefix(String prefix, String... values) {
      for (int i = 0; i < values.length; i++) {
         formParameters.put(prefix + "." + (i + 1), checkNotNull(values[i], prefix.toLowerCase() + "s[" + i + "]"));
      }
   }

   protected void indexFormValuesWithPrefix(String prefix, Iterable<String> values) {
      indexFormValuesWithPrefix(prefix, Iterables.toArray(values, String.class));
   }

   protected Set<String> getFormValuesWithKeysPrefixedBy(final String prefix) {
      Set<String> values = Sets.newLinkedHashSet();
      for (String key : Iterables.filter(formParameters.keySet(), new Predicate<String>() {

         public boolean apply(String input) {
            return input.startsWith(prefix);
         }

      })) {
         values.add(formParameters.get(key).iterator().next());

      }
      return values;
   }

}