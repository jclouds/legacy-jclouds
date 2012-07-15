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
import org.jclouds.vcloud.director.v1_5.domain.ProductSectionList;
import org.jclouds.vcloud.director.v1_5.domain.RasdItemsList;
import org.jclouds.vcloud.director.v1_5.domain.ScreenTicket;
import org.jclouds.vcloud.director.v1_5.domain.Task;
import org.jclouds.vcloud.director.v1_5.domain.VApp;
import org.jclouds.vcloud.director.v1_5.domain.Vm;
import org.jclouds.vcloud.director.v1_5.domain.VmPendingQuestion;
import org.jclouds.vcloud.director.v1_5.domain.VmQuestionAnswer;
import org.jclouds.vcloud.director.v1_5.domain.dmtf.RasdItem;
import org.jclouds.vcloud.director.v1_5.domain.params.DeployVAppParams;
import org.jclouds.vcloud.director.v1_5.domain.params.MediaInsertOrEjectParams;
import org.jclouds.vcloud.director.v1_5.domain.params.RelocateParams;
import org.jclouds.vcloud.director.v1_5.domain.params.UndeployVAppParams;
import org.jclouds.vcloud.director.v1_5.domain.section.GuestCustomizationSection;
import org.jclouds.vcloud.director.v1_5.domain.section.NetworkConnectionSection;
import org.jclouds.vcloud.director.v1_5.domain.section.OperatingSystemSection;
import org.jclouds.vcloud.director.v1_5.domain.section.RuntimeInfoSection;
import org.jclouds.vcloud.director.v1_5.domain.section.VirtualHardwareSection;

/**
 * Provides synchronous access to {@link Vm} objects.
 *
 * @author grkvlt@apache.org
 * @see VmAsyncClient
 * @version 1.5
 */
@Timeout(duration = 180, timeUnit = TimeUnit.SECONDS)
public interface VmClient {

   /**
    * Retrieves a {@link Vm}.
    *
    * @since 0.9
    * @see VAppClient#getVApp(URI)
    */
   Vm getVm(URI vmURI);

   /**
    * Modifies the name/description of a {@link Vm}.
    *
    * @since 0.9
    * @see VAppClient#modifyVApp(URI, VApp)
    */
   Task modifyVm(URI vmURI, Vm vm);

   /**
    * Deletes a {@link Vm}.
    *
    * @since 0.9
    * @see VAppClient#deleteVApp(URI)
    */
   Task deleteVm(URI vmURI);

   /**
    * Consolidates a {@link Vm}.
    *
    * <pre>
    * POST /vApp/{id}/action/consolidate
    * </pre>
    *
    * @since 1.5
    */
   Task consolidateVm(URI vmURI);

   /**
    * Deploys a {@link Vm}.
    *
    * @since 0.9
    * @see VAppClient#deploy(URI, DeployVAppParams)
    */
   Task deploy(URI vmURI, DeployVAppParams params);

   /**
    * Discard suspended state of a {@link Vm}.
    *
    * @since 0.9
    * @see VAppClient#discardSuspendedState(URI)
    */
   Task discardSuspendedState(URI vmURI);

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
   Task installVMwareTools(URI vmURI);

   /**
    * Relocates a {@link Vm}.
    *
    * <pre>
    * POST /vApp/{id}/action/relocate
    * </pre>
    *
    * @since 1.5
    */
   Task relocateVm(URI vmURI, RelocateParams params);

   /**
    * Undeploy a {@link Vm}.
    *
    * @since 0.9
    * @see VAppClient#undeploy(URI, UndeployVAppParams)
    */
   Task undeploy(URI vmURI, UndeployVAppParams params);

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
   Task upgradeHardwareVersion(URI vmURI);

   /**
    * Powers off a {@link Vm}.
    *
    * @since 0.9
    * @see VAppClient#powerOff(URI)
    */
   Task powerOff(URI vmURI);

   /**
    * Powers on a {@link Vm}.
    *
    * @since 0.9
    * @see VAppClient#powerOn(URI)
    */
   Task powerOn(URI vmURI);

   /**
    * Reboots a {@link Vm}.
    *
    * @since 0.9
    * @see VAppClient#reboot(URI)
    */
   Task reboot(URI vmURI);

   /**
    * Resets a {@link Vm}.
    *
    * @since 0.9
    * @see VAppClient#reset(URI)
    */
   Task reset(URI vmURI);

