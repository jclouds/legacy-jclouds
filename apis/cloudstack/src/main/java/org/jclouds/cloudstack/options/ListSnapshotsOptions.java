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

import org.jclouds.cloudstack.domain.Snapshot;

import com.google.common.collect.ImmutableSet;

/**
 * Options for the Snapshot listSnapshots method.
 *
 * @see org.jclouds.cloudstack.features.SnapshotApi#listSnapshots
 * @see org.jclouds.cloudstack.features.SnapshotApi#listSnapshots
 * @author Richard Downer
 */
public class ListSnapshotsOptions extends AccountInDomainOptions {

   public static final ListSnapshotsOptions NONE = new ListSnapshotsOptions(); 

   /**
    * @param id lists snapshot by snapshot ID
    */
   public ListSnapshotsOptions id(String id) {
      this.queryParameters.replaceValues("id", ImmutableSet.of(id + ""));
      return this;
   }

   /**
    * @param interval valid values are HOURLY, DAILY, WEEKLY, and MONTHLY.
    */
   public ListSnapshotsOptions interval(Snapshot.Interval interval) {
      this.queryParameters.replaceValues("intervaltype", ImmutableSet.of(interval + ""));
      return this;
   }

   /**
    * @param isRecursive defaults to false, but if true, lists all snapshots from the parent specified by the domain id till leaves.
    */
   public ListSnapshotsOptions isRecursive(boolean isRecursive) {
      this.queryParameters.replaceValues("isrecursive", ImmutableSet.of(isRecursive + ""));
      return this;
   }

   /**
    * @param keyword List by keyword
    */
   public ListSnapshotsOptions keyword(String keyword) {
      this.queryParameters.replaceValues("keyword", ImmutableSet.of(keyword + ""));
      return this;
   }

   /**
    * @param name lists snapshot by snapshot name
    */
   public ListSnapshotsOptions name(String name) {
      this.queryParameters.replaceValues("name", ImmutableSet.of(name + ""));
      return this;
   }

   /**
    * @param snapshotType valid values are MANUAL or RECURRING.
    */
   public ListSnapshotsOptions snapshotType(Snapshot.Type snapshotType) {
      this.queryParameters.replaceValues("snapshottype", ImmutableSet.of(snapshotType + ""));
      return this;
   }

   /**
    * @param volumeId the ID of the disk volume
    */
   public ListSnapshotsOptions volumeId(String volumeId) {
      this.queryParameters.replaceValues("volumeid", ImmutableSet.of(volumeId + ""));
      return this;
   }

   /**
    * @param projectId the project to list in
    */
   public ListSnapshotsOptions projectId(String projectId) {
      this.queryParameters.replaceValues("projectid", ImmutableSet.of(projectId + ""));
      return this;
   }

   public static class Builder {

      /**
       * @param account lists snapshot belonging to the specified account.
       * @param domainId The domain ID.
       */
      public static ListSnapshotsOptions accountInDomain(String account, String domainId) {
         return (ListSnapshotsOptions) new ListSnapshotsOptions().accountInDomain(account, domainId);
      }

      /**
       * @param domainId the domain ID.
       */
      public static ListSnapshotsOptions domainId(String domainId) {
         return (ListSnapshotsOptions) new ListSnapshotsOptions().domainId(domainId);
      }

      /**
       * @param id lists snapshot by snapshot ID
       */
      public static ListSnapshotsOptions id(String id) {
         return new ListSnapshotsOptions().id(id);
      }

      /**
       * @param interval valid values are HOURLY, DAILY, WEEKLY, and MONTHLY.
       */
      public static ListSnapshotsOptions interval(Snapshot.Interval interval) {
         return new ListSnapshotsOptions().interval(interval);
      }

      /**
       * @param isRecursive defaults to false, but if true, lists all snapshots from the parent specified by the domain id till leaves.
       */
      public static ListSnapshotsOptions isRecursive(boolean isRecursive) {
         return new ListSnapshotsOptions().isRecursive(isRecursive);
      }

      /**
       * @param keyword List by keyword
       */
      public static ListSnapshotsOptions keyword(String keyword) {
         return new ListSnapshotsOptions().keyword(keyword);
      }

      /**
       * @param name lists snapshot by snapshot name
       */
      public static ListSnapshotsOptions name(String name) {
         return new ListSnapshotsOptions().name(name);
      }

      /**
       * @param snapshotType valid values are MANUAL or RECURRING.
       */
      public static ListSnapshotsOptions snapshotType(Snapshot.Type snapshotType) {
         return new ListSnapshotsOptions().snapshotType(snapshotType);
      }

      /**
       * @param volumeId the ID of the disk volume
       */
      public static ListSnapshotsOptions volumeId(String volumeId) {
         return new ListSnapshotsOptions().volumeId(volumeId);
      }

      /**
       * @param projectId the project to list in
       */
      public static ListSnapshotsOptions projectId(String projectId) {
         return new ListSnapshotsOptions().projectId(projectId);
      }
   }

}
