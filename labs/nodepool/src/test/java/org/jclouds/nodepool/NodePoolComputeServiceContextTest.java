/*
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
package org.jclouds.nodepool;

import static org.jclouds.nodepool.config.NodePoolProperties.POOL_ADMIN_ACCESS;
import static org.testng.Assert.assertEquals;

import java.io.File;
import java.util.Properties;

import org.jclouds.ContextBuilder;
import org.jclouds.compute.ComputeService;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.nodepool.Backend;
import org.jclouds.nodepool.config.NodePoolProperties;
import org.jclouds.rest.annotations.Credential;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

/**
 * 
 */
@Test(groups = "unit", testName = "NodePoolComputeServiceContextTest")
public class NodePoolComputeServiceContextTest {

   public void testBinds() {
      final String basedir = "target/" + this.getClass().getSimpleName();
      new File(basedir).delete();

      Properties overrides = new Properties();
      overrides.setProperty(NodePoolProperties.BACKEND_PROVIDER, "stub");
      overrides.setProperty(NodePoolProperties.BASEDIR, basedir);
      // note no ssh module since we are stub and not trying ssh, yet
      overrides.setProperty(NodePoolProperties.BACKEND_MODULES, SLF4JLoggingModule.class.getName());
      overrides.setProperty(POOL_ADMIN_ACCESS, "adminUsername=pooluser,adminPassword=poolpass");

      ComputeService stub = ContextBuilder.newBuilder("nodepool").credentials("foo", "bar").endpoint("gooend")
               .apiVersion("1.1").buildVersion("1.1-2").overrides(overrides).buildInjector()
               .getInstance(Key.get(new TypeLiteral<Supplier<ComputeService>>() {
               }, Backend.class)).get();

      assertEquals(stub.getContext().unwrap().getIdentity(), "foo");
      assertEquals(stub.getContext().utils().injector().getInstance(Key.get(String.class, Credential.class)), "bar");
      assertEquals(stub.getContext().unwrap().getProviderMetadata().getEndpoint(), "gooend");
      assertEquals(stub.getContext().unwrap().getProviderMetadata().getApiMetadata().getVersion(), "1.1");
      assertEquals(stub.getContext().unwrap().getProviderMetadata().getApiMetadata().getBuildVersion().get(), "1.1-2");

      stub.getContext().close();
      new File(basedir).delete();

   }

}