   /**
    * Shuts down a {@link Vm}.
    *
    * @since 0.9
    * @see VAppClient#shutdown(URI)
    */
   Task shutdown(URI vmURI);

   /**
    * Suspends a {@link Vm}.
    *
    * @since 0.9
    * @see VAppClient#suspend(URI)
    */
   Task suspend(URI vmURI);

   /**
    * Retrieves the guest customization section of a {@link Vm}.
    *
    * <pre>
    * GET /vApp/{id}/guestCustomizationSection
    * </pre>
    *
    * @since 1.0
    * @see VAppClient#
    */
   GuestCustomizationSection getGuestCustomizationSection(URI vmURI);

   /**
    * Modifies the guest customization section of a {@link Vm}.
    *
    * <pre>
    * PUT /vApp/{id}/guestCustomizationSection
    * </pre>
    *
    * @since 1.0
    */
   Task modifyGuestCustomizationSection(URI vmURI, GuestCustomizationSection section);

   /**
    * Ejects media from a {@link Vm}.
    *
    * <pre>
    * PUT /vApp/{id}/media/action/ejectMedia
    * </pre>
    *
    * @since 0.9
    */
   Task ejectMedia(URI vmURI, MediaInsertOrEjectParams mediaParams);

   /**
    * Insert media into a {@link Vm}.
    *
    * <pre>
    * PUT /vApp/{id}/media/action/insertMedia
    * </pre>
    *
    * @since 0.9
    */
   Task insertMedia(URI vmURI, MediaInsertOrEjectParams mediaParams);

   /**
    * Retrieves the network connection section of a {@link Vm}.
    *
    * <pre>
    * GET /vApp/{id}/networkConnectionSection
    * </pre>
    *
    * @since 0.9
    */
   NetworkConnectionSection getNetworkConnectionSection(URI vmURI);

   /**
    * Modifies the network connection section of a {@link Vm}.
    *
    * <pre>
    * PUT /vApp/{id}/networkConnectionSection
    * </pre>
    *
    * @since 0.9
    */
   Task modifyNetworkConnectionSection(URI vmURI, NetworkConnectionSection section);

   /**
    * Retrieves the operating system section of a {@link Vm}.
    *
    * <pre>
    * GET /vApp/{id}/operatingSystemSection
    * </pre>
    *
    * @since 0.9
    */
   OperatingSystemSection getOperatingSystemSection(URI vmURI);

   /**
    * Modifies the operating system section of a {@link Vm}.
    *
    * <pre>
    * PUT /vApp/{id}/operatingSystemSection
    * </pre>
    *
    * @since 0.9
    */
   Task modifyOperatingSystemSection(URI vmURI, OperatingSystemSection section);

   /**
    * Retrieves {@link Vm} product sections.
    *
    * @since 1.5
    * @see VAppClient#getProductSections(URI)
    */
   ProductSectionList getProductSections(URI vmURI);

   /**
    * Modifies the product section information of a {@link Vm}.
    *
    * @since 1.5
    * @see VAppClient#modifyProductSections(URI, ProductSectionList)
    */
   Task modifyProductSections(URI vmURI, ProductSectionList sectionList);

   /**
    * Retrieves a pending question for a {@link Vm}.
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
   VmPendingQuestion getPendingQuestion(URI vmURI);

   /**
    * Answer a pending question on a {@link Vm}.
    *
    * The answer IDs of choice and question should match the ones returned from operation {@link #getPendingQuestion(URI)}.
    *
    * <pre>
    * POST /vApp/{id}/question/action/answer
    * </pre>
    *
    * @since 0.9
    */
   void answerQuestion(URI vmURI, VmQuestionAnswer answer);

   /**
    * Retrieves the runtime info section of a {@link Vm}.
    *
    * <pre>
    * GET /vApp/{id}/runtimeInfoSection
    * </pre>
    *
    * @since 1.5
    */
   RuntimeInfoSection getRuntimeInfoSection(URI vmURI);

   /**
    * Retrieves the thumbnail of the screen of a {@link Vm}.
    *
    * The content type of the response may vary (e.g. {@code image/png}, {@code image/gif}).
    *
    * <pre>
    * GET /vApp/{id}/screen
    * </pre>
    *
    * @since 0.9
    */
   byte[] getScreenImage(URI vmURI);

