/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.vcloud;

import static org.easymock.classextension.EasyMock.createMock;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_ENDPOINT;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_KEY;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_SESSIONINTERVAL;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_USER;
import static org.testng.Assert.assertEquals;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.concurrent.internal.SyncProxy;
import org.jclouds.predicates.AddressReachable;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.predicates.SocketOpen;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.RestContextBuilder;
import org.jclouds.rest.internal.RestContextImpl;
import org.jclouds.vcloud.config.VCloudContextModule;
import org.jclouds.vcloud.config.VCloudRestClientModule;
import org.jclouds.vcloud.domain.VApp;
import org.jclouds.vcloud.endpoints.Org;
import org.jclouds.vcloud.predicates.TaskSuccess;
import org.jclouds.vcloud.predicates.VAppNotFound;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of modules configured in VCloudContextBuilder
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "vcloud.VCloudContextBuilderTest")
public class VCloudContextBuilderTest {
   VCloudAsyncClient connection = createMock(VCloudAsyncClient.class);

   @ConfiguresRestClient
   private final class StubClientModule extends AbstractModule {
      @Override
      protected void configure() {
         bind(URI.class).annotatedWith(Org.class).toInstance(URI.create("http://org"));
         bind(VCloudAsyncClient.class).toInstance(connection);
      }
      @SuppressWarnings("unused")
      @Provides
      @Singleton
      protected Predicate<InetSocketAddress> socketTester(SocketOpen open) {
         return new RetryablePredicate<InetSocketAddress>(open, 130, 10, TimeUnit.SECONDS);
      }

      @SuppressWarnings("unused")
      @Provides
      @Singleton
      protected Predicate<InetAddress> addressTester(AddressReachable reachable) {
         return new RetryablePredicate<InetAddress>(reachable, 60, 5, TimeUnit.SECONDS);
      }

      @SuppressWarnings("unused")
      @Provides
      @Singleton
      protected Predicate<String> successTester(TaskSuccess success) {
         return new RetryablePredicate<String>(success, 600, 10, TimeUnit.SECONDS);
      }

      @SuppressWarnings("unused")
      @Provides
      @Singleton
      @Named("NOT_FOUND")
      protected Predicate<VApp> successTester(VAppNotFound notFound) {
         return new RetryablePredicate<VApp>(notFound, 5, 1, TimeUnit.SECONDS);
      }
      @SuppressWarnings("unused")
      @Provides
      @Singleton
      public VCloudClient provideClient(VCloudAsyncClient client) throws IllegalArgumentException,
               SecurityException, NoSuchMethodException {
         return SyncProxy.create(VCloudClient.class, client);
      }
   }

   public void testNewBuilder() {
      RestContextBuilder<VCloudAsyncClient, VCloudClient> builder = builder();

      assertEquals(builder.getProperties().getProperty(PROPERTY_VCLOUD_ENDPOINT),
               "http://localhost");
      assertEquals(builder.getProperties().getProperty(PROPERTY_VCLOUD_USER), "id");
      assertEquals(builder.getProperties().getProperty(PROPERTY_VCLOUD_KEY), "secret");
      assertEquals(builder.getProperties().getProperty(PROPERTY_VCLOUD_SESSIONINTERVAL), "480");
   }

   public void testBuildContext() {
      RestContextBuilder<VCloudAsyncClient, VCloudClient> builder = builder();
      RestContext<VCloudAsyncClient, VCloudClient> context = builder.buildContext();
      assertEquals(context.getClass(), RestContextImpl.class);
      assertEquals(context.getAsyncApi(), connection);
      assertEquals(context.getAccount(), "id");
      assertEquals(context.getEndPoint(), URI.create("http://org"));
   }

   public RestContextBuilder<VCloudAsyncClient, VCloudClient> builder() {
      return new VCloudContextBuilder(new VCloudPropertiesBuilder(URI.create("http://localhost"),
               "id", "secret").build()).withModules(new StubClientModule());
   }

   public void testBuildInjector() {
      RestContextBuilder<VCloudAsyncClient, VCloudClient> builder = builder();
      Injector i = builder.buildInjector();
      assert i.getInstance(Key.get(URI.class, Org.class)) != null;
      assert i.getInstance(Key.get(new TypeLiteral<RestContext<VCloudAsyncClient, VCloudClient>>() {
      })) != null;
   }

   protected void testAddContextModule() {
      List<Module> modules = new ArrayList<Module>();
      VCloudContextBuilder builder = (VCloudContextBuilder) builder();
      builder.addContextModule(modules);
      assertEquals(modules.size(), 1);
      assertEquals(modules.get(0).getClass(), VCloudContextModule.class);
   }

   protected void addClientModule() {
      List<Module> modules = new ArrayList<Module>();
      VCloudContextBuilder builder = (VCloudContextBuilder) builder();
      builder.addClientModule(modules);
      assertEquals(modules.size(), 1);
      assertEquals(modules.get(0).getClass(), VCloudRestClientModule.class);
   }

}
