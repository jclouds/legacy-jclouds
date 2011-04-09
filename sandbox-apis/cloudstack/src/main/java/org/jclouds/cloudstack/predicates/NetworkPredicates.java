/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.cloudstack.predicates;

import static com.google.common.base.Preconditions.checkNotNull;

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

   public static Predicate<NetworkService> supportsStaticNAT = Predicates.and(new NetworkServiceNamed("Firewall"),
            new CapabilitiesInclude("StaticNat"));

   public static Predicate<NetworkService> supportsPortForwarding = Predicates.and(new NetworkServiceNamed("Firewall"),
            new CapabilitiesInclude("PortForwarding"));

   public static Predicate<NetworkService> isLoadBalancerService = new NetworkServiceNamed("Lb");

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
    * @return always returns true.
    */
   public static Predicate<Network> any() {
      return Predicates.alwaysTrue();
   }
}
