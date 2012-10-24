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

package org.jclouds.abiquo.features;

import java.util.concurrent.TimeUnit;

import org.jclouds.abiquo.domain.infrastructure.options.DatacenterOptions;
import org.jclouds.abiquo.domain.infrastructure.options.IpmiOptions;
import org.jclouds.abiquo.domain.infrastructure.options.MachineOptions;
import org.jclouds.abiquo.domain.infrastructure.options.StoragePoolOptions;
import org.jclouds.abiquo.domain.network.options.IpOptions;
import org.jclouds.abiquo.domain.network.options.NetworkOptions;
import org.jclouds.abiquo.domain.options.search.FilterOptions;
import org.jclouds.abiquo.reference.annotations.EnterpriseEdition;
import org.jclouds.concurrent.Timeout;

import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.server.core.cloud.HypervisorTypesDto;
import com.abiquo.server.core.cloud.VirtualMachineWithNodeExtendedDto;
import com.abiquo.server.core.cloud.VirtualMachinesWithNodeExtendedDto;
import com.abiquo.server.core.enterprise.DatacentersLimitsDto;
import com.abiquo.server.core.enterprise.EnterpriseDto;
import com.abiquo.server.core.infrastructure.BladeLocatorLedDto;
import com.abiquo.server.core.infrastructure.DatacenterDto;
import com.abiquo.server.core.infrastructure.DatacentersDto;
import com.abiquo.server.core.infrastructure.FsmsDto;
import com.abiquo.server.core.infrastructure.LogicServerDto;
import com.abiquo.server.core.infrastructure.LogicServersDto;
import com.abiquo.server.core.infrastructure.MachineDto;
import com.abiquo.server.core.infrastructure.MachineIpmiStateDto;
import com.abiquo.server.core.infrastructure.MachineStateDto;
import com.abiquo.server.core.infrastructure.MachinesDto;
import com.abiquo.server.core.infrastructure.OrganizationDto;
import com.abiquo.server.core.infrastructure.OrganizationsDto;
import com.abiquo.server.core.infrastructure.RackDto;
import com.abiquo.server.core.infrastructure.RacksDto;
import com.abiquo.server.core.infrastructure.RemoteServiceDto;
import com.abiquo.server.core.infrastructure.RemoteServicesDto;
import com.abiquo.server.core.infrastructure.UcsRackDto;
import com.abiquo.server.core.infrastructure.UcsRacksDto;
import com.abiquo.server.core.infrastructure.network.ExternalIpDto;
import com.abiquo.server.core.infrastructure.network.ExternalIpsDto;
import com.abiquo.server.core.infrastructure.network.PublicIpDto;
import com.abiquo.server.core.infrastructure.network.PublicIpsDto;
import com.abiquo.server.core.infrastructure.network.UnmanagedIpDto;
import com.abiquo.server.core.infrastructure.network.UnmanagedIpsDto;
import com.abiquo.server.core.infrastructure.network.VLANNetworkDto;
import com.abiquo.server.core.infrastructure.network.VLANNetworksDto;
import com.abiquo.server.core.infrastructure.network.VlanTagAvailabilityDto;
import com.abiquo.server.core.infrastructure.storage.StorageDeviceDto;
import com.abiquo.server.core.infrastructure.storage.StorageDevicesDto;
import com.abiquo.server.core.infrastructure.storage.StorageDevicesMetadataDto;
import com.abiquo.server.core.infrastructure.storage.StoragePoolDto;
import com.abiquo.server.core.infrastructure.storage.StoragePoolsDto;
import com.abiquo.server.core.infrastructure.storage.TierDto;
import com.abiquo.server.core.infrastructure.storage.TiersDto;

/**
 * Provides synchronous access to Abiquo Infrastructure API.
 * 
 * @see API: <a href="http://community.abiquo.com/display/ABI20/API+Reference">
 *      http://community.abiquo.com/display/ABI20/API+Reference</a>
 * @see InfrastructureAsyncApi
 * @author Ignasi Barrera
 * @author Francesc Montserrat
 */
@Timeout(duration = 30, timeUnit = TimeUnit.SECONDS)
public interface InfrastructureApi {
   /*********************** Datacenter ***********************/

   /**
    * List all datacenters.
    * 
    * @return The list of Datacenters.
    */
   DatacentersDto listDatacenters();

   /**
    * Create a new datacenter.
    * 
    * @param datacenter
    *           The datacenter to be created.
    * @return The created datacenter.
    */
   DatacenterDto createDatacenter(DatacenterDto datacenter);

