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
package org.jclouds.cloudstack.predicates;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.cloudstack.domain.GuestIPType;
import org.jclouds.cloudstack.domain.Network;
import org.jclouds.cloudstack.domain.NetworkService;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;

/**
 * 
 * @author Adrian Cole
 */
public class NetworkPredicates {

   public static enum HasFirewallServiceWhichSupportsStaticNAT implements Predicate<Network> {
      INSTANCE;

      @Override
      public boolean apply(Network arg0) {
         return Iterables.any(checkNotNull(arg0, "network").getServices(), supportsStaticNAT);
      }

      @Override
      public String toString() {
         return supportsStaticNAT.toString();
      }
   }

   public static enum HasFirewallServiceWhichSupportsPortForwarding implements Predicate<Network> {
      INSTANCE;

      @Override
      public boolean apply(Network arg0) {
         return Iterables.any(checkNotNull(arg0, "network").getServices(), supportsPortForwarding);
      }

      @Override
      public String toString() {
         return supportsPortForwarding.toString();
      }
   }

   public static enum HasLoadBalancerService implements Predicate<Network> {
      INSTANCE;

      @Override
      public boolean apply(Network arg0) {
         return Iterables.any(checkNotNull(arg0, "network").getServices(), isLoadBalancerService);
      }

      @Override
      public String toString() {
         return isLoadBalancerService.toString();
      }
   }

   public static enum IsVirtualNetwork implements Predicate<Network> {
      INSTANCE;

      @Override
      public boolean apply(Network arg0) {
         boolean network = isVirtualNetwork.apply(checkNotNull(arg0, "network").getGuestIPType());
         return network;
      }

      @Override
      public String toString() {
         return isVirtualNetwork.toString();
      }
   }
    
   public static enum IsSharedNetwork implements Predicate<Network> {
      INSTANCE;

      @Override
      public boolean apply(Network arg0) {
         boolean network = isSharedNetwork.apply(checkNotNull(arg0, "network").getGuestIPType());
         return network;
      }

      @Override
      public String toString() {
         return isSharedNetwork.toString();
      }
   }

   public static enum IsIsolatedNetwork implements Predicate<Network> {
      INSTANCE;

      @Override
      public boolean apply(Network arg0) {
         boolean network = isIsolatedNetwork.apply(checkNotNull(arg0, "network").getGuestIPType());
         return network;
      }

      @Override
      public String toString() {
         return isIsolatedNetwork.toString();
      }
   }

    
   private static class DefaultNetworkInZone implements Predicate<Network> {
      private final String zoneId;

      public DefaultNetworkInZone(String zoneId) {
         this.zoneId = zoneId;
      }

      @Override
      public boolean apply(Network network) {
         return network.getZoneId().equals(zoneId) && network.isDefault();
      }
   }

   public static class NetworkServiceNamed implements Predicate<NetworkService> {
      private final String name;

      public NetworkServiceNamed(String name) {
         this.name = checkNotNull(name, "name");
      }

      @Override
      public boolean apply(NetworkService input) {
         return name.equals(checkNotNull(input, "networkService").getName());
      }

      @Override
      public String toString() {
         return "networkServiceNamed(" + name + ")";
      }
   }

   public static class GuestIPTypeIs implements Predicate<GuestIPType> {
      private final GuestIPType guestIPType;

      public GuestIPTypeIs(GuestIPType guestIPType) {
         this.guestIPType = guestIPType;
      }

      @Override
      public boolean apply(GuestIPType guestIPType) {
         return guestIPType == this.guestIPType;
      }

      @Override
      public String toString() {
         return "guestIPTypeIs(" + guestIPType + ')';
      }
   }
   
   public static class CapabilitiesInclude implements Predicate<NetworkService> {
      private final String capability;

      public CapabilitiesInclude(String capability) {
         this.capability = checkNotNull(capability, "capability");
      }

      @Override
      public boolean apply(NetworkService input) {
         return "true".equals(input.getCapabilities().get(capability));
      }

      @Override
      public String toString() {
         return "capabilitiesInclude(" + capability + ")";
      }
   }

   public static final Predicate<NetworkService> supportsStaticNAT = Predicates.and(new NetworkServiceNamed("Firewall"),
         new CapabilitiesInclude("StaticNat"));

   public static final Predicate<NetworkService> supportsPortForwarding = Predicates.and(new NetworkServiceNamed("Firewall"),
         new CapabilitiesInclude("PortForwarding"));

   public static final Predicate<NetworkService> isLoadBalancerService = new NetworkServiceNamed("Lb");

   public static final Predicate<GuestIPType> isVirtualNetwork = new GuestIPTypeIs(GuestIPType.VIRTUAL);

   public static final Predicate<GuestIPType> isIsolatedNetwork = new GuestIPTypeIs(GuestIPType.ISOLATED);

   public static final Predicate<GuestIPType> isSharedNetwork = new GuestIPTypeIs(GuestIPType.SHARED);

   /**
    * 
    * @return true, if the network supports static NAT.
    */
   public static Predicate<Network> supportsStaticNAT() {
      return HasFirewallServiceWhichSupportsStaticNAT.INSTANCE;
   }

   /**
    * 
    * @return true, if the network supports port forwarding.
    */
   public static Predicate<Network> supportsPortForwarding() {
      return HasFirewallServiceWhichSupportsPortForwarding.INSTANCE;
   }

   /**
    *
    * @return true, if the network supports load balancing.
    */
   public static Predicate<Network> hasLoadBalancerService() {
      return HasLoadBalancerService.INSTANCE;
   }

   /**
    *
    * @return true, if the network is a virtual network.
    */
   public static Predicate<Network> isVirtualNetwork() {
      return IsVirtualNetwork.INSTANCE;
   }

   /**
    *
    * @return true, if the network is an isolated network.
    */
   public static Predicate<Network> isIsolatedNetwork() {
      return IsIsolatedNetwork.INSTANCE;
   }

   /**
    *
    * @return true, if the network is a shared network.
    */
   public static Predicate<Network> isSharedNetwork() {
      return IsSharedNetwork.INSTANCE;
   }

   /**
    * Filters for default networks in a specific zone.
    *
    * @param zoneId the ID of the required zone.
    * @return networks in the zone that have the default flag set.
    */
   public static Predicate<Network> defaultNetworkInZone(final String zoneId) {
      return new DefaultNetworkInZone(zoneId);
   }

   /**
    * 
    * @return always returns true.
    */
   public static Predicate<Network> any() {
      return Predicates.alwaysTrue();
   }
}
