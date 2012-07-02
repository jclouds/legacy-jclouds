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
package org.jclouds.nodepool;

import java.util.Properties;

import javax.inject.Inject;

import org.jclouds.Context;
import org.jclouds.ContextBuilder;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadata.Status;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.nodepool.config.NodePoolProperties;
import org.jclouds.nodepool.internal.NodeMetadataStore;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.inject.Injector;

/**
 * 
 * @author Adrian Cole, David Alves
 */
@Test(groups = "unit", singleThreaded = true)
public class NodeMetadataStoreTest {

   @Inject
   NodeMetadataStore store;

   @BeforeMethod
   public void setUp() {
      Injector injector = createInjector();
      injector.injectMembers(this);
   }

   protected Injector createInjector() {
      Properties overrides = new Properties();
      overrides.setProperty(NodePoolProperties.BACKEND_PROVIDER, "stub");
      overrides.setProperty(NodePoolProperties.BASEDIR, "target/nodemetadatastoretest");
      // note no ssh module since we are stub and not trying ssh, yet
      overrides.setProperty(NodePoolProperties.BACKEND_MODULES, SLF4JLoggingModule.class.getName());
      Context nodePoolCtx = ContextBuilder.newBuilder("nodepool").credentials("foo", "bar").endpoint("gooend")
               .apiVersion("1.1").buildVersion("1.1-2").overrides(overrides).build();
      return nodePoolCtx.utils().getInjector();
   }

   public void testStore() {
      NodeMetadata meta = new NodeMetadataBuilder().id("testmeta").status(Status.ERROR).build();
      TemplateOptions options = new TemplateOptions().overrideLoginUser("testuser").overrideLoginPassword("testpass")
               .overrideAuthenticateSudo(true).userMetadata("testmetakey", "testmetavalue")
               .userMetadata("testmetakey2", "testmetavalue2").tags(ImmutableList.of("tag1", "tag2"));
      store.store(meta, options, "testgroup");
   }

}