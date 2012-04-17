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
package org.jclouds.vcloud.director.v1_5.domain.dmtf;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.dmtf.DMTFConstants.OVF_NS;

import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.jclouds.dmtf.ovf.internal.BaseVirtualSystem;
import org.jclouds.vcloud.director.v1_5.domain.section.OperatingSystemSection;
import org.jclouds.vcloud.director.v1_5.domain.section.VirtualHardwareSection;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * @author Adrian Cole
 * @author Adam Lowe
 */
@XmlRootElement(name = "VirtualSystem", namespace = OVF_NS)
public class VirtualSystem extends BaseVirtualSystem {

   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return builder().fromVirtualSystem(this);
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
   }
   
   public static class Builder<B extends Builder<B>> extends BaseVirtualSystem.Builder<B> {

      private OperatingSystemSection operatingSystem;
      private Set<VirtualHardwareSection> virtualHardwareSections = Sets.newLinkedHashSet();


      /**
       * @see BaseVirtualSystem#getOperatingSystemSection()
       */
      public B operatingSystemSection(OperatingSystemSection operatingSystem) {
         this.operatingSystem = operatingSystem;
         return self();
      }

      /**
       * @see BaseVirtualSystem#getVirtualHardwareSections()
       */
      public B virtualHardwareSection(VirtualHardwareSection virtualHardwareSection) {
         this.virtualHardwareSections.add(checkNotNull(virtualHardwareSection, "virtualHardwareSection"));
         return self();
      }

      /**
       * @see BaseVirtualSystem#getVirtualHardwareSections()
       */
      public B virtualHardwareSections(Iterable<? extends VirtualHardwareSection> virtualHardwareSections) {
         this.virtualHardwareSections = Sets.newLinkedHashSet(checkNotNull(virtualHardwareSections, "virtualHardwareSections"));
         return self();
      }
      
      /**
       * {@inheritDoc}
       */
      @Override
      public VirtualSystem build() {
         return new VirtualSystem(this);
      }

      public B fromVirtualSystem(VirtualSystem in) {
         return fromBaseVirtualSystem(in)
               .operatingSystemSection(in.getOperatingSystemSection())
               .virtualHardwareSections(in.getVirtualHardwareSections());
      }
   }

   @XmlElement(name = "OperatingSystemSection", namespace = OVF_NS)
   private OperatingSystemSection operatingSystem;
   @XmlElement(name = "VirtualHardwareSection", namespace = OVF_NS)
   private Set<? extends VirtualHardwareSection> virtualHardwareSections;

   private VirtualSystem(Builder<?> builder) {
      super(builder);
      this.operatingSystem = checkNotNull(builder.operatingSystem, "operatingSystem");
      this.virtualHardwareSections = ImmutableSet.copyOf(checkNotNull(builder.virtualHardwareSections, "virtualHardwareSections"));
   }
   
   private VirtualSystem() {
      // for JAXB
   }

   public OperatingSystemSection getOperatingSystemSection() {
      return operatingSystem;
   }

   /**
    * Each VirtualSystem element may contain one or more VirtualHardwareSection elements, each of
    * which describes the virtual virtualHardwareSections required by the virtual system.
    */
   public Set<? extends VirtualHardwareSection> getVirtualHardwareSections() {
      return virtualHardwareSections;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(super.hashCode(), operatingSystem, virtualHardwareSections);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;

      VirtualSystem other = (VirtualSystem) obj;
      return super.equals(other) 
            && equal(operatingSystem, other.operatingSystem)
            && equal(virtualHardwareSections, other.virtualHardwareSections);
   }

   @Override
   protected Objects.ToStringHelper string() {
      return super.string()
            .add("operatingSystem", operatingSystem)
            .add("virtualHardwareSections", virtualHardwareSections);
   }
}