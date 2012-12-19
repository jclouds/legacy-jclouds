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
import static com.google.common.collect.Iterables.find;

import java.util.List;

import org.jclouds.abiquo.AbiquoApi;
import org.jclouds.abiquo.AbiquoAsyncApi;
import org.jclouds.abiquo.domain.cloud.VirtualMachine;
import org.jclouds.abiquo.domain.enterprise.Enterprise;
import org.jclouds.abiquo.domain.infrastructure.options.MachineOptions;
import org.jclouds.abiquo.predicates.infrastructure.DatastorePredicates;
import org.jclouds.abiquo.predicates.infrastructure.NetworkInterfacePredicates;
import org.jclouds.abiquo.reference.ValidationErrors;
import org.jclouds.abiquo.reference.rest.ParentLinkName;
import org.jclouds.abiquo.rest.internal.ExtendedUtils;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseXMLWithJAXB;
import org.jclouds.rest.RestContext;

import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.model.enumerator.MachineState;
import com.abiquo.model.rest.RESTLink;
import com.abiquo.server.core.cloud.VirtualMachineWithNodeExtendedDto;
import com.abiquo.server.core.cloud.VirtualMachinesWithNodeExtendedDto;
import com.abiquo.server.core.enterprise.EnterpriseDto;
import com.abiquo.server.core.infrastructure.DatastoresDto;
import com.abiquo.server.core.infrastructure.MachineDto;
import com.abiquo.server.core.infrastructure.MachineStateDto;
import com.abiquo.server.core.infrastructure.RackDto;
import com.abiquo.server.core.infrastructure.network.NetworkInterfacesDto;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.inject.TypeLiteral;

/**
 * Adds high level functionality to {@link MachineDto}. This resource allows you
 * to manage physical machines in the cloud infrastructure.
 * 
 * @author Ignasi Barrera
 * @author Francesc Montserrat
 * @see API: <a
 *      href="http://community.abiquo.com/display/ABI20/MachineResource">
 *      http://community.abiquo.com/display/ABI20/MachineResource</a>
 */
public class Machine extends AbstractPhysicalMachine {
   /** The rack where the machine belongs. */
   protected Rack rack;

   /**
    * Constructor to be used only by the builder.
    */
   protected Machine(final RestContext<AbiquoApi, AbiquoAsyncApi> context, final MachineDto target) {
      super(context, target);
   }

   /**
    * Create a new physical machine in Abiquo. The best way to create a machine
    * if first calling {@link Datacenter#discoverSingleMachine} or
    * {@link Datacenter#discoverMultipleMachines}. This will return a new
    * {@link Machine}. The following steps are: enabling a datastore, selecting
    * a virtual switch and choosing a rack. Refer link for more information.
    * 
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/DatacenterResource#DatacenterResource-Retrieveremotemachineinformation"
    *      > http://community.abiquo.com/display/ABI20/DatacenterResource#
    *      DatacenterResource- Retrieveremotemachineinformation</a>
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/MachineResource#MachineResource-Createamachine"
    *      > http://community.abiquo.com/display/ABI20/MachineResource#
    *      MachineResource- Createamachine</a>
    */
   public void save() {
      target = context.getApi().getInfrastructureApi().createMachine(rack.unwrap(), target);
   }

   @Override
   public MachineState check() {
      MachineStateDto dto = context.getApi().getInfrastructureApi().checkMachineState(target, true);
      MachineState state = dto.getState();
      target.setState(state);
      return state;
   }

   // Parent access
   /**
    * Retrieve the unmanaged rack where the machine is.
    * 
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/RackResource#RackResource-RetrieveaRack"
    *      >
    *      http://community.abiquo.com/display/ABI20/RackResource#RackResource-
    *      RetrieveaRack</a>
    */
   public Rack getRack() {
      RESTLink link = checkNotNull(target.searchLink(ParentLinkName.RACK), ValidationErrors.MISSING_REQUIRED_LINK + " "
            + ParentLinkName.RACK);

      ExtendedUtils utils = (ExtendedUtils) context.getUtils();
      HttpResponse response = utils.getAbiquoHttpClient().get(link);

      ParseXMLWithJAXB<RackDto> parser = new ParseXMLWithJAXB<RackDto>(utils.getXml(), TypeLiteral.get(RackDto.class));

      return wrap(context, Rack.class, parser.apply(response));
   }

