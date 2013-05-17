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
package org.jclouds.rest.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.Properties;

import org.jclouds.ContextBuilder;
import org.jclouds.apis.ApiMetadata;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.logging.config.NullLoggingModule;
import org.jclouds.providers.ProviderMetadata;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Binder;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public abstract class BaseAsyncApiTest<T> extends BaseRestApiTest {

   protected RestAnnotationProcessor processor;

   protected abstract void checkFilters(HttpRequest request);

   protected Module createModule() {
      return new Module() {

         @Override
         public void configure(Binder binder) {

         }

      };
   }

   @BeforeClass
   protected void setupFactory() throws IOException {
      injector = createInjector();
      parserFactory = injector.getInstance(ParseSax.Factory.class);
      processor = injector.getInstance(RestAnnotationProcessor.class);
   }

   protected String identity = "identity";
   protected String credential = "credential";

   /**
    * @see org.jclouds.providers.Providers#withId
    */
   protected ProviderMetadata createProviderMetadata() {
      return null;
   }

   /**
    * @see org.jclouds.apis.Apis#withId
    */
   protected ApiMetadata createApiMetadata() {
      return null;
   }

   protected Injector createInjector() {
      ProviderMetadata pm = createProviderMetadata();

      ContextBuilder builder = pm != null ? ContextBuilder.newBuilder(pm) : ContextBuilder.newBuilder(ApiMetadata.class
            .cast(checkNotNull(createApiMetadata(),
                  "either createApiMetadata or createProviderMetadata must be overridden")));

      return builder.credentials(identity, credential)
            .modules(ImmutableSet.of(new MockModule(), new NullLoggingModule(), createModule()))
            .overrides(setupProperties()).buildInjector();
   }

   /**
    * override this to supply context-specific parameters during tests.
    */
   protected Properties setupProperties() {
      return new Properties();
   }
}
