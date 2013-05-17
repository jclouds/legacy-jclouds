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
package org.jclouds.cloudstack.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.and;
import static com.google.common.collect.Iterables.find;
import static org.jclouds.cloudstack.options.AssociateIPAddressOptions.Builder.networkId;
import static org.jclouds.cloudstack.options.ListPublicIPAddressesOptions.Builder.allocatedOnly;
import static org.jclouds.cloudstack.predicates.PublicIPAddressPredicates.associatedWithNetwork;
import static org.jclouds.cloudstack.predicates.PublicIPAddressPredicates.available;
import static org.jclouds.compute.reference.ComputeServiceConstants.COMPUTE_LOGGER;

import java.util.NoSuchElementException;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.cloudstack.CloudStackClient;
import org.jclouds.cloudstack.domain.AsyncCreateResponse;
import org.jclouds.cloudstack.domain.Network;
import org.jclouds.cloudstack.domain.PublicIPAddress;
import org.jclouds.cloudstack.features.AddressClient;
import org.jclouds.cloudstack.strategy.BlockUntilJobCompletesAndReturnResult;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class ReuseOrAssociateNewPublicIPAddress implements Function<Network, PublicIPAddress> {
   private final CloudStackClient client;
   private final BlockUntilJobCompletesAndReturnResult blockUntilJobCompletesAndReturnResult;
   @Resource
   @Named(COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   @Inject
   public ReuseOrAssociateNewPublicIPAddress(CloudStackClient client,
         BlockUntilJobCompletesAndReturnResult blockUntilJobCompletesAndReturnResult) {
      this.client = checkNotNull(client, "client");
      this.blockUntilJobCompletesAndReturnResult = checkNotNull(blockUntilJobCompletesAndReturnResult,
            "blockUntilJobCompletesAndReturnResult");
   }

   /**
    * Finds existing addresses who are ready for use and not assigned to a
    * machine.
    * 
    * @param networkId
    *           network id to search
    * @param client
    *           address client
    * @return address to use
    * @throws NoSuchElementException
    *            if there's no existing ip address that is free for use
    */
   public static PublicIPAddress findAvailableAndAssociatedWithNetwork(String networkId, AddressClient client) {
      return find(client.listPublicIPAddresses(allocatedOnly(true).networkId(networkId)),
            and(associatedWithNetwork(networkId), available()));
   }

   public static PublicIPAddress associateIPAddressInNetwork(Network network, CloudStackClient client,
         BlockUntilJobCompletesAndReturnResult blockUntilJobCompletesAndReturnResult) {
      AsyncCreateResponse job = client.getAddressClient().associateIPAddressInZone(network.getZoneId(),
            networkId(network.getId()));
      PublicIPAddress ip = blockUntilJobCompletesAndReturnResult.<PublicIPAddress> apply(job);
      assert ip.getZoneId().equals(network.getZoneId());
      return ip;
   }

   @Override
   public PublicIPAddress apply(Network input) {
      try {
         logger.debug(">> looking for existing address in network(%s)", input.getId());
         PublicIPAddress returnVal = findAvailableAndAssociatedWithNetwork(input.getId(), client.getAddressClient());
         logger.debug("<< reused address(%s)", returnVal.getId());
         return returnVal;
      } catch (NoSuchElementException e) {
         logger.debug(">> associating new address in network(%s)", input.getId());
         PublicIPAddress returnVal = associateIPAddressInNetwork(input, client, blockUntilJobCompletesAndReturnResult);
         logger.debug("<< associated address(%s)", returnVal.getId());
         return returnVal;
      }
   }
}
