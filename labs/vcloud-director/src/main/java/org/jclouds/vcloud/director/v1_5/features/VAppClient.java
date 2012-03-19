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
package org.jclouds.vcloud.director.v1_5.features;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.vcloud.director.v1_5.domain.ControlAccessParams;
import org.jclouds.vcloud.director.v1_5.domain.DeployVAppParams;
import org.jclouds.vcloud.director.v1_5.domain.GuestCustomizationSection;
import org.jclouds.vcloud.director.v1_5.domain.LeaseSettingsSection;
import org.jclouds.vcloud.director.v1_5.domain.MediaInsertOrEjectParams;
import org.jclouds.vcloud.director.v1_5.domain.NetworkConfigSection;
import org.jclouds.vcloud.director.v1_5.domain.NetworkConnectionSection;
import org.jclouds.vcloud.director.v1_5.domain.Owner;
import org.jclouds.vcloud.director.v1_5.domain.ProductSectionList;
import org.jclouds.vcloud.director.v1_5.domain.RasdItemsList;
import org.jclouds.vcloud.director.v1_5.domain.RecomposeVAppParams;
import org.jclouds.vcloud.director.v1_5.domain.RelocateParams;
import org.jclouds.vcloud.director.v1_5.domain.RuntimeInfoSection;
import org.jclouds.vcloud.director.v1_5.domain.ScreenTicket;
import org.jclouds.vcloud.director.v1_5.domain.Task;
import org.jclouds.vcloud.director.v1_5.domain.UndeployVAppParams;
import org.jclouds.vcloud.director.v1_5.domain.VApp;
import org.jclouds.vcloud.director.v1_5.domain.VmPendingQuestion;
import org.jclouds.vcloud.director.v1_5.domain.VmQuestionAnswer;
import org.jclouds.vcloud.director.v1_5.domain.cim.ResourceAllocationSettingData;
import org.jclouds.vcloud.director.v1_5.domain.ovf.NetworkSection;
import org.jclouds.vcloud.director.v1_5.domain.ovf.OperatingSystemSection;
import org.jclouds.vcloud.director.v1_5.domain.ovf.StartupSection;
import org.jclouds.vcloud.director.v1_5.domain.ovf.VirtualHardwareSection;

/**
 * Provides synchronous access to {@link VApp} objects.
 *
 * @author grkvlt@apache.org
 * @see VAppAsyncClient
 * @version 1.5
 */
@Timeout(duration = 180, timeUnit = TimeUnit.SECONDS)
public interface VAppClient {

   /**
    * Retrieves a vApp/VM.
    *
    * The vApp/VM could be in one of these statuses:
    * <ul>
    * <li>{@link org.jclouds.vcloud.director.v1_5.domain.ResourceEntityType.Status#FAILED_CREATION} -
    *    Transient entity state, e.g., model object is created but the corresponding VC backing does not
    *		exist yet. This is further sub-categorized in the respective entities.
    * <li>{@link org.jclouds.vcloud.director.v1_5.domain.ResourceEntityType.Status#UNRESOLVED} -
    *		Entity is whole, e.g., VM creation is complete and all the required model objects and VC backings are
    *		created.
    * <li>{@link org.jclouds.vcloud.director.v1_5.domain.ResourceEntityType.Status#RESOLVED} -
    *		Entity is resolved.
    * <li>{@link org.jclouds.vcloud.director.v1_5.domain.ResourceEntityType.Status#DEPLOYED} -
    *		Entity is deployed.
    * <li>{@link org.jclouds.vcloud.director.v1_5.domain.ResourceEntityType.Status#SUSPENDED} -
    *		All VMs of the vApp are suspended.
    * <li>{@link org.jclouds.vcloud.director.v1_5.domain.ResourceEntityType.Status#POWERED_ON} -
    *		All VMs of the vApp are powered on.
    * <li>{@link org.jclouds.vcloud.director.v1_5.domain.ResourceEntityType.Status#WAITING_FOR_INPUT} -
    *		VM is pending response on a question.
    * <li>{@link org.jclouds.vcloud.director.v1_5.domain.ResourceEntityType.Status#UNKNOWN} -
    *		Entity state could not be retrieved from the inventory, e.g., VM power state is null.
    * <li>{@link org.jclouds.vcloud.director.v1_5.domain.ResourceEntityType.Status#UNRECOGNIZED} -
    *		Entity state was retrieved from the inventory but could not be mapped to an internal state.
    * <li>{@link org.jclouds.vcloud.director.v1_5.domain.ResourceEntityType.Status#POWERED_OFF} -
    *		All VMs of the vApp are powered off.
    * <li>{@link org.jclouds.vcloud.director.v1_5.domain.ResourceEntityType.Status#INCONSISTENT_STATE} -
    *		Apply to VM status, if a vm is {@code POWERED_ON}, or {@code WAITING_FOR_INPUT}, but is
    *    undeployed, it is in an inconsistent state.
    * <li>{@link org.jclouds.vcloud.director.v1_5.domain.ResourceEntityType.Status#MIXED} -
    *		vApp status is set to {@code MIXED} when the VMs in the vApp are in different power states
    * </ul>
    *
    * <pre>
    * GET /vApp/{id}
    * </pre>
    *
    * @since 0.9
    */
   VApp getVApp(URI vAppURI);

