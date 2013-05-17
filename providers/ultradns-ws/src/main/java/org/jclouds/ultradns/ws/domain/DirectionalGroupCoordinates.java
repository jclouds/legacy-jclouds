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
package org.jclouds.ultradns.ws.domain;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;

/**
 * @author Adrian Cole
 */
public class DirectionalGroupCoordinates {

   private final String zoneName;
   private final String recordName;
   private final int recordType;
   private final String groupName;

   private DirectionalGroupCoordinates(String zoneName, String recordName, int recordType, String groupName) {
      this.zoneName = checkNotNull(zoneName, "zoneName");
      this.recordName = checkNotNull(recordName, "recordName");
      checkArgument(recordType >= 0, "recordType of %s must be >= 0", recordName);
      this.recordType = recordType;
      this.groupName = checkNotNull(groupName, "groupName");
   }

   /**
    * the {@link DirectionalPoolRecordDetail#getZoneName() name} of the zone.
    */
   public String getZoneName() {
      return zoneName;
   }

   /**
    * the {@link DirectionalPoolRecordDetail#getName() dname} of the record.
    */
   public String getRecordName() {
      return recordName;
   }

   /**
    * the {@link DirectionalPoolRecord#getType() recordType} of the record.
    * 
    * @see DirectionalPoolRecordDetail#getRecord()
    */
   public int getRecordType() {
      return recordType;
   }

   /**
    * the {@link DirectionalGroup#getName() name} of the directional group.
    * 
    * @see DirectionalPoolRecordDetail#getGroup()
    * @see DirectionalPoolRecordDetail#getGeolocationGroup()
    * @see DirectionalPoolRecordDetail#getSourceIpGroup()
    */
   public String getGroupName() {
      return groupName;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(zoneName, recordName, recordType, groupName);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      DirectionalGroupCoordinates that = DirectionalGroupCoordinates.class.cast(obj);
      return equal(this.zoneName, that.zoneName) && equal(this.recordName, that.recordName)
            && equal(this.recordType, that.recordType) && equal(this.groupName, that.groupName);
   }

   @Override
   public String toString() {
      return toStringHelper(this).add("zoneName", zoneName).add("recordName", recordName).add("recordType", recordType)
            .add("groupName", groupName).toString();
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().from(this);
   }

   public static final class Builder {
      private String zoneName;
      private String recordName;
      private int recordType = -1;
      private String groupName;

      /**
       * @see DirectionalGroupCoordinates#getZoneName()
       */
      public Builder zoneName(String zoneName) {
         this.zoneName = zoneName;
         return this;
      }

      /**
       * @see DirectionalGroupCoordinates#getRecordName()
       */
      public Builder recordName(String recordName) {
         this.recordName = recordName;
         return this;
      }

      /**
       * @see DirectionalGroupCoordinates#getRecordType()
       */
      public Builder recordType(int recordType) {
         this.recordType = recordType;
         return this;
      }

      /**
       * @see DirectionalGroupCoordinates#getGroupName()
       */
      public Builder groupName(String groupName) {
         this.groupName = groupName;
         return this;
      }

      public DirectionalGroupCoordinates build() {
         return new DirectionalGroupCoordinates(zoneName, recordName, recordType, groupName);
      }

      public Builder from(DirectionalGroupCoordinates in) {
         return zoneName(in.zoneName).recordName(in.zoneName).recordType(in.recordType).groupName(in.groupName);
      }
   }
}
