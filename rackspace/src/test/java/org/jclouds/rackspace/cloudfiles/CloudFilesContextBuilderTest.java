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
package org.jclouds.rackspace.cloudfiles;

import static org.testng.Assert.assertEquals;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.jclouds.rackspace.StubRackspaceAuthenticationModule;
import org.jclouds.rackspace.cloudfiles.config.CloudFilesStubClientModule;
import org.jclouds.rackspace.cloudfiles.internal.StubCloudFilesClient;
import org.jclouds.rackspace.cloudfiles.reference.CloudFilesConstants;
import org.jclouds.rackspace.config.RackspaceAuthenticationRestModule;
import org.jclouds.rackspace.reference.RackspaceConstants;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.internal.RestContextImpl;
import org.testng.annotations.Test;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of modules configured in CloudFilesContextBuilder
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "cloudfiles.CloudFilesContextBuilderTest")
public class CloudFilesContextBuilderTest {

   public void testNewBuilder() {
      CloudFilesContextBuilder builder = newBuilder();
      assertEquals(builder.getProperties().getProperty(
               CloudFilesConstants.PROPERTY_CLOUDFILES_METADATA_PREFIX), "X-Object-Meta-");
      assertEquals(builder.getProperties().getProperty(
               RackspaceConstants.PROPERTY_RACKSPACE_ENDPOINT), "https://api.mosso.com");
      assertEquals(builder.getProperties().getProperty(RackspaceConstants.PROPERTY_RACKSPACE_USER),
               "id");
      assertEquals(builder.getProperties().getProperty(RackspaceConstants.PROPERTY_RACKSPACE_KEY),
               "secret");
   }

   private CloudFilesContextBuilder newBuilder() {
      CloudFilesContextBuilder builder = new CloudFilesContextBuilder(
               new CloudFilesPropertiesBuilder("id", "secret").build());
      return builder;
   }

   public void testBuildContext() {
      RestContext<CloudFilesClient> context = newBuilder().withModules(
               new CloudFilesStubClientModule(), new StubRackspaceAuthenticationModule())
               .buildContext();
      assertEquals(context.getClass(), RestContextImpl.class);
      assertEquals(context.getApi().getClass(), StubCloudFilesClient.class);
      assertEquals(context.getAccount(), "id");
      assertEquals(context.getEndPoint(), URI.create("http://localhost/rackspacestub/cloudfiles"));
   }

   public void testBuildInjector() {
      Injector i = newBuilder().withModules(new CloudFilesStubClientModule(),
               new StubRackspaceAuthenticationModule()).buildInjector();
      assert i.getInstance(Key.get(new TypeLiteral<RestContext<CloudFilesClient>>() {
      })) != null;
   }

   protected void testAddContextModule() {
      List<Module> modules = new ArrayList<Module>();
      CloudFilesContextBuilder builder = newBuilder();
      builder.addContextModule(modules);
      assertEquals(modules.size(), 1);
      assertEquals(modules.get(0).getClass(), RackspaceAuthenticationRestModule.class);
   }

}