   /**
    * Modifies the name/description of a vApp/VM.
    *
    * <pre>
    * PUT /vApp/{id}
    * </pre>
    *
    * @since 0.9
    */
   Task modifyVApp(URI vAppURI, VApp vApp);

   /**
    * Deletes a vApp/VM.
    *
    * <pre>
    * DELETE /vApp/{id}
    * </pre>
    *
    * @since 0.9
    */
   Task deleteVApp(URI vAppURI);

   /**
    * Consolidates a vm.
    *
    * <pre>
    * POST /vApp/{id}/action/consolidate
    * </pre>
    *
    * @since 1.5
    */
   Task consolidateVApp(URI vAppURI);

   /**
    * Modifies the control access of a vApp.
    *
    * <pre>
    * POST /vApp/{id}/action/controlAccess
    * </pre>
    *
    * @since 0.9
    */
   ControlAccessParams controlAccess(URI vAppURI, ControlAccessParams params);

   /**
    * Deploys a vApp/VM.
    *
    * Deployment means allocation of all resource for a vApp/VM like CPU and memory
    * from a vDC resource pool. Deploying a vApp automatically deploys all of the
    * virtual machines it contains. As of version 1.5 the operation supports force
    * customization passed with {@link DeployVAppParamsType#setForceCustomization(Boolean)}
    * parameter.
    *
    * <pre>
    * POST /vApp/{id}/action/deploy
    * </pre>
    *
    * @since 0.9
    */
   Task deploy(URI vAppURI, DeployVAppParams params);

   /**
    * Discard suspended state of a vApp/VM.
    *
    * Discarding suspended state of a vApp automatically discarded suspended
    * states of all of the virtual machines it contains.
    *
    * <pre>
    * POST /vApp/{id}/action/discardSuspendedState
    * </pre>
    *
    * @since 0.9
    */
   Task discardSuspendedState(URI vAppURI);

   /**
    * Place the vApp into maintenance mode.
    *
    * While in maintenance mode, a system admin can operate on the vApp as
    * usual, but end users are restricted to read-only operations. Any
    * user-initiated tasks running when the vApp enters maintenance mode will
    * continue.
    *
    * <pre>
    * POST /vApp/{id}/action/enterMaintenanceMode
    * </pre>
    *
    * @since 1.5
    */
   void enterMaintenanceMode(URI vAppURI);

   /**
    * Take the vApp out of maintenance mode.
    *
    * <pre>
    * POST /vApp/{id}/action/exitMaintenanceMode
    * </pre>
    *
    * @since 1.5
    */
   void exitMaintenanceMode(URI vAppURI);

