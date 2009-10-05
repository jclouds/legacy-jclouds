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
package org.jclouds.mezeo.pcs2;

import static org.testng.Assert.assertEquals;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.jclouds.blobstore.integration.internal.StubBlobStore;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.mezeo.pcs2.config.RestPCSBlobStoreModule;
import org.jclouds.mezeo.pcs2.config.StubPCSBlobStoreModule;
import org.jclouds.mezeo.pcs2.config.PCSContextModule.PCSContextImpl;
import org.jclouds.mezeo.pcs2.internal.StubPCSConnection;
import org.jclouds.mezeo.pcs2.reference.PCSConstants;
import org.testng.annotations.Test;

import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * Tests behavior of modules configured in PCSContextBuilder
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "cloudfiles.PCSContextBuilderTest")
public class PCSContextBuilderTest {

   public void testNewBuilder() {
      PCSContextBuilder builder = new PCSContextBuilder(URI.create("https://localhost/pcsblob"),
               "id", "secret");
      assertEquals(builder.getProperties().getProperty(PCSConstants.PROPERTY_PCS2_ENDPOINT),
               "https://localhost/pcsblob");
      assertEquals(builder.getProperties().getProperty(PCSConstants.PROPERTY_PCS2_USER), "id");
      assertEquals(builder.getProperties().getProperty(PCSConstants.PROPERTY_PCS2_PASSWORD),
               "secret");
   }

   public void testBuildContext() {
      PCSContext context = new PCSContextBuilder(URI.create("https://localhost/pcsblob"), "id",
               "secret").withModule(new StubPCSBlobStoreModule()).buildContext();
      assertEquals(context.getClass(), PCSContextImpl.class);
      assertEquals(context.getApi().getClass(), StubPCSConnection.class);
      assertEquals(context.getBlobStore().getClass(), StubBlobStore.class);
      assertEquals(context.getAccount(), "id");
      assertEquals(context.getEndPoint(), URI.create("https://localhost/pcsblob"));
   }

   public void testBuildInjector() {
      Injector i = new PCSContextBuilder(URI.create("https://localhost/pcsblob"), "id", "secret")
               .buildInjector();
      assert i.getInstance(PCSContext.class) != null;
      // TODO: test all things taken from context
      assert i.getInstance(BasicAuthentication.class) != null;
   }

   protected void testAddContextModule() {
      List<Module> modules = new ArrayList<Module>();
      PCSContextBuilder builder = new PCSContextBuilder(URI.create("https://localhost/pcsblob"),
               "id", "secret");
      builder.addContextModule(modules);
      assertEquals(modules.size(), 1);
      assertEquals(modules.get(0).getClass(), RestPCSBlobStoreModule.class);
   }

   protected void addConnectionModule() {
      List<Module> modules = new ArrayList<Module>();
      PCSContextBuilder builder = new PCSContextBuilder(URI.create("https://localhost/pcsblob"),
               "id", "secret");
      builder.addConnectionModule(modules);
      assertEquals(modules.size(), 1);
      assertEquals(modules.get(0).getClass(), RestPCSBlobStoreModule.class);
   }

}
