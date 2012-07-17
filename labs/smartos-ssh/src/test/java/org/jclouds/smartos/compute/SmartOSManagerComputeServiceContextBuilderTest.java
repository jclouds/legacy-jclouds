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
package org.jclouds.smartos.compute;

import static org.testng.Assert.assertEquals;

import java.util.Properties;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;
import org.jclouds.ContextBuilder;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.internal.ContextImpl;
import org.jclouds.rest.internal.BaseRestClientTest;
import org.jclouds.smartos.SmartOSApiMetadata;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.testng.annotations.Test;

/**
 * 
 * @author Adrian Cole
 * 
 */
@Test(groups = "unit", testName = "ServerManagerContextBuilderTest")
public class SmartOSManagerComputeServiceContextBuilderTest {


   @Test
   public void testCanBuildWithApiMetadata() {
      ComputeServiceContext context = ContextBuilder.newBuilder(
              new SmartOSApiMetadata())
              .modules(ImmutableSet.<Module>of(getSshModule()))
              .build(ComputeServiceContext.class);
      context.close();
   }

   @Test
   public void testCanBuildById() {
      ComputeServiceContext context = ContextBuilder.newBuilder("smartos-ssh")
              .modules(ImmutableSet.<Module>of(getSshModule()))
              .build(ComputeServiceContext.class);
      context.close();
   }

   @Test
   public void testCanBuildWithOverridingProperties() {
      Properties overrides = new Properties();
      overrides.setProperty("smartos-ssh.endpoint", "http://host");
      overrides.setProperty("smartos-ssh.api-version", "1");

      ComputeServiceContext context = ContextBuilder.newBuilder("smartos-ssh")
              .modules(ImmutableSet.<Module>of(getSshModule()))
              .overrides(overrides).build(ComputeServiceContext.class);

      context.close();
   }

   @Test
   public void testUnwrapIsCorrectType() {
      ComputeServiceContext context = ContextBuilder.newBuilder("smartos-ssh")
              .modules(ImmutableSet.<Module>of(getSshModule()))
              .build(ComputeServiceContext.class);

      assertEquals(context.unwrap().getClass(), ContextImpl.class);

      context.close();
   }

    protected Module getSshModule() {
        return new SshjSshClientModule();
    }
}
