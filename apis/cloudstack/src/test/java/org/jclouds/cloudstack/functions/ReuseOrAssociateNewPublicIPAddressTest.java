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
package org.jclouds.cloudstack.functions;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.jclouds.cloudstack.options.AssociateIPAddressOptions.Builder.networkId;
import static org.jclouds.cloudstack.options.ListPublicIPAddressesOptions.Builder.allocatedOnly;
import static org.testng.Assert.assertEquals;

import org.jclouds.cloudstack.CloudStackClient;
import org.jclouds.cloudstack.domain.AsyncCreateResponse;
import org.jclouds.cloudstack.domain.Network;
import org.jclouds.cloudstack.domain.PublicIPAddress;
import org.jclouds.cloudstack.features.AddressClient;
import org.jclouds.cloudstack.strategy.BlockUntilJobCompletesAndReturnResult;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class ReuseOrAssociateNewPublicIPAddressTest {
   long networkId = 99l;
   long zoneId = 100l;
   // note that it is associated network, not networkId
   PublicIPAddress address = PublicIPAddress.builder().id(200).state(PublicIPAddress.State.ALLOCATED)
         .associatedNetworkId(networkId).zoneId(zoneId).build();

   public void testReuseWorks() throws SecurityException, NoSuchMethodException {

      // create mocks
      CloudStackClient client = createMock(CloudStackClient.class);
      BlockUntilJobCompletesAndReturnResult blockUntilJobCompletesAndReturnResult = createMock(BlockUntilJobCompletesAndReturnResult.class);
      AddressClient addressClient = createMock(AddressClient.class);
      expect(client.getAddressClient()).andReturn(addressClient).atLeastOnce();

      // an address is available
      expect(addressClient.listPublicIPAddresses(allocatedOnly(true).networkId(networkId))).andReturn(
            ImmutableSet.<PublicIPAddress> of(address));

      replay(client);
      replay(blockUntilJobCompletesAndReturnResult);
      replay(addressClient);

      assertEquals(
            new ReuseOrAssociateNewPublicIPAddress(client, blockUntilJobCompletesAndReturnResult).apply(Network
                  .builder().id(networkId).zoneId(zoneId).build()), address);

      verify(client);
      verify(blockUntilJobCompletesAndReturnResult);
      verify(addressClient);

   }

   public void testAssociateWorks() throws SecurityException, NoSuchMethodException {

      // create mocks
      CloudStackClient client = createMock(CloudStackClient.class);
      BlockUntilJobCompletesAndReturnResult blockUntilJobCompletesAndReturnResult = createMock(BlockUntilJobCompletesAndReturnResult.class);
      AddressClient addressClient = createMock(AddressClient.class);
      expect(client.getAddressClient()).andReturn(addressClient).atLeastOnce();

      // no ip addresses available
      expect(addressClient.listPublicIPAddresses(allocatedOnly(true).networkId(networkId))).andReturn(
            ImmutableSet.<PublicIPAddress> of());

      AsyncCreateResponse job = new AsyncCreateResponse(1, 2);
      // make sure we created the job relating to a new ip
      expect(addressClient.associateIPAddressInZone(zoneId, networkId(networkId))).andReturn(job);

      expect(blockUntilJobCompletesAndReturnResult.apply(job)).andReturn(address);

      replay(client);
      replay(addressClient);
      replay(blockUntilJobCompletesAndReturnResult);

      assertEquals(
            new ReuseOrAssociateNewPublicIPAddress(client, blockUntilJobCompletesAndReturnResult).apply(Network
                  .builder().id(networkId).zoneId(zoneId).build()), address);

      verify(client);
      verify(addressClient);
      verify(blockUntilJobCompletesAndReturnResult);

   }

}
