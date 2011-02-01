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

package org.jclouds.byon.functions;

import static org.testng.Assert.assertEquals;

import java.net.URI;
import java.util.Map;

import org.jclouds.byon.suppliers.SupplyFromProviderURIOrNodesProperty;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.domain.OperatingSystemBuilder;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.testng.annotations.Test;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

/**
 * 
 * @author Adrian Cole
 */
public class NodeToNodeMetadataTest {
   public static Location expectedLocationFromResource(String resource) {
      return new LocationBuilder().scope(LocationScope.PROVIDER).id("byon").description(resource).build();
   }

   public static NodeMetadata expectedNodeMetadataFromResource(String resource) {
      Location location = expectedLocationFromResource(resource);

      return new NodeMetadataBuilder().ids("cluster-1").group("hadoop").name("cluster-1").location(location).state(
               NodeState.RUNNING).operatingSystem(
               new OperatingSystemBuilder().description("redhat").family(OsFamily.RHEL).arch("x86").version("5.3")
                        .build()).publicAddresses(ImmutableSet.of("cluster-1.mydomain.com")).credentials(
               new Credentials("myUser", NodesFromYamlTest.key)).adminPassword("happy bear").build();
   }

   @Test
   public void testNodesParse() throws Exception {

      Map<String, Credentials> credentialStore = Maps.newLinkedHashMap();

      NodeToNodeMetadata parser = new NodeToNodeMetadata(
               Suppliers.ofInstance(expectedLocationFromResource("location")),
               new SupplyFromProviderURIOrNodesProperty(URI.create("test")), credentialStore);

      assertEquals(parser.apply(NodesFromYamlTest.TEST1), expectedNodeMetadataFromResource("location"));
      assertEquals(credentialStore, ImmutableMap.of("node#cluster-1", new Credentials("myUser", NodesFromYamlTest.key)));

   }
}
