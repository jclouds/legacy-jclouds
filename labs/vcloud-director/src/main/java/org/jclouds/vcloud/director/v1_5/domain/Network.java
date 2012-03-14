/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 *(Link.builder().regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless(Link.builder().required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.vcloud.director.v1_5.domain;

import static com.google.common.base.Objects.equal;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

@XmlSeeAlso( {OrgNetwork.class, ExternalNetwork.class} )
public abstract class Network extends EntityType {
   public abstract static class Builder<T extends Builder<T>> extends EntityType.Builder<T> {
      protected NetworkConfiguration networkConfiguration;

      /**
       * @see Network#getConfiguration()
       */
      public T configuration(NetworkConfiguration networkConfiguration) {
         this.networkConfiguration = networkConfiguration;
         return self();
      }

      public T fromNetworkType(Network in) {
         return fromEntityType(in).configuration(in.getConfiguration());
      }
   }
   
   public Network(Builder<?> b) {
      super(b);
      networkConfiguration = b.networkConfiguration;
   }

   protected Network() {
      // for JAXB
   }
   
   @SuppressWarnings("unchecked")
   public static <T extends Network> T toSubType(Network clazz) {
      assert clazz instanceof Network;
      return (T)clazz;
   }

   @XmlElement(name = "Configuration")
   private NetworkConfiguration networkConfiguration;

   /**
    * @return optional configuration
    */
   public NetworkConfiguration getConfiguration() {
      return networkConfiguration;
   }

   @Override
   public boolean equals(Object o) {
      if (!super.equals(o))
         return false;
      Network that = Network.class.cast(o);
      return super.equals(that) && equal(networkConfiguration, that.networkConfiguration);
   }

   @Override
   public int hashCode() {
      return super.hashCode() + Objects.hashCode(networkConfiguration);
   }

   @Override
   public ToStringHelper string() {
      return super.string().add("configuration", networkConfiguration);
   }
}
