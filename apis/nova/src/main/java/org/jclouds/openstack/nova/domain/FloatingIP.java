/*
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
package org.jclouds.openstack.nova.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.beans.ConstructorProperties;
import java.net.URI;
import java.util.List;
import java.util.Map;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Check <a href="http://wiki.openstack.org/os_api_floating_ip">Floating IP Wiki
 * page</a>. Available since OpenStack Diablo release and API 1.1.
 * 
 * @author chamerling
*/
public class FloatingIP extends Resource {

   public static Builder<?> builder() { 
      return new ConcreteBuilder();
   }
   
   public Builder<?> toBuilder() { 
      return new ConcreteBuilder().fromFloatingIP(this);
   }

   public abstract static class Builder<T extends Builder<T>> extends Resource.Builder<T>  {
      protected String ip;
      protected String fixedIP;
      protected int instanceID;
   
      /** 
       * @see FloatingIP#getIp()
       */
      public T ip(String ip) {
         this.ip = ip;
         return self();
      }

      /** 
       * @see FloatingIP#getFixedIP()
       */
      public T fixedIP(String fixedIP) {
         this.fixedIP = fixedIP;
         return self();
      }

      /** 
       * @see FloatingIP#getInstanceID()
       */
      public T instanceID(int instanceID) {
         this.instanceID = instanceID;
         return self();
      }

      public FloatingIP build() {
         return new FloatingIP(id, links, orderedSelfReferences, ip, fixedIP, instanceID);
      }
      
      public T fromFloatingIP(FloatingIP in) {
         return super.fromResource(in)
                  .ip(in.getIp())
                  .fixedIP(in.getFixedIP())
                  .instanceID(in.getInstanceID());
      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   private final String ip;
   private final String fixedIP;
   private final int instanceID;

   @ConstructorProperties({
         "id", "links", "orderedSelfReferences", "ip", "fixed_ip", "instance_id"
   })
   protected FloatingIP(int id, List<Map<String, String>> links, Map<LinkType, URI> orderedSelfReferences, String ip,
                        String fixedIP, int instanceID) {
      super(id, links, orderedSelfReferences);
      this.ip = checkNotNull(ip, "ip");
      this.fixedIP = checkNotNull(fixedIP, "fixedIP");
      this.instanceID = instanceID;
   }

   /**
    * @return the ip
    */
   public String getIp() {
      return this.ip;
   }

   /**
    * @return the fixedIP
    */
   public String getFixedIP() {
      return this.fixedIP;
   }

   /**
    * @return the instanceID
    */
   public int getInstanceID() {
      return this.instanceID;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), ip, fixedIP, instanceID);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      FloatingIP that = FloatingIP.class.cast(obj);
      return super.equals(that)
               && Objects.equal(this.ip, that.ip)
               && Objects.equal(this.fixedIP, that.fixedIP)
               && Objects.equal(this.instanceID, that.instanceID);
   }
   
   protected ToStringHelper string() {
      return super.string().add("ip", ip).add("fixedIP", fixedIP).add("instanceID", instanceID);
   }

}
