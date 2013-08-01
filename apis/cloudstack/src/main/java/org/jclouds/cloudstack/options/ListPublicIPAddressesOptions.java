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
package org.jclouds.cloudstack.options;

import com.google.common.collect.ImmutableSet;

/**
 * Options used to control what ip addresss information is returned
 * 
 * @see <a href=
 *      "http://download.cloud.com/releases/2.2.0/api/user/listIPAddresses.html"
 *      />
 * @author Adrian Cole
 */
public class ListPublicIPAddressesOptions extends AccountInDomainOptions {

   public static final ListPublicIPAddressesOptions NONE = new ListPublicIPAddressesOptions();

   /**
    * @param id
    *           lists ip address by id
    */
   public ListPublicIPAddressesOptions id(String id) {
      this.queryParameters.replaceValues("id", ImmutableSet.of(id + ""));
      return this;
   }

   /**
    * @param allocatedOnly
    *           limits search results to allocated public IP addresses
    */
   public ListPublicIPAddressesOptions allocatedOnly(boolean allocatedOnly) {
      this.queryParameters.replaceValues("allocatedonly", ImmutableSet.of(allocatedOnly + ""));
      return this;

   }

   /**
    * @param networkId
    *           list ip addresss by networkId.
    */
   public ListPublicIPAddressesOptions networkId(String networkId) {
      this.queryParameters.replaceValues("networkid", ImmutableSet.of(networkId + ""));
      return this;

   }

   /**
    * @param projectId
    *           list ip addresss by project.
    */
   public ListPublicIPAddressesOptions projectId(String projectId) {
      this.queryParameters.replaceValues("projectid", ImmutableSet.of(projectId + ""));
      return this;

   }

   /**
    * @param VLANId
    *           lists all public IP addresses by VLAN ID
    */
   public ListPublicIPAddressesOptions VLANId(String VLANId) {
      this.queryParameters.replaceValues("vlanid", ImmutableSet.of(VLANId + ""));
      return this;

   }

   /**
    * @param IPAddress
    *           lists the specified IP address
    */
   public ListPublicIPAddressesOptions IPAddress(String IPAddress) {
      this.queryParameters.replaceValues("ipaddress", ImmutableSet.of(IPAddress));
      return this;
   }

   /**
    * @param zoneId
    *           lists all public IP addresses by Zone ID
    */
   public ListPublicIPAddressesOptions zoneId(String zoneId) {
      this.queryParameters.replaceValues("zoneid", ImmutableSet.of(zoneId + ""));
      return this;

   }

   /**
    * @param usesVirtualNetwork
    *           the virtual network for the IP address
    */
   public ListPublicIPAddressesOptions usesVirtualNetwork(boolean usesVirtualNetwork) {
      this.queryParameters.replaceValues("forvirtualnetwork", ImmutableSet.of(usesVirtualNetwork + ""));
      return this;
   }

   public static class Builder {

      /**
       * @see ListPublicIPAddressesOptions#accountInDomain
       */
      public static ListPublicIPAddressesOptions accountInDomain(String account, String domain) {
         ListPublicIPAddressesOptions options = new ListPublicIPAddressesOptions();
         return options.accountInDomain(account, domain);
      }

      /**
       * @see ListPublicIPAddressesOptions#IPAddress
       */
      public static ListPublicIPAddressesOptions IPAddress(String IPAddress) {
         ListPublicIPAddressesOptions options = new ListPublicIPAddressesOptions();
         return options.IPAddress(IPAddress);
      }

      /**
       * @see ListPublicIPAddressesOptions#domainId
       */
      public static ListPublicIPAddressesOptions domainId(String id) {
         ListPublicIPAddressesOptions options = new ListPublicIPAddressesOptions();
         return options.domainId(id);
      }

      /**
       * @see ListPublicIPAddressesOptions#id
       */
      public static ListPublicIPAddressesOptions id(String id) {
         ListPublicIPAddressesOptions options = new ListPublicIPAddressesOptions();
         return options.id(id);
      }

      /**
       * @see ListPublicIPAddressesOptions#allocatedOnly
       */
      public static ListPublicIPAddressesOptions allocatedOnly(boolean allocatedOnly) {
         ListPublicIPAddressesOptions options = new ListPublicIPAddressesOptions();
         return options.allocatedOnly(allocatedOnly);
      }

      /**
       * @see ListPublicIPAddressesOptions#networkId
       */
      public static ListPublicIPAddressesOptions networkId(String id) {
         ListPublicIPAddressesOptions options = new ListPublicIPAddressesOptions();
         return options.networkId(id);
      }

      /**
       * @see ListPublicIPAddressesOptions#projectId(String)
       */
      public static ListPublicIPAddressesOptions projectId(String id) {
         ListPublicIPAddressesOptions options = new ListPublicIPAddressesOptions();
         return options.projectId(id);
      }

      /**
       * @see ListPublicIPAddressesOptions#VLANId
       */
      public static ListPublicIPAddressesOptions VLANId(String id) {
         ListPublicIPAddressesOptions options = new ListPublicIPAddressesOptions();
         return options.VLANId(id);
      }

      /**
       * @see ListPublicIPAddressesOptions#zoneId
       */
      public static ListPublicIPAddressesOptions zoneId(String id) {
         ListPublicIPAddressesOptions options = new ListPublicIPAddressesOptions();
         return options.zoneId(id);
      }

      /**
       * @see ListPublicIPAddressesOptions#usesVirtualNetwork
       */
      public static ListPublicIPAddressesOptions usesVirtualNetwork(boolean usesVirtualNetwork) {
         ListPublicIPAddressesOptions options = new ListPublicIPAddressesOptions();
         return options.usesVirtualNetwork(usesVirtualNetwork);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ListPublicIPAddressesOptions accountInDomain(String account, String domain) {
      return ListPublicIPAddressesOptions.class.cast(super.accountInDomain(account, domain));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ListPublicIPAddressesOptions domainId(String domainId) {
      return ListPublicIPAddressesOptions.class.cast(super.domainId(domainId));
   }
}
