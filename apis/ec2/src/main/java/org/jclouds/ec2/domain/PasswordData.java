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
package org.jclouds.ec2.domain;

import java.util.Date;

/**
 * Holds the encrypted Windows Administrator password for an instance.
 *
 * @author Richard Downer
 */
public class PasswordData {

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {

      private String requestId;
      private String instanceId;
      private Date timestamp;
      private String passwordData;

      private Builder() {}

      public Builder requestId(String requestId) {
         this.requestId = requestId;
         return this;
      }

      public Builder instanceId(String instanceId) {
         this.instanceId = instanceId;
         return this;
      }

      public Builder timestamp(Date timestamp) {
         this.timestamp = timestamp;
         return this;
      }

      public Builder passwordData(String passwordData) {
         this.passwordData = passwordData;
         return this;
      }

      public PasswordData build() {
         return new PasswordData(requestId, instanceId, timestamp, passwordData);
      }
   }

   private String requestId;
   private String instanceId;
   private Date timestamp;
   private String passwordData;

   public PasswordData(String requestId, String instanceId, Date timestamp, String passwordData) {
      this.requestId = requestId;
      this.instanceId = instanceId;
      this.timestamp = timestamp;
      this.passwordData = passwordData;
   }

   public String getRequestId() {
      return requestId;
   }

   public String getInstanceId() {
      return instanceId;
   }

   public Date getTimestamp() {
      return timestamp;
   }

   public String getPasswordData() {
      return passwordData;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      PasswordData that = (PasswordData) o;

      if (instanceId != null ? !instanceId.equals(that.instanceId) : that.instanceId != null) return false;
      if (passwordData != null ? !passwordData.equals(that.passwordData) : that.passwordData != null) return false;
      if (requestId != null ? !requestId.equals(that.requestId) : that.requestId != null) return false;
      if (timestamp != null ? !timestamp.equals(that.timestamp) : that.timestamp != null) return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = requestId != null ? requestId.hashCode() : 0;
      result = 31 * result + (instanceId != null ? instanceId.hashCode() : 0);
      result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
      result = 31 * result + (passwordData != null ? passwordData.hashCode() : 0);
      return result;
   }

   @Override
   public String toString() {
      return "PasswordData{" +
         "requestId='" + requestId + '\'' +
         ", instanceId='" + instanceId + '\'' +
         ", timestamp=" + timestamp +
         ", passwordData='" + passwordData + '\'' +
         '}';
   }
}
