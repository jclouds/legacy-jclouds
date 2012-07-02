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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.Set;

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
import org.jclouds.util.Strings2;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Injector;

/**
 * 
 * @author Adrian Cole, David Alves
 */
@Test(groups = "unit")
public class NodeMetadataStoreTest {

   @Inject
   NodeMetadataStore store;
   private String nodeId1;
   private String nodeId2;
   private String baseDir;
   private NodeMetadata nodeMeta1;
   private NodeMetadata nodeMeta2;
   private TemplateOptions templateOptions;

   @BeforeMethod
   public void setUp() {
      Injector injector = createInjector();
      injector.injectMembers(this);
      this.nodeMeta1 = new NodeMetadataBuilder().id(nodeId1).status(Status.ERROR).build();
      this.nodeMeta2 = new NodeMetadataBuilder().id(nodeId2).status(Status.ERROR).build();
      this.templateOptions = new TemplateOptions().overrideLoginUser("testuser").overrideLoginPassword("testpass")
               .overrideAuthenticateSudo(true).userMetadata("testmetakey", "testmetavalue")
               .overrideLoginPrivateKey("pk").userMetadata("testmetakey2", "testmetavalue2")
               .tags(ImmutableList.of("tag1", "tag2"));
   }

   protected Injector createInjector() {
      this.baseDir = "target/nodemetadatastoretest";
      this.nodeId1 = "testmeta1";
      this.nodeId2 = "testmeta2";
      Properties overrides = new Properties();
      overrides.setProperty(NodePoolProperties.BACKEND_PROVIDER, "stub");
      overrides.setProperty(NodePoolProperties.BASEDIR, baseDir);
      // note no ssh module since we are stub and not trying ssh, yet
      overrides.setProperty(NodePoolProperties.BACKEND_MODULES, SLF4JLoggingModule.class.getName());
      Context nodePoolCtx = ContextBuilder.newBuilder("nodepool").credentials("foo", "bar").overrides(overrides)
               .build();
      return nodePoolCtx.utils().getInjector();
   }

   @Test(groups = "unit")
   public void testStore() throws FileNotFoundException, IOException {
      store.store(nodeMeta1, templateOptions, "testgroup");
      store.store(nodeMeta2, templateOptions, "testgroup");
      String readJSon = Strings2.toStringAndClose(new FileInputStream(baseDir + File.separator + "nodes"
               + File.separator + nodeId1));
      assertEquals(
               readJSon,
               "{\"userGroup\":\"testgroup\",\"userTags\":[\"tag1\",\"tag2\"],\"userMetadata\":{\"testmetakey\":\"testmetavalue\",\"testmetakey2\":\"testmetavalue2\"},\"user\":\"testuser\",\"password\":\"testpass\",\"privateKey\":\"pk\",\"authenticateSudo\":true}");
   }

   @Test(groups = "unit", dependsOnMethods = "testStore")
   public void testLoad() {
      NodeMetadata loaded = store.load(nodeMeta1);
      assertEquals(loaded.getId(), nodeId1);
      assertTrue(loaded.getTags().contains("tag1"));
      assertTrue(loaded.getTags().contains("tag2"));
      assertTrue(loaded.getUserMetadata().containsKey("testmetakey")
               && loaded.getUserMetadata().get("testmetakey").equals("testmetavalue"));
      assertTrue(loaded.getUserMetadata().containsKey("testmetakey2")
               && loaded.getUserMetadata().get("testmetakey2").equals("testmetavalue2"));
      assertEquals(loaded.getCredentials().getUser(), "testuser");
      assertEquals(loaded.getCredentials().getPassword(), "testpass");
      assertEquals(loaded.getCredentials().getPrivateKey(), "pk");
      assertEquals(loaded.getCredentials().shouldAuthenticateSudo(), true);
      assertEquals(loaded.getGroup(), "testgroup");
   }

   @Test(groups = "unit", dependsOnMethods = "testLoad")
   public void testLoadAll() {
      Set<NodeMetadata> loaded = store.loadAll(ImmutableSet.of(nodeMeta1, nodeMeta2));
      assertSame(2, loaded.size());
   }

   @Test(groups = "unit", dependsOnMethods = "testLoadAll")
   public void testDeleteMapping() {
      store.deleteMapping(nodeId1);
      // make sure the other node is still there and this one isn't
      assertNull(store.load(nodeMeta1));
      assertEquals(nodeMeta2.getId(), store.load(nodeMeta2).getId());
   }

   public void testDeleteAllMappings() {
      store.deleteAllMappings();
      assertNull(store.load(nodeMeta1));
      assertNull(store.load(nodeMeta2));
      assertSame(new File(baseDir + File.separator + "nodes").listFiles().length, 0);
   }

}