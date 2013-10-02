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

import org.jclouds.cloudstack.domain.AllocationState;
import org.jclouds.cloudstack.domain.Host;

import com.google.common.collect.ImmutableSet;

/**
 * Options used to control what hosts information is returned
 *
 * @author Andrei Savu
 * @see <a
 *      href="http://download.cloud.com/releases/2.2.0/api_2.2.12/global_admin/listHosts.html"
 *      />
 */
public class ListHostsOptions extends AccountInDomainOptions {

   public static final ListHostsOptions NONE = new ListHostsOptions();

   /**
    * @param id the id of the host
    */
   public ListHostsOptions id(String id) {
      this.queryParameters.replaceValues("id", ImmutableSet.of(id + ""));
      return this;
   }

   /**
    * @param allocationState list hosts by allocation state
    */
   public ListHostsOptions allocationState(AllocationState allocationState) {
      this.queryParameters.replaceValues("allocationstate", ImmutableSet.of(allocationState.toString()));
      return this;
   }

   /**
    * @param clusterId lists hosts existing in particular cluster
    */
   public ListHostsOptions clusterId(String clusterId) {
      this.queryParameters.replaceValues("clusterid", ImmutableSet.of(clusterId + ""));
      return this;
   }

   /**
    * @param keyword List by keyword
    */
   public ListHostsOptions keyword(String keyword) {
      this.queryParameters.replaceValues("keyword", ImmutableSet.of(keyword));
      return this;
   }

   /**
    * @param name List by name
    */
   public ListHostsOptions name(String name) {
      this.queryParameters.replaceValues("name", ImmutableSet.of(name));
      return this;
   }

   /**
    * @param page
    */
   public ListHostsOptions page(long page) {
      this.queryParameters.replaceValues("page", ImmutableSet.of(page + ""));
      return this;
   }

   /**
    * @param pageSize the page size
    */
   public ListHostsOptions pageSize(long pageSize) {
      this.queryParameters.replaceValues("pagesize", ImmutableSet.of(pageSize + ""));
      return this;
   }

   /**
    * @param podId the Pod ID for the host
    */
   public ListHostsOptions podId(String podId) {
      this.queryParameters.replaceValues("podid", ImmutableSet.of(podId + ""));
      return this;
   }

   /**
    * @param state the state of the host
    */
   public ListHostsOptions state(String state) {
      this.queryParameters.replaceValues("state", ImmutableSet.of(state));
      return this;
   }

   /**
    * @param type the type of the host
    */
   public ListHostsOptions type(Host.Type type) {
      this.queryParameters.replaceValues("type", ImmutableSet.of(type.toString()));
      return this;
   }

   /**
    * @param virtualMachineId lists hosts in the same cluster as this VM and flag hosts with
    *                         enough CPU/RAm to host this VM
    */
   public ListHostsOptions virtualMachineId(String virtualMachineId) {
      this.queryParameters.replaceValues("virtualmachineid", ImmutableSet.of(virtualMachineId + ""));
      return this;
   }

   /**
    * @param zoneId the Zone ID for the host
    */
   public ListHostsOptions zoneId(String zoneId) {
      this.queryParameters.replaceValues("zoneid", ImmutableSet.of(zoneId + ""));
      return this;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ListHostsOptions accountInDomain(String account, String domain) {
      return ListHostsOptions.class.cast(super.accountInDomain(account, domain));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ListHostsOptions domainId(String domainId) {
      return ListHostsOptions.class.cast(super.domainId(domainId));
   }

   public static class Builder {
      /**
       * @see ListHostsOptions#id
       */
      public static ListHostsOptions id(String id) {
         ListHostsOptions options = new ListHostsOptions();
         return options.id(id);
      }

      /**
       * @see ListHostsOptions#allocationState
       */
      public static ListHostsOptions allocationState(AllocationState allocationState) {
         ListHostsOptions options = new ListHostsOptions();
         return options.allocationState(allocationState);
      }

      /**
       * @see ListHostsOptions#clusterId
       */
      public static ListHostsOptions clusterId(String clusterId) {
         ListHostsOptions options = new ListHostsOptions();
         return options.clusterId(clusterId);
      }

      /**
       * @see ListHostsOptions#keyword(String)
       */
      public static ListHostsOptions keyword(String keyword) {
         ListHostsOptions options = new ListHostsOptions();
         return options.keyword(keyword);
      }

      /**
       * @see ListHostsOptions#name(String)
       */
      public static ListHostsOptions name(String name) {
         ListHostsOptions options = new ListHostsOptions();
         return options.name(name);
      }

      /**
       * @see ListHostsOptions#page
       */
      public static ListHostsOptions page(long page) {
         ListHostsOptions options = new ListHostsOptions();
         return options.page(page);
      }

      /**
       * @see ListHostsOptions#pageSize
       */
      public static ListHostsOptions pageSize(long pageSize) {
         ListHostsOptions options = new ListHostsOptions();
         return options.pageSize(pageSize);
      }

      /**
       * @see ListHostsOptions#podId
       */
      public static ListHostsOptions podId(String podId) {
         ListHostsOptions options = new ListHostsOptions();
         return options.podId(podId);
      }

      /**
       * @see ListHostsOptions#state
       */
      public static ListHostsOptions state(String state) {
         ListHostsOptions options = new ListHostsOptions();
         return options.state(state);
      }

      /**
       * @see ListHostsOptions#type
       */
      public static ListHostsOptions type(Host.Type type) {
         ListHostsOptions options = new ListHostsOptions();
         return options.type(type);
      }

      /**
       * @see ListHostsOptions#virtualMachineId
       */
      public static ListHostsOptions virtualMachineId(String virtualMachineId) {
         ListHostsOptions options = new ListHostsOptions();
         return options.virtualMachineId(virtualMachineId);
      }

      /**
       * @see ListHostsOptions#zoneId
       */
      public static ListHostsOptions zoneId(String zoneId) {
         ListHostsOptions options = new ListHostsOptions();
         return options.zoneId(zoneId);
      }

      /**
       * @see ListHostsOptions#accountInDomain
       */
      public static ListHostsOptions accountInDomain(String account, String domain) {
         ListHostsOptions options = new ListHostsOptions();
         return options.accountInDomain(account, domain);
      }

      /**
       * @see ListHostsOptions#domainId
       */
      public static ListHostsOptions domainId(String domainId) {
         ListHostsOptions options = new ListHostsOptions();
         return options.domainId(domainId);
      }
   }

}
