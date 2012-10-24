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

import org.jclouds.abiquo.domain.cloud.options.VirtualApplianceOptions;
import org.jclouds.abiquo.domain.cloud.options.VirtualDatacenterOptions;
import org.jclouds.abiquo.domain.cloud.options.VirtualMachineOptions;
import org.jclouds.abiquo.domain.cloud.options.VirtualMachineTemplateOptions;
import org.jclouds.abiquo.domain.cloud.options.VolumeOptions;
import org.jclouds.abiquo.domain.network.options.IpOptions;
import org.jclouds.abiquo.reference.annotations.EnterpriseEdition;
import org.jclouds.concurrent.Timeout;

import com.abiquo.model.transport.AcceptedRequestDto;
import com.abiquo.server.core.appslibrary.VirtualMachineTemplateDto;
import com.abiquo.server.core.appslibrary.VirtualMachineTemplatesDto;
import com.abiquo.server.core.cloud.VirtualApplianceDto;
import com.abiquo.server.core.cloud.VirtualApplianceStateDto;
import com.abiquo.server.core.cloud.VirtualAppliancesDto;
import com.abiquo.server.core.cloud.VirtualDatacenterDto;
import com.abiquo.server.core.cloud.VirtualDatacentersDto;
import com.abiquo.server.core.cloud.VirtualMachineDto;
import com.abiquo.server.core.cloud.VirtualMachineStateDto;
import com.abiquo.server.core.cloud.VirtualMachineTaskDto;
import com.abiquo.server.core.cloud.VirtualMachineWithNodeExtendedDto;
import com.abiquo.server.core.cloud.VirtualMachinesWithNodeExtendedDto;
import com.abiquo.server.core.enterprise.EnterpriseDto;
import com.abiquo.server.core.infrastructure.DatacenterDto;
import com.abiquo.server.core.infrastructure.network.PrivateIpDto;
import com.abiquo.server.core.infrastructure.network.PrivateIpsDto;
import com.abiquo.server.core.infrastructure.network.PublicIpDto;
import com.abiquo.server.core.infrastructure.network.PublicIpsDto;
import com.abiquo.server.core.infrastructure.network.VLANNetworkDto;
import com.abiquo.server.core.infrastructure.network.VLANNetworksDto;
import com.abiquo.server.core.infrastructure.network.VMNetworkConfigurationsDto;
import com.abiquo.server.core.infrastructure.storage.DiskManagementDto;
import com.abiquo.server.core.infrastructure.storage.DisksManagementDto;
import com.abiquo.server.core.infrastructure.storage.TierDto;
import com.abiquo.server.core.infrastructure.storage.TiersDto;
import com.abiquo.server.core.infrastructure.storage.VolumeManagementDto;
import com.abiquo.server.core.infrastructure.storage.VolumesManagementDto;

/**
 * Provides synchronous access to Abiquo Cloud API.
 * 
 * @see API: <a href="http://community.abiquo.com/display/ABI20/API+Reference">
 *      http://community.abiquo.com/display/ABI20/API+Reference</a>
 * @see CloudAsyncApi
 * @author Ignasi Barrera
 * @author Francesc Montserrat
 */
@Timeout(duration = 30, timeUnit = TimeUnit.SECONDS)
public interface CloudApi {
   /*********************** Virtual Datacenter ***********************/

   /**
    * List all virtual datacenters.
    * 
    * @param options
    *           Optional query params.
    * @return The list of Datacenters.
    */
   VirtualDatacentersDto listVirtualDatacenters(VirtualDatacenterOptions options);

   /**
    * Get the given virtual datacenter.
    * 
    * @param virtualDatacenterId
    *           The id of the virtual datacenter.
    * @return The virtual datacenter or <code>null</code> if it does not exist.
    */
   VirtualDatacenterDto getVirtualDatacenter(Integer virtualDatacenterId);

   /**
    * Create a new virtual datacenter.
    * 
    * @param virtualDatacenter
    *           The virtual datacenter to be created.
    * @param datacenter
    *           Datacenter where the virtualdatacenter will be deployed.
    * @param enterprise
    *           Enterprise of the virtual datacenter.
    * @return The created virtual datacenter.
    */
   VirtualDatacenterDto createVirtualDatacenter(VirtualDatacenterDto virtualDatacenter, DatacenterDto datacenter,
         EnterpriseDto enterprise);

