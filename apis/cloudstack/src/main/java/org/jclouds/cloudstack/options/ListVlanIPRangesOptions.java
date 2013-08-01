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
 * Options to the listVLANIPRanges() API call
 *
 * @author Richard Downer
 */
public class ListVlanIPRangesOptions extends AccountInDomainOptions {

   public static class Builder {

      public static ListVlanIPRangesOptions accountInDomain(String account, String domainId) {
         return new ListVlanIPRangesOptions().accountInDomain(account, domainId);
      }

      public static ListVlanIPRangesOptions domainId(String domainId) {
         return new ListVlanIPRangesOptions().domainId(domainId);
      }

      public static ListVlanIPRangesOptions forVirtualNetwork(boolean forVirtualNetwork) {
         return new ListVlanIPRangesOptions().forVirtualNetwork(forVirtualNetwork);
      }

      public static ListVlanIPRangesOptions id(String id) {
         return new ListVlanIPRangesOptions().id(id);
      }

      public static ListVlanIPRangesOptions keyword(String keyword) {
         return new ListVlanIPRangesOptions().keyword(keyword);
      }

      public static ListVlanIPRangesOptions networkId(String networkId) {
         return new ListVlanIPRangesOptions().networkId(networkId);
      }

      public static ListVlanIPRangesOptions podId(String podId) {
         return new ListVlanIPRangesOptions().podId(podId);
      }

      public static ListVlanIPRangesOptions projectId(String projectId) {
         return new ListVlanIPRangesOptions().projectId(projectId);
      }

      public static ListVlanIPRangesOptions vlan(long vlan) {
         return new ListVlanIPRangesOptions().vlan(vlan);
      }

      public static ListVlanIPRangesOptions vlan(String vlan) {
         return new ListVlanIPRangesOptions().vlan(vlan+"");
      }

      public static ListVlanIPRangesOptions zoneId(String zoneId) {
         return new ListVlanIPRangesOptions().zoneId(zoneId);
      }
   }

   @Override
   public ListVlanIPRangesOptions accountInDomain(String account, String domainId) {
      return (ListVlanIPRangesOptions) super.accountInDomain(account, domainId);
   }

   @Override
   public ListVlanIPRangesOptions domainId(String domainId) {
      return (ListVlanIPRangesOptions) super.domainId(domainId);
   }

   public ListVlanIPRangesOptions forVirtualNetwork(boolean forVirtualNetwork) {
      this.queryParameters.replaceValues("forvirtualnetwork", ImmutableSet.of(forVirtualNetwork+""));
      return this;
   }

   public ListVlanIPRangesOptions id(String id) {
      this.queryParameters.replaceValues("id", ImmutableSet.of(id+""));
      return this;
   }

   public ListVlanIPRangesOptions keyword(String keyword) {
      this.queryParameters.replaceValues("keyword", ImmutableSet.of(keyword));
      return this;
   }

   public ListVlanIPRangesOptions networkId(String networkId) {
      this.queryParameters.replaceValues("networkid", ImmutableSet.of(networkId+""));
      return this;
   }

   public ListVlanIPRangesOptions podId(String podId) {
      this.queryParameters.replaceValues("podid", ImmutableSet.of(podId+""));
      return this;
   }

   public ListVlanIPRangesOptions projectId(String projectId) {
      this.queryParameters.replaceValues("projectid", ImmutableSet.of(projectId+""));
      return this;
   }

   public ListVlanIPRangesOptions vlan(String vlan) {
      this.queryParameters.replaceValues("vlan", ImmutableSet.of(vlan));
      return this;
   }

   public ListVlanIPRangesOptions vlan(long vlan) {
      this.queryParameters.replaceValues("vlan", ImmutableSet.of(vlan+""));
      return this;
   }

   public ListVlanIPRangesOptions zoneId(String zoneId) {
      this.queryParameters.replaceValues("zoneid", ImmutableSet.of(zoneId+""));
      return this;
   }
}