   /**
    * Get the given datacenter.
    * 
    * @param datacenterId
    *           The id of the datacenter.
    * @return The datacenter or <code>null</code> if it does not exist.
    */
   DatacenterDto getDatacenter(Integer datacenterId);

   /**
    * Updates an existing datacenter.
    * 
    * @param datacenter
    *           The new attributes for the datacenter.
    * @return The updated datacenter.
    */
   DatacenterDto updateDatacenter(DatacenterDto datacenter);

   /**
    * Deletes an existing datacenter.
    * 
    * @param datacenter
    *           The datacenter to delete.
    */
   void deleteDatacenter(DatacenterDto datacenter);

   /**
    * Retrieve remote machine information.
    * 
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/DatacenterResource#DatacenterResource-Retrieveremotemachineinformation"
    *      > http://community.abiquo.com/display/ABI20/DatacenterResource#
    *      DatacenterResource- Retrieveremotemachineinformation</a>
    * @param datacenter
    *           The datacenter.
    * @param ip
    *           IP address of the remote hypervisor to connect.
    * @param hypervisorType
    *           Kind of hypervisor we want to connect. Valid values are {vbox,
    *           kvm, xen-3, vmx-04, hyperv-301, xenserver}.
    * @param user
    *           User to log in.
    * @param password
    *           Password to authenticate.
    * @return The physical machine.
    */
   @Timeout(duration = 90, timeUnit = TimeUnit.SECONDS)
   MachineDto discoverSingleMachine(DatacenterDto datacenter, String ip, HypervisorType hypervisorType, String user,
         String password);

   /**
    * Retrieve remote machine information.
    * 
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/DatacenterResource#DatacenterResource-Retrieveremotemachineinformation"
    *      > http://community.abiquo.com/display/ABI20/DatacenterResource#
    *      DatacenterResource- Retrieveremotemachineinformation</a>
    * @param datacenter
    *           The datacenter.
    * @param ip
    *           IP address of the remote hypervisor to connect.
    * @param hypervisorType
    *           Kind of hypervisor we want to connect. Valid values are {vbox,
    *           kvm, xen-3, vmx-04, hyperv-301, xenserver}.
    * @param user
    *           User to log in.
    * @param password
    *           Password to authenticate.
    * @param options
    *           Optional query params.
    * @return The physical machine.
    */
   @Timeout(duration = 90, timeUnit = TimeUnit.SECONDS)
   MachineDto discoverSingleMachine(DatacenterDto datacenter, String ip, HypervisorType hypervisorType, String user,
         String password, MachineOptions options);

   /**
    * Retrieve a list of remote machine information.
    * 
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/DatacenterResource#DatacenterResource-Retrievealistofremotemachineinformation"
    *      > http://community.abiquo.com/display/ABI20/DatacenterResource#
    *      DatacenterResource- Retrievealistofremotemachineinformation</a>
    * @param datacenter
    *           The datacenter.
    * @param ipFrom
    *           IP address of the remote first hypervisor to check.
    * @param ipTo
    *           IP address of the remote last hypervisor to check.
    * @param hypervisorType
    *           Kind of hypervisor we want to connect. Valid values are {vbox,
    *           kvm, xen-3, vmx-04, hyperv-301, xenserver}.
    * @param user
    *           User to log in.
    * @param password
    *           Password to authenticate.
    * @return The physical machine list.
    */
   @Timeout(duration = 90, timeUnit = TimeUnit.SECONDS)
   MachinesDto discoverMultipleMachines(final DatacenterDto datacenter, final String ipFrom, final String ipTo,
         final HypervisorType hypervisorType, final String user, final String password);

   /**
    * Retrieve a list of remote machine information.
    * 
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/DatacenterResource#DatacenterResource-Retrievealistofremotemachineinformation"
    *      > http://community.abiquo.com/display/ABI20/DatacenterResource#
    *      DatacenterResource- Retrievealistofremotemachineinformation</a>
    * @param datacenter
    *           The datacenter.
    * @param ipFrom
    *           IP address of the remote first hypervisor to check.
    * @param ipTo
    *           IP address of the remote last hypervisor to check.
    * @param hypervisorType
    *           Kind of hypervisor we want to connect. Valid values are {vbox,
    *           kvm, xen-3, vmx-04, hyperv-301, xenserver}.
    * @param user
    *           User to log in.
    * @param password
    *           Password to authenticate.
    * @param options
    *           Optional query params.
    * @return The physical machine list.
    */
   @Timeout(duration = 90, timeUnit = TimeUnit.SECONDS)
   MachinesDto discoverMultipleMachines(final DatacenterDto datacenter, final String ipFrom, final String ipTo,
         final HypervisorType hypervisorType, final String user, final String password, final MachineOptions options);