   /**
    * Updates an existing virtual datacenter.
    * 
    * @param virtualDatacenter
    *           The new attributes for the virtual datacenter.
    * @return The updated virtual datacenter.
    */
   VirtualDatacenterDto updateVirtualDatacenter(VirtualDatacenterDto virtualDatacenter);

   /**
    * Deletes an existing virtual datacenter.
    * 
    * @param virtualDatacenter
    *           The virtual datacenter to delete.
    */
   void deleteVirtualDatacenter(VirtualDatacenterDto virtualDatacenter);

   /**
    * List all available templates for the given virtual datacenter.
    * 
    * @param virtualDatacenter
    *           The virtual datacenter.
    * @return The list of available templates.
    */
   VirtualMachineTemplatesDto listAvailableTemplates(VirtualDatacenterDto virtualDatacenter);

   /**
    * List all available templates for the given virtual datacenter.
    * 
    * @param virtualDatacenter
    *           The virtual datacenter.
    * @param options
    *           Filtering options.
    * @return The list of available templates.
    */
   VirtualMachineTemplatesDto listAvailableTemplates(VirtualDatacenterDto virtualDatacenter,
         VirtualMachineTemplateOptions options);

   /**
    * List all available ips to purchase in the datacenter by the virtual
    * datacenter.
    * 
    * @param virtualDatacenter
    *           The virtual datacenter.
    * @param options
    *           Filtering options.
    * @return The list of available ips.
    */
   PublicIpsDto listAvailablePublicIps(VirtualDatacenterDto virtualDatacenter, IpOptions options);

   /**
    * List all purchased public ip addresses in the virtual datacenter.
    * 
    * @param virtualDatacenter
    *           The virtual datacenter.
    * @param options
    *           Filtering options.
    * @return The list of purchased ips.
    */
   PublicIpsDto listPurchasedPublicIps(VirtualDatacenterDto virtualDatacenter, IpOptions options);

   /**
    * Purchase a public IP.
    * 
    * @param ip
    *           The public ip address to purchase.
    * @return The purchased public ip.
    */
   PublicIpDto purchasePublicIp(PublicIpDto publicIp);

   /**
    * Release a public IP.
    * 
    * @param ip
    *           The public ip address to purchase.
    * @return The release public ip.
    */
   PublicIpDto releasePublicIp(PublicIpDto publicIp);

   /**
    * List the storage tiers available for the given virtual datacenter.
    * 
    * @param virtualDatacenter
    *           The virtual datacenter.
    * @return The storage tiers available to the given virtual datacenter.
    */
   @EnterpriseEdition
   TiersDto listStorageTiers(VirtualDatacenterDto virtualDatacenter);

   /**
    * Get the storage tier from the given virtual datacenter.
    * 
    * @param virtualDatacenter
    *           The virtual datacenter.
    * @param The
    *           id of the storage tier.
    * @return The storage tiers available to the given virtual datacenter.
    */
   @EnterpriseEdition
   TierDto getStorageTier(VirtualDatacenterDto virtualDatacenter, Integer tierId);

   /*********************** Private Network ***********************/

   /**
    * Get the default network of the virtual datacenter.
    * 
    * @param virtualDatacenter
    *           The virtual datacenter.
    * @return The default network of the virtual datacenter.
    */
   VLANNetworkDto getDefaultNetwork(VirtualDatacenterDto virtualDatacenter);

   /**
    * Set the default network of the virtual datacenter.
    * 
    * @param virtualDatacenter
    *           The virtual datacenter.
    * @param network
    *           The default network.
    */
   void setDefaultNetwork(VirtualDatacenterDto virtualDatacenter, VLANNetworkDto network);

   /**
    * List all private networks for a virtual datacenter.
    * 
    * @param virtualDatacenter
    *           The virtual datacenter.
    * @return The list of private networks for the virtual datacenter.
    */
   VLANNetworksDto listPrivateNetworks(VirtualDatacenterDto virtualDatacenter);

   /**
    * Get the given private network from the given virtual datacenter.
    * 
    * @param virtualDatacenter
    *           The virtual datacenter.
    * @param virtualApplianceId
    *           The id of the private network.
    * @return The private network or <code>null</code> if it does not exist.
    */
   VLANNetworkDto getPrivateNetwork(VirtualDatacenterDto virtualDatacenter, Integer privateNetworkId);