   // Children access

   @Override
   public List<Datastore> getDatastores() {
      return wrap(context, Datastore.class, target.getDatastores().getCollection());
   }

   @Override
   public Datastore findDatastore(final String name) {
      return find(getDatastores(), DatastorePredicates.name(name), null);
   }

   @Override
   public List<NetworkInterface> getNetworkInterfaces() {
      return wrap(context, NetworkInterface.class, target.getNetworkInterfaces().getCollection());
   }

   @Override
   public NetworkInterface findNetworkInterface(final String name) {
      return find(getNetworkInterfaces(), NetworkInterfacePredicates.name(name), null);
   }

   /**
    * Gets the list of virtual machines in the physical machine.
    * 
    * @return The list of virtual machines in the physical machine.
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/Machine+Resource#MachineResource-Retrievethelistofvirtualmachinesbymachine'shypervisor"
    *      > http://community.abiquo.com/display/ABI20/Machine+Resource#
    *      MachineResource-
    *      Retrievethelistofvirtualmachinesbymachine'shypervisor</a>
    */
   public List<VirtualMachine> listVirtualMachines() {
      MachineOptions options = MachineOptions.builder().sync(false).build();
      VirtualMachinesWithNodeExtendedDto vms = context.getApi().getInfrastructureApi()
            .listVirtualMachinesByMachine(target, options);
      return wrap(context, VirtualMachine.class, vms.getCollection());
   }

   /**
    * Gets the list of virtual machines in the physical machine matching the
    * given filter.
    * 
    * @param filter
    *           The filter to apply.
    * @return The list of virtual machines in the physical machine matching the
    *         given filter.
    */
   public List<VirtualMachine> listVirtualMachines(final Predicate<VirtualMachine> filter) {
      return ImmutableList.copyOf(filter(listVirtualMachines(), filter));
   }

   /**
    * Gets a single virtual machine in the physical machine matching the given
    * filter.
    * 
    * @param filter
    *           The filter to apply.
    * @return The virtual machine or <code>null</code> if none matched the given
    *         filter.
    */
   public VirtualMachine findVirtualMachine(final Predicate<VirtualMachine> filter) {
      return Iterables.getFirst(filter(listVirtualMachines(), filter), null);
   }

   /**
    * Gets the list of virtual machines in the physical machine synchronizing
    * virtual machines from remote hypervisor with abiquo's database.
    * 
    * @return The list of virtual machines in the physical machine.
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/Machine+Resource#MachineResource-Retrievethelistofvirtualmachinesbymachine'shypervisor"
    *      > http://community.abiquo.com/display/ABI20/Machine+Resource#
    *      MachineResource-
    *      Retrievethelistofvirtualmachinesbymachine'shypervisor</a>
    */
   public List<VirtualMachine> listRemoteVirtualMachines() {
      MachineOptions options = MachineOptions.builder().sync(true).build();
      VirtualMachinesWithNodeExtendedDto vms = context.getApi().getInfrastructureApi()
            .listVirtualMachinesByMachine(target, options);
      return wrap(context, VirtualMachine.class, vms.getCollection());
   }

   /**
    * Gets the list of virtual machines in the physical machine matching the
    * given filter synchronizing virtual machines from remote hypervisor with
    * abiquo's database.
    * 
    * @param filter
    *           The filter to apply.
    * @return The list of remote virtual machines in the physical machine
    *         matching the given filter.
    */
   public List<VirtualMachine> listRemoteVirtualMachines(final Predicate<VirtualMachine> filter) {
      return ImmutableList.copyOf(filter(listVirtualMachines(), filter));
   }

   /**
    * Gets a single virtual machine in the physical machine matching the given
    * filter synchronizing virtual machines from remote hypervisor with abiquo's
    * database.
    * 
    * @param filter
    *           The filter to apply.
    * @return The virtual machine or <code>null</code> if none matched the given
    *         filter.
    */
   public VirtualMachine findRemoteVirtualMachine(final Predicate<VirtualMachine> filter) {
      return Iterables.getFirst(filter(listVirtualMachines(), filter), null);
   }

