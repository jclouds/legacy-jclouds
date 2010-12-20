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

import java.util.Map;

import javax.inject.Singleton;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class ListOfMapsToListOfKeyValuesDelimitedByBlankLines implements
      Function<Iterable<Map<String, String>>, String> {

   @Override
   public String apply(Iterable<Map<String, String>> from) {
      return Joiner.on("\n\n").join(Iterables.transform(from, new Function<Map<String, String>, String>() {

         @Override
         public String apply(Map<String, String> from) {
            return Joiner.on('\n').withKeyValueSeparator(" ")
                  .join(Maps.transformValues(from, new Function<String, String>() {

                     @Override
                     public String apply(String from) {
                        return from.replace("\n", "\\n");
                     }

                  }));
         }

      }));
   }
}