   /**
    * Create a new private network in a virtual datacenter.
    * 
    * @param virtualDatacenter
    *           The virtual datacenter.
    * @param privateNetwork
    *           The private network to be created.
    * @return The created private network.
    */
   VLANNetworkDto createPrivateNetwork(final VirtualDatacenterDto virtualDatacenter, final VLANNetworkDto privateNetwork);

   /**
    * Updates an existing private network from the given virtual datacenter.
    * 
    * @param privateNetwork
    *           The new attributes for the private network.
    * @return The updated private network.
    */
   VLANNetworkDto updatePrivateNetwork(VLANNetworkDto privateNetwork);

   /**
    * Deletes an existing private network.
    * 
    * @param privateNetwork
    *           The private network to delete.
    */
   void deletePrivateNetwork(VLANNetworkDto privateNetwork);

   /*********************** Private Network IPs ***********************/

   /**
    * List all ips for a private network.
    * 
    * @param network
    *           The private network.
    * @return The list of ips for the private network.
    */
   PrivateIpsDto listPrivateNetworkIps(VLANNetworkDto network);

   /**
    * List all ips for a private network with options.
    * 
    * @param network
    *           The private network.
    * @param options
    *           Filtering options.
    * @return The list of ips for the private network.
    */
   PrivateIpsDto listPrivateNetworkIps(VLANNetworkDto network, IpOptions options);

   /**
    * Get the requested ip from the given private network.
    * 
    * @param network
    *           The private network.
    * @param ipId
    *           The id of the ip to get.
    * @return The requested ip.
    */
   PrivateIpDto getPrivateNetworkIp(VLANNetworkDto network, Integer ipId);

   /*********************** Virtual Appliance ***********************/

   /**
    * List all virtual appliance for a virtual datacenter.
    * 
    * @param virtualDatacenter
    *           The virtual datacenter.
    * @return The list of virtual appliances for the virtual datacenter.
    */
   VirtualAppliancesDto listVirtualAppliances(VirtualDatacenterDto virtualDatacenter);

   /**
    * Get the given virtual appliance from the given virtual datacenter.
    * 
    * @param virtualDatacenter
    *           The virtual datacenter.
    * @param virtualApplianceId
    *           The id of the virtual appliance.
    * @return The virtual appliance or <code>null</code> if it does not exist.
    */
   VirtualApplianceDto getVirtualAppliance(VirtualDatacenterDto virtualDatacenter, Integer virtualApplianceId);

   /**
    * Create a new virtual appliance in a virtual datacenter.
    * 
    * @param virtualDatacenter
    *           The virtual datacenter.
    * @param virtualAppliance
    *           The virtual appliance to be created.
    * @return The created virtual appliance.
    */
   VirtualApplianceDto createVirtualAppliance(VirtualDatacenterDto virtualDatacenter,
         VirtualApplianceDto virtualAppliance);

   /**
    * Updates an existing virtual appliance from the given virtual datacenter.
    * 
    * @param virtualAppliance
    *           The new attributes for the virtual appliance.
    * @return The updated virtual appliance.
    */
   VirtualApplianceDto updateVirtualAppliance(VirtualApplianceDto virtualAppliance);

   /**
    * Deletes an existing virtual appliance.
    * 
    * @param virtualAppliance
    *           The virtual appliance to delete.
    */
   void deleteVirtualAppliance(VirtualApplianceDto virtualAppliance);

   /**
    * Deletes an existing virtual appliance.
    * 
    * @param virtualAppliance
    *           The virtual appliance to delete.
    * @param options
    *           The options to customize the delete operation (e.g. Force
    *           delete).
    */
   void deleteVirtualAppliance(VirtualApplianceDto virtualAppliance, VirtualApplianceOptions options);

   /**
    * Deploy a virtual appliance.
    * 
    * @param virtualAppliance
    *           The virtual appliance to deploy
    * @param options
    *           the extra options for the deploy process.
    * @return Response message to the deploy request.
    */
   @Timeout(duration = 90, timeUnit = TimeUnit.SECONDS)
   AcceptedRequestDto<String> deployVirtualAppliance(VirtualApplianceDto virtualAppliance, VirtualMachineTaskDto options);

