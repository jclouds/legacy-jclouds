/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.config;

import static org.easymock.EasyMock.createMock;
import static org.testng.Assert.assertEquals;

import javax.inject.Inject;

import org.jclouds.domain.Credentials;
import org.jclouds.http.IntegrationTestAsyncClient;
import org.jclouds.http.IntegrationTestClient;
import org.jclouds.providers.AnonymousProviderMetadata;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.providers.config.BindProviderMetadataContextAndCredentials;
import org.jclouds.rest.RestApiMetadata;
import org.jclouds.rest.RestContext;
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
@Test(groups = "unit", testName = "BindRestContextWithWildcardExtendsExplicitAndRawTypeTest")
public class BindRestContextWithWildcardExtendsExplicitAndRawTypeTest {

   @SuppressWarnings( { "unused", "unchecked" })
   private static class ExpectedBindings {

      private final RestContext raw;
      private final RestContext<IntegrationTestClient, IntegrationTestAsyncClient> explicit;

      @Inject
      public ExpectedBindings(RestContext raw,
               RestContext<IntegrationTestClient, IntegrationTestAsyncClient> explicit) {
         this.raw = raw;
         this.explicit = explicit;
      }

   }

   @Test
   public void testRawAndExplicit() {
      ProviderMetadata md = AnonymousProviderMetadata.forClientMappedToAsyncClientOnEndpoint(
               IntegrationTestClient.class, IntegrationTestAsyncClient.class, "http://localhost");

      ExpectedBindings bindings = injectorFor(md).getInstance(ExpectedBindings.class);
      assertEquals(bindings.raw, bindings.explicit);
   }

   private Injector injectorFor(ProviderMetadata md) {
      return Guice.createInjector(
               new BindNameToContext("test"),
               new BindProviderMetadataContextAndCredentials(md, new Credentials("user", "pass")),
               new BindRestContextWithWildcardExtendsExplicitAndRawType(RestApiMetadata.class.cast(md
                                 .getApiMetadata())),
                                 
               // stuff needed for RestContextImpl
               new MockModule(),
               new AbstractModule() {
                  
                  @Override
                  protected void configure() {
                     bind(Utils.class).toInstance(createMock(Utils.class));
                     bind(IntegrationTestClient.class).toInstance(createMock(IntegrationTestClient.class));
                     bind(IntegrationTestAsyncClient.class).toInstance(createMock(IntegrationTestAsyncClient.class));
                  }
               });
   }

   @SuppressWarnings( { "unused", "unchecked" })
   private static class ExpectedBindingsWithWildCardExtends {

      private final RestContext raw;
      private final RestContext<IntegrationTestClient, IntegrationTestAsyncClient> explicit;
      private final RestContext<? extends IntegrationTestClient, ? extends IntegrationTestAsyncClient> wildcardExtends;

      @Inject
      public ExpectedBindingsWithWildCardExtends(RestContext raw,
               RestContext<IntegrationTestClient, IntegrationTestAsyncClient> explicit,
               RestContext<? extends IntegrationTestClient, ? extends IntegrationTestAsyncClient> wildcardExtends) {
         this.raw = raw;
         this.explicit = explicit;
         this.wildcardExtends = wildcardExtends;
      }

   }

   @SuppressWarnings("unchecked")
   @Test
   public void testRawExplicitAndWildCardExtends() {
      ProviderMetadata md = AnonymousProviderMetadata.forClientMappedToAsyncClientOnEndpoint(
               IntegrationTestClient.class, IntegrationTestAsyncClient.class, "http://localhost");

      TypeToken wildCardExtendsType = new TypeToken<RestContext<? extends IntegrationTestClient, ? extends IntegrationTestAsyncClient>>() {
      };
      
      md = md.toBuilder().apiMetadata(md.getApiMetadata().toBuilder().context(wildCardExtendsType).build()).build();

      ExpectedBindingsWithWildCardExtends bindings = injectorFor(md).getInstance(ExpectedBindingsWithWildCardExtends.class);
      assertEquals(bindings.raw, bindings.explicit);
      assertEquals(bindings.explicit, bindings.wildcardExtends);
   }
}
