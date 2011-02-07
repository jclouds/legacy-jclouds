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

package org.jclouds.cloudstack.options;

import org.jclouds.cloudstack.domain.NetworkType;
import org.jclouds.cloudstack.domain.TrafficType;
import org.jclouds.http.options.BaseHttpRequestOptions;

import com.google.common.collect.ImmutableSet;

/**
 * Options used to control what networks information is returned
 * 
 * @see <a href="http://download.cloud.com/releases/2.2/api/user/listNetworks.html" />
 * @author Adrian Cole
 */
public class ListNetworksOptions extends BaseHttpRequestOptions {

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
   public ListNetworksOptions id(String id) {
      this.queryParameters.replaceValues("id", ImmutableSet.of(id));
      return this;
   }

   /**
    * 
    * @param account
    *           account who will own the VLAN. If VLAN is Zone wide, this parameter should be
    *           ommited
    */
   public ListNetworksOptions account(String account) {
      this.queryParameters.replaceValues("account", ImmutableSet.of(account));
      return this;
   }

   /**
    * @param domainId
    *           domain ID of the account owning a VLAN
    */
   public ListNetworksOptions domainId(String domainId) {
      this.queryParameters.replaceValues("domainid", ImmutableSet.of(domainId));
      return this;

   }

   /**
    * @param zoneId
    *           the Zone ID of the network
    */
   public ListNetworksOptions zoneId(String zoneId) {
      this.queryParameters.replaceValues("zoneid", ImmutableSet.of(zoneId));
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
       * @see ListNetworksOptions#domainId
       */
      public static ListNetworksOptions domainId(String id) {
         ListNetworksOptions options = new ListNetworksOptions();
         return options.domainId(id);
      }

      /**
       * @see ListNetworksOptions#account
       */
      public static ListNetworksOptions account(String account) {
         ListNetworksOptions options = new ListNetworksOptions();
         return options.account(account);
      }

      /**
       * @see ListNetworksOptions#id
       */
      public static ListNetworksOptions id(String id) {
         ListNetworksOptions options = new ListNetworksOptions();
         return options.id(id);
      }

      /**
       * @see ListNetworksOptions#zoneId
       */
      public static ListNetworksOptions zoneId(String id) {
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
   }

}