   /**
    * Retreives limits for the given datacenter and any enterprise.
    * 
    * @param datacenter
    *           The datacenter.
    * @return The usage limits for the datacenter on any enterprise.
    */
   DatacentersLimitsDto listLimits(DatacenterDto datacenter);

   /**
    * Check the state of a remote machine. This machine does not need to be
    * managed by Abiquo.
    * 
    * @param datacenter
    *           The datacenter.
    * @param ip
    *           IP address of the remote hypervisor to connect.
    * @param hypervisorType
    *           Kind of hypervisor we want to connect. Valid values are {vbox,
    *           kvm, xen-3, vmx-04, hyperv-301, xenserver}.
    * @param user
    *           User to log in.
    * @param password
    *           Password to authenticate.
    * @return The physical machine state information.
    */
   MachineStateDto checkMachineState(DatacenterDto datacenter, String ip, HypervisorType hypervisorType, String user,
         String password);

   /**
    * Check the state of a remote machine. This machine does not need to be
    * managed by Abiquo.
    * 
    * @param datacenter
    *           The datacenter.
    * @param ip
    *           IP address of the remote hypervisor to connect.
    * @param hypervisorType
    *           Kind of hypervisor we want to connect. Valid values are {vbox,
    *           kvm, xen-3, vmx-04, hyperv-301, xenserver}.
    * @param user
    *           User to log in.
    * @param password
    *           Password to authenticate.
    * @param options
    *           Optional query params.
    * @return The physical machine state information.
    */
   MachineStateDto checkMachineState(DatacenterDto datacenter, String ip, HypervisorType hypervisorType, String user,
         String password, MachineOptions options);

   /**
    * Check the ipmi configuration state of a remote machine. This machine does
    * not need to be managed by Abiquo.
    * 
    * @param datacenter
    *           The datacenter.
    * @param ip
    *           IP address of the remote hypervisor to connect.
    * @param user
    *           User to log in.
    * @param password
    *           Password to authenticate.
    * @return The ipmi configuration state information
    */
   MachineIpmiStateDto checkMachineIpmiState(DatacenterDto datacenter, String ip, String user, String password);

   /**
    * Check the ipmi configuration state of a remote machine. This machine does
    * not need to be managed by Abiquo.
    * 
    * @param datacenter
    *           The datacenter.
    * @param ip
    *           IP address of the remote hypervisor to connect.
    * @param user
    *           User to log in.
    * @param password
    *           Password to authenticate.
    * @param options
    *           Optional query params.
    * @return The ipmi configuration state information
    */
   MachineIpmiStateDto checkMachineIpmiState(DatacenterDto datacenter, String ip, String user, String password,
         IpmiOptions options);

   /*********************** Hypervisor ***********************/

   /**
    * Retreives the hypervisor type of a remote a machine.
    * 
    * @param datacenter
    *           The datacenter.
    * @param options
    *           Optional query params.
    * @return The hypervisor type.
    */
   String getHypervisorTypeFromMachine(DatacenterDto datacenter, DatacenterOptions options);

   /**
    * Retreives the hypervisor types in the datacenter.
    * 
    * @param datacenter
    *           The datacenter.
    * @return The hypervisor types.
    */
   HypervisorTypesDto getHypervisorTypes(DatacenterDto datacenter);

   /*********************** Unmanaged Rack ********************** */

   /**
    * List all not managed racks for a datacenter.
    * 
    * @param datacenter
    *           The datacenter.
    * @return The list of not managed racks for the datacenter.
    */
   RacksDto listRacks(DatacenterDto datacenter);

   /**
    * Create a new not managed rack in a datacenter.
    * 
    * @param datacenter
    *           The datacenter.
    * @param rack
    *           The rack to be created.
    * @return The created rack.
    */
   RackDto createRack(final DatacenterDto datacenter, final RackDto rack);

   /**
    * Get the given rack from the given datacenter.
    * 
    * @param datacenter
    *           The datacenter.
    * @param rackId
    *           The id of the rack.
    * @return The rack or <code>null</code> if it does not exist.
    */
   RackDto getRack(DatacenterDto datacenter, Integer rackId);

   /**
    * Updates an existing rack from the given datacenter.
    * 
    * @param rack
    *           The new attributes for the rack.
    * @return The updated rack.
    */
   RackDto updateRack(final RackDto rack);

