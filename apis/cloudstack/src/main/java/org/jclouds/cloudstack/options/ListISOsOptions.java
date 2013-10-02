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

import java.util.Set;

import org.jclouds.cloudstack.domain.ISO;

import com.google.common.collect.ImmutableSet;

/**
 * Options for the ISO listISOs method.
 *
 * @see org.jclouds.cloudstack.features.ISOApi#listISOs
 * @see org.jclouds.cloudstack.features.ISOApi#listISOs
 * @author Richard Downer
 */
public class ListISOsOptions extends AccountInDomainOptions {
   public static final ListISOsOptions NONE = new ListISOsOptions();

   private static final Set<String> TRUE = ImmutableSet.of(Boolean.toString(true));
   private static final Set<String> FALSE = ImmutableSet.of(Boolean.toString(false));

   /**
    * the ISO is bootable
    */
   public ListISOsOptions bootable() {
      this.queryParameters.replaceValues("bootable", TRUE);
      return this;
   }

   /**
    * the ISO is bootable
    */
   public ListISOsOptions notBootable() {
      this.queryParameters.replaceValues("bootable", FALSE);
      return this;
   }

   /**
    * @param hypervisor the hypervisor for which to restrict the search
    */
   public ListISOsOptions hypervisor(String hypervisor) {
      this.queryParameters.replaceValues("hypervisor", ImmutableSet.of(hypervisor));
      return this;
   }

   /**
    * @param id list all isos by id
    */
   public ListISOsOptions id(String id) {
      this.queryParameters.replaceValues("id", ImmutableSet.of(id));
      return this;
   }

   /**
    * @param projectId list ISOs in the given project
    */
   public ListISOsOptions projectId(String projectId) {
      this.queryParameters.replaceValues("projectid", ImmutableSet.of(projectId + ""));
      return this;
   }

   /**
    * @param isoFilter possible values are "featured", "self", "self-executable","executable", and "community".
    */
   public ListISOsOptions isoFilter(ISO.ISOFilter isoFilter) {
      this.queryParameters.replaceValues("isofilter", ImmutableSet.of(isoFilter.name()));
      return this;
   }

   /**
    * the ISO is publicly available to all users
    */
   public ListISOsOptions isPublic() {
      this.queryParameters.replaceValues("ispublic", TRUE);
      return this;
   }

   /**
    * the ISO is not publicly available to all users
    */
   public ListISOsOptions isPrivate() {
      this.queryParameters.replaceValues("ispublic", FALSE);
      return this;
   }

   /**
    * this ISO is ready to be deployed
    */
   public ListISOsOptions isReady() {
      this.queryParameters.replaceValues("isready", TRUE);
      return this;
   }

   /**
    * this ISO is not ready to be deployed
    */
   public ListISOsOptions isNotReady() {
      this.queryParameters.replaceValues("isready", FALSE);
      return this;
   }

   /**
    * @param keyword List by keyword
    */
   public ListISOsOptions keyword(String keyword) {
      this.queryParameters.replaceValues("keyword", ImmutableSet.of(keyword));
      return this;
   }

   /**
    * @param name list all isos by name
    */
   public ListISOsOptions name(String name) {
      this.queryParameters.replaceValues("name", ImmutableSet.of(name));
      return this;
   }

   /**
    * @param zoneId the ID of the zone
    */
   public ListISOsOptions zoneId(String zoneId) {
      this.queryParameters.replaceValues("zoneid", ImmutableSet.of(zoneId));
      return this;
   }

   public static class Builder {
      /**
       * @param account the account of the ISO file. Must be used with the domainId parameter.
       */
      public static ListISOsOptions accountInDomain(String account, String domainId) {
         return (ListISOsOptions) new ListISOsOptions().accountInDomain(account, domainId);
      }

      /**
       * the ISO is bootable
       */
      public static ListISOsOptions bootable() {
         return new ListISOsOptions().bootable();
      }

      /**
       * the ISO is bootable
       */
      public static ListISOsOptions notBootable() {
         return new ListISOsOptions().notBootable();
      }

      /**
       * @param domainId lists all available ISO files by ID of a domain. If used with the account parameter, lists all available ISO files for the account in the ID of a domain.
       */
      public static ListISOsOptions domainId(String domainId) {
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
      public static ListISOsOptions id(String id) {
         return new ListISOsOptions().id(id);
      }

      /**
       * @param projectId list ISOs in project
       */
      public static ListISOsOptions projectId(String projectId) {
         return new ListISOsOptions().projectId(projectId);
      }

      /**
       * @param isoFilter possible values are "featured", "self", "self-executable","executable", and "community".
       */
      public static ListISOsOptions isoFilter(ISO.ISOFilter isoFilter) {
         return new ListISOsOptions().isoFilter(isoFilter);
      }

      /**
       * the ISO is publicly available to all users
       */
      public static ListISOsOptions isPublic() {
         return new ListISOsOptions().isPublic();
      }

      /**
       * the ISO is not publicly available to all users
       */
      public static ListISOsOptions isPrivate() {
         return new ListISOsOptions().isPrivate();
      }

      /**
       * this ISO is ready to be deployed
       */
      public static ListISOsOptions isReady() {
         return new ListISOsOptions().isReady();
      }

      /**
       * this ISO is not ready to be deployed
       */
      public static ListISOsOptions isNotReady() {
         return new ListISOsOptions().isNotReady();
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
      public static ListISOsOptions zoneId(String zoneId) {
         return new ListISOsOptions().zoneId(zoneId);
      }
   }
}