   /**
    * Retrieve a screen ticket for remote console connection to a {@link Vm}.
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
   ScreenTicket getScreenTicket(URI vmURI);

   /**
    * Retrieves the virtual hardware section of a {@link Vm}.
    *
    * <pre>
    * GET /vApp/{id}/virtualHardwareSection
    * </pre>
    *
    * @since 0.9
    */
   VirtualHardwareSection getVirtualHardwareSection(URI vmURI);

   /**
    * Modifies the virtual hardware section of a {@link Vm}.
    *
    * <pre>
    * PUT /vApp/{id}/virtualHardwareSection
    * </pre>
    *
    * @since 0.9
    */
   Task modifyVirtualHardwareSection(URI vmURI, VirtualHardwareSection section);

   /**
    * Retrieves the CPU properties in virtual hardware section of a {@link Vm}.
    *
    * <pre>
    * GET /vApp/{id}/virtualHardwareSection/cpu
    * </pre>
    *
    * @since 0.9
    */
   RasdItem getVirtualHardwareSectionCpu(URI vmURI);

   /**
    * Modifies the CPU properties in virtual hardware section of a {@link Vm}.
    *
    * <pre>
    * PUT /vApp/{id}/virtualHardwareSection/cpu
    * </pre>
    *
    * @since 0.9
    */
   Task modifyVirtualHardwareSectionCpu(URI vmURI, RasdItem rasd);

   /**
    * Retrieves a list of items for disks from virtual hardware section of a {@link Vm}.
    *
    * <pre>
    * GET /vApp/{id}/virtualHardwareSection/disks
    * </pre>
    *
    * @since 0.9
    */
   RasdItemsList getVirtualHardwareSectionDisks(URI vmURI);

   /**
    * Modifies the disks list in virtual hardware section of a {@link Vm}.
    *
    * <pre>
    * PUT /vApp/{id}/virtualHardwareSection/disks
    * </pre>
    *
    * @since 0.9
    */
   Task modifyVirtualHardwareSectionDisks(URI vmURI, RasdItemsList rasdItemsList);

   /**
    * Retrieves the list of items that represents the floppies and CD/DVD drives in a {@link Vm}.
    *
    * <pre>
    * GET /vApp/{id}/virtualHardwareSection/media
    * </pre>
    *
    * @since 0.9
    */
   RasdItemsList getVirtualHardwareSectionMedia(URI vmURI);

   /**
    * Retrieves the item that contains memory information from virtual hardware section of a {@link Vm}.
    *
    * <pre>
    * GET /vApp/{id}/virtualHardwareSection/memory
    * </pre>
    *
    * @since 0.9
    */
   RasdItem getVirtualHardwareSectionMemory(URI vmURI);

   /**
    * Modifies the memory properties in virtual hardware section of a {@link Vm}.
    *
    * <pre>
    * PUT /vApp/{id}/virtualHardwareSection/memory
    * </pre>
    *
    * @since 0.9
    */
   Task modifyVirtualHardwareSectionMemory(URI vmURI, RasdItem rasd);

   /**
    * Retrieves a list of items for network cards from virtual hardware section of a {@link Vm}.
    *
    * <pre>
    * GET /vApp/{id}/virtualHardwareSection/networkCards
    * </pre>
    *
    * @since 0.9
    */
   RasdItemsList getVirtualHardwareSectionNetworkCards(URI vmURI);

   /**
    * Modifies the network cards list in virtual hardware section of a {@link Vm}.
    *
    * <pre>
    * PUT /vApp/{id}/virtualHardwareSection/networkCards
    * </pre>
    *
    * @since 0.9
    */
   Task modifyVirtualHardwareSectionNetworkCards(URI vmURI, RasdItemsList rasdItemsList);

   /**
    * Retrieves a list of items for serial ports from virtual hardware section of a {@link Vm}.
    *
    * <pre>
    * GET /vApp/{id}/virtualHardwareSection/serialPorts
    * </pre>
    *
    * @since 1.5
    */
   RasdItemsList getVirtualHardwareSectionSerialPorts(URI vmURI);

   /**
    * Modifies the serial ports list in virtual hardware section of a {@link Vm}.
    *
    * <pre>
    * PUT /vApp/{id}/virtualHardwareSection/serialPorts
    * </pre>
    *
    * @since 1.5
    */
   Task modifyVirtualHardwareSectionSerialPorts(URI vmURI, RasdItemsList rasdItemsList);

   /**
    * Synchronous access to {@link Vm} {@link Metadata} features.
    */
   @Delegate
   MetadataClient.Writeable getMetadataClient();
}
