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
package org.jclouds.byon.functions;

import static org.testng.Assert.assertEquals;

import java.net.URI;
import java.util.Map;
import java.util.Set;

import org.jclouds.byon.suppliers.SupplyFromProviderURIOrNodesProperty;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.NodeMetadata.Status;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.jclouds.domain.LoginCredentials;
import org.testng.annotations.Test;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

/**
 * 
 * @author Adrian Cole
 */
@Test(singleThreaded = true, testName = "NodeToNodeMetadataTest")
public class NodeToNodeMetadataTest {
   public static Location expectedProviderLocationFromResource(String resource) {
      return new LocationBuilder().scope(LocationScope.PROVIDER).id("byon").description(resource).build();
   }

   public static Location zoneCalled(String zone, Location parent) {
      return new LocationBuilder().scope(LocationScope.ZONE).id(zone).description(zone).parent(parent).build();
   }

   String resource = "location";

   Location provider = expectedProviderLocationFromResource(resource);

   Map<String, Credentials> credentialStore = Maps.newLinkedHashMap();

   NodeToNodeMetadata parser = new NodeToNodeMetadata(Suppliers.ofInstance(provider),
         Suppliers.<Set<? extends Location>> ofInstance(ImmutableSet.of(provider, zoneCalled("virginia", provider))),
         new SupplyFromProviderURIOrNodesProperty(URI.create("test")), credentialStore);

   public static NodeMetadata expectedNodeMetadataFromResource(String resource) {
      return expectedNodeMetadataFromResource(resource, expectedProviderLocationFromResource(resource));
   }

   public static NodeMetadata expectedNodeMetadataFromResource(String resource, Location location) {
      return expectedNodeMetadataFromResource(1, resource, location);
   }

   public static NodeMetadata expectedNodeMetadataFromResource(int id, String resource, Location location) {
      return expectedNodeMetadataFromResource(id, resource, location, 22);
   }

   public static NodeMetadata expectedNodeMetadataFromResource(int id, String resource, Location location, int loginPort) {
      return new NodeMetadataBuilder()
            .ids("cluster-" + id)
            .group("hadoop")
            .name("cluster-" + id)
            .loginPort(loginPort)
            .hostname("cluster-" + id + ".mydomain.com")
            .location(location)
            .userMetadata(ImmutableMap.of("Name", "foo"))
            .tags(ImmutableSet.of("vanilla"))
            .status(Status.RUNNING)
            .operatingSystem(
                  OperatingSystem.builder().description("redhat").family(OsFamily.RHEL).arch("x86").version("5.3")
                        .build())
            .publicAddresses(ImmutableSet.of("cluster-" + id + ".mydomain.com"))
            .credentials(
                  LoginCredentials.builder().user("myUser").privateKey(NodesFromYamlTest.key).password("happy bear")
                        .authenticateSudo(true).build()).build();
   }

   @Test
   public void testNodesParse() throws Exception {
      assertEquals(parser.apply(NodesFromYamlTest.TEST1), expectedNodeMetadataFromResource(resource, provider));
      assertEquals(credentialStore, ImmutableMap.of("node#cluster-1", new Credentials("myUser", NodesFromYamlTest.key)));
   }

   @Test
   public void testNodesParseLocation() throws Exception {
      assertEquals(parser.apply(NodesFromYamlTest.TEST2),
            expectedNodeMetadataFromResource(resource, zoneCalled("virginia", provider)));
      assertEquals(credentialStore, ImmutableMap.of("node#cluster-1", new Credentials("myUser", NodesFromYamlTest.key)));
   }

   @Test
   public void testNodesParseLoginPort() throws Exception {
      assertEquals(parser.apply(NodesFromYamlTest.TEST3), expectedNodeMetadataFromResource(2, resource, provider, 2022));
   }
}
