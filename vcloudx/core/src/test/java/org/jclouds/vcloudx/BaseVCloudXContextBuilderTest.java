/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.vcloudx;

import static org.easymock.classextension.EasyMock.createMock;
import static org.testng.Assert.assertEquals;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.jclouds.cloud.CloudContext;
import org.jclouds.cloud.CloudContextBuilder;
import org.jclouds.cloud.ConfiguresCloudConnection;
import org.jclouds.vcloudx.config.BaseRestVCloudXConnectionModule;
import org.jclouds.vcloudx.config.BaseVCloudXContextModule;
import org.jclouds.vcloudx.config.BaseVCloudXContextModule.VCloudXContextImpl;
import org.jclouds.vcloudx.endpoints.Org;
import org.jclouds.vcloudx.reference.VCloudXConstants;
import org.testng.annotations.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of modules configured in BaseVCloudXContextBuilder
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "vcloudx.BaseVCloudXContextBuilderTest")
public class BaseVCloudXContextBuilderTest {
   VCloudXConnection connection = createMock(VCloudXConnection.class);

   @ConfiguresCloudConnection
   private final class StubConnectionModule extends AbstractModule {
      @Override
      protected void configure() {
         bind(URI.class).annotatedWith(Org.class).toInstance(URI.create("http://org"));
         bind(VCloudXConnection.class).toInstance(connection);
      }
   }

   public void testNewBuilder() {
      CloudContextBuilder<VCloudXConnection> builder = builder();

      assertEquals(builder.getProperties().getProperty(VCloudXConstants.PROPERTY_VCLOUDX_ENDPOINT),
               "http://localhost");
      assertEquals(builder.getProperties().getProperty(VCloudXConstants.PROPERTY_VCLOUDX_USER),
               "id");
      assertEquals(builder.getProperties().getProperty(VCloudXConstants.PROPERTY_VCLOUDX_KEY),
               "secret");
      assertEquals(builder.getProperties().getProperty(
               VCloudXConstants.PROPERTY_VCLOUDX_SESSIONINTERVAL), "540");
   }

   public void testBuildContext() {
      CloudContextBuilder<VCloudXConnection> builder = builder();
      CloudContext<VCloudXConnection> context = builder.buildContext();
      assertEquals(context.getClass(), VCloudXContextImpl.class);
      assertEquals(context.getApi(), connection);
      assertEquals(context.getAccount(), "id");
      assertEquals(context.getEndPoint(), URI.create("http://org"));
   }

   public CloudContextBuilder<VCloudXConnection> builder() {
      return new BaseVCloudXContextBuilder(URI.create("http://localhost"), "id", "secret")
               .withModule(new StubConnectionModule());
   }

   public void testBuildInjector() {
      CloudContextBuilder<VCloudXConnection> builder = builder();
      Injector i = builder.buildInjector();
      assert i.getInstance(Key.get(new TypeLiteral<VCloudXContext<VCloudXConnection>>() {
      })) != null;
   }

   protected void testAddContextModule() {
      List<Module> modules = new ArrayList<Module>();
      BaseVCloudXContextBuilder builder = new BaseVCloudXContextBuilder(URI
               .create("http://localhost"), "id", "secret");
      builder.addContextModule(modules);
      assertEquals(modules.size(), 1);
      assertEquals(modules.get(0).getClass(), BaseVCloudXContextModule.class);
   }

   protected void addConnectionModule() {
      List<Module> modules = new ArrayList<Module>();
      BaseVCloudXContextBuilder builder = new BaseVCloudXContextBuilder(URI
               .create("http://localhost"), "id", "secret");
      builder.addConnectionModule(modules);
      assertEquals(modules.size(), 1);
      assertEquals(modules.get(0).getClass(), BaseRestVCloudXConnectionModule.class);
   }

}
