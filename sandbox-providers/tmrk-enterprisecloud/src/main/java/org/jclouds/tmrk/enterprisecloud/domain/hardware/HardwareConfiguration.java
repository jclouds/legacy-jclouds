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
package org.jclouds.tmrk.enterprisecloud.domain.hardware;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.tmrk.enterprisecloud.domain.Action;
import org.jclouds.tmrk.enterprisecloud.domain.Actions;
import org.jclouds.tmrk.enterprisecloud.domain.internal.BaseResource;
import org.jclouds.tmrk.enterprisecloud.domain.network.Nics;
import org.jclouds.tmrk.enterprisecloud.domain.network.VirtualNic;

import javax.xml.bind.annotation.XmlElement;
import java.net.URI;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * <xs:complexType name="HardwareConfiguration">
 * @author Jason King
 */
public class HardwareConfiguration extends BaseResource<HardwareConfiguration> {

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Builder toBuilder() {
      return new Builder().fromHardwareConfiguration(this);
   }

   public static class Builder extends BaseResource.Builder<HardwareConfiguration> {

       // TODO Links
       private Set<Action> actions = Sets.newLinkedHashSet();
       private int processorCount;
       private Memory memory;
       private Set<VirtualDisk> virtualDisks = Sets.newLinkedHashSet();
       private Set<VirtualNic> virtualNics = Sets.newLinkedHashSet();

       /**
        * @see HardwareConfiguration#getActions
        */
       public Builder actions(Set<Action> actions) {
          this.actions = ImmutableSet.<Action> copyOf(checkNotNull(actions, "actions"));
          return this;
       }

       /**
        * @see HardwareConfiguration#getProcessorCount
        */
       public Builder processorCount(int processorCount) {
          this.processorCount = processorCount;
          return this;
       }

       /**
        * @see HardwareConfiguration#getMemory
        */
       public Builder memory(Memory memory) {
          this.memory = memory;
          return this;
       }

       /**
        * @see HardwareConfiguration#getVirtualDisks
        */
       public Builder disks(Set<VirtualDisk> virtualDisks) {
          this.virtualDisks = ImmutableSet.<VirtualDisk> copyOf(checkNotNull(virtualDisks, "virtualDisks"));
          return this;
       }


       /**
        * @see HardwareConfiguration#getVirtualNics
        */
       public Builder nics(Set<VirtualNic> virtualNics) {
          this.virtualNics = ImmutableSet.<VirtualNic> copyOf(checkNotNull(virtualNics, "virtualNics"));
          return this;
       }

       @Override
       public HardwareConfiguration build() {
           return new HardwareConfiguration(actions, processorCount, memory, virtualDisks, virtualNics);
       }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromResource(BaseResource<HardwareConfiguration> in) {
         return Builder.class.cast(super.fromResource(in));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder type(String type) {
         return Builder.class.cast(super.type(type));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder href(URI href) {
         return Builder.class.cast(super.href(href));
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Builder fromAttributes(Map<String, String> attributes) {
         return Builder.class.cast(super.fromAttributes(attributes));
      }

      public Builder fromHardwareConfiguration(HardwareConfiguration in) {
        return fromResource(in).actions(in.getActions())
                               .processorCount(in.getProcessorCount())
                               .memory(in.getMemory())
                               .disks(in.getVirtualDisks())
                               .nics(in.getVirtualNics());
      }
   }

   @XmlElement(name = "Actions", required = false)
   private Actions actions = new Actions();

   @XmlElement(name = "ProcessorCount", required = true)
   private int processorCount;

   @XmlElement(name = "Memory", required = false)
   private Memory memory;

   @XmlElement(name = "Disks", required = false)
   private Disks virtualDisks = new Disks();

   @XmlElement(name = "Nics", required = false)
   private Nics virtualNics = new Nics();

   public HardwareConfiguration(Set<Action> actions, int processorCount, @Nullable Memory memory, Set<VirtualDisk> virtualDisks, Set<VirtualNic> virtualNics) {
       this.actions = Actions.builder().actions(checkNotNull(actions, "actions")).build();
       for( VirtualDisk disk: checkNotNull(virtualDisks, "virtualDisks")) this.virtualDisks.setVirtualDisk(disk);
       for( VirtualNic virtualNic: checkNotNull(virtualNics, "virtualNics")) this.virtualNics.setVirtualNic(virtualNic);

       this.processorCount = processorCount;
       this.memory = memory;
   }

    protected HardwareConfiguration() {
        //For JAXB
    }

    public Set<Action> getActions() {
       return Collections.unmodifiableSet(actions.getActions());
    }

    public int getProcessorCount() {
        return processorCount;
    }

    public Memory getMemory() {
        return memory;
    }

    public Set<VirtualDisk> getVirtualDisks() {
        return Collections.unmodifiableSet(virtualDisks.getVirtualDisks());
    }

    public Set<VirtualNic> getVirtualNics() {
        return Collections.unmodifiableSet(virtualNics.getVirtualNics());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        HardwareConfiguration that = (HardwareConfiguration) o;

        if (processorCount != that.processorCount) return false;
        if (!actions.equals(that.actions)) return false;
        if (!virtualDisks.equals(that.virtualDisks)) return false;
        if (memory != null ? !memory.equals(that.memory) : that.memory != null)
            return false;
        if (!virtualNics.equals(that.virtualNics)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + actions.hashCode();
        result = 31 * result + processorCount;
        result = 31 * result + (memory != null ? memory.hashCode() : 0);
        result = 31 * result + virtualDisks.hashCode();
        result = 31 * result + virtualNics.hashCode();
        return result;
    }

    @Override
    public String string() {
        return super.string()+", actions="+actions+", processorCount="+processorCount+
              ", memory="+memory+", disks="+ virtualDisks +", nics="+ virtualNics;
    }
}
