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
 * Unless(Link.builder().required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.vcloud.director.v1_5.domain.network;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

import org.jclouds.vcloud.director.v1_5.domain.Entity;
import org.jclouds.vcloud.director.v1_5.domain.org.OrgNetwork;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

@XmlSeeAlso({ OrgNetwork.class, ExternalNetwork.class })
public abstract class Network extends Entity {
   
   @XmlType
   @XmlEnum(String.class)
   public static enum FenceMode {
      @XmlEnumValue("bridged") BRIDGED("bridged"),
      @XmlEnumValue("isolated") ISOLATED("isolated"),
      @XmlEnumValue("natRouted") NAT_ROUTED("natRouted"),
      UNRECOGNIZED("unrecognized");
      
      public static final List<FenceMode> ALL = ImmutableList.of(
            BRIDGED, ISOLATED, NAT_ROUTED);

      protected final String stringValue;

      FenceMode(String stringValue) {
         this.stringValue = stringValue;
      }

      public String value() {
         return stringValue;
      }

      protected final static Map<String, FenceMode> FENCE_MODE_BY_ID = Maps.uniqueIndex(
            ImmutableSet.copyOf(FenceMode.values()), new Function<FenceMode, String>() {
               @Override
               public String apply(FenceMode input) {
                  return input.stringValue;
               }
            });

      public static FenceMode fromValue(String value) {
         FenceMode mode = FENCE_MODE_BY_ID.get(checkNotNull(value, "stringValue"));
         return mode == null ? UNRECOGNIZED : mode;
      }
   }
   
   public abstract static class Builder<T extends Builder<T>> extends Entity.Builder<T> {
      protected NetworkConfiguration networkConfiguration;

      /**
       * @see Network#getConfiguration()
       */
      public T configuration(NetworkConfiguration networkConfiguration) {
         this.networkConfiguration = networkConfiguration;
         return self();
      }

      public T fromNetwork(Network in) {
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
