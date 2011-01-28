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

import java.util.Map;

import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.domain.NodeState;
import org.jclouds.compute.domain.OperatingSystemBuilder;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import org.jclouds.domain.LocationScope;
import org.jclouds.domain.internal.LocationImpl;
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
   public static final Location location = new LocationImpl(LocationScope.PROVIDER, "byon", "byon", null);

   public static final NodeMetadata TEST1 = new NodeMetadataBuilder().ids("cluster-1").tag("hadoop").name("cluster-1").location(
            location).state(NodeState.RUNNING).operatingSystem(
            new OperatingSystemBuilder().name("redhat").family(OsFamily.RHEL).arch("x86").version("5.3").description(
                     "xyz").build()).publicAddresses(ImmutableSet.of("cluster-1.mydomain.com")).credentials(
            new Credentials("myUser", "fancyfoot")).adminPassword("sudo").build();

   @Test
   public void testNodesParse() throws Exception {

      Map<String, Credentials> credentialStore = Maps.newLinkedHashMap();

      NodeToNodeMetadata parser = new NodeToNodeMetadata(Suppliers.ofInstance(location), credentialStore);

      assertEquals(parser.apply(NodesFromYamlTest.TEST1), TEST1);
      assertEquals(credentialStore, ImmutableMap.of("node#cluster-1",  new Credentials("myUser", "fancyfoot")));

   }
}