   /**
    * Undeploy a virtual appliance.
    * 
    * @param virtualAppliance
    *           The virtual appliance to undeploy
    * @param options
    *           the extra options for the undeploy process.
    * @return Response message to the undeploy request.
    */
   @Timeout(duration = 90, timeUnit = TimeUnit.SECONDS)
   AcceptedRequestDto<String> undeployVirtualAppliance(VirtualApplianceDto virtualAppliance,
         VirtualMachineTaskDto options);

   /**
    * Get the state of the given virtual appliance.
    * 
    * @param virtualAppliance
    *           The given virtual appliance.
    * @return The state of the given virtual appliance.
    */
   VirtualApplianceStateDto getVirtualApplianceState(VirtualApplianceDto virtualAppliance);

   /**
    * Gets the price of the given virtual appliance.
    * 
    * @param virtualAppliance
    *           The virtual appliance to get the price of.
    * @return A <code>String</code> representation of the price of the virtual
    *         appliance.
    */
   String getVirtualAppliancePrice(VirtualApplianceDto virtualAppliance);

   /*********************** Virtual Machine ***********************/

   /**
    * List all virtual machines for a virtual appliance.
    * 
    * @param virtualAppliance
    *           The virtual appliance.
    * @return The list of virtual machines for the virtual appliance.
    */
   VirtualMachinesWithNodeExtendedDto listVirtualMachines(VirtualApplianceDto virtualAppliance);

   /**
    * List all virtual machines for a virtual appliance.
    * 
    * @param virtualAppliance
    *           The virtual appliance.
    * @param options
    *           The options to filter the list of virtual machines.
    * @return The list of virtual machines for the virtual appliance.
    */
   VirtualMachinesWithNodeExtendedDto listVirtualMachines(VirtualApplianceDto virtualAppliance,
         VirtualMachineOptions options);

   /**
    * Get the given virtual machine from the given virtual machine.
    * 
    * @param virtualAppliance
    *           The virtual appliance.
    * @param virtualMachineId
    *           The id of the virtual machine.
    * @return The virtual machine or <code>null</code> if it does not exist.
    */
   VirtualMachineWithNodeExtendedDto getVirtualMachine(VirtualApplianceDto virtualAppliance, Integer virtualMachineId);

   /**
    * Create a new virtual machine in a virtual appliance.
    * 
    * @param virtualAppliance
    *           The virtual appliance.
    * @param virtualMachine
    *           The virtual machine to be created.
    * @return The created virtual machine.
    */
   VirtualMachineWithNodeExtendedDto createVirtualMachine(VirtualApplianceDto virtualAppliance,
         VirtualMachineWithNodeExtendedDto virtualMachine);

   /**
    * Deletes an existing virtual machine.
    * 
    * @param virtualMachine
    *           The virtual machine to delete.
    */
   void deleteVirtualMachine(VirtualMachineDto virtualMachine);

   /**
    * Updates an existing virtual machine from the given virtual appliance.
    * 
    * @param virtualMachine
    *           The new attributes for the virtual machine.
    * @return The task reference or <code>null</code> if the operation completed
    *         synchronously.
    */
   AcceptedRequestDto<String> updateVirtualMachine(VirtualMachineWithNodeExtendedDto virtualMachine);

   /**
    * Updates an existing virtual machine from the given virtual appliance.
    * 
    * @param virtualMachine
    *           The new attributes for the virtual machine.
    * @param options
    *           The update options.
    * @return The task reference or <code>null</code> if the operation completed
    *         synchronously.
    */
   AcceptedRequestDto<String> updateVirtualMachine(VirtualMachineWithNodeExtendedDto virtualMachine,
         VirtualMachineOptions options);

   /**
    * Changes the state an existing virtual machine.
    * 
    * @param virtualMachine
    *           The given virtual machine.
    * @param state
    *           The new state.
    * @return The task reference.
    */
   AcceptedRequestDto<String> changeVirtualMachineState(VirtualMachineDto virtualMachine, VirtualMachineStateDto state);

   /**
    * Get the state of the given virtual machine.
    * 
    * @param virtualMachine
    *           The given virtual machine.
    * @return The state of the given virtual machine.
    */
   VirtualMachineStateDto getVirtualMachineState(VirtualMachineDto virtualMachine);

   /**
    * Deploy a virtual machine with task options.
    * 
    * @param virtualMachine
    *           The virtual machine to deploy.
    * @param options
    *           extra deploy options.
    * @return Response message to the deploy request.
    */
   @Timeout(duration = 90, timeUnit = TimeUnit.SECONDS)
   AcceptedRequestDto<String> deployVirtualMachine(VirtualMachineDto virtualMachine, VirtualMachineTaskDto options);

