/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.json;

import static org.testng.Assert.assertEquals;

import java.util.Set;

import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.UnwrapOnlyNestedJsonValue;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSortedSet;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.util.Types;

/**
 * 
 * @author Adrian Cole
 */
public abstract class BaseSetParserTest<T> extends BaseParserTest<Set<T>, T> {

   @SuppressWarnings("unchecked")
   // crazy stuff due to type erasure
   @Override
   protected Function<HttpResponse, Set<T>> getParser(Injector i) {
      return (Function<HttpResponse, Set<T>>) i.getInstance(Key.get(TypeLiteral.get(
               Types.newParameterizedType(UnwrapOnlyNestedJsonValue.class, Types
                        .newParameterizedType(Set.class, type()))).getType()));
   }

   public void compare(Set<T> expects, Set<T> response) {
      assertEquals(ImmutableSortedSet.copyOf(response).toString(), ImmutableSortedSet.copyOf(expects).toString());
   }

}
