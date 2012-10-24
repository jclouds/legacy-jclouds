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
import org.jclouds.abiquo.reference.annotations.EnterpriseEdition;
import org.jclouds.abiquo.reference.rest.ParentLinkName;
import org.jclouds.rest.RestContext;

import com.abiquo.server.core.infrastructure.FsmsDto;
import com.abiquo.server.core.infrastructure.LogicServersDto;
import com.abiquo.server.core.infrastructure.MachinesDto;
import com.abiquo.server.core.infrastructure.OrganizationsDto;
import com.abiquo.server.core.infrastructure.RackDto;
import com.abiquo.server.core.infrastructure.UcsRackDto;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * Adds high level functionality to {@link RackDto}.
 * 
 * @author Ignasi Barrera
 * @author Francesc Montserrat
 * @see API: <a href="http://community.abiquo.com/display/ABI20/RackResource">
 *      http://community.abiquo.com/display/ABI20/RackResource</a>
 */
@EnterpriseEdition
public class ManagedRack extends DomainWrapper<UcsRackDto> {
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
   protected ManagedRack(final RestContext<AbiquoApi, AbiquoAsyncApi> context, final UcsRackDto target) {
      super(context, target);
   }

   // Domain operations

   /**
    * Delete the managed rack.
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
    * Create a new managed rack in Abiquo. This method wil discover the blades
    * configured in the UCS. If the data provided for the connection is invalid
    * a UcsRack will be created in Abiquo but with no Physical Machines attached
    * to it.
    * 
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/RackResource#RackResource-CreateanewUCSRack"
    *      > http://community.abiquo.com/display/ABI20/RackResource#RackResource
    *      -CreateanewUCSRack< /a>
    */
   public void save() {
      target = context.getApi().getInfrastructureApi().createManagedRack(datacenter.unwrap(), target);
   }

