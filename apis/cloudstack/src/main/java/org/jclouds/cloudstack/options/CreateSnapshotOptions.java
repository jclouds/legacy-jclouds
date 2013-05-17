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
 * Options for the Snapshot createSnapshot method.
 *
 * @see org.jclouds.cloudstack.features.SnapshotClient#createSnapshot
 * @see org.jclouds.cloudstack.features.SnapshotAsyncClient#createSnapshot
 * @author Richard Downer
 */
public class CreateSnapshotOptions extends AccountInDomainOptions {

   public static final CreateSnapshotOptions NONE = new CreateSnapshotOptions(); 

   /**
    * @param policyId policy id of the snapshot, if this is null, then use MANUAL_POLICY.
    */
   public CreateSnapshotOptions policyId(String policyId) {
      this.queryParameters.replaceValues("policyid", ImmutableSet.of(policyId + ""));
      return this;
   }

   public static class Builder {

      /**
       * @param account The account of the snapshot.
       * @param domainId The domain ID of the snapshot.
       */
      public static CreateSnapshotOptions accountInDomain(String account, String domainId) {
         return (CreateSnapshotOptions) new CreateSnapshotOptions().accountInDomain(account, domainId);
      }

      /**
       * @param domainId The domain ID of the snapshot.
       */
      public static CreateSnapshotOptions domainId(String domainId) {
         return (CreateSnapshotOptions) new CreateSnapshotOptions().domainId(domainId);
      }

      /**
       * @param policyId policy id of the snapshot, if this is null, then use MANUAL_POLICY.
       */
      public static CreateSnapshotOptions policyId(String policyId) {
         return new CreateSnapshotOptions().policyId(policyId);
      }
   }

}
