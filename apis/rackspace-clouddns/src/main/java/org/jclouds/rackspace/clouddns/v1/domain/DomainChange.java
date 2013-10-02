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
package org.jclouds.rackspace.clouddns.v1.domain;

import static com.google.common.base.Objects.equal;

import java.beans.ConstructorProperties;
import java.util.Date;
import java.util.List;

import com.google.common.base.Objects;

public class DomainChange {
   private final Date from;
   private final Date to;
   private final List<Change> changes;

   @ConstructorProperties({ "from", "to", "changes" })
   private DomainChange(Date from, Date to, List<Change> changes) {
      this.from = from;
      this.to = to;
      this.changes = changes;
   }

   public Date getFrom() {
      return from;
   }

   public Date getTo() {
      return to;
   }

   public List<Change> getChanges() {
      return changes;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(from, to, changes);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      DomainChange that = DomainChange.class.cast(obj);
      return equal(this.from, that.from) && equal(this.to, that.to) && equal(this.changes, that.changes);
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this).omitNullValues().add("from", from).add("to", to).add("changes", changes)
            .toString();
   }

   public static class Change {
      private final String domain;
      private final String action;
      private final String targetType;
      private final int accountId;
      private final int targetId;
      private final List<ChangeDetail> changeDetails;

      @ConstructorProperties({ "domain", "action", "targetType", "accountId", "targetId", "changeDetails" })
      protected Change(String domain, String action, String targetType, int accountId, int targetId,
            List<ChangeDetail> changeDetails) {
         this.domain = domain;
         this.action = action;
         this.targetType = targetType;
         this.accountId = accountId;
         this.targetId = targetId;
         this.changeDetails = changeDetails;
      }

      public String getDomain() {
         return domain;
      }

      public String getAction() {
         return action;
      }

      public String getTargetType() {
         return targetType;
      }

      public int getAccountId() {
         return accountId;
      }

      public int getTargetId() {
         return targetId;
      }

      public List<ChangeDetail> getChangeDetails() {
         return changeDetails;
      }

      @Override
      public int hashCode() {
         return Objects.hashCode(domain, action, targetType, accountId, targetId, changeDetails);
      }

      @Override
      public boolean equals(Object obj) {
         if (this == obj)
            return true;
         if (obj == null || getClass() != obj.getClass())
            return false;
         Change that = Change.class.cast(obj);
         return equal(this.domain, that.domain) && equal(this.action, that.action)
               && equal(this.targetType, that.targetType) && equal(this.accountId, that.accountId)
               && equal(this.targetId, that.targetId) && equal(this.changeDetails, that.changeDetails);
      }

      @Override
      public String toString() {
         return Objects.toStringHelper(this).omitNullValues().add("domain", domain).add("action", action)
               .add("targetType", targetType).add("accountId", accountId).add("targetId", targetId)
               .add("changeDetails", changeDetails).toString();
      }
   }

   public static class ChangeDetail {
      private final String field;
      private final String originalValue;
      private final String newValue;

      @ConstructorProperties({ "field", "originalValue", "newValue" })
      protected ChangeDetail(String field, String originalValue, String newValue) {
         this.field = field;
         this.originalValue = originalValue;
         this.newValue = newValue;
      }

      public String getField() {
         return field;
      }

      public String getOriginalValue() {
         return originalValue;
      }

      public String getNewValue() {
         return newValue;
      }

      @Override
      public int hashCode() {
         return Objects.hashCode(field, originalValue, newValue);
      }

      @Override
      public boolean equals(Object obj) {
         if (this == obj)
            return true;
         if (obj == null || getClass() != obj.getClass())
            return false;
         ChangeDetail that = ChangeDetail.class.cast(obj);
         return equal(this.field, that.field) && equal(this.originalValue, that.originalValue)
               && equal(this.newValue, that.newValue);
      }

      @Override
      public String toString() {
         return Objects.toStringHelper(this).omitNullValues().add("field", field).add("originalValue", originalValue)
               .add("newValue", newValue).toString();
      }
   }
}
