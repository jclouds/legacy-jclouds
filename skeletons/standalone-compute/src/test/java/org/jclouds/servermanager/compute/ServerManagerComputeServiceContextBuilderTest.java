/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
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
 */
package org.jclouds.servermanager.compute;

import static org.testng.Assert.assertEquals;

import java.util.Properties;

import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextBuilder;
import org.jclouds.rest.RestContext;
import org.jclouds.servermanager.ServerManager;
import org.jclouds.servermanager.ServerManagerApiMetadata;
import org.testng.annotations.Test;

/**
 * 
 * @author Adrian Cole
 * 
 */
@Test(groups = "unit", testName = "ServerManagerComputeServiceContextBuilderTest")
public class ServerManagerComputeServiceContextBuilderTest {

   @Test
   public void testCanBuildDirectly() {
      ComputeServiceContext<ServerManager, ServerManager> context = new ServerManagerComputeServiceContextBuilder()
            .build();
      context.close();
   }

   @Test
   public void testCanBuildWithApiMetadata() {
      ComputeServiceContext<ServerManager, ServerManager> context = ComputeServiceContextBuilder.newBuilder(
            new ServerManagerApiMetadata()).build();
      context.close();
   }

   @Test
   public void testCanBuildById() {
      ComputeServiceContext<?, ?> context = ComputeServiceContextBuilder.newBuilder("servermanager").build();
      context.close();
   }

   @Test
   public void testCanBuildWithOverridingProperties() {
      Properties overrides = new Properties();
      overrides.setProperty("servermanager.endpoint", "http://host");
      overrides.setProperty("servermanager.api-version", "1");

      ComputeServiceContext<ServerManager, ServerManager> context = new ServerManagerComputeServiceContextBuilder()
            .overrides(overrides).build();

      context.close();
   }

   @Test
   public void testProviderSpecificContextIsCorrectType() {
      ComputeServiceContext<ServerManager, ServerManager> context = new ServerManagerComputeServiceContextBuilder()
            .build();

      RestContext<ServerManager, ServerManager> providerContext = context.getProviderSpecificContext();

      assertEquals(providerContext.getApi().getClass(), ServerManager.class);

      context.close();
   }
}
