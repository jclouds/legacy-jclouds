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
import static org.jclouds.http.HttpConstants.PROPERTY_HTTP_ADDRESS;
import static org.testng.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.jclouds.aws.s3.config.LiveS3ConnectionModule;
import org.jclouds.aws.s3.config.S3ContextModule;
import org.jclouds.aws.s3.internal.GuiceS3Context;
import org.jclouds.aws.s3.xml.config.S3ParserModule;
import org.jclouds.cloud.CloudContext;
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
      S3ContextBuilder builder = S3ContextBuilder.newBuilder("id", "secret");
      assertEquals(builder.getProperties().getProperty(PROPERTY_HTTP_ADDRESS), "s3.amazonaws.com");
      assertEquals(builder.getProperties().getProperty(PROPERTY_AWS_ACCESSKEYID), "id");
      assertEquals(builder.getProperties().getProperty(PROPERTY_AWS_SECRETACCESSKEY), "secret");
   }

   public void testBuildContext() {
      CloudContext<S3Connection> context = S3ContextBuilder.newBuilder("id", "secret")
               .buildContext();
      assertEquals(context.getClass(), GuiceS3Context.class);
   }

   public void testBuildInjector() {
      Injector i = S3ContextBuilder.newBuilder("id", "secret").buildInjector();
      assert i.getInstance(S3Context.class) != null;
   }

   protected void testAddParserModule() {
      List<Module> modules = new ArrayList<Module>();
      S3ContextBuilder builder = S3ContextBuilder.newBuilder("id", "secret");
      builder.addParserModule(modules);
      assertEquals(modules.size(), 1);
      assertEquals(modules.get(0).getClass(), S3ParserModule.class);
   }

   protected void testAddContextModule() {
      List<Module> modules = new ArrayList<Module>();
      S3ContextBuilder builder = S3ContextBuilder.newBuilder("id", "secret");
      builder.addContextModule(modules);
      assertEquals(modules.size(), 1);
      assertEquals(modules.get(0).getClass(), S3ContextModule.class);
   }

   protected void addConnectionModule() {
      List<Module> modules = new ArrayList<Module>();
      S3ContextBuilder builder = S3ContextBuilder.newBuilder("id", "secret");
      builder.addConnectionModule(modules);
      assertEquals(modules.size(), 1);
      assertEquals(modules.get(0).getClass(), LiveS3ConnectionModule.class);
   }

}
