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
package org.jclouds.rest;

import static org.jclouds.rest.RestContextFactory.createContextBuilder;

import java.io.IOException;
import java.util.Properties;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.logging.config.NullLoggingModule;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public abstract class RestClientTest<T> extends BaseRestClientTest {

   protected RestAnnotationProcessor<T> processor;

   protected abstract TypeLiteral<RestAnnotationProcessor<T>> createTypeLiteral();

   protected abstract void checkFilters(HttpRequest request);

   abstract public RestContextSpec<?, ?> createContextSpec();

   protected Module createModule() {
      return new Module() {

         @Override
         public void configure(Binder binder) {

         }

      };
   }

   @BeforeClass
   protected void setupFactory() throws IOException {
      RestContextSpec<?, ?> contextSpec = createContextSpec();
      injector = createContextBuilder(contextSpec,
            ImmutableSet.of(new MockModule(), new NullLoggingModule(), createModule()), getProperties())
            .buildInjector();
      parserFactory = injector.getInstance(ParseSax.Factory.class);
      processor = injector.getInstance(Key.get(createTypeLiteral()));
   }

   protected Properties getProperties() {
      return new Properties();
   }
}