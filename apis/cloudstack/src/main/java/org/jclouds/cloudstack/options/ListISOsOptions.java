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

import org.jclouds.cloudstack.domain.ISO;

import com.google.common.collect.ImmutableSet;

/**
 * Options for the ISO listISOs method.
 *
 * @see org.jclouds.cloudstack.features.ISOClient#listISOs
 * @see org.jclouds.cloudstack.features.ISOAsyncClient#listISOs
 * @author Richard Downer
 */
public class ListISOsOptions extends AccountInDomainOptions {

   public static final ListISOsOptions NONE = new ListISOsOptions();

   /**
    * @param bootable true if the ISO is bootable, false otherwise
    */
   public ListISOsOptions bootable(boolean bootable) {
      this.queryParameters.replaceValues("bootable", ImmutableSet.of(bootable + ""));
      return this;
   }

   /**
    * @param hypervisor the hypervisor for which to restrict the search
    */
   public ListISOsOptions hypervisor(String hypervisor) {
      this.queryParameters.replaceValues("hypervisor", ImmutableSet.of(hypervisor + ""));
      return this;
   }

   /**
    * @param id list all isos by id
    */
   public ListISOsOptions id(long id) {
      this.queryParameters.replaceValues("id", ImmutableSet.of(id + ""));
      return this;
   }

   /**
    * @param isoFilter possible values are "featured", "self", "self-executable","executable", and "community".
    */
   public ListISOsOptions isoFilter(ISO.ISOFilter isoFilter) {
      this.queryParameters.replaceValues("isofilter", ImmutableSet.of(isoFilter + ""));
      return this;
   }

   /**
    * @param isPublic true if the ISO is publicly available to all users, false otherwise.
    */
   public ListISOsOptions isPublic(boolean isPublic) {
      this.queryParameters.replaceValues("ispublic", ImmutableSet.of(isPublic + ""));
      return this;
   }

   /**
    * @param isReady true if this ISO is ready to be deployed
    */
   public ListISOsOptions isReady(boolean isReady) {
      this.queryParameters.replaceValues("isready", ImmutableSet.of(isReady + ""));
      return this;
   }

   /**
    * @param keyword List by keyword
    */
   public ListISOsOptions keyword(String keyword) {
      this.queryParameters.replaceValues("keyword", ImmutableSet.of(keyword + ""));
      return this;
   }

   /**
    * @param name list all isos by name
    */
   public ListISOsOptions name(String name) {
      this.queryParameters.replaceValues("name", ImmutableSet.of(name + ""));
      return this;
   }

   /**
    * @param zoneId the ID of the zone
    */
   public ListISOsOptions zoneId(long zoneId) {
      this.queryParameters.replaceValues("zoneid", ImmutableSet.of(zoneId + ""));
      return this;
   }

   public static class Builder {

      /**
       * @param account the account of the ISO file. Must be used with the domainId parameter.
       */
      public static ListISOsOptions accountInDomain(String account, long domainId) {
         return (ListISOsOptions) new ListISOsOptions().accountInDomain(account, domainId);
      }

      /**
       * @param bootable true if the ISO is bootable, false otherwise
       */
      public static ListISOsOptions bootable(boolean bootable) {
         return new ListISOsOptions().bootable(bootable);
      }

      /**
       * @param domainId lists all available ISO files by ID of a domain. If used with the account parameter, lists all available ISO files for the account in the ID of a domain.
       */
      public static ListISOsOptions domainId(long domainId) {
         return (ListISOsOptions) new ListISOsOptions().domainId(domainId);
      }

      /**
       * @param hypervisor the hypervisor for which to restrict the search
       */
      public static ListISOsOptions hypervisor(String hypervisor) {
         return new ListISOsOptions().hypervisor(hypervisor);
      }

      /**
       * @param id list all isos by id
       */
      public static ListISOsOptions id(long id) {
         return new ListISOsOptions().id(id);
      }

      /**
       * @param isoFilter possible values are "featured", "self", "self-executable","executable", and "community".
       */
      public static ListISOsOptions isoFilter(ISO.ISOFilter isoFilter) {
         return new ListISOsOptions().isoFilter(isoFilter);
      }

      /**
       * @param isPublic true if the ISO is publicly available to all users, false otherwise.
       */
      public static ListISOsOptions isPublic(boolean isPublic) {
         return new ListISOsOptions().isPublic(isPublic);
      }

      /**
       * @param isReady true if this ISO is ready to be deployed
       */
      public static ListISOsOptions isReady(boolean isReady) {
         return new ListISOsOptions().isReady(isReady);
      }

      /**
       * @param keyword List by keyword
       */
      public static ListISOsOptions keyword(String keyword) {
         return new ListISOsOptions().keyword(keyword);
      }

      /**
       * @param name list all isos by name
       */
      public static ListISOsOptions name(String name) {
         return new ListISOsOptions().name(name);
      }

      /**
       * @param zoneId the ID of the zone
       */
      public static ListISOsOptions zoneId(long zoneId) {
         return new ListISOsOptions().zoneId(zoneId);
      }
   }

}
