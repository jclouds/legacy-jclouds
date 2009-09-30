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
package org.jclouds.rackspace.cloudfiles;

import static org.jclouds.blobstore.reference.BlobStoreConstants.PROPERTY_USER_METADATA_PREFIX;
import static org.testng.Assert.assertEquals;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.jclouds.cloud.CloudContext;
import org.jclouds.rackspace.StubRackspaceAuthenticationModule;
import org.jclouds.rackspace.cloudfiles.config.CloudFilesContextModule.CloudFilesContextImpl;
import org.jclouds.rackspace.cloudfiles.integration.StubCloudFilesConnectionModule;
import org.jclouds.rackspace.config.RestRackspaceAuthenticationModule;
import org.jclouds.rackspace.reference.RackspaceConstants;
import org.testng.annotations.Test;

import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * Tests behavior of modules configured in CloudFilesContextBuilder
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "cloudfiles.CloudFilesContextBuilderTest")
public class CloudFilesContextBuilderTest {

   public void testNewBuilder() {
      CloudFilesContextBuilder builder = new CloudFilesContextBuilder("id", "secret");
      assertEquals(builder.getProperties().getProperty(PROPERTY_USER_METADATA_PREFIX),
               "X-Object-Meta-");
      assertEquals(builder.getProperties().getProperty(
               RackspaceConstants.PROPERTY_RACKSPACE_ENDPOINT), "https://api.mosso.com");
      assertEquals(builder.getProperties().getProperty(RackspaceConstants.PROPERTY_RACKSPACE_USER),
               "id");
      assertEquals(builder.getProperties().getProperty(RackspaceConstants.PROPERTY_RACKSPACE_KEY),
               "secret");
   }

   public void testBuildContext() {
      CloudContext<CloudFilesBlobStore> context = new CloudFilesContextBuilder("id", "secret")
               .withModules(new StubCloudFilesConnectionModule(),
                        new StubRackspaceAuthenticationModule()).buildContext();
      assertEquals(context.getClass(), CloudFilesContextImpl.class);
      assertEquals(context.getAccount(), "id");
      assertEquals(context.getEndPoint(), URI.create("http://localhost/rackspacestub/cloudfiles"));
   }

   public void testBuildInjector() {
      Injector i = new CloudFilesContextBuilder("id", "secret").withModules(
               new StubCloudFilesConnectionModule(), new StubRackspaceAuthenticationModule())
               .buildInjector();
      assert i.getInstance(CloudFilesContextImpl.class) != null;
   }

   protected void testAddContextModule() {
      List<Module> modules = new ArrayList<Module>();
      CloudFilesContextBuilder builder = new CloudFilesContextBuilder("id", "secret");
      builder.addContextModule(modules);
      assertEquals(modules.size(), 1);
      assertEquals(modules.get(0).getClass(), RestRackspaceAuthenticationModule.class);
   }

}
