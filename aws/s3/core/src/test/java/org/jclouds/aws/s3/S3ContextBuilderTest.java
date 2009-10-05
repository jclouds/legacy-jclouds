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
package org.jclouds.aws.s3;

import static org.jclouds.aws.reference.AWSConstants.PROPERTY_AWS_ACCESSKEYID;
import static org.jclouds.aws.reference.AWSConstants.PROPERTY_AWS_SECRETACCESSKEY;
import static org.jclouds.blobstore.reference.BlobStoreConstants.PROPERTY_USER_METADATA_PREFIX;
import static org.testng.Assert.assertEquals;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.jclouds.aws.s3.config.RestS3ConnectionModule;
import org.jclouds.aws.s3.config.S3ContextModule;
import org.jclouds.aws.s3.config.StubS3BlobStoreModule;
import org.jclouds.aws.s3.config.S3ContextModule.S3ContextImpl;
import org.jclouds.aws.s3.internal.StubS3Connection;
import org.jclouds.blobstore.integration.internal.StubBlobStore;
import org.testng.annotations.Test;

import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * Tests behavior of modules configured in S3ContextBuilder
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "s3.S3ContextBuilderTest")
public class S3ContextBuilderTest {

   public void testNewBuilder() {
      S3ContextBuilder builder = new S3ContextBuilder("id", "secret");
      assertEquals(builder.getProperties().getProperty(PROPERTY_USER_METADATA_PREFIX),
               "x-amz-meta-");
      assertEquals(builder.getProperties().getProperty(PROPERTY_AWS_ACCESSKEYID), "id");
      assertEquals(builder.getProperties().getProperty(PROPERTY_AWS_SECRETACCESSKEY), "secret");
   }

   public void testBuildContext() {
      S3Context context = new S3ContextBuilder("id", "secret").withModules(
               new StubS3BlobStoreModule()).buildContext();
      assertEquals(context.getClass(), S3ContextImpl.class);
      assertEquals(context.getApi().getClass(), StubS3Connection.class);
      assertEquals(context.getBlobStore().getClass(), StubBlobStore.class);
      assertEquals(context.getAccount(), "id");
      assertEquals(context.getEndPoint(), URI.create("https://localhost/s3stub"));
   }

   public void testBuildInjector() {
      Injector i = new S3ContextBuilder("id", "secret").withModules(new StubS3BlobStoreModule())
               .buildInjector();
      assert i.getInstance(S3Context.class) != null;
   }

   protected void testAddContextModule() {
      List<Module> modules = new ArrayList<Module>();
      S3ContextBuilder builder = new S3ContextBuilder("id", "secret");
      builder.addContextModule(modules);
      assertEquals(modules.size(), 1);
      assertEquals(modules.get(0).getClass(), S3ContextModule.class);
   }

   protected void addConnectionModule() {
      List<Module> modules = new ArrayList<Module>();
      S3ContextBuilder builder = new S3ContextBuilder("id", "secret");
      builder.addConnectionModule(modules);
      assertEquals(modules.size(), 1);
      assertEquals(modules.get(0).getClass(), RestS3ConnectionModule.class);
   }

}
