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
 * @author grkvlt@apache.org, Adrian Cole
 * @see VmAsyncApi
 * @version 1.5
 */
public interface VmApi {

   /**
    * Retrieves a {@link Vm}.
    * 
    * @since 0.9
    * @see VAppApi#get(String)
    */
   Vm get(String vmUrn);

   Vm get(URI vmHref);

   /**
    * Modifies the name/description of a {@link Vm}.
    * 
    * @since 0.9
    * @see VAppApi#edit(String, VApp)
    */
   Task edit(String vmUrn, Vm vm);

   Task edit(URI vmHref, Vm vm);

   /**
    * Deletes a {@link Vm}.
    * 
    * @since 0.9
    * @see VAppApi#remove(String)
    */
   Task remove(String vmUrn);

   Task remove(URI vmHref);

   /**
    * Consolidates a {@link Vm}.
    * 
    * <pre>
    * POST /vApp/{id}/action/consolidate
    * </pre>
    * 
    * @since 1.5
    */
   Task consolidate(String vmUrn);

   Task consolidate(URI vmHref);

   /**
    * Deploys a {@link Vm}.
    * 
    * @since 0.9
    * @see VAppApi#deploy(String, DeployVAppParams)
    */
   Task deploy(String vmUrn, DeployVAppParams params);

   Task deploy(URI vmHref, DeployVAppParams params);

   /**
    * Discard suspended state of a {@link Vm}.
    * 
    * @since 0.9
    * @see VAppApi#discardSuspendedState(String)
    */
   Task discardSuspendedState(String vmUrn);

   Task discardSuspendedState(URI vmHref);

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
   Task installVMwareTools(String vmUrn);

   Task installVMwareTools(URI vmHref);

   /**
    * Relocates a {@link Vm}.
    * 
    * <pre>
    * POST /vApp/{id}/action/relocate
    * </pre>
    * 
    * @since 1.5
    */
   Task relocate(String vmUrn, RelocateParams params);

   Task relocate(URI vmHref, RelocateParams params);

   /**
    * Undeploy a {@link Vm}.
    * 
    * @since 0.9
    * @see VAppApi#undeploy(String, UndeployVAppParams)
    */
   Task undeploy(String vmUrn, UndeployVAppParams params);

   Task undeploy(URI vmHref, UndeployVAppParams params);

   /**
    * Upgrade virtual hardware version of a VM to the highest supported virtual hardware version of
    * provider vDC where the VM locates.
    * 
    * <pre>
    * POST /vApp/{id}/action/upgradeHardwareVersion
    * </pre>
    * 
    * @since 1.5
    */
   Task upgradeHardwareVersion(String vmUrn);

   Task upgradeHardwareVersion(URI vmHref);

   /**
    * Powers off a {@link Vm}.
    * 
    * @since 0.9
    * @see VAppApi#powerOff(String)
    */
   Task powerOff(String vmUrn);

   Task powerOff(URI vmHref);

   /**
    * Powers on a {@link Vm}.
    * 
    * @since 0.9
    * @see VAppApi#powerOn(String)
    */
   Task powerOn(String vmUrn);

   Task powerOn(URI vmHref);

   /**
    * Reboots a {@link Vm}.
    * 
    * @since 0.9
    * @see VAppApi#reboot(String)
    */
   Task reboot(String vmUrn);

   Task reboot(URI vmHref);

   /**
    * Resets a {@link Vm}.
    * 
    * @since 0.9
    * @see VAppApi#reset(String)
    */
   Task reset(String vmUrn);

   Task reset(URI vmHref);

   /**
    * Shuts down a {@link Vm}.
    * 
    * @since 0.9
    * @see VAppApi#shutdown(String)
    */
   Task shutdown(String vmUrn);

   Task shutdown(URI vmHref);

   /**
    * Suspends a {@link Vm}.
    * 
    * @since 0.9
    * @see VAppApi#suspend(String)
    */
   Task suspend(String vmUrn);

   Task suspend(URI vmHref);

   /**
    * Retrieves the guest customization section of a {@link Vm}.
    * 
    * <pre>
    * GET /vApp/{id}/guestCustomizationSection
    * </pre>
    * 
    * @since 1.0
    */
   GuestCustomizationSection getGuestCustomizationSection(String vmUrn);

   GuestCustomizationSection getGuestCustomizationSection(URI vmHref);

   /**
    * Modifies the guest customization section of a {@link Vm}.
    * 
    * <pre>
    * PUT /vApp/{id}/guestCustomizationSection
    * </pre>
    * 
    * @since 1.0
    */
   Task editGuestCustomizationSection(String vmUrn, GuestCustomizationSection section);

   Task editGuestCustomizationSection(URI vmHref, GuestCustomizationSection section);

   /**
    * Ejects media from a {@link Vm}.
    * 
    * <pre>
    * PUT /vApp/{id}/media/action/ejectMedia
    * </pre>
    * 
    * @since 0.9
    */
   Task ejectMedia(String vmUrn, MediaInsertOrEjectParams mediaParams);

   Task ejectMedia(URI vmHref, MediaInsertOrEjectParams mediaParams);

