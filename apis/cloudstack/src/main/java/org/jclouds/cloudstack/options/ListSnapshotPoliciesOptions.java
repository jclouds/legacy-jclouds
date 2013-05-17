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
 * Options for the Snapshot listSnapshotPolicies method.
 *
 * @see org.jclouds.cloudstack.features.SnapshotClient#listSnapshotPolicies
 * @see org.jclouds.cloudstack.features.SnapshotAsyncClient#listSnapshotPolicies
 * @author Richard Downer
 */
public class ListSnapshotPoliciesOptions extends AccountInDomainOptions {

   public static final ListSnapshotPoliciesOptions NONE = new ListSnapshotPoliciesOptions(); 

   /**
    * @param keyword List by keyword
    */
   public ListSnapshotPoliciesOptions keyword(String keyword) {
      this.queryParameters.replaceValues("keyword", ImmutableSet.of(keyword + ""));
      return this;
   }

   public static class Builder {

      /**
       * @param account lists snapshot policies for the specified account.
       */
      public static ListSnapshotPoliciesOptions accountInDomain(String account, String domainId) {
         return (ListSnapshotPoliciesOptions) new ListSnapshotPoliciesOptions().accountInDomain(account, domainId);
      }

      /**
       * @param domainId the domain ID.
       */
      public static ListSnapshotPoliciesOptions domainId(String domainId) {
         return (ListSnapshotPoliciesOptions) new ListSnapshotPoliciesOptions().domainId(domainId);
      }

      /**
       * @param keyword List by keyword
       */
      public static ListSnapshotPoliciesOptions keyword(String keyword) {
         return new ListSnapshotPoliciesOptions().keyword(keyword);
      }
   }

}
