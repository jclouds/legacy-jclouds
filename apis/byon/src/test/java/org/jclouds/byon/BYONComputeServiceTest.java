/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.byon;

import static org.testng.Assert.assertEquals;

import java.net.URI;
import java.util.Map;
import java.util.Properties;

import org.jclouds.byon.functions.NodeToNodeMetadataTest;
import org.jclouds.byon.functions.NodesFromYamlTest;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.ssh.jsch.config.JschSshClientModule;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "live")
public class BYONComputeServiceTest {

   @Test
   public void testNodesParseWithFileUrl() throws Exception {
      assertNodesParse("file://" + getClass().getResource("/test1.yaml").getPath());
   }

   @Test
   public void testNodesParseWithClasspathUrl() throws Exception {
      assertNodesParse("classpath:///test1.yaml");
   }

   private void assertNodesParse(String endpoint) {
      ComputeServiceContext context = null;
      try {

         Properties props = new Properties();
         props.setProperty("byon.endpoint", endpoint);
         context = new ComputeServiceContextFactory().createContext("byon", "foo", "bar", ImmutableSet
                  .<Module> of(new JschSshClientModule()), props);

         assertEquals(context.getProviderSpecificContext().getEndpoint(), URI.create(endpoint));

         @SuppressWarnings("unchecked")
         Supplier<Map<String, Node>> supplier = (Supplier<Map<String, Node>>) context.getProviderSpecificContext()
                  .getApi();

         assertEquals(supplier.get().size(), context.getComputeService().listNodes().size());
         assertEquals(supplier.get(), ImmutableMap.<String, Node> of(NodesFromYamlTest.TEST1.id,
                  NodesFromYamlTest.TEST1));

         assertEquals(context.getComputeService().listNodes(), ImmutableSet.of(NodeToNodeMetadataTest
                  .expectedNodeMetadataFromResource(endpoint)));

      } finally {
         if (context != null)
            context.close();
      }
   }
}
