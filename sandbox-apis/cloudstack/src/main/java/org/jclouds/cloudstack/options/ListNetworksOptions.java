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
package org.jclouds.cloudstack.options;

import org.jclouds.cloudstack.domain.NetworkType;
import org.jclouds.cloudstack.domain.TrafficType;

import com.google.common.collect.ImmutableSet;

/**
 * Options used to control what networks information is returned
 * 
 * @see <a
 *      href="http://download.cloud.com/releases/2.2.0/api/user/listNetworks.html"
 *      />
 * @author Adrian Cole
 */
public class ListNetworksOptions extends AccountInDomainOptions {

   public static final ListNetworksOptions NONE = new ListNetworksOptions();

   /**
    * @param isDefault
    *           true if network is default, false otherwise
    */
   public ListNetworksOptions isDefault(boolean isDefault) {
      this.queryParameters.replaceValues("isdefault", ImmutableSet.of(isDefault + ""));
      return this;
   }

   /**
    * @param isShared
    *           true if network is shared, false otherwise
    */
   public ListNetworksOptions isShared(boolean isShared) {
      this.queryParameters.replaceValues("isshared", ImmutableSet.of(isShared + ""));
      return this;
   }

   /**
    * @param isSystem
    *           true if network is system, false otherwise
    */
   public ListNetworksOptions isSystem(boolean isSystem) {
      this.queryParameters.replaceValues("issystem", ImmutableSet.of(isSystem + ""));
      return this;
   }

   /**
    * @param type
    *           the type of the network
    */
   public ListNetworksOptions type(NetworkType type) {
      this.queryParameters.replaceValues("type", ImmutableSet.of(type.toString()));
      return this;
   }

   /**
    * @param id
    *           list networks by id
    */
   public ListNetworksOptions id(long id) {
      this.queryParameters.replaceValues("id", ImmutableSet.of(id + ""));
      return this;
   }

   /**
    * @param zoneId
    *           the Zone ID of the network
    */
   public ListNetworksOptions zoneId(long zoneId) {
      this.queryParameters.replaceValues("zoneid", ImmutableSet.of(zoneId + ""));
      return this;

   }

   /**
    * @param trafficType
    *           type of the traffic
    */
   public ListNetworksOptions trafficType(TrafficType trafficType) {
      this.queryParameters.replaceValues("traffictype", ImmutableSet.of(trafficType.toString()));
      return this;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ListNetworksOptions accountInDomain(String account, long domain) {
      return ListNetworksOptions.class.cast(super.accountInDomain(account, domain));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ListNetworksOptions domainId(long domainId) {
      return ListNetworksOptions.class.cast(super.domainId(domainId));
   }

   public static class Builder {
      /**
       * @see ListNetworksOptions#isDefault
       */
      public static ListNetworksOptions isDefault(boolean isDefault) {
         ListNetworksOptions options = new ListNetworksOptions();
         return options.isDefault(isDefault);
      }

      /**
       * @see ListNetworksOptions#isShared
       */
      public static ListNetworksOptions isShared(boolean isShared) {
         ListNetworksOptions options = new ListNetworksOptions();
         return options.isShared(isShared);
      }

      /**
       * @see ListNetworksOptions#isSystem
       */
      public static ListNetworksOptions isSystem(boolean isSystem) {
         ListNetworksOptions options = new ListNetworksOptions();
         return options.isSystem(isSystem);
      }

      /**
       * @see ListNetworksOptions#type
       */
      public static ListNetworksOptions type(NetworkType type) {
         ListNetworksOptions options = new ListNetworksOptions();
         return options.type(type);
      }

      /**
       * @see ListNetworksOptions#id
       */
      public static ListNetworksOptions id(long id) {
         ListNetworksOptions options = new ListNetworksOptions();
         return options.id(id);
      }

      /**
       * @see ListNetworksOptions#zoneId
       */
      public static ListNetworksOptions zoneId(long id) {
         ListNetworksOptions options = new ListNetworksOptions();
         return options.zoneId(id);
      }

      /**
       * @see ListNetworksOptions#trafficType
       */
      public static ListNetworksOptions trafficType(TrafficType trafficType) {
         ListNetworksOptions options = new ListNetworksOptions();
         return options.trafficType(trafficType);
      }

      /**
       * @see ListNetworksOptions#accountInDomain
       */
      public static ListNetworksOptions accountInDomain(String account, long domain) {
         ListNetworksOptions options = new ListNetworksOptions();
         return options.accountInDomain(account, domain);
      }

      /**
       * @see ListNetworksOptions#domainId
       */
      public static ListNetworksOptions domainId(long domainId) {
         ListNetworksOptions options = new ListNetworksOptions();
         return options.domainId(domainId);
      }
   }

}