   /**
    * Installs VMware tools to the virtual machine.
    *
    * It should be running in order for them to be installed.
    *
    * <pre>
    * POST /vApp/{id}/action/installVMwareTools
    * </pre>
    *
    * @since 1.5
    */
   Task installVMwareTools(URI vAppURI);

   /**
    * Recompose a vApp by removing its own VMs and/or adding new ones from other
    * vApps or vApp templates.
    *
    * To remove VMs you should put their references in elements. The way you add
    * VMs is the same as described in compose vApp operation
    * {@link VdcClient#composeVApp(URI, org.jclouds.vcloud.director.v1_5.domain.ComposeVAppParams)}.
    * The status of vApp will be in {@link org.jclouds.vcloud.director.v1_5.domain.ResourceEntityType.Status#UNRESOLVED}
    * until the recompose task is finished.
    *
    * <pre>
    * POST /vApp/{id}/action/recomposeVApp
    * </pre>
    *
    * @since 1.0
    */
   Task recomposeVApp(URI vAppURI, RecomposeVAppParams params);

   /**
    * Relocates a vm.
    *
    * <pre>
    * POST /vApp/{id}/action/relocate
    * </pre>
    *
    * @since 1.5
    */
   Task relocate(URI vAppURI, RelocateParams params);

   /**
    * Undeploy a vApp/VM.
    *
    * Undeployment means deallocation of all resources for a vApp/VM like CPU
    * and memory from a vDC resource pool. Undeploying a vApp automatically
    * undeploys all of the virtual machines it contains.
    *
    * <pre>
    * POST /vApp/{id}/action/undeploy
    * </pre>
    *
    * @since 0.9
    */
   Task undeploy(URI vAppURI, UndeployVAppParams params);

   /**
    * Upgrade virtual hardware version of a VM to the highest supported virtual
    * hardware version of provider vDC where the VM locates.
    *
    * <pre>
    * POST /vApp/{id}/action/upgradeHardwareVersion
    * </pre>
    *
    * @since 1.5
    */
   Task upgradeHardwareVersion(URI vAppURI);

   /**
    * Retrieves the control access information for a vApp.
    *
    * The vApp could be shared to everyone or could be shared to specific user,
    * by modifying the control access values.
    *
    * <pre>
    * GET /vApp/{id}/controlAccess
    * </pre>
    *
    * @since 0.9
    */
   ControlAccessParams getControlAccess(URI vAppURI);

   /**
    * Powers off a vApp/VM.
    *
    * If the operation is used over a vApp then all VMs are powered off. This operation is allowed only when the vApp/VM is powered on.
    *
    * <pre>
    * POST /vApp/{id}/power/action/powerOff
    * </pre>
    *
    * @since 0.9
    */
   Task powerOff(URI vAppURI);

   /**
    * Powers on a vApp/VM.
    *
    * If the operation is used over a vApp then all VMs are powered on. This
    * operation is allowed only when the vApp/VM is powered off.
    *
    * <pre>
    * POST /vApp/{id}/power/action/powerOn
    * </pre>
    *
    * @since 0.9
    */
   Task powerOn(URI vAppURI);

   /**
    * Reboots a vApp/VM.
    *
    * The vApp/VM should be started in order to reboot it.
    *
    * <pre>
    * POST /vApp/{id}/power/action/reboot
    * </pre>
    *
    * @since 0.9
    */
   Task reboot(URI vAppURI);

   /**
    * Resets a vApp/VM.
    *
    * If the operation is used over a vApp then all VMs are reset. This
    * operation is allowed only when the vApp/VM is powered on.
    *
    * <pre>
    * POST /vApp/{id}/power/action/reset
    * </pre>
    *
    * @since 0.9
    */
   Task reset(URI vAppURI);

   /**
    * Shutdowns a vApp/VM.
    *
    * If the operation is used over a vApp then all VMs are shutdown. This
    * operation is allowed only when the vApp/VM is powered on.
    *
    * <pre>
    * POST /vApp/{id}/power/action/shutdown
    * </pre>
    *
    * @since 0.9
    */
   Task shutdown(URI vAppURI);

