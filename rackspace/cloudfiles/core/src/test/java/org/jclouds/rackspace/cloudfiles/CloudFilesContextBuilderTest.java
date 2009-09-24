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
import static org.jclouds.http.HttpConstants.PROPERTY_HTTP_ADDRESS;
import static org.testng.Assert.assertEquals;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.blobstore.functions.ParseBlobFromHeadersAndHttpContent.BlobFactory;
import org.jclouds.blobstore.functions.ParseBlobMetadataFromHeaders.BlobMetadataFactory;
import org.jclouds.cloud.CloudContext;
import org.jclouds.rackspace.cloudfiles.config.RestCloudFilesBlobStoreModule;
import org.jclouds.rackspace.cloudfiles.internal.GuiceCloudFilesContext;
import org.jclouds.rackspace.config.RackspaceAuthenticationModule;
import org.jclouds.rackspace.reference.RackspaceConstants;
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
      CloudFilesContextBuilder builder = CloudFilesContextBuilder.newBuilder("id", "secret");
      assertEquals(builder.getProperties().getProperty(PROPERTY_USER_METADATA_PREFIX),
               "X-Object-Meta-");
      assertEquals(builder.getProperties().getProperty(PROPERTY_HTTP_ADDRESS), "api.mosso.com");
      assertEquals(builder.getProperties().getProperty(RackspaceConstants.PROPERTY_RACKSPACE_USER),
               "id");
      assertEquals(builder.getProperties().getProperty(RackspaceConstants.PROPERTY_RACKSPACE_KEY),
               "secret");
   }

   public void testBuildContext() {
      CloudContext<CloudFilesBlobStore> context = CloudFilesContextBuilder.newBuilder("id",
               "secret").buildContext();
      assertEquals(context.getClass(), GuiceCloudFilesContext.class);
      assertEquals(context.getAccount(), "id");
      assertEquals(context.getEndPoint(), URI.create("https://api.mosso.com:443"));
   }

   public void testBuildInjector() {
      Injector i = CloudFilesContextBuilder.newBuilder("id", "secret").buildInjector();
      assert i.getInstance(CloudFilesContext.class) != null;
      assert i.getInstance(GuiceCloudFilesContext.CloudFilesObjectMapFactory.class) != null;
      assert i.getInstance(GuiceCloudFilesContext.CloudFilesInputStreamMapFactory.class) != null;
      assert i.getInstance(Key.get(new TypeLiteral<BlobMetadataFactory<BlobMetadata>>() {
      })) != null;
      assert i.getInstance(Key
               .get(new TypeLiteral<BlobFactory<BlobMetadata, Blob<BlobMetadata>>>() {
               })) != null;
   }

   protected void testAddContextModule() {
      List<Module> modules = new ArrayList<Module>();
      CloudFilesContextBuilder builder = CloudFilesContextBuilder.newBuilder("id", "secret");
      builder.addContextModule(modules);
      assertEquals(modules.size(), 1);
      assertEquals(modules.get(0).getClass(), RackspaceAuthenticationModule.class);
   }

   protected void addConnectionModule() {
      List<Module> modules = new ArrayList<Module>();
      CloudFilesContextBuilder builder = CloudFilesContextBuilder.newBuilder("id", "secret");
      builder.addConnectionModule(modules);
      assertEquals(modules.size(), 1);
      assertEquals(modules.get(0).getClass(), RestCloudFilesBlobStoreModule.class);
   }

}