   /**
    * Deletes an existing rack.
    * 
    * @param rack
    *           The rack to delete.
    */
   void deleteRack(final RackDto rack);

   /*********************** Managed Rack **********************/

   /**
    * List all managed racks for a datacenter.
    * 
    * @param datacenter
    *           The datacenter.
    * @return The list of managed racks for the datacenter.
    */
   @EnterpriseEdition
   @Timeout(duration = 60, timeUnit = TimeUnit.SECONDS)
   UcsRacksDto listManagedRacks(DatacenterDto datacenter);

   /**
    * Create a new managed rack in a datacenter.
    * 
    * @param datacenter
    *           The datacenter.
    * @param rack
    *           The managed rack to be created.
    * @return The created rack.
    */
   @EnterpriseEdition
   @Timeout(duration = 90, timeUnit = TimeUnit.SECONDS)
   UcsRackDto createManagedRack(final DatacenterDto datacenter, final UcsRackDto rack);

   /**
    * Get the given managed rack from the given datacenter.
    * 
    * @param datacenter
    *           The datacenter.
    * @param rackId
    *           The id of the rack.
    * @return The rack or <code>null</code> if it does not exist.
    */
   @EnterpriseEdition
   @Timeout(duration = 60, timeUnit = TimeUnit.SECONDS)
   UcsRackDto getManagedRack(DatacenterDto datacenter, Integer rackId);

   /**
    * Updates an existing managed rack from the given datacenter.
    * 
    * @param rack
    *           The new attributes for the rack.
    * @return The updated rack.
    */
   @EnterpriseEdition
   @Timeout(duration = 90, timeUnit = TimeUnit.SECONDS)
   UcsRackDto updateManagedRack(final UcsRackDto rack);

   /**
    * List all service profiles of the ucs rack.
    * 
    * @param rack
    *           The ucs rack.
    * @return The list of service profiles for the rack.
    */
   @EnterpriseEdition
   @Timeout(duration = 90, timeUnit = TimeUnit.SECONDS)
   LogicServersDto listServiceProfiles(UcsRackDto rack);

   /**
    * List service profiles of the ucs rack with filtering options.
    * 
    * @param rack
    *           The ucs rack.
    * @param options
    *           Optional query params.
    * @return The list of service profiles for the rack.
    */
   @EnterpriseEdition
   @Timeout(duration = 90, timeUnit = TimeUnit.SECONDS)
   LogicServersDto listServiceProfiles(UcsRackDto rack, FilterOptions options);

   /**
    * List all service profile templates of the ucs rack.
    * 
    * @param rack
    *           The ucs rack.
    * @return The list of service profile templates for the rack.
    */
   @EnterpriseEdition
   @Timeout(duration = 90, timeUnit = TimeUnit.SECONDS)
   LogicServersDto listServiceProfileTemplates(UcsRackDto rack);

   /**
    * List all service profile templates of the ucs rack with options.
    * 
    * @param rack
    *           The ucs rack.
    * @param options
    *           Optional query params.
    * @return The list of service profile templates for the rack.
    */
   @EnterpriseEdition
   @Timeout(duration = 90, timeUnit = TimeUnit.SECONDS)
   LogicServersDto listServiceProfileTemplates(UcsRackDto rack, FilterOptions options);

   /**
    * List all organizations of the ucs rack.
    * 
    * @param rack
    *           The ucs rack.
    * @return The list of organizations for the rack.
    */
   @EnterpriseEdition
   @Timeout(duration = 90, timeUnit = TimeUnit.SECONDS)
   OrganizationsDto listOrganizations(UcsRackDto rack);

   /**
    * List all organizations of the ucs rack with options.
    * 
    * @param rack
    *           The ucs rack.
    * @param options
    *           Optional query params.
    * @return The list of organizations for the rack.
    */
   @EnterpriseEdition
   @Timeout(duration = 90, timeUnit = TimeUnit.SECONDS)
   OrganizationsDto listOrganizations(UcsRackDto rack, FilterOptions options);

   /**
    * Clone a service profile.
    * 
    * @param rack
    *           The managed rack where thw service profile will be created.
    * @param logicServer
    *           The original logic server.
    * @param organization
    *           The organization to be associated.
    * @param newName
    *           The name of the new service profile.
    */
   @EnterpriseEdition
   @Timeout(duration = 90, timeUnit = TimeUnit.SECONDS)
   void cloneLogicServer(UcsRackDto rack, LogicServerDto logicServer, OrganizationDto organization, String newName);