   /**
    * Suspends a vApp/VM.
    *
    * If the operation is used over a vApp then all VMs are suspended. This
    * operation is allowed only when the vApp/VM is powered on.
    *
    * <pre>
    * POST /vApp/{id}/power/action/suspend
    * </pre>
    *
    * @since 0.9
    */
   Task suspend(URI vAppURI);

   /**
    * Retrieves the guest customization section of a VM.
    *
    * <pre>
    * GET /vApp/{id}/guestCustomizationSection
    * </pre>
    *
    * @since 1.0
    */
   GuestCustomizationSection getGuestCustomizationSection(URI vmURI);

   /**
    * Modifies the guest customization section of a VM.
    *
    * <pre>
    * PUT /vApp/{id}/guestCustomizationSection
    * </pre>
    *
    * @since 1.0
    */
   Task modifyGuestCustomizationSection(URI vmURI, GuestCustomizationSection section);

   /**
    * Retrieves the lease settings section of a vApp or vApp template.
    *
    * <pre>
    * GET /vApp/{id}/leaseSettingsSection
    * </pre>
    *
    * @since 0.9
    */
   LeaseSettingsSection getLeaseSettingsSection(URI vAppURI);

   /**
    * Modifies the lease settings section of a vApp or vApp template.
    *
    * <pre>
    * PUT /vApp/{id}/leaseSettingsSection
    * </pre>
    *
    * @since 0.9
    */
   Task modifyLeaseSettingsSection(URI vAppURI, LeaseSettingsSection section);

   /**
    * Ejects a media from a VM.
    *
    * <pre>
    * PUT /vApp/{id}/media/action/ejectMedia
    * </pre>
    *
    * @since 0.9
    */
   Task ejectMedia(URI vmURI, MediaInsertOrEjectParams mediaParams);

   /**
    * Inserts a media into a VM.
    *
    * <pre>
    * PUT /vApp/{id}/media/action/insertMedia
    * </pre>
    *
    * @since 0.9
    */
   Task insertMedia(URI vmURI, MediaInsertOrEjectParams mediaParams);

   /**
    * @return synchronous access to {@link Metadata} features
    */
   @Delegate
   MetadataClient.Writeable getMetadataClient();

   /**
    * Retrieves the network config section of a vApp or vApp template.
    *
    * <pre>
    * GET /vApp/{id}/networkConfigSection
    * </pre>
    *
    * @since 0.9
    */
   NetworkConfigSection getNetworkConfigSection(URI vmURI);

   /**
    * Modifies the network config section of a vApp.
    *
    * <pre>
    * PUT /vApp/{id}/networkConfigSection
    * </pre>
    *
    * @since 0.9
    */
   Task modifyNetworkConfigSection(URI vmURI, NetworkConfigSection section);

   /**
    * Retrieves the network connection section of a VM.
    *
    * <pre>
    * GET /vApp/{id}/networkConnectionSection
    * </pre>
    *
    * @since 0.9
    */
   NetworkConnectionSection getNetworkConnectionSection(URI vmURI);

   /**
    * Modifies the network connection section of a VM.
    *
    * <pre>
    * PUT /vApp/{id}/networkConnectionSection
    * </pre>
    *
    * @since 0.9
    */
   Task modifyNetworkConnectionSection(URI vmURI, NetworkConnectionSection section);

   /**
    * Retrieves the network section of a vApp or vApp template.
    *
    * <pre>
    * GET /vApp/{id}/networkSection
    * </pre>
    *
    * @since 0.9
    */
   NetworkSection getNetworkSection(URI vAppURI);

   /**
    * Retrieves the operating system section of a VM.
    *
    * <pre>
    * GET /vApp/{id}/operatingSystemSection
    * </pre>
    *
    * @since 0.9
    */
   OperatingSystemSection getOperatingSystemSection(URI vmURI);

   /**
    * Modifies the operating system section of a VM.
    *
    * <pre>
    * PUT /vApp/{id}/operatingSystemSection
    * </pre>
    *
    * @since 0.9
    */
   Task modifyOperatingSystemSection(URI vmURI, OperatingSystemSection section);

