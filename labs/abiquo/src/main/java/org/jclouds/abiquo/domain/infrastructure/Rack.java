/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.abiquo.domain.infrastructure;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.filter;

import java.util.List;

import org.jclouds.abiquo.AbiquoAsyncApi;
import org.jclouds.abiquo.AbiquoApi;
import org.jclouds.abiquo.domain.DomainWrapper;
import org.jclouds.abiquo.reference.ValidationErrors;
import org.jclouds.abiquo.reference.rest.ParentLinkName;
import org.jclouds.rest.RestContext;

import com.abiquo.server.core.infrastructure.MachineDto;
import com.abiquo.server.core.infrastructure.MachinesDto;
import com.abiquo.server.core.infrastructure.RackDto;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * Adds high level functionality to {@link RackDto}. Represents unmanaged racks
 * in the Abiquo platform.
 * 
 * @author Ignasi Barrera
 * @author Francesc Montserrat
 * @see API: <a href="http://community.abiquo.com/display/ABI20/RackResource">
 *      http://community.abiquo.com/display/ABI20/RackResource</a>
 */
public class Rack extends DomainWrapper<RackDto> {
   /** The default minimum VLAN id. */
   private static final int DEFAULT_VLAN_ID_MIN = 2;

   /** The default maximum VLAN id. */
   private static final int DEFAULT_VLAN_ID_MAX = 4094;

   /** The default maximum VLAN per virtual datacenter. */
   private static final int DEFAULT_VLAN_PER_VDC = 1;

   /** The default nrsq factor. */
   private static final int DEFAULT_NRSQ = 10;

   /** The datacenter where the rack belongs. */
   private Datacenter datacenter;

   /**
    * Constructor to be used only by the builder.
    */
   protected Rack(final RestContext<AbiquoApi, AbiquoAsyncApi> context, final RackDto target) {
      super(context, target);
   }

   // Domain operations

   /**
    * Delete the unmanaged rack.
    * 
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/RackResource#RackResource-DeleteaRack"
    *      >
    *      http://community.abiquo.com/display/ABI20/Rack+Resource#RackResource
    *      #RackResource- DeleteaRack</a>
    */
   public void delete() {
      context.getApi().getInfrastructureApi().deleteRack(target);
      target = null;
   }

   /**
    * Create a new unmanaged rack in Abiquo.
    * 
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/RackResource#RackResource-CreateanewRack"
    *      > http://community.abiquo.com/display/ABI20/RackResource#RackResource
    *      -CreateanewRack</a>
    */
   public void save() {
      target = context.getApi().getInfrastructureApi().createRack(datacenter.unwrap(), target);
   }

   /**
    * Update rack information in the server with the data from this rack.
    * 
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/RackResource#RackResource-UpdateanexistingRack"
    *      >
    *      http://community.abiquo.com/display/ABI20/RackResource#RackResource-
    *      UpdateanexistingRack </a>
    */
   public void update() {
      target = context.getApi().getInfrastructureApi().updateRack(target);
   }

   // Parent access
   /**
    * Retrieve the datacenter where this rack is.
    * 
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/DatacenterResource#DatacenterResource-Retrieveadatacenter"
    *      > http://community.abiquo.com/display/ABI20/DatacenterResource#
    *      DatacenterResource- Retrieveadatacenter</a>
    */
   public Datacenter getDatacenter() {
      Integer datacenterId = target.getIdFromLink(ParentLinkName.DATACENTER);
      return wrap(context, Datacenter.class, context.getApi().getInfrastructureApi().getDatacenter(datacenterId));
   }

   // Children access

   /**
    * Retrieve the list of physical machines in this rack.
    * 
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/MachineResource#MachineResource-RetrievealistofMachines"
    *      > http://community.abiquo.com/display/ABI20/MachineResource#
    *      MachineResource- RetrievealistofMachines</a>
    */
   public List<Machine> listMachines() {
      MachinesDto machines = context.getApi().getInfrastructureApi().listMachines(target);
      return wrap(context, Machine.class, machines.getCollection());
   }

   /**
    * Retrieve a filtered list of physical machines in this rack.
    * 
    * @param filter
    *           Filter to be applied to the list.
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/MachineResource#MachineResource-RetrievealistofMachines"
    *      > http://community.abiquo.com/display/ABI20/MachineResource#
    *      MachineResource- RetrievealistofMachines</a>
    */
   public List<Machine> listMachines(final Predicate<Machine> filter) {
      return Lists.newLinkedList(filter(listMachines(), filter));
   }

   /**
    * Retrieve the first physical machine matching the filter within the list of
    * machines in this rack.
    * 
    * @param filter
    *           Filter to be applied to the list.
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/MachineResource#MachineResource-RetrievealistofMachines"
    *      > http://community.abiquo.com/display/ABI20/MachineResource#
    *      MachineResource- RetrievealistofMachines</a>
    */
   public Machine findMachine(final Predicate<Machine> filter) {
      return Iterables.getFirst(filter(listMachines(), filter), null);
   }

   /**
    * Retrieve a single physical machine.
    * 
    * @param id
    *           Unique ID of the physical machine in this rack.
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/MachineResource#MachineResource-RetrieveaMachine"
    *      > http://community.abiquo.com/display/ABI20/MachineResource#
    *      MachineResource-RetrieveaMachine </a>
    * @return Unmanaged rack with the given id or <code>null</code> if it does
    *         not exist.
    */
   public Machine getMachine(final Integer id) {
      MachineDto machine = context.getApi().getInfrastructureApi().getMachine(target, id);
      return wrap(context, Machine.class, machine);
   }

