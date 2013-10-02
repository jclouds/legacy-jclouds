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

import org.jclouds.cloudstack.domain.PublicIPAddress;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

/**
 * 
 * @author Adrian Cole
 */
public class PublicIPAddressPredicates {

   public static final class AssociatedWithNetwork implements Predicate<PublicIPAddress> {
      private final String networkId;

      public AssociatedWithNetwork(String networkId) {
         this.networkId = checkNotNull(networkId, "networkId");
      }

      @Override
      public boolean apply(PublicIPAddress input) {
         return networkId.equals(checkNotNull(input, "ipaddress").getAssociatedNetworkId());
      }

      @Override
      public String toString() {
         return "associatedWithNetwork(" + networkId + ")";
      }
   }

   public static enum Available implements Predicate<PublicIPAddress> {
      INSTANCE;

      @Override
      public boolean apply(PublicIPAddress arg0) {
         return !checkNotNull(arg0, "ipaddress").isSourceNAT() && !arg0.isStaticNAT()
               && arg0.getVirtualMachineId() == null && arg0.getState() == PublicIPAddress.State.ALLOCATED;
      }

      @Override
      public String toString() {
         return "available()";
      }
   }

   /**
    * 
    * @return true, if the public ip address is not assigned to a VM or NAT rule
    */
   public static Predicate<PublicIPAddress> available() {
      return Available.INSTANCE;
   }

   /**
    * 
    * @return true, if the public ip address is associated with the specified
    *         network
    */
   public static Predicate<PublicIPAddress> associatedWithNetwork(final String networkId) {
      return new AssociatedWithNetwork(networkId);
   }

   /**
    * 
    * @return always returns true.
    */
   public static Predicate<PublicIPAddress> any() {
      return Predicates.alwaysTrue();
   }
}