   /**
    * Delete a service profile.
    * 
    * @param rack
    *           The managed rack where the service profile will be created.
    * @param logicServer
    *           The original logic server.
    */
   @EnterpriseEdition
   @Timeout(duration = 90, timeUnit = TimeUnit.SECONDS)
   void deleteLogicServer(UcsRackDto rack, LogicServerDto logicServer);

   /**
    * Associate a service profile with a blade.
    * 
    * @param rack
    *           The managed rack where the service profile is.
    * @param logicServer
    *           The logic server.
    * @param organization
    *           The organization to be associated.
    * @param bladeName
    *           The name of the blade.
    */
   @EnterpriseEdition
   @Timeout(duration = 90, timeUnit = TimeUnit.SECONDS)
   void associateLogicServer(UcsRackDto rack, LogicServerDto logicServer, OrganizationDto organization, String bladeName);

   /**
    * Associate a service profile with a blade instantiating a service profile
    * template.
    * 
    * @param rack
    *           The managed rack where the service profile is.
    * @param logicServer
    *           The logic server.
    * @param organization
    *           The organization to be associated.
    * @param newName
    *           Name of the new service profile.
    * @param bladeName
    *           The name of the blade.
    */
   @EnterpriseEdition
   @Timeout(duration = 90, timeUnit = TimeUnit.SECONDS)
   void associateTemplate(UcsRackDto rack, LogicServerDto logicServer, OrganizationDto organization, String newName,
         String bladeName);

   /**
    * Clone a service profile and associate it with a blade.
    * 
    * @param rack
    *           The managed rack where the service profile is.
    * @param logicServer
    *           The logic server.
    * @param organization
    *           The organization to be associated.
    * @param newName
    *           Name of the new service profile.
    * @param bladeName
    *           The name of the blade.
    */
   @EnterpriseEdition
   @Timeout(duration = 90, timeUnit = TimeUnit.SECONDS)
   void cloneAndAssociateLogicServer(UcsRackDto rack, LogicServerDto logicServer, OrganizationDto organization,
         String newName, String bladeName);

   /**
    * Dissociate a service profile from a blade.
    * 
    * @param rack
    *           The managed rack where the service profile is.
    * @param logicServer
    *           The logic server.
    */
   @EnterpriseEdition
   @Timeout(duration = 90, timeUnit = TimeUnit.SECONDS)
   void dissociateLogicServer(UcsRackDto rack, LogicServerDto logicServer);

   /**
    * Get FSM list of an entity
    * 
    * @param rack
    *           The managed rack where the entity belongs.
    * @param dn
    *           Distinguished name of the entity.
    * @param fsm
    *           The fsm.
    */
   @EnterpriseEdition
   @Timeout(duration = 90, timeUnit = TimeUnit.SECONDS)
   FsmsDto listFsms(UcsRackDto rack, String dn);

   /*********************** Remote Service ********************** */

   /**
    * List all remote services of the datacenter.
    * 
    * @param datacenter
    *           The datacenter.
    * @return The list of remote services for the datacenter.
    */
   RemoteServicesDto listRemoteServices(DatacenterDto dataceter);

   /**
    * Create a new remote service in a datacenter.
    * 
    * @param datacenter
    *           The datacenter.
    * @param remoteService
    *           The remote service to be created.
    * @return The created remote service.
    */
   RemoteServiceDto createRemoteService(final DatacenterDto datacenter, final RemoteServiceDto remoteService);

   /**
    * Get the given remote service from the given datacenter.
    * 
    * @param datacenter
    *           The datacenter.
    * @param remoteServiceType
    *           The type of the remote service.
    * @return The remote service or <code>null</code> if it does not exist.
    */
   RemoteServiceDto getRemoteService(DatacenterDto datacenter, RemoteServiceType remoteServiceType);

   /**
    * Updates an existing remote service from the given datacenter.
    * 
    * @param remoteService
    *           The new attributes for the remote service.
    * @return The updated remote service.
    */
   RemoteServiceDto updateRemoteService(RemoteServiceDto remoteService);

   /**
    * Deletes an existing remote service.
    * 
    * @param remoteService
    *           The remote service to delete.
    */
   void deleteRemoteService(RemoteServiceDto remoteService);

   /**
    * Check if the given remote service is available and properly configured.
    * 
    * @param remoteService
    *           The remote service to check.
    * @return A Boolean indicating if the remote service is available.
    */
   boolean isAvailable(RemoteServiceDto remoteService);

