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

import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.UnwrapOnlyNestedJsonValue;
import org.jclouds.io.Payloads;
import org.jclouds.json.config.GsonModule;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.util.Types;

/**
 * 
 * @author Adrian Cole
 */
public abstract class BaseParserTest<T, G> {

   @Test
   public void test() {

      T expects = expected();

      Function<HttpResponse, T> parser = getParser(getInjector());
      T response = parser.apply(new HttpResponse(200, "ok", Payloads.newInputStreamPayload(getClass()
               .getResourceAsStream(resource()))));
      compare(expects, response);
   }

   public void compare(T expects, T response) {
      assertEquals(response.toString(), expects.toString());
   }

   protected Injector getInjector() {
      return Guice.createInjector(new GsonModule() {

         @Override
         protected void configure() {
            bind(DateAdapter.class).to(Iso8601DateAdapter.class);
            super.configure();
         }

      });

   }

   @SuppressWarnings("unchecked")
   protected Function<HttpResponse, T> getParser(Injector i) {
      return (Function<HttpResponse, T>) i.getInstance(Key.get(TypeLiteral.get(
               Types.newParameterizedType(UnwrapOnlyNestedJsonValue.class, type())).getType()));
   }

   public abstract Class<G> type();

   public abstract String resource();

   public abstract T expected();
}
