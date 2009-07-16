/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.rackspace.cloudservers.domain;

import java.net.InetAddress;
import java.util.List;

import com.google.gson.annotations.SerializedName;
import com.google.inject.internal.Lists;

public class Addresses {
   @SerializedName("public")
   private List<InetAddress> publicAddresses = Lists.newArrayList();
   @SerializedName("private")
   private List<InetAddress> privateAddresses = Lists.newArrayList();

   public Addresses() {
   }

   public Addresses(List<InetAddress> publicAddresses, List<InetAddress> privateAddresses) {
      this.publicAddresses = publicAddresses;
      this.privateAddresses = privateAddresses;
   }

   public void setPublicAddresses(List<InetAddress> publicAddresses) {
      this.publicAddresses = publicAddresses;
   }

   public List<InetAddress> getPublicAddresses() {
      return publicAddresses;
   }

   public void setPrivateAddresses(List<InetAddress> privateAddresses) {
      this.privateAddresses = privateAddresses;
   }

   public List<InetAddress> getPrivateAddresses() {
      return privateAddresses;
   }

   @Override
   public String toString() {
      return "Addresses [privateAddresses=" + privateAddresses + ", publicAddresses="
               + publicAddresses + "]";
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((privateAddresses == null) ? 0 : privateAddresses.hashCode());
      result = prime * result + ((publicAddresses == null) ? 0 : publicAddresses.hashCode());
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
      Addresses other = (Addresses) obj;
      if (privateAddresses == null) {
         if (other.privateAddresses != null)
            return false;
      } else if (!privateAddresses.equals(other.privateAddresses))
         return false;
      if (publicAddresses == null) {
         if (other.publicAddresses != null)
            return false;
      } else if (!publicAddresses.equals(other.publicAddresses))
         return false;
      return true;
   }

}
