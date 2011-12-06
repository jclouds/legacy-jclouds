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
import org.jclouds.cloudstack.domain.NetworkType;
import org.jclouds.cloudstack.domain.TrafficType;

/**
 * Options used to control what hosts information is returned
 * 
 * @see <a
 *      href="http://download.cloud.com/releases/2.2.0/api_2.2.12/global_admin/listHosts.html"
 *      />
 * @author Andrei Savu
 */
public class ListHostsOptions extends AccountInDomainOptions {

   public static final ListHostsOptions NONE = new ListHostsOptions();

   /**
    * @param isDefault
    *           true if network is default, false otherwise
    */
   public ListHostsOptions isDefault(boolean isDefault) {
      this.queryParameters.replaceValues("isdefault", ImmutableSet.of(isDefault + ""));
      return this;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ListHostsOptions accountInDomain(String account, long domain) {
      return ListHostsOptions.class.cast(super.accountInDomain(account, domain));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ListHostsOptions domainId(long domainId) {
      return ListHostsOptions.class.cast(super.domainId(domainId));
   }

   public static class Builder {
      /**
       * @see org.jclouds.cloudstack.options.ListHostsOptions#isDefault
       */
      public static ListHostsOptions isDefault(boolean isDefault) {
         ListHostsOptions options = new ListHostsOptions();
         return options.isDefault(isDefault);
      }

      /**
       * @see org.jclouds.cloudstack.options.ListHostsOptions#accountInDomain
       */
      public static ListHostsOptions accountInDomain(String account, long domain) {
         ListHostsOptions options = new ListHostsOptions();
         return options.accountInDomain(account, domain);
      }

      /**
       * @see org.jclouds.cloudstack.options.ListHostsOptions#domainId
       */
      public static ListHostsOptions domainId(long domainId) {
         ListHostsOptions options = new ListHostsOptions();
         return options.domainId(domainId);
      }
   }

}
