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
package org.jclouds.trmk.vcloud_0_8.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.CaseFormat;

/**
 * @author Adrian Cole
 */
public class IpAddress implements Comparable<IpAddress> {

   public static enum Status {
      AVAILABLE, ASSIGNED, UNRECOGNIZED;
      public String value() {
         return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, name());
      }

      @Override
      public String toString() {
         return value();
      }

      public static Status fromValue(String status) {
         try {
            return valueOf(CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, checkNotNull(status, "status")));
         } catch (IllegalArgumentException e) {
            return UNRECOGNIZED;
         }
      }

   }

   private final String address;
   private final Status status;
   @Nullable
   private final String server;

   public IpAddress(String address, Status status, String server) {
      this.address = address;
      this.status = status;
      this.server = server;
   }

   public String getAddress() {
      return address;
   }

   public Status getStatus() {
      return status;
   }

   public String getServer() {
      return server;
   }

   @Override
   public String toString() {
      return "IpAddress [address=" + address + ", server=" + server + ", status=" + status + "]";
   }

   @Override
   public int compareTo(IpAddress o) {
      return (this == o) ? 0 : getAddress().compareTo(o.getAddress());
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((address == null) ? 0 : address.hashCode());
      result = prime * result + ((server == null) ? 0 : server.hashCode());
      result = prime * result + ((status == null) ? 0 : status.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      IpAddress other = (IpAddress) obj;
      if (address == null) {
         if (other.address != null)
            return false;
      } else if (!address.equals(other.address))
         return false;
      if (server == null) {
         if (other.server != null)
            return false;
      } else if (!server.equals(other.server))
         return false;
      if (status == null) {
         if (other.status != null)
            return false;
      } else if (!status.equals(other.status))
         return false;
      return true;
   }

}
