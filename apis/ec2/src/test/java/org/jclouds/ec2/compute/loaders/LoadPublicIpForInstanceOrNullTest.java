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
package org.jclouds.ec2.compute.loaders;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.testng.Assert.assertEquals;

import org.jclouds.ec2.EC2Client;
import org.jclouds.ec2.compute.domain.RegionAndName;
import org.jclouds.ec2.domain.PublicIpInstanceIdPair;
import org.jclouds.ec2.services.ElasticIPAddressClient;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", singleThreaded = true, testName = "LoadPublicIpForInstanceOrNullTest")
public class LoadPublicIpForInstanceOrNullTest {

   @Test
   public void testReturnsPublicIpOnMatch() throws Exception {
      EC2Client client = createMock(EC2Client.class);
      ElasticIPAddressClient ipClient = createMock(ElasticIPAddressClient.class);

      expect(client.getElasticIPAddressServices()).andReturn(ipClient).atLeastOnce();
      expect(ipClient.describeAddressesInRegion("region")).andReturn(
               ImmutableSet.<PublicIpInstanceIdPair> of(new PublicIpInstanceIdPair("region", "1.1.1.1", "i-blah")))
               .atLeastOnce();

      replay(client);
      replay(ipClient);

      LoadPublicIpForInstanceOrNull parser = new LoadPublicIpForInstanceOrNull(client);

      assertEquals(parser.load(new RegionAndName("region", "i-blah")), "1.1.1.1");

      verify(client);
      verify(ipClient);
   }

   @Test
   public void testReturnsNullWhenNotFound() throws Exception {
      EC2Client client = createMock(EC2Client.class);
      ElasticIPAddressClient ipClient = createMock(ElasticIPAddressClient.class);

      expect(client.getElasticIPAddressServices()).andReturn(ipClient).atLeastOnce();

      expect(ipClient.describeAddressesInRegion("region")).andReturn(ImmutableSet.<PublicIpInstanceIdPair> of())
               .atLeastOnce();

      replay(client);
      replay(ipClient);

      LoadPublicIpForInstanceOrNull parser = new LoadPublicIpForInstanceOrNull(client);

      assertEquals(parser.load(new RegionAndName("region", "i-blah")), null);

      verify(client);
      verify(ipClient);

   }

   @Test
   public void testReturnsNullWhenNotAssigned() throws Exception {
      EC2Client client = createMock(EC2Client.class);
      ElasticIPAddressClient ipClient = createMock(ElasticIPAddressClient.class);

      expect(client.getElasticIPAddressServices()).andReturn(ipClient).atLeastOnce();

      expect(ipClient.describeAddressesInRegion("region")).andReturn(
               ImmutableSet.<PublicIpInstanceIdPair> of(new PublicIpInstanceIdPair("region", "1.1.1.1", null)))
               .atLeastOnce();

      replay(client);
      replay(ipClient);

      LoadPublicIpForInstanceOrNull parser = new LoadPublicIpForInstanceOrNull(client);

      assertEquals(parser.load(new RegionAndName("region", "i-blah")), null);

      verify(client);
      verify(ipClient);

   }

}