   /**
    * Uneploy a virtual machine with task options.
    * 
    * @param virtualMachine
    *           The virtual machine to undeploy.
    * @param options
    *           extra deploy unoptions.
    * @return Response message to the undeploy request.
    */
   @Timeout(duration = 90, timeUnit = TimeUnit.SECONDS)
   AcceptedRequestDto<String> undeployVirtualMachine(VirtualMachineDto virtualMachine, VirtualMachineTaskDto options);

   /**
    * List all available network configurations for a virtual machine.
    * 
    * @param virtualMachine
    *           The virtual machine.
    * @return The list of network configurations.
    */
   VMNetworkConfigurationsDto listNetworkConfigurations(VirtualMachineDto virtualMachine);

   /**
    * Sets the gateway network to be used by this virtual machine.
    * 
    * @param virtualMachine
    *           The virtual machine.
    * @param network
    *           The gateway network to use.
    */
   void setGatewayNetwork(final VirtualMachineDto virtualMachine, final VLANNetworkDto network);

   /**
    * Reboot a virtual machine.
    * 
    * @param virtualMachine
    *           The virtual machine to reboot.
    * @return Response message to the reset request.
    */
   AcceptedRequestDto<String> rebootVirtualMachine(VirtualMachineDto virtualMachine);

   /******************* Virtual Machine Template ***********************/

   /**
    * Get the template of a virtual machine.
    * 
    * @param virtualMachine
    *           The given virtual machine.
    * @return The template of the given virtual machine.
    */
   VirtualMachineTemplateDto getVirtualMachineTemplate(VirtualMachineDto virtualMachine);

   /**
    * Get the volumes attached to the given virtual machine.
    * 
    * @param virtualMachine
    *           The virtual machine.
    * @return The volumes attached to the given virtual machine.
    */
   VolumesManagementDto listAttachedVolumes(VirtualMachineDto virtualMachine);

   /**
    * Detach all volumes from the given virtual machine.
    * <p>
    * If the virtual machine is deployed, the operation will be executed
    * asynchronously.
    * 
    * @param virtualMachine
    *           The virtual machine.
    * @return The task reference or <code>null</code> if the operation completed
    *         synchronously.
    */
   AcceptedRequestDto<String> detachAllVolumes(VirtualMachineDto virtualMachine);

   /**
    * Replaces the current volumes attached to the virtual machine with the
    * given ones.
    * <p>
    * If the virtual machine is deployed, the operation will be executed
    * asynchronously.
    * 
    * @param virtualMachine
    *           The virtual machine.
    * @param options
    *           virtual machine parameters
    * @param volumes
    *           The new volumes for the virtual machine.
    * @return The task reference or <code>null</code> if the operation completed
    *         synchronously.
    */
   @Timeout(duration = 90, timeUnit = TimeUnit.SECONDS)
   AcceptedRequestDto<String> replaceVolumes(VirtualMachineDto virtualMachine, VirtualMachineOptions options,
         VolumeManagementDto... volumes);

   /**
    * List all hard disks attached to the given virtual machine.
    * 
    * @param virtualMachine
    *           The virtual machine.
    * @return The hard disks attached to the virtual machine.
    */
   DisksManagementDto listAttachedHardDisks(VirtualMachineDto virtualMachine);

   /**
    * Detach all hard disks from the given virtual machine.
    * <p>
    * If the virtual machine is deployed, the operation will be executed
    * asynchronously.
    * 
    * @param virtualMachine
    *           The virtual machine.
    * @return The task reference or <code>null</code> if the operation completed
    *         synchronously.
    */
   AcceptedRequestDto<String> detachAllHardDisks(VirtualMachineDto virtualMachine);

   /**
    * Replaces the current hard disks attached to the virtual machine with the
    * given ones.
    * <p>
    * If the virtual machine is deployed, the operation will be executed
    * asynchronously.
    * 
    * @param virtualMachine
    *           The virtual machine.
    * @param hardDisks
    *           The new hard disks for the virtual machine.
    * @return The task reference or <code>null</code> if the operation completed
    *         synchronously.
    */
   AcceptedRequestDto<String> replaceHardDisks(VirtualMachineDto virtualMachine, DiskManagementDto... hardDisks);

