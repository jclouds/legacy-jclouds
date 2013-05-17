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
package org.jclouds.config;

import static com.google.common.base.Suppliers.ofInstance;
import static org.easymock.EasyMock.createMock;
import static org.testng.Assert.assertEquals;

import javax.inject.Inject;

import org.jclouds.Context;
import org.jclouds.domain.Credentials;
import org.jclouds.http.IntegrationTestAsyncClient;
import org.jclouds.http.IntegrationTestClient;
import org.jclouds.providers.AnonymousProviderMetadata;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.providers.config.BindProviderMetadataContextAndCredentials;
import org.jclouds.rest.ApiContext;
import org.jclouds.rest.HttpApiMetadata;
import org.jclouds.rest.Utils;
import org.jclouds.rest.internal.BaseRestApiTest.MockModule;
import org.testng.annotations.Test;

import com.google.common.reflect.TypeToken;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "BindApiContextWithWildcardExtendsExplicitAndRawTypeTest")
public class BindApiContextWithWildcardExtendsExplicitAndRawTypeTest {

   @SuppressWarnings("rawtypes")
   private static class ExpectedBindings {

      private final ApiContext raw;
      private final ApiContext<IntegrationTestClient> explicit;

      @Inject
      public ExpectedBindings(ApiContext raw, ApiContext<IntegrationTestClient> explicit) {
         this.raw = raw;
         this.explicit = explicit;
      }

   }

   @Test
   public void testRawAndExplicit() {
      ProviderMetadata md = AnonymousProviderMetadata.forApiOnEndpoint(IntegrationTestClient.class, "http://localhost");

      ExpectedBindings bindings = injectorFor(md).getInstance(ExpectedBindings.class);
      assertEquals(bindings.raw, bindings.explicit);
   }

   private Injector injectorFor(ProviderMetadata md) {
      return Guice.createInjector(new BindNameToContext("test"), new BindProviderMetadataContextAndCredentials(md,
            ofInstance(new Credentials("user", "pass"))), new BindApiContextWithWildcardExtendsExplicitAndRawType(
            HttpApiMetadata.class.cast(md.getApiMetadata())),

            // stuff needed for ApiContextImpl
            new MockModule(), new AbstractModule() {

               @Override
               protected void configure() {
                  bind(Utils.class).toInstance(createMock(Utils.class));
                  bind(IntegrationTestClient.class).toInstance(createMock(IntegrationTestClient.class));
                  bind(IntegrationTestAsyncClient.class).toInstance(createMock(IntegrationTestAsyncClient.class));
               }
            });
   }

   @SuppressWarnings("rawtypes")
   private static class ExpectedBindingsWithWildCardExtends {

      private final ApiContext raw;
      private final ApiContext<IntegrationTestClient> explicit;
      private final ApiContext<? extends IntegrationTestClient> wildcardExtends;

      @Inject
      public ExpectedBindingsWithWildCardExtends(ApiContext raw, ApiContext<IntegrationTestClient> explicit,
            ApiContext<? extends IntegrationTestClient> wildcardExtends) {
         this.raw = raw;
         this.explicit = explicit;
         this.wildcardExtends = wildcardExtends;
      }

   }

   @Test
   public void testRawExplicitAndWildCardExtends() {
      ProviderMetadata md = AnonymousProviderMetadata.forApiOnEndpoint(IntegrationTestClient.class, "http://localhost");

      TypeToken<? extends Context> wildCardExtendsType = new TypeToken<ApiContext<? extends IntegrationTestClient>>() {
         private static final long serialVersionUID = 1L;
      };

      md = md.toBuilder().apiMetadata(md.getApiMetadata().toBuilder().context(wildCardExtendsType).build()).build();

      ExpectedBindingsWithWildCardExtends bindings = injectorFor(md).getInstance(
            ExpectedBindingsWithWildCardExtends.class);
      assertEquals(bindings.raw, bindings.explicit);
      assertEquals(bindings.explicit, bindings.wildcardExtends);
   }
}