   /**
    * Retrieves the owner of a vApp.
    *
    * <pre>
    * GET /vApp/{id}/owner
    * </pre>
    *
    * @since 1.5
    */
   Owner getOwner(URI vAppURI);

   /**
    * Changes VApp owner.
    *
    * <pre>
    * PUT /vApp/{id}/owner
    * </pre>
    *
    * @since 1.5
    */
   void modifyOwner(URI vAppURI, Owner owner);

   /**
    * Retrieves VAppTemplate/VM product sections.
    *
    * <pre>
    * GET /vApp/{id}/productSections
    * </pre>
    *
    * @since 1.5
    */
   ProductSectionList getProductSections(URI vAppURI);

   /**
    * Modifies the product section information of a vApp/VM.
    *
    * <pre>
    * PUT /vApp/{id}/productSections
    * </pre>
    *
    * @since 1.5
    */
   Task modifyProductSections(URI vAppURI, ProductSectionList sectionList);

   /**
    * Retrieves a pending question for a VM.
    *
    * The user should answer to the question by operation {@link #answerQuestion(URI, VmQuestionAnswer)}.
    * Usually questions will be asked when the VM is powering on.
    *
    * <pre>
    * GET /vApp/{id}/question
    * </pre>
    *
    * @since 0.9
    */
   VmPendingQuestion getPendingQuestion(URI vAppURI);

   /**
    * Answer on a pending question.
    *
    * The answer IDs of choice and question should match the ones returned from operation {@link #getPendingQuestion(URI)}.
    *
    * <pre>
    * PUT /vApp/{id}/question/action/answer
    * </pre>
    *
    * @since 0.9
    */
   void answerQuestion(URI vAppURI, VmQuestionAnswer answer);

   /**
    * Retrieves the runtime info section of a VM.
    *
    * <pre>
    * GET /vApp/{id}/runtimeInfoSection
    * </pre>
    *
    * @since 1.5
    */
   RuntimeInfoSection getRuntimeInfoSection(URI vmURI);

   /**
    * Retrieves the thumbnail of the screen of a VM.
    *
    * The content type of the response may vary (e.g. {@code image/png}, {@code image/gif}).
    *
    * <pre>
    * GET /vApp/{id}/screen
    * </pre>
    *
    * @since 0.9
    */
   byte[] getScreenImage(URI vAppURI);

   /**
    * Retrieve a screen ticket for remote console connection to a VM.
    *
    * A screen ticket is a string that includes the virtual machine's IP address, its managed object reference, and a string
    * that has been encoded as described in RFC 2396. Each VM element in a vApp includes a link where rel="screen:acquireTicket".
    * You can use that link to request a screen ticket that you can use with the vmware-vmrc utility to open a VMware Remote
    * Console for the virtual machine represented by that VM element. The vApp should be running to get a valid screen ticket.
    *
    * <pre>
    * GET /vApp/{id}/screen/action/acquireTicket
    * </pre>
    *
    * @since 0.9
    */
   ScreenTicket getScreenTicket(URI vAppURI);

   /**
    * Retrieves the startup section of a VApp.
    *
    * <pre>
    * GET /vApp/{id}/startupSection
    * </pre>
    *
    * @since 0.9
    */
   StartupSection getStartupSection(URI vAppURI);

   /**
    * Modifies the startup section of a VApp.
    *
    * <pre>
    * PUT /vApp/{id}/startupSection
    * </pre>
    *
    * @since 0.9
    */
   Task modifyStartupSection(URI vAppURI, StartupSection section);

   /**
    * Retrieves the virtual hardware section of a VM.
    *
    * <pre>
    * GET /vApp/{id}/virtualHardwareSection
    * </pre>
    *
    * @since 0.9
    */
   VirtualHardwareSection getVirtualHardwareSection(URI vmURI);

