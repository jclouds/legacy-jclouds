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
package org.jclouds.ec2.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Date;

import com.google.common.base.Objects;

/**
 * The encrypted administrator password for an instance running Windows.
 * 
 * <h4>Note</h4>
 * 
 * The Windows password is only generated the first time an AMI is launched. It is not generated for
 * rebundled AMIs or after the password is changed on an instance.
 * 
 * The password is encrypted using the key pair that you provided.
 * 
 * @see <a
 *      href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-GetPasswordData.html"
 *      >doc</a>
 * 
 * @author Richard Downer
 */
public class PasswordData {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromPasswordData(this);
   }

   public static class Builder {

      protected String instanceId;
      protected Date timestamp;
      protected String passwordData;

      /**
       * @see PasswordData#getInstanceId()
       */
      public Builder instanceId(String instanceId) {
         this.instanceId = instanceId;
         return this;
      }

      /**
       * @see PasswordData#getTimestamp()
       */
      public Builder timestamp(Date timestamp) {
         this.timestamp = timestamp;
         return this;
      }

      /**
       * @see PasswordData#getPasswordData()
       */
      public Builder passwordData(String passwordData) {
         this.passwordData = passwordData;
         return this;
      }

      public PasswordData build() {
         return new PasswordData(instanceId, timestamp, passwordData);
      }

      public Builder fromPasswordData(PasswordData in) {
         return this.instanceId(in.getInstanceId()).timestamp(in.getTimestamp()).passwordData(in.getPasswordData());
      }
   }

   protected final String instanceId;
   protected final Date timestamp;
   protected final String passwordData;

   protected PasswordData(String instanceId, Date timestamp, String passwordData) {
      this.instanceId = checkNotNull(instanceId, "instanceId");
      this.timestamp = checkNotNull(timestamp, "timestamp");
      this.passwordData = checkNotNull(passwordData, "passwordData");
   }

   /**
    * The ID of the instance.
    */
   public String getInstanceId() {
      return instanceId;
   }

   /**
    * The time the data was last updated.
    */
   public Date getTimestamp() {
      return timestamp;
   }

   /**
    * The password of the instance.
    */
   public String getPasswordData() {
      return passwordData;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      return Objects.hashCode(instanceId);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      PasswordData other = PasswordData.class.cast(obj);
      return Objects.equal(this.instanceId, other.instanceId);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return Objects.toStringHelper(this).omitNullValues().add("instanceId", instanceId).add("timestamp", timestamp)
               .add("passwordData", passwordData).toString();
   }

}