   /**
    * Reserve the machine for the given enterprise.
    * <p>
    * When a {@link Machine} is reserved for an {@link Enterprise}, only the
    * users of that enterprise will be able to deploy {@link VirtualMachine}s in
    * it.
    * 
    * @param enterprise
    *           The enterprise reserving the machine.
    */
   public void reserveFor(final Enterprise enterprise) {
      target = context.getApi().getInfrastructureApi().reserveMachine(enterprise.unwrap(), target);
   }

   /**
    * Cancels the machine reservation for the given enterprise.
    * 
    * @param enterprise
    *           The enterprise to cancel reservation for.
    */
   public void cancelReservationFor(final Enterprise enterprise) {
      context.getApi().getInfrastructureApi().cancelReservation(enterprise.unwrap(), target);
      target.getLinks().remove(target.searchLink(ParentLinkName.ENTERPRISE));
   }

   /**
    * Check if the machine is reserved.
    * 
    * @return Boolean indicating if the machine is reserved for an enterprise.
    */
   public boolean isReserved() {
      return target.searchLink(ParentLinkName.ENTERPRISE) != null;
   }

   /**
    * Get the enterprise that has reserved the machine or <code>null</code> if
    * the machine is not reserved.
    * 
    * @return The enterprise that has reserved the machine or <code>null</code>
    *         if the machine is not reserved.
    */
   public Enterprise getOwnerEnterprise() {
      if (!isReserved()) {
         return null;
      }

      EnterpriseDto enterprise = context.getApi().getEnterpriseApi()
            .getEnterprise(target.getIdFromLink(ParentLinkName.ENTERPRISE));

      return wrap(context, Enterprise.class, enterprise);
   }

   // Builder

   public static Builder builder(final RestContext<AbiquoApi, AbiquoAsyncApi> context, final Rack rack) {
      return new Builder(context, rack);
   }

   public static class Builder {
      private RestContext<AbiquoApi, AbiquoAsyncApi> context;

      private String name;

      private String description;

      private Integer virtualRamInMb;

      private Integer virtualRamUsedInMb = DEFAULT_VRAM_USED;

      private Integer virtualCpuCores;

      private Integer virtualCpusUsed = DEFAULT_VCPU_USED;

      private Integer port;

      private String ip;

      private MachineState state = MachineState.STOPPED;

      private String ipService;

      private HypervisorType type;

      private String user;

      private String password;

      private Iterable<Datastore> datastores;

      private Iterable<NetworkInterface> networkInterfaces;

      private String ipmiIp;

      private Integer ipmiPort;

      private String ipmiUser;

      private String ipmiPassword;

      private Rack rack;

      public Builder(final RestContext<AbiquoApi, AbiquoAsyncApi> context, final Rack rack) {
         super();
         checkNotNull(rack, ValidationErrors.NULL_RESOURCE + Rack.class);
         this.rack = rack;
         this.context = context;
      }

      public Builder state(final MachineState state) {
         this.state = state;
         return this;
      }

      public Builder ipmiPassword(final String ipmiPassword) {
         this.ipmiPassword = ipmiPassword;
         return this;
      }

      public Builder ipmiUser(final String ipmiUser) {
         this.ipmiUser = ipmiUser;
         return this;
      }

      public Builder ipmiPort(final int ipmiPort) {
         this.ipmiPort = ipmiPort;
         return this;
      }

      public Builder ipmiIp(final String ipmiIp) {
         this.ipmiIp = ipmiIp;
         return this;
      }

      public Builder user(final String user) {
         this.user = user;
         return this;
      }

      public Builder ip(final String ip) {
         this.ip = ip;
         if (ipService == null) {
            ipService = ip;
         }
         return this;
      }

      public Builder ipService(final String ipService) {
         this.ipService = ipService;
         return this;
      }

      public Builder password(final String password) {
         this.password = password;
         return this;
      }

      public Builder name(final String name) {
         this.name = name;
         return this;
      }

      public Builder description(final String description) {
         this.description = description;
         return this;
      }