   /*********************** Machine ********************** */

   /**
    * Create a new physical machine in a rack.
    * 
    * @param rack
    *           The rack.
    * @param machine
    *           The physical machine to be created.
    * @return The created physical machine.
    */
   MachineDto createMachine(RackDto rack, MachineDto machine);

   /**
    * Get the given machine from the given rack.
    * 
    * @param rack
    *           The rack.
    * @param machineId
    *           The id of the machine.
    * @return The machine or <code>null</code> if it does not exist.
    */
   MachineDto getMachine(RackDto rack, Integer machineId);

   /**
    * Checks the real infrastructure state for the given physical machine. The
    * machine is updated with the result state.
    * 
    * @param machine
    *           The machine to check
    * @paran boolean that indicates a database synchronization
    * @return A machineStateDto with a machine state value from enum
    *         MachineState
    */
   MachineStateDto checkMachineState(MachineDto machine, boolean sync);

   /**
    * Checks the ipmi configuration state for the given physical machine.
    * 
    * @param machine
    *           The machine to check
    * @return A machineIpmiStateDto with a machine ipmi configuration state
    *         value from enum MachineState
    */
   MachineIpmiStateDto checkMachineIpmiState(MachineDto machine);

   /**
    * Updates an existing physical machine.
    * 
    * @param machine
    *           The new attributes for the physical machine.
    * @return The updated machine.
    */
   MachineDto updateMachine(MachineDto machine);

   /**
    * Deletes an existing physical machine.
    * 
    * @param machine
    *           The physical machine to delete.
    */
   void deleteMachine(MachineDto machine);

   /**
    * Reserve the given machine for the given enterprise.
    * 
    * @param enterprise
    *           The enterprise reserving the machine.
    * @param machine
    *           The machine to reserve.
    * @return The reserved machine.
    */
   MachineDto reserveMachine(EnterpriseDto enterprise, MachineDto machine);

   /**
    * Cancels the reservation of the given machine.
    * 
    * @param enterprise
    *           The enterprise to cancel reservation.
    * @param machine
    *           The machine to release.
    */
   Void cancelReservation(EnterpriseDto enterprise, MachineDto machine);

   /**
    * List all machines racks for a rack.
    * 
    * @param rack
    *           The rack.
    * @return The list of physical machines for the rack.
    */
   MachinesDto listMachines(RackDto rack);

   /*********************** Blade ***********************/

   /**
    * Power off a physical machine in a UCS rack.
    * 
    * @param machime
    *           The phyisical machine.
    */
   @EnterpriseEdition
   void powerOff(MachineDto machine);

   /**
    * Power on a physical machine in a UCS rack.
    * 
    * @param machime
    *           The phyisical machine.
    */
   @EnterpriseEdition
   void powerOn(MachineDto machine);

   /**
    * Get the logic server associated with a machine in a Cisc UCS rack.
    * 
    * @param machime
    *           The phyisical machine.
    * @return The logic server.
    */
   @EnterpriseEdition
   LogicServerDto getLogicServer(MachineDto machine);

   /**
    * Turn off locator led of a physical machine in a UCS rack.
    * 
    * @param machime
    *           The phyisical machine.
    */
   @EnterpriseEdition
   void ledOn(MachineDto machine);

   /**
    * Light locator led of a physical machine in a UCS rack.
    * 
    * @param machime
    *           The phyisical machine.
    */
   @EnterpriseEdition
   void ledOff(MachineDto machine);

   /**
    * Get led locator info from a physical machine in a UCS rack.
    * 
    * @param machime
    *           The phyisical machine.
    * @return Led locator information.
    */
   @EnterpriseEdition
   BladeLocatorLedDto getLocatorLed(MachineDto machine);

   /**
    * List all virtual machines in a physical machine.
    * 
    * @param machine
    *           The physical machine.
    * @return The list of virtual machines in the physical machine.
    */
   VirtualMachinesWithNodeExtendedDto listVirtualMachinesByMachine(MachineDto machine, MachineOptions options);

   /**
    * Get the given virtual machine
    * 
    * @param machine
    * @param virtualMachineId
    * @return
    */
   VirtualMachineWithNodeExtendedDto getVirtualMachine(MachineDto machine, Integer virtualMachineId);

   /*********************** Storage Device ***********************/

   /**
    * List all storage devices of the datacenter.
    * 
    * @param datacenter
    *           The datacenter.
    * @return The list of storage devices in the datacenter.
    */
   @EnterpriseEdition
   StorageDevicesDto listStorageDevices(DatacenterDto datacenter);

