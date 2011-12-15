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
package org.jclouds.byon;

import static org.jclouds.byon.functions.NodeToNodeMetadataTest.expectedNodeMetadataFromResource;
import static org.jclouds.byon.functions.NodeToNodeMetadataTest.expectedProviderLocationFromResource;
import static org.jclouds.byon.functions.NodeToNodeMetadataTest.zoneCalled;
import static org.testng.Assert.assertEquals;

import java.net.URI;
import java.util.Properties;

import org.jclouds.byon.config.CacheNodeStoreModule;
import org.jclouds.byon.functions.NodesFromYamlTest;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.domain.Location;
import org.jclouds.domain.LoginCredentials;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.inject.Module;

/**
 * 
 * @author Adrian Cole
 */
@Test(singleThreaded = true, testName = "BYONComputeServiceTest")
public class BYONComputeServiceTest {

   @Test
   public void testNodesParseNodeMap() throws Exception {
      assertNodesParse(
            "foo",
            ImmutableSet.<Module> of(new CacheNodeStoreModule(ImmutableMap.<String, Node> of(
                  NodesFromYamlTest.TEST1.getId(), NodesFromYamlTest.TEST1))));
   }

   @Test
   public void testNodesParseWithFileUrl() throws Exception {
      assertNodesParse("file://" + getClass().getResource("/test1.yaml").getPath(), ImmutableSet.<Module> of());
   }

   @Test
   public void testNodesParseWithClasspathUrl() throws Exception {
      assertNodesParse("classpath:///test1.yaml", ImmutableSet.<Module> of());
   }

   private void assertNodesParse(String endpoint, Iterable<Module> modules) {
      ComputeServiceContext context = null;
      try {
         Location providerLocation = expectedProviderLocationFromResource(endpoint);

         Properties props = new Properties();
         props.setProperty("byon.endpoint", endpoint);
         context = new ComputeServiceContextFactory().createContext("byon", "foo", "bar", modules, props);

         assertEquals(context.getProviderSpecificContext().getEndpoint(), URI.create(endpoint));

         @SuppressWarnings("unchecked")
         Supplier<LoadingCache<String, Node>> supplier = (Supplier<LoadingCache<String, Node>>) context.getProviderSpecificContext()
               .getApi();

         assertEquals(supplier.get().size(), context.getComputeService().listNodes().size());
         assertEquals(supplier.get().asMap(),
               ImmutableMap.<String, Node> of(NodesFromYamlTest.TEST1.getId(), NodesFromYamlTest.TEST1));

         assertEquals(context.getComputeService().listNodes().toString(),
               ImmutableSet.of(expectedNodeMetadataFromResource(endpoint)).toString());
         assertEquals(context.getComputeService().listAssignableLocations(), ImmutableSet.of(providerLocation));
      } finally {
         if (context != null)
            context.close();
      }
   }

   public void testNodesWithLocations() {
      ComputeServiceContext context = null;
      try {
         String endpoint = "file://" + getClass().getResource("/test_location.yaml").getPath();
         Properties props = new Properties();
         props.setProperty("byon.endpoint", endpoint);
         context = new ComputeServiceContextFactory().createContext("byon", "foo", "bar", ImmutableSet.<Module> of(),
               props);

         assertEquals(context.getProviderSpecificContext().getEndpoint(), URI.create(endpoint));

         @SuppressWarnings("unchecked")
         Supplier<LoadingCache<String, Node>> supplier = (Supplier<LoadingCache<String, Node>>) context.getProviderSpecificContext()
               .getApi();

         assertEquals(supplier.get().size(), context.getComputeService().listNodes().size());
         assertEquals(supplier.get().asMap(), ImmutableMap.<String, Node> of(NodesFromYamlTest.TEST2.getId(),
               NodesFromYamlTest.TEST2, NodesFromYamlTest.TEST3.getId(), NodesFromYamlTest.TEST3));
         Location providerLocation = expectedProviderLocationFromResource(endpoint);

         Location virginia = zoneCalled("virginia", providerLocation);
         Location maryland = zoneCalled("maryland", providerLocation);

         assertEquals(
               context.getComputeService().listNodes().toString(),
               ImmutableSet.of(expectedNodeMetadataFromResource(1, endpoint, virginia),
                     expectedNodeMetadataFromResource(2, endpoint, maryland, 2022)).toString());

         assertEquals(NodeMetadata.class.cast(Iterables.get(context.getComputeService().listNodes(), 0))
               .getCredentials(),
               LoginCredentials
                     .builder()
                     .user("myUser")
                     .password("happy bear")
                     .authenticateSudo(true)
                     .privateKey(
                           "-----BEGIN RSA PRIVATE KEY-----\n"
                                 + "MIIEowIBAAKCAQEAuzaE6azgUxwESX1rCGdJ5xpdrc1XC311bOGZBCE8NA+CpFh2\n"
                                 + "u01Vfv68NC4u6LFgdXSY1vQt6hiA5TNqQk0TyVfFAunbXgTekF6XqDPQUf1nq9aZ\n"
                                 + "lMvo4vlaLDKBkhG5HJE/pIa0iB+RMZLS0GhxsIWerEDmYdHKM25o\n"
                                 + "-----END RSA PRIVATE KEY-----\n").build()

         );

         assertEquals(context.getComputeService().listAssignableLocations(), ImmutableSet.of(virginia, maryland));
      } finally {
         if (context != null)
            context.close();
      }
   }
}