   /*********************** Hard disks ***********************/

   /**
    * List all hard disks in the given virtual datacenter.
    * 
    * @param virtualDatacenter
    *           The virtual datacenter.
    * @return The hard disks in the virtual datacenter.
    */
   DisksManagementDto listHardDisks(VirtualDatacenterDto virtualDatacenter);

   /**
    * Get the hard disk with the given id in the given virtual datacenter.
    * 
    * @param virtualDatacenter
    *           The virtual datacenter.
    * @param diskId
    *           The id of the hard disk to get.
    * @return The requested hard disk or <code>null</code> if it does not exist.
    */
   DiskManagementDto getHardDisk(VirtualDatacenterDto virtualDatacenter, Integer diskId);

   /**
    * Creates a new hard disk in the given virtual datacenter.
    * 
    * @param virtualDatacenter
    *           The virtual datacenter where the hard disk will be created.
    * @param hardDisk
    *           The hard disk to create.
    * @return The created hard disk.
    */
   DiskManagementDto createHardDisk(VirtualDatacenterDto virtualDatacenter, DiskManagementDto hardDisk);

   /**
    * Deletes the given hard disk.
    * 
    * @param hardDisk
    *           The hard disk to delete.
    */
   void deleteHardDisk(DiskManagementDto hardDisk);

   /*********************** Volumes ***********************/

   /**
    * List all volumes in the given virtual datacenter.
    * 
    * @param virtualDatacenter
    *           The virtual datacenter.
    * @return The volumes in the virtual datacenter.
    */
   @EnterpriseEdition
   VolumesManagementDto listVolumes(VirtualDatacenterDto virtualDatacenter);

   /**
    * List all volumes in the given virtual datacenter.
    * 
    * @param virtualDatacenter
    *           The virtual datacenter.
    * @param options
    *           Optional parameters to filter the volume list.
    * @return The volumes in the virtual datacenter.
    */
   @EnterpriseEdition
   VolumesManagementDto listVolumes(VirtualDatacenterDto virtualDatacenter, VolumeOptions options);

   /**
    * Get a volume from the given virtual datacenter.
    * 
    * @param virtualDatacenter
    *           The virtual datacenter.
    * @param volumeId
    *           The id of the volume to get.
    * @return The volume or <code>null</code> if it does not exist.
    */
   @EnterpriseEdition
   VolumeManagementDto getVolume(VirtualDatacenterDto virtualDatacenter, Integer volumeId);

   /**
    * Creates a volume in the given virtual datacenter.
    * 
    * @param virtualDatacenter
    *           The virtual datacenter where the volume will be created.
    * @param volume
    *           The volume to create. This volume dto must contain a link to the
    *           tier where the volume should be created.
    * @return The created volume.
    */
   @EnterpriseEdition
   @Timeout(duration = 90, timeUnit = TimeUnit.SECONDS)
   VolumeManagementDto createVolume(VirtualDatacenterDto virtualDatacenter, VolumeManagementDto volume);

   /**
    * Modifies the given volume.
    * <p>
    * If the virtual machine is deployed and the size of the volume is changed,
    * then an asynchronous task will be generated to refresh the resources of
    * the virtual machine in the hypervisor.
    * 
    * @param volume
    *           The volume to modify.
    * @return The task reference or <code>null</code> if no task was generated.
    */
   @EnterpriseEdition
   @Timeout(duration = 90, timeUnit = TimeUnit.SECONDS)
   AcceptedRequestDto<String> updateVolume(VolumeManagementDto volume);

   /**
    * Delete the given volume.
    * 
    * @param volume
    *           The volume to delete.
    */
   @EnterpriseEdition
   @Timeout(duration = 90, timeUnit = TimeUnit.SECONDS)
   void deleteVolume(VolumeManagementDto volume);

   /**
    * Moves the given volume to a new virtual datacenter.
    * <p>
    * The Abiquo API will return a 301 (Moved Permanently), so redirects must be
    * enabled to make this method succeed.
    * 
    * @param volume
    *           The volume to move.
    * @param newVirtualDatacenter
    *           The destination virtual datacenter.
    * @return The reference to the volume in the new virtual datacenter.
    */
   @EnterpriseEdition
   VolumeManagementDto moveVolume(VolumeManagementDto volume, VirtualDatacenterDto newVirtualDatacenter);

}
