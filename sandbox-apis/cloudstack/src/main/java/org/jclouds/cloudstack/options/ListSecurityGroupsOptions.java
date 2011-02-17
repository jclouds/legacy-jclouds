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

import org.jclouds.http.options.BaseHttpRequestOptions;

import com.google.common.collect.ImmutableSet;

/**
 * Options used to control what security groups are returned
 * 
 * @see <a href="http://download.cloud.com/releases/2.2/api/user/listSecurityGroups.html" />
 * @author Adrian Cole
 */
public class ListSecurityGroupsOptions extends BaseHttpRequestOptions {

   public static final ListSecurityGroupsOptions NONE = new ListSecurityGroupsOptions();

   /**
    * @param id
    *           the ID of the security group
    */
   public ListSecurityGroupsOptions id(long id) {
      this.queryParameters.replaceValues("id", ImmutableSet.of(id + ""));
      return this;
   }

   /**
    * @param domainId
    *           the ID of the domain associated with the security group
    */
   public ListSecurityGroupsOptions domainId(long domainId) {
      this.queryParameters.replaceValues("domainid", ImmutableSet.of(domainId + ""));
      return this;

   }

   /**
    * @param account
    *           the security group account
    */
   public ListSecurityGroupsOptions account(String account) {
      this.queryParameters.replaceValues("account", ImmutableSet.of(account));
      return this;
   }

   /**
    * @param securityGroupName
    *           lists security groups by name
    */
   public ListSecurityGroupsOptions securityGroupName(String securityGroupName) {
      this.queryParameters.replaceValues("securitygroupname", ImmutableSet.of(securityGroupName));
      return this;
   }

   /**
    * @param virtualMachineId
    *           the ID of the virtual machine. Pass this in if you want to see the available service
    *           offering that a virtual machine can be changed to.
    */
   public ListSecurityGroupsOptions virtualMachineId(long virtualMachineId) {
      this.queryParameters.replaceValues("virtualmachineid", ImmutableSet.of(virtualMachineId + ""));
      return this;

   }

   public static class Builder {

      /**
       * @see ListSecurityGroupsOptions#account
       */
      public static ListSecurityGroupsOptions account(String account) {
         ListSecurityGroupsOptions options = new ListSecurityGroupsOptions();
         return options.account(account);
      }

      /**
       * @see ListSecurityGroupsOptions#securityGroupName
       */
      public static ListSecurityGroupsOptions securityGroupName(String securityGroupName) {
         ListSecurityGroupsOptions options = new ListSecurityGroupsOptions();
         return options.securityGroupName(securityGroupName);
      }

      /**
       * @see ListSecurityGroupsOptions#domainId
       */
      public static ListSecurityGroupsOptions domainId(long id) {
         ListSecurityGroupsOptions options = new ListSecurityGroupsOptions();
         return options.domainId(id);
      }

      /**
       * @see ListSecurityGroupsOptions#id
       */
      public static ListSecurityGroupsOptions id(long id) {
         ListSecurityGroupsOptions options = new ListSecurityGroupsOptions();
         return options.id(id);
      }

      /**
       * @see ListSecurityGroupsOptions#virtualMachineId
       */
      public static ListSecurityGroupsOptions virtualMachineId(long virtualMachineId) {
         ListSecurityGroupsOptions options = new ListSecurityGroupsOptions();
         return options.virtualMachineId(virtualMachineId);
      }
   }
}