   /**
    * Modifies the virtual hardware section of a VM.
    *
    * <pre>
    * PUT /vApp/{id}/virtualHardwareSection
    * </pre>
    *
    * @since 0.9
    */
   Task modifyVirtualHardwareSection(URI vmURI, VirtualHardwareSection section);

   /**
    * Retrieves the CPU properties in virtual hardware section of a VM.
    *
    * <pre>
    * GET /vApp/{id}/virtualHardwareSection/cpu
    * </pre>
    *
    * @since 0.9
    */
   ResourceAllocationSettingData getVirtualHardwareSectionCpu(URI vAppURI);

   /**
    * Modifies the CPU properties in virtual hardware section of a VM.
    *
    * <pre>
    * PUT /vApp/{id}/virtualHardwareSection/cpu
    * </pre>
    *
    * @since 0.9
    */
   Task modifyVirtualHardwareSectionCpu(URI vAppURI, ResourceAllocationSettingData rasd);

   /**
    * Retrieves a list of ResourceAllocationSettingData items for disks from virtual hardware section of a VM.
    *
    * <pre>
    * GET /vApp/{id}/virtualHardwareSection/disks
    * </pre>
    *
    * @since 0.9
    */
   RasdItemsList getVirtualHardwareSectionDisks(URI vAppURI);

   /**
    * Modifies the disks list in virtual hardware section of a VM.
    *
    * <pre>
    * PUT /vApp/{id}/virtualHardwareSection/disks
    * </pre>
    *
    * @since 0.9
    */
   Task modifyVirtualHardwareSectionDisks(URI vAppURI, RasdItemsList rasdItemsList);

   /**
    * Retrieves the list of ResourceAllocationSettingData items that represents the floppies and CD/DVD drives in a VM.
    *
    * <pre>
    * GET /vApp/{id}/virtualHardwareSection/media
    * </pre>
    *
    * @since 0.9
    */
   RasdItemsList getVirtualHardwareSectionMedia(URI vAppURI);

   /**
    * Retrieves the ResourceAllocationSettingData item that contains memory information from virtual hardware section of a VM.
    *
    * <pre>
    * GET /vApp/{id}/virtualHardwareSection/memory
    * </pre>
    *
    * @since 0.9
    */
   ResourceAllocationSettingData getVirtualHardwareSectionMemory(URI vAppURI);

   /**
    * Modifies the memory properties in virtual hardware section of a VM.
    *
    * <pre>
    * PUT /vApp/{id}/virtualHardwareSection/memory
    * </pre>
    *
    * @since 0.9
    */
   Task modifyVirtualHardwareSectionMemory(URI vAppURI, ResourceAllocationSettingData rasd);

   /**
    * Retrieves a list of ResourceAllocationSettingData items for network cards from virtual hardware section of a VM.
    *
    * <pre>
    * GET /vApp/{id}/virtualHardwareSection/networkCards
    * </pre>
    *
    * @since 0.9
    */
   RasdItemsList getVirtualHardwareSectionNetworkCards(URI vAppURI);

   /**
    * Modifies the network cards list in virtual hardware section of a VM.
    *
    * <pre>
    * PUT /vApp/{id}/virtualHardwareSection/networkCards
    * </pre>
    *
    * @since 0.9
    */
   Task modifyVirtualHardwareSectionNetworkCards(URI vAppURI, RasdItemsList rasdItemsList);

   /**
    * Retrieves a list of ResourceAllocationSettingData items for serial ports from virtual hardware section of a VM.
    *
    * <pre>
    * GET /vApp/{id}/virtualHardwareSection/serialPorts
    * </pre>
    *
    * @since 1.5
    */
   RasdItemsList getVirtualHardwareSectionSerialPorts(URI vAppURI);

   /**
    * Modifies the serial ports list in virtual hardware section of a VM.
    *
    * <pre>
    * PUT /vApp/{id}/virtualHardwareSection/serialPorts
    * </pre>
    *
    * @since 1.5
    */
   Task modifyVirtualHardwareSectionSerialPorts(URI vAppURI, RasdItemsList rasdItemsList);
}
