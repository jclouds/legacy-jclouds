/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.mezeo.pcs2;

import static org.testng.Assert.assertEquals;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.mezeo.pcs2.config.PCSRestClientModule;
import org.jclouds.mezeo.pcs2.reference.PCSConstants;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.internal.RestContextImpl;
import org.testng.annotations.Test;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of modules configured in RestContext<PCSClient>Builder
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "cloudfiles.PCSContextBuilderTest")
public class PCSContextBuilderTest {

   public void testNewBuilder() {
      PCSContextBuilder builder = newBuilder();
      assertEquals(builder.getProperties().getProperty(PCSConstants.PROPERTY_PCS2_ENDPOINT),
               "https://localhost/pcsblob");
      assertEquals(builder.getProperties().getProperty(PCSConstants.PROPERTY_PCS2_USER), "id");
      assertEquals(builder.getProperties().getProperty(PCSConstants.PROPERTY_PCS2_PASSWORD),
               "secret");
   }

   public void testBuildContext() {
      RestContext<PCSClient> context = newBuilder().buildContext();
      assertEquals(context.getClass(), RestContextImpl.class);
      assertEquals(context.getAccount(), "id");
      assertEquals(context.getEndPoint(), URI.create("https://localhost/pcsblob"));
   }

   public void testBuildInjector() {
      Injector i = newBuilder().buildInjector();
      assert i.getInstance(Key.get(new TypeLiteral<RestContext<PCSClient>>() {
      })) != null; // TODO: test all things taken from context
      assert i.getInstance(BasicAuthentication.class) != null;
   }

   protected void testAddContextModule() {
      List<Module> modules = new ArrayList<Module>();
      PCSContextBuilder builder = newBuilder();
      builder.addContextModule(modules);
      assertEquals(modules.size(), 1);
      assertEquals(modules.get(0).getClass(), PCSRestClientModule.class);
   }

   private PCSContextBuilder newBuilder() {
      PCSContextBuilder builder = new PCSContextBuilder(new PCSPropertiesBuilder(URI
               .create("https://localhost/pcsblob"), "id", "secret").build());
      return builder;
   }

   protected void addClientModule() {
      List<Module> modules = new ArrayList<Module>();
      PCSContextBuilder builder = newBuilder();
      builder.addClientModule(modules);
      assertEquals(modules.size(), 1);
      assertEquals(modules.get(0).getClass(), PCSRestClientModule.class);
   }

}