   /**
    * Update rack information in the server with the data from this rack. The IP
    * data member cannot be updated. If changed will be ignored and the old IP
    * will remain.
    * 
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/RackResource#RackResource#RackResource-UpdateanexistingUCSrack"
    *      > http://community.abiquo.com/display/ABI20/RackResource#RackResource
    *      #RackResource- UpdateanexistingUCSrack</a>
    */
   public void update() {
      target = context.getApi().getInfrastructureApi().updateManagedRack(target);
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
    * Retrieve the list of blades in this rack.
    * 
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/MachineResource#MachineResource-RetrievealistofMachines"
    *      > http://community.abiquo.com/display/ABI20/MachineResource#
    *      MachineResource- RetrievealistofMachines</a>
    */
   public List<Blade> listMachines() {
      MachinesDto machines = context.getApi().getInfrastructureApi().listMachines(target);
      return wrap(context, Blade.class, machines.getCollection());
   }

   /**
    * Retrieve a filtered list of blades in this rack.
    * 
    * @param filter
    *           Filter to be applied to the list.
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/MachineResource#MachineResource-RetrievealistofMachines"
    *      > http://community.abiquo.com/display/ABI20/MachineResource#
    *      MachineResource- RetrievealistofMachines</a>
    */
   public List<Blade> listMachines(final Predicate<Blade> filter) {
      return Lists.newLinkedList(filter(listMachines(), filter));
   }

   /**
    * Retrieve the first blade matching the filter within the list of machines
    * in this rack.
    * 
    * @param filter
    *           Filter to be applied to the list.
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/MachineResource#MachineResource-RetrievealistofMachines"
    *      > http://community.abiquo.com/display/ABI20/MachineResource#
    *      MachineResource- RetrievealistofMachines</a>
    */
   public Blade findMachine(final Predicate<Blade> filter) {
      return Iterables.getFirst(filter(listMachines(), filter), null);
   }

   /**
    * Retrieve the list of service profiles in this UCS rack.
    * 
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/RackResource#RackResource-RetrievealistofallservicesprofilesinaUCSrack"
    *      > http://community.abiquo.com/display/ABI20/RackResource#RackResource
    *      - RetrievealistofallservicesprofilesinaUCSrack</a>
    */
   public List<LogicServer> listServiceProfiles() {
      LogicServersDto profiles = context.getApi().getInfrastructureApi().listServiceProfiles(target);
      return wrap(context, LogicServer.class, profiles.getCollection());
   }

   /**
    * Retrieve a filtered list of service profiles in this UCS rack.
    * 
    * @param filter
    *           Filter to be applied to the list.
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/RackResource#RackResource-RetrievealistofallservicesprofilesinaUCSrack"
    *      > http://community.abiquo.com/display/ABI20/RackResource#RackResource
    *      - RetrievealistofallservicesprofilesinaUCSrack</a>
    */
   public List<LogicServer> listServiceProfiles(final Predicate<LogicServer> filter) {
      return Lists.newLinkedList(filter(listServiceProfiles(), filter));
   }

   /**
    * Retrieve the first service profile matching the filter within the list of
    * profiles in this rack.
    * 
    * @param filter
    *           Filter to be applied to the list.
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/RackResource#RackResource-RetrievealistofallservicesprofilesinaUCSrack"
    *      > http://community.abiquo.com/display/ABI20/RackResource#RackResource
    *      - RetrievealistofallservicesprofilesinaUCSrack</a>
    */
   public LogicServer findServiceProfile(final Predicate<LogicServer> filter) {
      return Iterables.getFirst(filter(listServiceProfiles(), filter), null);
   }

   /**
    * Retrieve the list of service profile templates in this UCS rack.
    * 
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/RackResource-RetrievealistofallServicesProfilesTemplatesinaUCSRack"
    *      > http://community.abiquo.com/display/ABI20/RackResource-
    *      RetrievealistofallServicesProfilesTemplatesinaUCSRack</a>
    */
   public List<LogicServer> listServiceProfileTemplates() {
      LogicServersDto templates = context.getApi().getInfrastructureApi().listServiceProfileTemplates(target);
      return wrap(context, LogicServer.class, templates.getCollection());
   }

   /**
    * Retrieve a filtered list of service profile templates in this UCS rack.
    * 
    * @param filter
    *           Filter to be applied to the list.
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/RackResource-RetrievealistofallServicesProfilesTemplatesinaUCSRack"
    *      > http://community.abiquo.com/display/ABI20/RackResource-
    *      RetrievealistofallServicesProfilesTemplatesinaUCSRack</a>
    */
   public List<LogicServer> listServiceProfileTemplates(final Predicate<LogicServer> filter) {
      return Lists.newLinkedList(filter(listServiceProfileTemplates(), filter));
   }

   /**
    * Retrieve the first service profile template matching the filter within the
    * list of templates in this rack.
    * 
    * @param filter
    *           Filter to be applied to the list.
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/RackResource-RetrievealistofallServicesProfilesTemplatesinaUCSRack"
    *      > http://community.abiquo.com/display/ABI20/RackResource-
    *      RetrievealistofallServicesProfilesTemplatesinaUCSRack</a>
    */
   public LogicServer findServiceProfileTemplate(final Predicate<LogicServer> filter) {
      return Iterables.getFirst(filter(listServiceProfileTemplates(), filter), null);
   }

   /**
    * Retrieve the list of organization in this UCS rack. The credentials in the
    * UcsRack configuration might not have enough rights in the UCS to retrieve
    * all organizations. Then only the allowed ones are returned. This data is
    * not persisted in Abiquo.
    * 
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/RackResource#RackResource-
    *      RetrieveallorganizationsfromaUCS">
    *      http://community.abiquo.com/display/ABI20/</a>
    */
   public List<Organization> listOrganizations() {
      OrganizationsDto organizations = context.getApi().getInfrastructureApi().listOrganizations(target);
      return wrap(context, Organization.class, organizations.getCollection());
   }

   /**
    * Retrieve a filtered list of organization in this UCS rack. The credentials
    * in the UcsRack configuration might not have enough rights in the UCS to
    * retrieve all organizations. Then only the allowed ones are returned. This
    * data is not persisted in Abiquo.
    * 
    * @param filter
    *           Filter to be applied to the list.
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/RackResource#RackResource-
    *      RetrieveallorganizationsfromaUCS" >
    *      http://community.abiquo.com/display/ABI20/RackResource#RackResource-
    *      RetrieveallorganizationsfromaUCS</a>
    */
   public List<Organization> listOrganizations(final Predicate<Organization> filter) {
      return Lists.newLinkedList(filter(listOrganizations(), filter));
   }

   /**
    * Retrieve the first organization matching the filter within the list of
    * organization in this rack. The credentials in the UcsRack configuration
    * might not have enough rights in the UCS to retrieve all organizations.
    * Then only the allowed ones are returned. This data is not persisted in
    * Abiquo.
    * 
    * @param filter
    *           Filter to be applied to the list.
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/RackResource#RackResource-
    *      RetrieveallorganizationsfromaUCS">
    *      http://community.abiquo.com/display/ABI20/RackResource#RackResource-
    *      RetrieveallorganizationsfromaUCS</a>
    */
   public Organization findOrganization(final Predicate<Organization> filter) {
      return Iterables.getFirst(filter(listOrganizations(), filter), null);
   }

   /**
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/RackResource#RackResource-RetrieveFSMofanentityinUCS"
    *      > http://community.abiquo.com/display/ABI20/RackResource#RackResource
    *      - RetrieveFSMofanentityinUCS</a>
    */
   public List<Fsm> listFsm(final String entityName) {
      FsmsDto fsms = context.getApi().getInfrastructureApi().listFsms(target, entityName);
      return wrap(context, Fsm.class, fsms.getCollection());
   }

   // Actions

   /**
    * Clone a Service Profile this rack. This data is not persisted in Abiquo.
    * 
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/RackResource#RackResource-ClonelogicserverinUCS"
    *      > http://community.abiquo.com/display/ABI20/RackResource#RackResource
    *      - ClonelogicserverinUCS</a>
    */
   public void cloneLogicServer(final LogicServer logicServer, final Organization organization, final String newName) {
      context.getApi().getInfrastructureApi()
            .cloneLogicServer(this.unwrap(), logicServer.unwrap(), organization.unwrap(), newName);
   }

   /**
    * Associate a Service Profile and a Blade in UCS. If the Service Profile is
    * already associated then the request cannot be completed. This data is not
    * persisted in Abiquo.
    * 
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/RackResource#RackResource-AssociatelogicserverwithabladeinUCS"
    *      > http://community.abiquo.com/display/ABI20/RackResource#RackResource
    *      - AssociatelogicserverwithabladeinUCS</a>
    */
   public void associateLogicServer(final String bladeName, final LogicServer logicServer,
         final Organization organization) {
      context.getApi().getInfrastructureApi()
            .associateLogicServer(this.unwrap(), logicServer.unwrap(), organization.unwrap(), bladeName);
   }

   /**
    * Clone and associate a Service Profile and a Blade in UCS. If the Blade is
    * already associated then Abiquo will dissociate it first. If the request
    * cannot be completed successfully the Blade might be left with no Service
    * Profile associated. This data is not persisted in Abiquo.
    * 
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/RackResource#RackResource-CloneandassociateLogicServerwithabladeinUCS"
    *      > http://community.abiquo.com/display/ABI20/RackResource#RackResource
    *      - CloneandassociateLogicServerwithabladeinUCS</a>
    */
   public void cloneAndAssociateLogicServer(final String bladeName, final LogicServer logicServer,
         final Organization organization, final String logicServerName) {
      context
            .getApi()
            .getInfrastructureApi()
            .cloneAndAssociateLogicServer(this.unwrap(), logicServer.unwrap(), organization.unwrap(), bladeName,
                  logicServerName);
   }

   /**
    * Instantiate and associate a Service Profile Template and a Blade in UCS.
    * If the Service Profile is already associated the request cannot be
    * successful. If the Blade is already associated then Abiquo will dissociate
    * it first. If the request cannot be completed successfully the Blade might
    * be left with no Service Profile associated. This data is not persisted in
    * Abiquo.
    * 
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/RackResource#RackResource-AssociateabladewithaLogicServerTemplate"
    *      > http://community.abiquo.com/display/ABI20/RackResource#RackResource
    *      - AssociateabladewithaLogicServerTemplate</a>
    */
   public void associateLogicServerTemplate(final String bladeName, final LogicServer logicServer,
         final Organization organization, final String logicServerName) {
      context.getApi().getInfrastructureApi()
            .associateTemplate(this.unwrap(), logicServer.unwrap(), organization.unwrap(), bladeName, logicServerName);
   }

   /**
    * Dissociates a Service Profile and a Blade in UCS. This data is not
    * persisted in Abiquo.
    * 
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/RackResource#RackResource-DisassociatelogicserverfromabladeinUCS"
    *      > http://community.abiquo.com/display/ABI20/RackResource#RackResource
    *      - DisassociatelogicserverfromabladeinUCS</a>
    */
   public void disassociateLogicServer(final LogicServer logicServer) {
      context.getApi().getInfrastructureApi().dissociateLogicServer(this.unwrap(), logicServer.unwrap());
   }

   /**
    * Deletes a Service Profile in UCS. This data is not persisted in Abiquo.
    * 
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/RackResource#RackResource-DeletelogicserverwithabladeinUCS"
    *      > http://community.abiquo.com/display/ABI20/RackResource#RackResource
    *      - DeletelogicserverwithabladeinUCS</a>
    */
   public void deleteLogicServer(final LogicServer logicServer) {
      context.getApi().getInfrastructureApi().deleteLogicServer(this.unwrap(), logicServer.unwrap());
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

      private Integer port;

      private String ip;

      private String password;

      private String user;

      private String defaultTemplate;

      private Integer maxMachinesOn;

      private Datacenter datacenter;

      public Builder(final RestContext<AbiquoApi, AbiquoAsyncApi> context, final Datacenter datacenter) {
         super();
         checkNotNull(datacenter, ValidationErrors.NULL_RESOURCE + Datacenter.class);
         this.datacenter = datacenter;
         this.context = context;
      }

      public Builder port(final Integer port) {
         this.port = port;
         return this;
      }

      public Builder ipAddress(final String ip) {
         this.ip = ip;
         return this;
      }

      public Builder password(final String password) {
         this.password = password;
         return this;
      }

      public Builder user(final String user) {
         this.user = user;
         return this;
      }

      public Builder defaultTemplate(final String defaultTemplate) {
         this.defaultTemplate = defaultTemplate;
         return this;
      }

      public Builder maxMachinesOn(final Integer maxMachinesOn) {
         this.maxMachinesOn = maxMachinesOn;
         return this;
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

      public ManagedRack build() {
         UcsRackDto dto = new UcsRackDto();
         dto.setId(id);
         dto.setName(name);
         dto.setShortDescription(shortDescription);
         dto.setHaEnabled(haEnabled);
         dto.setNrsq(nrsq);
         dto.setVlanIdMax(vlanIdMax);
         dto.setVlanIdMin(vlanIdMin);
         dto.setVlanPerVdcReserved(vlanPerVdcReserved);
         dto.setVlansIdAvoided(vlansIdAvoided);
         dto.setPort(port);
         dto.setIp(ip);
         dto.setPassword(password);
         dto.setUser(user);
         dto.setDefaultTemplate(defaultTemplate);
         dto.setMaxMachinesOn(maxMachinesOn);

         ManagedRack rack = new ManagedRack(context, dto);
         rack.datacenter = datacenter;
         return rack;
      }

      public static Builder fromRack(final ManagedRack in) {
         return ManagedRack.builder(in.context, in.datacenter).id(in.getId()).name(in.getName())
               .shortDescription(in.getShortDescription()).haEnabled(in.isHaEnabled()).nrsq(in.getNrsq())
               .vlanIdMax(in.getVlanIdMax()).vlanIdMin(in.getVlanIdMin())
               .vlanPerVdcReserved(in.getVlanPerVdcReserved()).VlansIdAvoided(in.getVlansIdAvoided())
               .port(in.getPort()).ipAddress(in.getIp()).password(in.getPassword()).user(in.getUser())
               .defaultTemplate(in.getDefaultTemplate()).maxMachinesOn(in.getMaxMachinesOn());
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

   public String getIp() {
      return target.getIp();
   }

   public String getLongDescription() {
      return target.getLongDescription();
   }

   public Integer getMaxMachinesOn() {
      return target.getMaxMachinesOn();
   }

   public String getPassword() {
      return target.getPassword();
   }

   public Integer getPort() {
      return target.getPort();
   }

   public String getUser() {
      return target.getUser();
   }

   public void setDefaultTemplate(final String defaultTemplate) {
      target.setDefaultTemplate(defaultTemplate);
   }

   public String getDefaultTemplate() {
      return target.getDefaultTemplate();
   }

   public void setIp(final String ip) {
      target.setIp(ip);
   }

   public void setMaxMachinesOn(final Integer maxMachinesOn) {
      target.setMaxMachinesOn(maxMachinesOn);
   }

   public void setPassword(final String password) {
      target.setPassword(password);
   }

   public void setPort(final Integer port) {
      target.setPort(port);
   }

   public void setUser(final String user) {
      target.setUser(user);
   }

   @Override
   public String toString() {
      return "ManagedRack [id=" + getId() + ", name=" + getName() + ", shortDescription=" + getShortDescription()
            + ", haEnabled=" + isHaEnabled() + ", nrsq=" + getNrsq() + ", vlanIdMax=" + getVlanIdMax() + ", vlanIdMin="
            + getVlanIdMin() + ", vlanPerVdcReserved=" + getVlanPerVdcReserved() + ", vlansIdAvoided="
            + getVlansIdAvoided() + ", ip=" + getIp() + ", longDescription=" + getLongDescription()
            + ", maxMachinesOn=" + getMaxMachinesOn() + ", password=**PROTECTED**, port=" + getPort() + ", user="
            + getUser() + ", defaultTemplate=" + getDefaultTemplate() + "]";
   }

}
