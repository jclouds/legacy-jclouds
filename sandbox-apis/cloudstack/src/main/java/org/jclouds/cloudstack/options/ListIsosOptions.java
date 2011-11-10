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

import com.google.common.collect.ImmutableSet;
import org.jclouds.cloudstack.domain.Iso;
import org.jclouds.http.options.BaseHttpRequestOptions;

/**
 * Options for the Iso listIsos method.
 *
 * @see org.jclouds.cloudstack.features.IsoClient#listIsos
 * @see org.jclouds.cloudstack.features.IsoAsyncClient#listIsos
 * @author Richard Downer
 */
public class ListIsosOptions extends AccountInDomainOptions {

   public static final ListIsosOptions NONE = new ListIsosOptions(); 

   /**
    * @param bootable true if the ISO is bootable, false otherwise
    */
   public ListIsosOptions bootable(boolean bootable) {
      this.queryParameters.replaceValues("bootable", ImmutableSet.of(bootable + ""));
      return this;
   }

   /**
    * @param hypervisor the hypervisor for which to restrict the search
    */
   public ListIsosOptions hypervisor(String hypervisor) {
      this.queryParameters.replaceValues("hypervisor", ImmutableSet.of(hypervisor + ""));
      return this;
   }

   /**
    * @param id list all isos by id
    */
   public ListIsosOptions id(long id) {
      this.queryParameters.replaceValues("id", ImmutableSet.of(id + ""));
      return this;
   }

   /**
    * @param isoFilter possible values are "featured", "self", "self-executable","executable", and "community".
    */
   public ListIsosOptions isoFilter(Iso.IsoFilter isoFilter) {
      this.queryParameters.replaceValues("isofilter", ImmutableSet.of(isoFilter + ""));
      return this;
   }

   /**
    * @param isPublic true if the ISO is publicly available to all users, false otherwise.
    */
   public ListIsosOptions isPublic(boolean isPublic) {
      this.queryParameters.replaceValues("ispublic", ImmutableSet.of(isPublic + ""));
      return this;
   }

   /**
    * @param isReady true if this ISO is ready to be deployed
    */
   public ListIsosOptions isReady(boolean isReady) {
      this.queryParameters.replaceValues("isready", ImmutableSet.of(isReady + ""));
      return this;
   }

   /**
    * @param keyword List by keyword
    */
   public ListIsosOptions keyword(String keyword) {
      this.queryParameters.replaceValues("keyword", ImmutableSet.of(keyword + ""));
      return this;
   }

   /**
    * @param name list all isos by name
    */
   public ListIsosOptions name(String name) {
      this.queryParameters.replaceValues("name", ImmutableSet.of(name + ""));
      return this;
   }

   /**
    * @param zoneId the ID of the zone
    */
   public ListIsosOptions zoneId(long zoneId) {
      this.queryParameters.replaceValues("zoneid", ImmutableSet.of(zoneId + ""));
      return this;
   }

   public static class Builder {

      /**
       * @param account the account of the ISO file. Must be used with the domainId parameter.
       */
      public static ListIsosOptions accountInDomain(String account, long domainId) {
         return (ListIsosOptions) new ListIsosOptions().accountInDomain(account, domainId);
      }

      /**
       * @param bootable true if the ISO is bootable, false otherwise
       */
      public static ListIsosOptions bootable(boolean bootable) {
         return new ListIsosOptions().bootable(bootable);
      }

      /**
       * @param domainId lists all available ISO files by ID of a domain. If used with the account parameter, lists all available ISO files for the account in the ID of a domain.
       */
      public static ListIsosOptions domainId(long domainId) {
         return (ListIsosOptions) new ListIsosOptions().domainId(domainId);
      }

      /**
       * @param hypervisor the hypervisor for which to restrict the search
       */
      public static ListIsosOptions hypervisor(String hypervisor) {
         return new ListIsosOptions().hypervisor(hypervisor);
      }

      /**
       * @param id list all isos by id
       */
      public static ListIsosOptions id(long id) {
         return new ListIsosOptions().id(id);
      }

      /**
       * @param isoFilter possible values are "featured", "self", "self-executable","executable", and "community".
       */
      public static ListIsosOptions isoFilter(Iso.IsoFilter isoFilter) {
         return new ListIsosOptions().isoFilter(isoFilter);
      }

      /**
       * @param isPublic true if the ISO is publicly available to all users, false otherwise.
       */
      public static ListIsosOptions isPublic(boolean isPublic) {
         return new ListIsosOptions().isPublic(isPublic);
      }

      /**
       * @param isReady true if this ISO is ready to be deployed
       */
      public static ListIsosOptions isReady(boolean isReady) {
         return new ListIsosOptions().isReady(isReady);
      }

      /**
       * @param keyword List by keyword
       */
      public static ListIsosOptions keyword(String keyword) {
         return new ListIsosOptions().keyword(keyword);
      }

      /**
       * @param name list all isos by name
       */
      public static ListIsosOptions name(String name) {
         return new ListIsosOptions().name(name);
      }

      /**
       * @param zoneId the ID of the zone
       */
      public static ListIsosOptions zoneId(long zoneId) {
         return new ListIsosOptions().zoneId(zoneId);
      }
   }

}