   /**
    * List all supported storage devices.
    * 
    * @param datacenter
    *           The datacenter.
    * @return The list of supported storage devices.
    */
   @EnterpriseEdition
   StorageDevicesMetadataDto listSupportedStorageDevices(DatacenterDto datacenter);

   /**
    * Get the storage device.
    * 
    * @param storageDeviceId
    *           The id of the storage device.
    * @return The storage device or <code>null</code> if it does not exist.
    */
   @EnterpriseEdition
   StorageDeviceDto getStorageDevice(DatacenterDto datacenter, Integer storageDeviceId);

   /**
    * Create a new storage device.
    * 
    * @param datacenter
    *           The datacenter.
    * @param storageDevice
    *           The storage device to be created.
    * @return The created storage device.
    */
   @EnterpriseEdition
   StorageDeviceDto createStorageDevice(final DatacenterDto datacenter, final StorageDeviceDto storageDevice);

   /**
    * Deletes an existing storage device.
    * 
    * @param storageDevice
    *           The storage device to delete.
    */
   @EnterpriseEdition
   void deleteStorageDevice(StorageDeviceDto storageDevice);

   /**
    * Updates an existing storage device.
    * 
    * @param storageDevice
    *           The new attributes for the storage device.
    * @return The updated storage device.
    */
   @EnterpriseEdition
   StorageDeviceDto updateStorageDevice(StorageDeviceDto storageDevice);

   /*********************** Tier ***********************/
   /**
    * List all tiers of the datacenter.
    * 
    * @param datacenter
    *           The datacenter.
    * @return The list of tiers in the datacenter.
    */
   @EnterpriseEdition
   TiersDto listTiers(DatacenterDto datacenter);

   /**
    * Updates a tier.
    * 
    * @param tier
    *           The new attributes for the tier.
    * @return The updated tier.
    */
   @EnterpriseEdition
   TierDto updateTier(TierDto tier);

   /**
    * Get the tier.
    * 
    * @param tierId
    *           The id of the tier.
    * @return The tier or <code>null</code> if it does not exist.
    */
   @EnterpriseEdition
   TierDto getTier(DatacenterDto datacenter, Integer tierId);

   /*********************** Storage Pool ***********************/

   /**
    * List storage pools on a storage device.
    * 
    * @param storageDevice
    *           The storage device.
    * @param options
    *           Optional query params.
    * @return The list of storage pools in the storage device.
    */
   @EnterpriseEdition
   StoragePoolsDto listStoragePools(StorageDeviceDto storageDeviceDto, StoragePoolOptions storagePoolOptions);

   /**
    * List storage pools on a tier.
    * 
    * @param tier
    *           The tier device.
    * @return The list of storage pools in the tier.
    */
   @EnterpriseEdition
   StoragePoolsDto listStoragePools(TierDto tier);

   /**
    * Create a new storage pool in a storage device.
    * 
    * @param storageDevice
    *           The storage device.
    * @param storagePool
    *           The storage pool to be created.
    * @return The created storage pool.
    */
   @EnterpriseEdition
   StoragePoolDto createStoragePool(StorageDeviceDto storageDevice, StoragePoolDto storagePool);

   /**
    * Updates a storage pool.
    * 
    * @param storagePool
    *           The new attributes for the storage pool.
    * @return The updated tier.
    */
   @EnterpriseEdition
   StoragePoolDto updateStoragePool(StoragePoolDto storagePool);

   /**
    * Deletes an existing storage pool.
    * 
    * @param storagePool
    *           The storage pool to delete.
    */
   @EnterpriseEdition
   void deleteStoragePool(StoragePoolDto storagePool);

   /**
    * Get the storage pool.
    * 
    * @param storageDevice
    *           The storage device.
    * @param storagePoolId
    *           The id of the storage pool.
    * @return The storage pool or <code>null</code> if it does not exist.
    */
   @EnterpriseEdition
   StoragePoolDto getStoragePool(StorageDeviceDto storageDevice, String storagePoolId);

   /**
    * Refresh the given storage pool data.
    * 
    * @param storagePool
    *           The storage pool to refresh.
    * @param options
    *           The options to query the storage pool.
    * @return The updated storage pool.
    */
   @EnterpriseEdition
   StoragePoolDto refreshStoragePool(StoragePoolDto storagePool, StoragePoolOptions options);

   /*********************** Network ***********************/