   /**
    * Insert media into a {@link Vm}.
    * 
    * <pre>
    * PUT /vApp/{id}/media/action/insertMedia
    * </pre>
    * 
    * @since 0.9
    */
   Task insertMedia(String vmUrn, MediaInsertOrEjectParams mediaParams);

   Task insertMedia(URI vmHref, MediaInsertOrEjectParams mediaParams);

   /**
    * Retrieves the network connection section of a {@link Vm}.
    * 
    * <pre>
    * GET /vApp/{id}/networkConnectionSection
    * </pre>
    * 
    * @since 0.9
    */
   NetworkConnectionSection getNetworkConnectionSection(String vmUrn);

   NetworkConnectionSection getNetworkConnectionSection(URI vmHref);

   /**
    * Modifies the network connection section of a {@link Vm}.
    * 
    * <pre>
    * PUT /vApp/{id}/networkConnectionSection
    * </pre>
    * 
    * @since 0.9
    */
   Task editNetworkConnectionSection(String vmUrn, NetworkConnectionSection section);

   Task editNetworkConnectionSection(URI vmHref, NetworkConnectionSection section);

   /**
    * Retrieves the operating system section of a {@link Vm}.
    * 
    * <pre>
    * GET /vApp/{id}/operatingSystemSection
    * </pre>
    * 
    * @since 0.9
    */
   OperatingSystemSection getOperatingSystemSection(String vmUrn);

   OperatingSystemSection getOperatingSystemSection(URI vmHref);

   /**
    * Modifies the operating system section of a {@link Vm}.
    * 
    * <pre>
    * PUT /vApp/{id}/operatingSystemSection
    * </pre>
    * 
    * @since 0.9
    */
   Task editOperatingSystemSection(String vmUrn, OperatingSystemSection section);

   Task editOperatingSystemSection(URI vmHref, OperatingSystemSection section);

   /**
    * Retrieves {@link Vm} product sections.
    * 
    * @since 1.5
    * @see VAppApi#getProductSections(String)
    */
   ProductSectionList getProductSections(String vmUrn);

   ProductSectionList getProductSections(URI vmHref);

   /**
    * Modifies the product section information of a {@link Vm}.
    * 
    * @since 1.5
    * @see VAppApi#editProductSections(String, ProductSectionList)
    */
   Task editProductSections(String vmUrn, ProductSectionList sectionList);

   Task editProductSections(URI vmHref, ProductSectionList sectionList);

   /**
    * Retrieves a pending question for a {@link Vm}.
    * 
    * The user should answer to the question by operation
    * {@link #answerQuestion(String, VmQuestionAnswer)}. Usually questions will be asked when the VM
    * is powering on.
    * 
    * <pre>
    * GET /vApp/{id}/question
    * </pre>
    * 
    * @since 0.9
    */
   VmPendingQuestion getPendingQuestion(String vmUrn);

   VmPendingQuestion getPendingQuestion(URI vmHref);

   /**
    * Answer a pending question on a {@link Vm}.
    * 
    * The answer IDs of choice and question should match the ones returned from operation
    * {@link #getPendingQuestion(String)}.
    * 
    * <pre>
    * POST /vApp/{id}/question/action/answer
    * </pre>
    * 
    * @since 0.9
    */
   void answerQuestion(String vmUrn, VmQuestionAnswer answer);

   void answerQuestion(URI vmHref, VmQuestionAnswer answer);

   /**
    * Retrieves the runtime info section of a {@link Vm}.
    * 
    * <pre>
    * GET /vApp/{id}/runtimeInfoSection
    * </pre>
    * 
    * @since 1.5
    */
   RuntimeInfoSection getRuntimeInfoSection(String vmUrn);

   RuntimeInfoSection getRuntimeInfoSection(URI vmHref);

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
   byte[] getScreenImage(String vmUrn);

   byte[] getScreenImage(URI vmHref);

   /**
    * Retrieve a screen ticket for remote console connection to a {@link Vm}.
    * 
    * A screen ticket is a string that includes the virtual machine's IP address, its managed object
    * reference, and a string that has been encoded as described in RFC 2396. Each VM element in a
    * vApp includes a link where rel="screen:acquireTicket". You can use that link to request a
    * screen ticket that you can use with the vmware-vmrc utility to open a VMware Remote Console
    * for the virtual machine represented by that VM element. The vApp should be running to get a
    * valid screen ticket.
    * 
    * <pre>
    * GET /vApp/{id}/screen/action/acquireTicket
    * </pre>
    * 
    * @since 0.9
    */
   ScreenTicket getScreenTicket(String vmUrn);

   ScreenTicket getScreenTicket(URI vmHref);

   /**
    * Retrieves the virtual hardware section of a {@link Vm}.
    * 
    * <pre>
    * GET /vApp/{id}/virtualHardwareSection
    * </pre>
    * 
    * @since 0.9
    */
   VirtualHardwareSection getVirtualHardwareSection(String vmUrn);

   VirtualHardwareSection getVirtualHardwareSection(URI vmHref);

