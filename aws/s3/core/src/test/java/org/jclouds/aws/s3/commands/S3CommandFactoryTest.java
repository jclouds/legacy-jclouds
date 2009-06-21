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
package org.jclouds.aws.s3.commands;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;

import java.net.URI;

import org.jclouds.aws.s3.commands.config.S3CommandsModule;
import org.jclouds.aws.s3.commands.options.CopyObjectOptions;
import org.jclouds.aws.s3.commands.options.GetObjectOptions;
import org.jclouds.aws.s3.commands.options.ListBucketOptions;
import org.jclouds.aws.s3.commands.options.PutBucketOptions;
import org.jclouds.aws.s3.commands.options.PutObjectOptions;
import org.jclouds.aws.s3.domain.S3Object;
import org.jclouds.aws.s3.domain.S3Bucket.Metadata.LocationConstraint;
import org.jclouds.aws.s3.xml.config.S3ParserModule;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * @author Adrian Cole
 */
@Test(groups = { "unit" }, testName = "s3.S3CommandFactoryTest")
public class S3CommandFactoryTest {

   Injector injector = null;
   S3CommandFactory commandFactory = null;

   @BeforeTest
   void setUpInjector() {
      injector = Guice.createInjector(new S3ParserModule(), new S3CommandsModule() {
         @Override
         protected void configure() {
            bind(URI.class).toInstance(URI.create("http://localhost:8080"));
            super.configure();
         }
      });
      commandFactory = injector.getInstance(S3CommandFactory.class);
   }

   @AfterTest
   void tearDownInjector() {
      commandFactory = null;
      injector = null;
   }

   @Test
   void testCreateCopyObject() {
      assert commandFactory.createCopyObject("sourcebucket", "sourceObject", "destbucket",
               "destObject", CopyObjectOptions.NONE) != null;
   }

   @Test
   void testCreateCopyObjectOptions() {
      assert commandFactory.createCopyObject("sourcebucket", "sourceObject", "destbucket",
               "destObject", new CopyObjectOptions()) != null;
   }

   @Test
   void testCreateDeleteBucket() {
      assert commandFactory.createDeleteBucket("test") != null;
   }

   @Test
   void testCreateDeleteObject() {
      assert commandFactory.createDeleteObject("test", "blah") != null;
   }

   @Test
   void testCreateHeadBucket() {
      assert commandFactory.createHeadBucket("test") != null;
   }

   @Test
   void testCreatePutBucket() {
      assert commandFactory.createPutBucket("test", PutBucketOptions.NONE) != null;
   }

   @Test
   void testCreatePutBucketOptions() {
      assert commandFactory.createPutBucket("test", PutBucketOptions.Builder
               .createIn(LocationConstraint.EU)) != null;
   }

   @Test
   void testCreatePutObject() {
      S3Object.Metadata metadata = createMock(S3Object.Metadata.class);
      S3Object object = new S3Object(metadata);
      expect(metadata.getSize()).andReturn(4L).atLeastOnce();
      expect(metadata.getKey()).andReturn("rawr");
      expect(metadata.getContentType()).andReturn("text/xml").atLeastOnce();
      expect(metadata.getCacheControl()).andReturn("no-cache").atLeastOnce();
      expect(metadata.getContentDisposition()).andReturn("disposition").atLeastOnce();
      expect(metadata.getContentEncoding()).andReturn("encoding").atLeastOnce();
      expect(metadata.getMd5()).andReturn("encoding".getBytes()).atLeastOnce();
      Multimap<String, String> userMdata = HashMultimap.create();
      expect(metadata.getUserMetadata()).andReturn(userMdata).atLeastOnce();

      replay(metadata);
      object.setData("<a></a>");

      assert commandFactory.createPutObject("test", object, PutObjectOptions.NONE) != null;
   }

   @Test
   void testCreateGetObject() {
      assert commandFactory.createGetObject("test", "blah", GetObjectOptions.NONE) != null;
   }

   @Test
   void testCreateHeadMetadata() {
      assert commandFactory.createHeadMetadata("test", "blah") != null;
   }

   @Test
   void testCreateListAllMyBuckets() {
      assert commandFactory.createGetMetadataForOwnedBuckets() != null;
   }

   @Test
   void testCreateListBucket() {
      assert commandFactory.createListBucket("test", ListBucketOptions.NONE) != null;
   }

}