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

/**
 * Options used to control what volume
 *
 * @author Vijay Kiran
 */
public class ListVolumesOptions extends AccountInDomainOptions {

   public static final ListVolumesOptions NONE = new ListVolumesOptions();

   /**
    * List volumes on specified host
    *
    * @param hostId hostId.
    */
   public ListVolumesOptions hostId(long hostId) {
      this.queryParameters.replaceValues("hostid", ImmutableSet.of(hostId + ""));
      return this;
   }

   /**
    * @param id the ID of the volume
    */
   public ListVolumesOptions id(long id) {
      this.queryParameters.replaceValues("id", ImmutableSet.of(id + ""));
      return this;
   }

   /**
    * @param recursive defaults to false, but if true, lists all volumes from the parent specified by the domain id till leaves.
    */
   public ListVolumesOptions isRecursive(boolean recursive) {
      this.queryParameters.replaceValues("isrecursive", ImmutableSet.of(String.valueOf(recursive)));
      return this;
   }


   /**
    * @param keyword List by keyword
    */
   public ListVolumesOptions keyword(String keyword) {
      this.queryParameters.replaceValues("keyword", ImmutableSet.of(keyword));
      return this;
   }

   /**
    * @param name the name of the disk volume
    */
   public ListVolumesOptions name(String name) {
      this.queryParameters.replaceValues("name", ImmutableSet.of(name));
      return this;
   }

   /**
    * @param podId the pod id the disk volume belongs to
    */
   public ListVolumesOptions podId(long podId) {
      this.queryParameters.replaceValues("podid", ImmutableSet.of(podId + ""));
      return this;

   }

   /**
    * @param type the type of the disk volume
    */
   public ListVolumesOptions type(String type) {
      this.queryParameters.replaceValues("type", ImmutableSet.of(type + ""));
      return this;
   }

   /**
    * @param virtualMachineId list volumes by id virtual machine.
    */
   public ListVolumesOptions virtualMachineId(long virtualMachineId) {
      this.queryParameters.replaceValues("virtualmachineid", ImmutableSet.of(virtualMachineId + ""));
      return this;

   }

   /**
    * @param zoneId list volumes  by zoneId.
    */
   public ListVolumesOptions zoneId(long zoneId) {
      this.queryParameters.replaceValues("zoneid", ImmutableSet.of(zoneId + ""));
      return this;

   }


   public static class Builder {

      /**
       * @see ListVolumesOptions#accountInDomain
       */
      public static ListVolumesOptions accountInDomain(String account, long domain) {
         ListVolumesOptions options = new ListVolumesOptions();
         return options.accountInDomain(account, domain);
      }

      /**
       * @see ListVolumesOptions#domainId
       */
      public static ListVolumesOptions domainId(long id) {
         ListVolumesOptions options = new ListVolumesOptions();
         return options.domainId(id);
      }

      /**
       * @see ListVolumesOptions#hostId
       */
      public static ListVolumesOptions hostId(long id) {
         ListVolumesOptions options = new ListVolumesOptions();
         return options.hostId(id);
      }

      /**
       * @see ListVolumesOptions#id
       */
      public static ListVolumesOptions id(long id) {
         ListVolumesOptions options = new ListVolumesOptions();
         return options.id(id);
      }

      /**
       * @see ListVolumesOptions#isRecursive(boolean)
       */
      public static ListVolumesOptions isRecursive(boolean recursive) {
         ListVolumesOptions options = new ListVolumesOptions();
         return options.isRecursive(recursive);
      }

      /**
       * @see ListVolumesOptions#name
       */
      public static ListVolumesOptions keyword(String keyword) {
         ListVolumesOptions options = new ListVolumesOptions();
         return options.keyword(keyword);
      }

      /**
       * @see ListVolumesOptions#name
       */
      public static ListVolumesOptions name(String name) {
         ListVolumesOptions options = new ListVolumesOptions();
         return options.name(name);
      }


      /**
       * @see ListVolumesOptions#podId
       */
      public static ListVolumesOptions podId(long id) {
         ListVolumesOptions options = new ListVolumesOptions();
         return options.podId(id);
      }

      /**
       * @see ListVolumesOptions#type
       */
      public static ListVolumesOptions type(String type) {
         ListVolumesOptions options = new ListVolumesOptions();
         return options.type(type);
      }

      /**
       * @see ListVolumesOptions#virtualMachineId(long)
       */
      public static ListVolumesOptions virtualMachineId(long virtualMachineId) {
         ListVolumesOptions options = new ListVolumesOptions();
         return options.virtualMachineId(virtualMachineId);
      }

      /**
       * @see ListVolumesOptions#zoneId
       */
      public static ListVolumesOptions zoneId(long id) {
         ListVolumesOptions options = new ListVolumesOptions();
         return options.zoneId(id);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ListVolumesOptions accountInDomain(String account, long domain) {
      return ListVolumesOptions.class.cast(super.accountInDomain(account, domain));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ListVolumesOptions domainId(long domainId) {
      return ListVolumesOptions.class.cast(super.domainId(domainId));
   }
}
