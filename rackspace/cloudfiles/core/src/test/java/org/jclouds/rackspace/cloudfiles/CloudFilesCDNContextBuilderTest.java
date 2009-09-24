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

import static org.jclouds.http.HttpConstants.PROPERTY_HTTP_ADDRESS;
import static org.testng.Assert.assertEquals;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.jclouds.cloud.CloudContext;
import org.jclouds.rackspace.cloudfiles.config.RestCloudFilesCDNConnectionModule;
import org.jclouds.rackspace.cloudfiles.internal.GuiceCloudFilesCDNContext;
import org.jclouds.rackspace.config.RackspaceAuthenticationModule;
import org.jclouds.rackspace.reference.RackspaceConstants;
import org.testng.annotations.Test;

import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * Tests behavior of modules configured in CloudFilesCDNContextBuilder
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "cloudfiles.CloudFilesCDNContextBuilderTest")
public class CloudFilesCDNContextBuilderTest {

   public void testNewBuilder() {
      CloudFilesCDNContextBuilder builder = CloudFilesCDNContextBuilder.newBuilder("id", "secret");
      assertEquals(builder.getProperties().getProperty(PROPERTY_HTTP_ADDRESS), "api.mosso.com");
      assertEquals(builder.getProperties().getProperty(RackspaceConstants.PROPERTY_RACKSPACE_USER),
               "id");
      assertEquals(builder.getProperties().getProperty(RackspaceConstants.PROPERTY_RACKSPACE_KEY),
               "secret");
   }

   public void testBuildContext() {
      CloudContext<CloudFilesCDNConnection> context = CloudFilesCDNContextBuilder.newBuilder("id",
               "secret").buildContext();
      assertEquals(context.getClass(), GuiceCloudFilesCDNContext.class);
      assertEquals(context.getAccount(), "id");
      assertEquals(context.getEndPoint(), URI.create("https://api.mosso.com:443"));
   }

   public void testBuildInjector() {
      Injector i = CloudFilesCDNContextBuilder.newBuilder("id", "secret").buildInjector();
      assert i.getInstance(CloudFilesCDNContext.class) != null;
   }

   protected void testAddContextModule() {
      List<Module> modules = new ArrayList<Module>();
      CloudFilesCDNContextBuilder builder = CloudFilesCDNContextBuilder.newBuilder("id", "secret");
      builder.addContextModule(modules);
      assertEquals(modules.size(), 1);
      assertEquals(modules.get(0).getClass(), RackspaceAuthenticationModule.class);
   }

   protected void addConnectionModule() {
      List<Module> modules = new ArrayList<Module>();
      CloudFilesCDNContextBuilder builder = CloudFilesCDNContextBuilder.newBuilder("id", "secret");
      builder.addConnectionModule(modules);
      assertEquals(modules.size(), 1);
      assertEquals(modules.get(0).getClass(), RestCloudFilesCDNConnectionModule.class);
   }

}