   /**
    * List all public, external and not managed networks of a datacenter.
    * 
    * @param datacenter
    *           The datacenter.
    * @return The list of not public, external and not managed for the
    *         datacenter.
    */
   @EnterpriseEdition
   VLANNetworksDto listNetworks(DatacenterDto datacenter);

   /**
    * List networks of a datacenter with options.
    * 
    * @param datacenter
    *           The datacenter.
    * @param options
    *           Optional query params.
    * @return The list of not public, external and not managed for the
    *         datacenter.
    */
   @EnterpriseEdition
   VLANNetworksDto listNetworks(DatacenterDto datacenter, NetworkOptions options);

   /**
    * Get the given network from the given datacenter.
    * 
    * @param datacenter
    *           The datacenter.
    * @param networkId
    *           The id of the network.
    * @return The rack or <code>null</code> if it does not exist.
    */
   VLANNetworkDto getNetwork(DatacenterDto datacenter, Integer networkId);

   /**
    * Create a new public network.
    * 
    * @param storageDevice
    *           The storage device.
    * @param storagePool
    *           The storage pool to be created.
    * @return The created storage pool.
    */
   @EnterpriseEdition
   VLANNetworkDto createNetwork(DatacenterDto datacenter, VLANNetworkDto network);

   /**
    * Updates a network.
    * 
    * @param network
    *           The new attributes for the network.
    * @return The updated tier.
    */
   @EnterpriseEdition
   VLANNetworkDto updateNetwork(VLANNetworkDto network);

   /**
    * Deletes an existing network.
    * 
    * @param network
    *           The network to delete.
    */
   @EnterpriseEdition
   void deleteNetwork(VLANNetworkDto network);

   /**
    * Check the availability of a tag.
    * 
    * @param datacenter
    *           The datacenter.
    * @param tag
    *           Tag to check.
    * @return A tag availability object.
    */
   @EnterpriseEdition
   VlanTagAvailabilityDto checkTagAvailability(DatacenterDto datacenter, Integer tag);

   /*********************** Network IPs ***********************/

   /**
    * List all the IPs in the given public network.
    * 
    * @param network
    *           The public network.
    * @return The IPs in the given public network.
    * @since 2.3
    */
   PublicIpsDto listPublicIps(VLANNetworkDto network);

   /**
    * List all the IPs in the given public network.
    * 
    * @param network
    *           The public network.
    * @param options
    *           The filtering options.
    * @return The IPs in the given public network.
    * @since 2.3
    */
   PublicIpsDto listPublicIps(VLANNetworkDto network, IpOptions options);

   /**
    * Get the given public ip.
    * 
    * @param network
    *           The public network.
    * @param ipId
    *           The id of the ip to get.
    * @return The requested ip.
    * @since 2.3
    */
   PublicIpDto getPublicIp(VLANNetworkDto network, Integer ipId);

   /**
    * List all the IPs in the given external network.
    * 
    * @param network
    *           The external network.
    * @return The IPs in the given external network.
    * @since 2.3
    */
   ExternalIpsDto listExternalIps(VLANNetworkDto network);

   /**
    * List all the IPs in the given external network.
    * 
    * @param network
    *           The external network.
    * @param options
    *           The filtering options.
    * @return The IPs in the given external network.
    * @since 2.3
    */
   ExternalIpsDto listExternalIps(VLANNetworkDto network, IpOptions options);

   /**
    * Get the given external ip.
    * 
    * @param network
    *           The external network.
    * @param ipId
    *           The id of the ip to get.
    * @return The requested ip.
    * @since 2.3
    */
   ExternalIpDto getExternalIp(VLANNetworkDto network, Integer ipId);

   /**
    * List all the IPs in the given unmanaged network.
    * 
    * @param network
    *           The unmanaged network.
    * @return The IPs in the given unmanaged network.
    * @since 2.3
    */
   UnmanagedIpsDto listUnmanagedIps(VLANNetworkDto network);

   /**
    * List all the IPs in the given unmanaged network.
    * 
    * @param network
    *           The unmanaged network.
    * @param options
    *           The filtering options.
    * @return The IPs in the given unmanaged network.
    * @since 2.3
    */
   UnmanagedIpsDto listUnmanagedIps(VLANNetworkDto network, IpOptions options);

   /**
    * Get the given unmanaged ip.
    * 
    * @param network
    *           The unmanaged network.
    * @param ipId
    *           The id of the ip to get.
    * @return The requested ip.
    * @since 2.3
    */
   UnmanagedIpDto getUnmanagedIp(VLANNetworkDto network, Integer ipId);
}