   // Builder

   public static Builder builder(final RestContext<AbiquoApi, AbiquoAsyncApi> context, final Datacenter datacenter) {
      return new Builder(context, datacenter);
   }

   public static class Builder {
      private RestContext<AbiquoApi, AbiquoAsyncApi> context;

      private Integer id;

      private String name;

      private String shortDescription;

      private boolean haEnabled = false;

      private Integer nrsq = DEFAULT_NRSQ;

      private Integer vlanIdMax = DEFAULT_VLAN_ID_MAX;

      private Integer vlanIdMin = DEFAULT_VLAN_ID_MIN;

      private Integer vlanPerVdcReserved = DEFAULT_VLAN_PER_VDC;

      private String vlansIdAvoided;

      private Datacenter datacenter;

      public Builder(final RestContext<AbiquoApi, AbiquoAsyncApi> context, final Datacenter datacenter) {
         super();
         checkNotNull(datacenter, ValidationErrors.NULL_RESOURCE + Datacenter.class);
         this.datacenter = datacenter;
         this.context = context;
      }

      public Builder id(final Integer id) {
         this.id = id;
         return this;
      }

      public Builder name(final String name) {
         this.name = name;
         return this;
      }

      public Builder shortDescription(final String shortDescription) {
         this.shortDescription = shortDescription;
         return this;
      }

      public Builder haEnabled(final boolean haEnabled) {
         this.haEnabled = haEnabled;
         return this;
      }

      public Builder nrsq(final int nrsq) {
         this.nrsq = nrsq;
         return this;
      }

      public Builder vlanIdMax(final int vlanIdMax) {
         this.vlanIdMax = vlanIdMax;
         return this;
      }

      public Builder vlanIdMin(final int vlanIdMin) {
         this.vlanIdMin = vlanIdMin;
         return this;
      }

      public Builder vlanPerVdcReserved(final int vlanPerVdcExpected) {
         this.vlanPerVdcReserved = vlanPerVdcExpected;
         return this;
      }

      public Builder VlansIdAvoided(final String vlansIdAvoided) {
         this.vlansIdAvoided = vlansIdAvoided;
         return this;
      }

      public Builder datacenter(final Datacenter datacenter) {
         checkNotNull(datacenter, ValidationErrors.NULL_RESOURCE + Datacenter.class);
         this.datacenter = datacenter;
         return this;
      }

      public Rack build() {
         RackDto dto = new RackDto();
         dto.setId(id);
         dto.setName(name);
         dto.setShortDescription(shortDescription);
         dto.setHaEnabled(haEnabled);
         dto.setNrsq(nrsq);
         dto.setVlanIdMax(vlanIdMax);
         dto.setVlanIdMin(vlanIdMin);
         dto.setVlanPerVdcReserved(vlanPerVdcReserved);
         dto.setVlansIdAvoided(vlansIdAvoided);
         Rack rack = new Rack(context, dto);
         rack.datacenter = datacenter;
         return rack;
      }

      public static Builder fromRack(final Rack in) {
         return Rack.builder(in.context, in.datacenter).id(in.getId()).name(in.getName())
               .shortDescription(in.getShortDescription()).haEnabled(in.isHaEnabled()).nrsq(in.getNrsq())
               .vlanIdMax(in.getVlanIdMax()).vlanIdMin(in.getVlanIdMin())
               .vlanPerVdcReserved(in.getVlanPerVdcReserved()).VlansIdAvoided(in.getVlansIdAvoided());
      }
   }

   // Delegate methods

   public Integer getId() {
      return target.getId();
   }

   public String getName() {
      return target.getName();
   }

   public String getShortDescription() {
      return target.getShortDescription();
   }

   public void setName(final String name) {
      target.setName(name);
   }

   public void setShortDescription(final String description) {
      target.setShortDescription(description);
   }

   public void setHaEnabled(final boolean haEnabled) {
      target.setHaEnabled(haEnabled);
   }

   public boolean isHaEnabled() {
      return target.isHaEnabled();
   }

   public Integer getNrsq() {
      return target.getNrsq();
   }

   public Integer getVlanIdMax() {
      return target.getVlanIdMax();
   }

   public Integer getVlanIdMin() {
      return target.getVlanIdMin();
   }

   public Integer getVlanPerVdcReserved() {
      return target.getVlanPerVdcReserved();
   }

   public String getVlansIdAvoided() {
      return target.getVlansIdAvoided();
   }

   public void setNrsq(final Integer nrsq) {
      target.setNrsq(nrsq);
   }

   public void setVlanIdMax(final Integer vlanIdMax) {
      target.setVlanIdMax(vlanIdMax);
   }

   public void setVlanIdMin(final Integer vlanIdMin) {
      target.setVlanIdMin(vlanIdMin);
   }

   public void setVlanPerVdcReserved(final Integer vlanPerVdcReserved) {
      target.setVlanPerVdcReserved(vlanPerVdcReserved);
   }

   public void setVlansIdAvoided(final String vlansIdAvoided) {
      target.setVlansIdAvoided(vlansIdAvoided);
   }

   @Override
   public String toString() {
      return "Rack [id=" + getId() + ", name=" + getName() + ", description=" + getShortDescription() + ", haEnabled="
            + isHaEnabled() + ", nrsq=" + getNrsq() + ", vlanIdMax=" + getVlanIdMax() + ", vlanIdMin=" + getVlanIdMin()
            + ", vlanPerVdcReserved=" + getVlanPerVdcReserved() + ", vlansIdAvoided=" + getVlansIdAvoided() + "]";
   }

}