      public Builder port(final int port) {
         this.port = port;
         return this;
      }

      public Builder datastores(final Iterable<Datastore> datastores) {
         this.datastores = datastores;
         return this;
      }

      public Builder networkInterfaces(final Iterable<NetworkInterface> networkInterfaces) {
         this.networkInterfaces = networkInterfaces;
         return this;
      }

      public Builder virtualRamInMb(final int virtualRamInMb) {
         this.virtualRamInMb = virtualRamInMb;
         return this;
      }

      public Builder virtualRamUsedInMb(final int virtualRamUsedInMb) {
         this.virtualRamUsedInMb = virtualRamUsedInMb;
         return this;
      }

      public Builder virtualCpuCores(final int virtualCpuCores) {
         this.virtualCpuCores = virtualCpuCores;
         return this;
      }

      public Builder virtualCpusUsed(final int virtualCpusUsed) {
         this.virtualCpusUsed = virtualCpusUsed;
         return this;
      }

      public Builder hypervisorType(final HypervisorType hypervisorType) {
         this.type = hypervisorType;

         // Sets default hypervisor port
         if (this.port == null) {
            this.port = hypervisorType.defaultPort;
         }

         return this;
      }

      public Builder rack(final Rack rack) {
         checkNotNull(rack, ValidationErrors.NULL_RESOURCE + Datacenter.class);
         this.rack = rack;
         return this;
      }

      public Machine build() {
         MachineDto dto = new MachineDto();
         dto.setName(name);
         dto.setDescription(description);
         dto.setVirtualRamInMb(virtualRamInMb);
         dto.setVirtualRamUsedInMb(virtualRamUsedInMb);
         dto.setVirtualCpuCores(virtualCpuCores);
         dto.setVirtualCpusUsed(virtualCpusUsed);
         if (port != null) {
            dto.setPort(port);
         }
         dto.setIp(ip);
         dto.setIpService(ipService);
         dto.setType(type);
         dto.setUser(user);
         dto.setPassword(password);
         dto.setIpmiIP(ipmiIp);
         dto.setIpmiPassword(ipmiPassword);
         if (ipmiPort != null) {
            dto.setIpmiPort(ipmiPort);
         }
         dto.setIpmiUser(ipmiUser);
         dto.setState(state);

         DatastoresDto datastoresDto = new DatastoresDto();
         datastoresDto.getCollection().addAll(unwrap(datastores));
         dto.setDatastores(datastoresDto);

         NetworkInterfacesDto networkInterfacesDto = new NetworkInterfacesDto();
         networkInterfacesDto.getCollection().addAll(unwrap(networkInterfaces));
         dto.setNetworkInterfaces(networkInterfacesDto);

         Machine machine = new Machine(context, dto);
         machine.rack = rack;

         return machine;
      }

      public static Builder fromMachine(final Machine in) {
         Builder builder = Machine.builder(in.context, in.rack).name(in.getName()).description(in.getDescription())
               .virtualCpuCores(in.getVirtualCpuCores()).virtualCpusUsed(in.getVirtualCpusUsed())
               .virtualRamInMb(in.getVirtualRamInMb()).virtualRamUsedInMb(in.getVirtualRamUsedInMb())
               .port(in.getPort()).ip(in.getIp()).ipService(in.getIpService()).hypervisorType(in.getType())
               .user(in.getUser()).password(in.getPassword()).ipmiIp(in.getIpmiIp()).ipmiPassword(in.getIpmiPassword())
               .ipmiUser(in.getIpmiUser()).state(in.getState()).datastores(in.getDatastores())
               .networkInterfaces(in.getNetworkInterfaces());

         // Parameters that can be null
         if (in.getIpmiPort() != null) {
            builder.ipmiPort(in.getIpmiPort());
         }

         return builder;
      }
   }

   // Delegate methods

   public void setRack(final Rack rack) {
      this.rack = rack;
   }

   public VirtualMachine getVirtualMachine(final Integer virtualMachineId) {
      VirtualMachineWithNodeExtendedDto vm = context.getApi().getInfrastructureApi()
            .getVirtualMachine(target, virtualMachineId);
      return wrap(context, VirtualMachine.class, vm);
   }

}
