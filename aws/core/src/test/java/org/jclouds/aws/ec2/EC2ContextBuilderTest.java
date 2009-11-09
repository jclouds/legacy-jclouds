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
package org.jclouds.aws.ec2;

import static org.jclouds.aws.reference.AWSConstants.PROPERTY_AWS_ACCESSKEYID;
import static org.jclouds.aws.reference.AWSConstants.PROPERTY_AWS_SECRETACCESSKEY;
import static org.testng.Assert.assertEquals;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.jclouds.aws.ec2.config.EC2RestClientModule;
import org.jclouds.aws.ec2.filters.FormSigner;
import org.jclouds.aws.ec2.reference.EC2Constants;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.internal.RestContextImpl;
import org.testng.annotations.Test;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of modules configured in EC2ContextBuilderT
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ec2.EC2ContextBuilderTest")
public class EC2ContextBuilderTest {

   public void testNewBuilder() {
      EC2ContextBuilder builder = newBuilder();
      assertEquals(builder.getProperties().getProperty(EC2Constants.PROPERTY_EC2_ENDPOINT),
               "http://ec2.amazonaws.com");
      assertEquals(builder.getProperties().getProperty(PROPERTY_AWS_ACCESSKEYID), "id");
      assertEquals(builder.getProperties().getProperty(PROPERTY_AWS_SECRETACCESSKEY), "secret");
   }

   public void testBuildContext() {
      RestContext<EC2Client> context = newBuilder().buildContext();
      assertEquals(context.getClass(), RestContextImpl.class);
      assertEquals(context.getAccount(), "id");
      assertEquals(context.getEndPoint(), URI.create("http://ec2.amazonaws.com"));
   }

   public void testBuildInjector() {
      Injector i = newBuilder().buildInjector();
      assert i.getInstance(Key.get(new TypeLiteral<RestContext<EC2Client>>() {
      })) != null; // TODO: test all things taken from context
      assert i.getInstance(FormSigner.class) != null;
   }

   protected void testAddContextModule() {
      List<Module> modules = new ArrayList<Module>();
      EC2ContextBuilder builder = newBuilder();
      builder.addContextModule(modules);
      assertEquals(modules.size(), 1);
      assertEquals(modules.get(0).getClass(), EC2RestClientModule.class);
   }

   private EC2ContextBuilder newBuilder() {
      EC2ContextBuilder builder = new EC2ContextBuilder(new EC2PropertiesBuilder("id", "secret")
               .build());
      return builder;
   }

   protected void addClientModule() {
      List<Module> modules = new ArrayList<Module>();
      EC2ContextBuilder builder = newBuilder();
      builder.addClientModule(modules);
      assertEquals(modules.size(), 1);
      assertEquals(modules.get(0).getClass(), EC2RestClientModule.class);
   }

}
