/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.byon;

import static org.jclouds.byon.functions.NodeToNodeMetadataTest.expectedNodeMetadataFromResource;
import static org.jclouds.byon.functions.NodeToNodeMetadataTest.expectedProviderLocationFromResource;
import static org.jclouds.byon.functions.NodeToNodeMetadataTest.zoneCalled;
import static org.testng.Assert.assertEquals;

import org.jclouds.ContextBuilder;
import org.jclouds.byon.config.BYONComputeServiceContextModule;
import org.jclouds.byon.config.CacheNodeStoreModule;
import org.jclouds.byon.functions.NodesFromYamlTest;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.domain.Location;
import org.jclouds.domain.LoginCredentials;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

/**
 * 
 * @author Adrian Cole
 */
@Test(singleThreaded = true, testName = "BYONComputeServiceTest")
public class BYONComputeServiceTest {

   @Test
   public void testNodesParseNodeMap() throws Exception {
      assertNodesParse("foo", ContextBuilder.newBuilder(
               new BYONApiMetadata().toBuilder().defaultModule(BYONComputeServiceContextModule.class).build())
               .endpoint("foo").modules(
                        ImmutableSet.<Module> of(new CacheNodeStoreModule(ImmutableMap.<String, Node> of(
                                 NodesFromYamlTest.TEST1.getId(), NodesFromYamlTest.TEST1)))).build(
                        ComputeServiceContext.class));
   }

   @Test
   public void testNodesParseWithFileUrl() throws Exception {
      assertNodesParse("file://" + getClass().getResource("/test1.yaml").getPath(),  ContextBuilder.newBuilder(new BYONApiMetadata()).endpoint(
               "file://" + getClass().getResource("/test1.yaml").getPath()).build(ComputeServiceContext.class));
   }

   @Test
   public void testNodesParseWithClasspathUrl() throws Exception {
      assertNodesParse("classpath:///test1.yaml", ContextBuilder.newBuilder(new BYONApiMetadata()).endpoint(
               "classpath:///test1.yaml").build(ComputeServiceContext.class));
   }

   private void assertNodesParse(String endpoint, ComputeServiceContext context) {
      try {
         Location providerLocation = expectedProviderLocationFromResource(endpoint);

         Supplier<LoadingCache<String, Node>> supplier = supplier(context);

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
         context = ContextBuilder.newBuilder(new BYONApiMetadata()).endpoint(endpoint).build(ComputeServiceContext.class);

         Supplier<LoadingCache<String, Node>> supplier = supplier(context);

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

   private Supplier<LoadingCache<String, Node>> supplier(ComputeServiceContext context) {
      Supplier<LoadingCache<String, Node>> supplier = context.utils().injector().getInstance(
               Key.get(new TypeLiteral<Supplier<LoadingCache<String, Node>>>() {
               }));
      return supplier;
   }
}
