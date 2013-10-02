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
package org.jclouds.rackspace.cloudloadbalancers.v1.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * @see VirtualIP
 * 
 * @author Everett Toews
 */
public class VirtualIPWithId extends VirtualIP {

   private final int id;
   private final String address;

   public VirtualIPWithId(Type type, IPVersion ipVersion, int id, String address) {
      super(type, ipVersion);
      this.id = id;
      this.address = checkNotNull(address, "address");
   }

   public int getId() {
      return id;
   }

   public String getAddress() {
      return address;
   }
   
   protected ToStringHelper string() {
      return Objects.toStringHelper(this).omitNullValues()
            .add("id", id).add("address", address).add("ipVersion", getIpVersion()).add("type", getType());
   }
   
   @Override
   public String toString() {
      return string().toString();
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;

      VirtualIPWithId that = VirtualIPWithId.class.cast(obj);
      return Objects.equal(this.id, that.id);
   }
}
