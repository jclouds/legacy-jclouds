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
package org.jclouds.rds.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;

/**
 *  
 * @see <a href="http://docs.amazonwebservices.com/AmazonRDS/latest/APIReference/API_Subnet.html"
 *      >doc</a>
 * 
 * @author Adrian Cole
 */
public class Subnet {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromSubnet(this);
   }

   public static class Builder {

      protected String id;
      protected String availabilityZone;
      protected String status;

      /**
       * @see Subnet#getId()
       */
      public Builder id(String id) {
         this.id = id;
         return this;
      }

      /**
       * @see Subnet#getAvailabilityZone()
       */
      public Builder availabilityZone(String availabilityZone) {
         this.availabilityZone = availabilityZone;
         return this;
      }

      /**
       * @see Subnet#getStatus()
       */
      public Builder status(String status) {
         this.status = status;
         return this;
      }

      public Subnet build() {
         return new Subnet(id, availabilityZone, status);
      }

      public Builder fromSubnet(Subnet in) {
         return this.id(in.getId()).availabilityZone(in.getAvailabilityZone()).status(in.getStatus());
      }
   }

   protected final String id;
   protected final String availabilityZone;
   protected final String status;

   protected Subnet(String id, String availabilityZone, String status) {
      this.id = checkNotNull(id, "id");
      this.availabilityZone = checkNotNull(availabilityZone, "availabilityZone");
      this.status = checkNotNull(status, "status");
   }

   /**
    * Specifies the identifier of the subnet.
    */
   public String getId() {
      return id;
   }

   /**
    * The name of the availability zone.
    */
   public String getAvailabilityZone() {
      return availabilityZone;
   }

   /**
    * Specifies the current status of the securityGroup.
    */
   public String getStatus() {
      return status;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      return Objects.hashCode(id);
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
      Subnet other = Subnet.class.cast(obj);
      return Objects.equal(this.id, other.id);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return Objects.toStringHelper(this).omitNullValues().add("id", id).add("availabilityZone", availabilityZone)
               .add("status", status).toString();
   }

}