   /**
    * Modifies the virtual hardware section of a {@link Vm}.
    * 
    * <pre>
    * PUT /vApp/{id}/virtualHardwareSection
    * </pre>
    * 
    * @since 0.9
    */
   Task editVirtualHardwareSection(String vmUrn, VirtualHardwareSection section);

   Task editVirtualHardwareSection(URI vmHref, VirtualHardwareSection section);

   /**
    * Retrieves the CPU properties in virtual hardware section of a {@link Vm}.
    * 
    * <pre>
    * GET /vApp/{id}/virtualHardwareSection/cpu
    * </pre>
    * 
    * @since 0.9
    */
   RasdItem getVirtualHardwareSectionCpu(String vmUrn);

   RasdItem getVirtualHardwareSectionCpu(URI vmHref);

   /**
    * Modifies the CPU properties in virtual hardware section of a {@link Vm}.
    * 
    * <pre>
    * PUT /vApp/{id}/virtualHardwareSection/cpu
    * </pre>
    * 
    * @since 0.9
    */
   Task editVirtualHardwareSectionCpu(String vmUrn, RasdItem rasd);

   Task editVirtualHardwareSectionCpu(URI vmHref, RasdItem rasd);

   /**
    * Retrieves a list of items for disks from virtual hardware section of a {@link Vm}.
    * 
    * <pre>
    * GET /vApp/{id}/virtualHardwareSection/disks
    * </pre>
    * 
    * @since 0.9
    */
   RasdItemsList getVirtualHardwareSectionDisks(String vmUrn);

   RasdItemsList getVirtualHardwareSectionDisks(URI vmHref);

   /**
    * Modifies the disks list in virtual hardware section of a {@link Vm}.
    * 
    * <pre>
    * PUT /vApp/{id}/virtualHardwareSection/disks
    * </pre>
    * 
    * @since 0.9
    */
   Task editVirtualHardwareSectionDisks(String vmUrn, RasdItemsList rasdItemsList);

   Task editVirtualHardwareSectionDisks(URI vmHref, RasdItemsList rasdItemsList);

   /**
    * Retrieves the list of items that represents the floppies and CD/DVD drives in a {@link Vm}.
    * 
    * <pre>
    * GET /vApp/{id}/virtualHardwareSection/media
    * </pre>
    * 
    * @since 0.9
    */
   RasdItemsList getVirtualHardwareSectionMedia(String vmUrn);

   RasdItemsList getVirtualHardwareSectionMedia(URI vmHref);

   /**
    * Retrieves the item that contains memory information from virtual hardware section of a
    * {@link Vm}.
    * 
    * <pre>
    * GET /vApp/{id}/virtualHardwareSection/memory
    * </pre>
    * 
    * @since 0.9
    */
   RasdItem getVirtualHardwareSectionMemory(String vmUrn);

   RasdItem getVirtualHardwareSectionMemory(URI vmHref);

   /**
    * Modifies the memory properties in virtual hardware section of a {@link Vm}.
    * 
    * <pre>
    * PUT /vApp/{id}/virtualHardwareSection/memory
    * </pre>
    * 
    * @since 0.9
    */
   Task editVirtualHardwareSectionMemory(String vmUrn, RasdItem rasd);

   Task editVirtualHardwareSectionMemory(URI vmHref, RasdItem rasd);

   /**
    * Retrieves a list of items for network cards from virtual hardware section of a {@link Vm}.
    * 
    * <pre>
    * GET /vApp/{id}/virtualHardwareSection/networkCards
    * </pre>
    * 
    * @since 0.9
    */
   RasdItemsList getVirtualHardwareSectionNetworkCards(String vmUrn);

   RasdItemsList getVirtualHardwareSectionNetworkCards(URI vmHref);

   /**
    * Modifies the network cards list in virtual hardware section of a {@link Vm}.
    * 
    * <pre>
    * PUT /vApp/{id}/virtualHardwareSection/networkCards
    * </pre>
    * 
    * @since 0.9
    */
   Task editVirtualHardwareSectionNetworkCards(String vmUrn, RasdItemsList rasdItemsList);

   Task editVirtualHardwareSectionNetworkCards(URI vmHref, RasdItemsList rasdItemsList);

   /**
    * Retrieves a list of items for serial ports from virtual hardware section of a {@link Vm}.
    * 
    * <pre>
    * GET /vApp/{id}/virtualHardwareSection/serialPorts
    * </pre>
    * 
    * @since 1.5
    */
   RasdItemsList getVirtualHardwareSectionSerialPorts(String vmUrn);

   RasdItemsList getVirtualHardwareSectionSerialPorts(URI vmHref);

   /**
    * Modifies the serial ports list in virtual hardware section of a {@link Vm}.
    * 
    * <pre>
    * PUT /vApp/{id}/virtualHardwareSection/serialPorts
    * </pre>
    * 
    * @since 1.5
    */
   Task editVirtualHardwareSectionSerialPorts(String vmUrn, RasdItemsList rasdItemsList);

   Task editVirtualHardwareSectionSerialPorts(URI vmHref, RasdItemsList rasdItemsList);
}
