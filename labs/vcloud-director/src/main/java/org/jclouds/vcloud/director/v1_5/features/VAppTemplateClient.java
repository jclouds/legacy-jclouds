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
package org.jclouds.vcloud.director.v1_5.features;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.vcloud.director.v1_5.domain.CustomizationSection;
import org.jclouds.vcloud.director.v1_5.domain.GuestCustomizationSection;
import org.jclouds.vcloud.director.v1_5.domain.LeaseSettingsSection;
import org.jclouds.vcloud.director.v1_5.domain.Metadata;
import org.jclouds.vcloud.director.v1_5.domain.NetworkConfigSection;
import org.jclouds.vcloud.director.v1_5.domain.NetworkConnectionSection;
import org.jclouds.vcloud.director.v1_5.domain.Owner;
import org.jclouds.vcloud.director.v1_5.domain.ProductSectionList;
import org.jclouds.vcloud.director.v1_5.domain.RelocateParams;
import org.jclouds.vcloud.director.v1_5.domain.Task;
import org.jclouds.vcloud.director.v1_5.domain.VAppTemplate;
import org.jclouds.vcloud.director.v1_5.domain.ovf.Envelope;
import org.jclouds.vcloud.director.v1_5.domain.ovf.NetworkSection;

/**
 * Provides synchronous access to {@link org.jclouds.vcloud.director.v1_5.domain.VAppTemplate} objects.
 *
 * @author Adam Lowe
 * @see org.jclouds.vcloud.director.v1_5.features.VAppTemplateAsyncClient
 */
@Timeout(duration = 180, timeUnit = TimeUnit.SECONDS)
public interface VAppTemplateClient {

   /**
    * Retrieves a vApp template (can be used also to retrieve a VM from a vApp Template).
    * The vApp could be in one of these statues: FAILED_CREATION(-1) - Transient entity state, 
    * e.g., model object is created but the corresponding VC backing does not exist yet. This 
    * is further sub-categorized in the respective entities. UNRESOLVED(0) - Entity is whole, 
    * e.g., VM creation is complete and all the required model objects and VC backings are created. 
    * RESOLVED(1) - Entity is resolved. UNKNOWN(6) - Entity state could not be retrieved from 
    * the inventory, e.g., VM power state is null. POWERED_OFF(8) - All VMs of the vApp template 
    * are powered off. MIXED(10) - vApp template status is set to MIXED when the VMs in the 
    * vApp are in different power states.
    *
    * <pre>
    * GET /vAppTemplate/{id}
    * </pre>
    *
    * @param templateUri the URI of the template
    * @return the requested template
    */
   VAppTemplate getVAppTemplate(URI templateUri);

   /**
    * Modifies only the name/description of a vApp template.
    *
    * <pre>
    * PUT /vAppTemplate/{id}
    * </pre>
    * 
    * @param templateUri the URI of the template
    * @param template    the template containing the new name and/or description
    * @return the task performing the action. This operation is asynchronous and the user 
    * should monitor the returned task status in order to check when it is completed.
    */
   Task editVAppTemplate(URI templateUri, VAppTemplate template);

   /**
    * Deletes a vApp template.
    *
    * <pre>
    * DELETE /vAppTemplate/{id}
    * </pre>
    *
    * @param templateUri the URI of the template
    * @return the task performing the action. This operation is asynchronous and the user 
    * should monitor the returned task status in order to check when it is completed.
    */
   Task deleteVappTemplate(URI templateUri);

   /**
    * Consolidates a VM
    *
    * <pre>
    * POST /vAppTemplate/{id}/action/consolidate
    * </pre>
    *
    * @param templateUri the URI of the template
    * @return the task performing the action. This operation is asynchronous and the user 
    * should monitor the returned task status in order to check when it is completed.
    */
   Task consolidateVappTemplate(URI templateUri);

   /**
    * Disables the download link to the ovf of a vApp template.
    *
    * <pre>
    * POST /vAppTemplate/{id}/action/disableDownload
    * </pre>
    *
    * @param templateUri the URI of the template
    */
   void disableDownloadVappTemplate(URI templateUri);

   /**
    * Enables downloading of the ovf of a vApp template.
    *
    * <pre>
    * POST /vAppTemplate/{id}/action/enableDownload
    * </pre>
    *
    * @param templateUri the URI of the template
    * @return the task performing the action. This operation is asynchronous and the user 
    * should monitor the returned task status in order to check when it is completed.
    */
   Task enableDownloadVappTemplate(URI templateUri);

   /**
    * Relocates a virtual machine in a vApp template to a different datastore.
    *
    * <pre>
    * POST /vAppTemplate/{id}/action/relocate
    * </pre>
    *
    * @param templateUri the URI of the template
    * @param params      contains the reference to the new datastore
    * @return the task performing the action. This operation is asynchronous and the user 
    * should monitor the returned task status in order to check when it is completed.
    */
   Task relocateVappTemplate(URI templateUri, RelocateParams params);

   /**
    * Retrieves the customization section of a vApp template.
    *
    * <pre>
    * GET /vAppTemplate/{id}/customizationSection
    * </pre>
    *
    * @param templateUri the URI of the template
    * @return the customization section
    */
   CustomizationSection getVAppTemplateCustomizationSection(URI templateUri);

   /**
    * Modifies the vApp template customization information.
    *
    * <pre>
    * PUT /vAppTemplate/{id}/customizationSection
    * </pre>
    *
    * @param templateUri the URI of the template
    * @param section     the new configuration to apply
    * @return the task performing the action. This operation is asynchronous and the user 
    * should monitor the returned task status in order to check when it is completed.
    */
   Task editVAppTemplateCustomizationSection(URI templateUri, CustomizationSection section);

   /**
    * Retrieves the Guest Customization Section of a VM
    *
    * <pre>
    * GET /vAppTemplate/{id}/guestCustomizationSection
    * </pre>
    *
    * @param templateUri the URI of the template
    * @return the guest customization section
    */
   GuestCustomizationSection getVAppTemplateGuestCustomizationSection(URI templateUri);

   /**
    * Modifies the guest customization options of a VM.
    *
    * <pre>
    * PUT /vAppTemplate/{id}/guestCustomizationSection
    * </pre>
    *
    * @param templateUri the URI of the template
    * @param section     the new configuration to apply
    * @return the task performing the action. This operation is asynchronous and the user 
    * should monitor the returned task status in order to check when it is completed.
    */
   Task editVAppTemplateGuestCustomizationSection(URI templateUri, GuestCustomizationSection section);

   /**
    * Retrieves the lease settings section of a vApp or vApp template
    *
    * <pre>
    * GET /vAppTemplate/{id}/leaseSettingsSection
    * </pre>
    *
    * @param templateUri the URI of the template
    * @return the lease settings
    */
   LeaseSettingsSection getVappTemplateLeaseSettingsSection(URI templateUri);

   /**
    * Modifies the lease settings section of a vApp or vApp template.
    *
    * <pre>
    * PUT /vAppTemplate/{id}/leaseSettingsSection
    * </pre>
    *
    * @param templateUri the URI of the template
    * @param section     the new configuration to apply
    * @return the task performing the action. This operation is asynchronous and the user 
    * should monitor the returned task status in order to check when it is completed.
    */
   Task editVappTemplateLeaseSettingsSection(URI templateUri, LeaseSettingsSection section);

   /**
    * Retrieves the network config section of a vApp or vApp template.
    * 
    * <pre>
    * GET /vAppTemplate/{id}/networkConfigSection
    * </pre>
    *
    * @param templateUri the URI of the template
    * @return the network config section requested
    */
   NetworkConfigSection getVAppTemplateNetworkConfigSection(URI templateUri);

   /**
    * Modifies the network config section of a vApp. There are three general types of vApp 
    * networks which could be configured from this section. They are specified by the element 
    * value in /. isolated - this is a vApp network which is not connected to any external 
    * organization network and is used only to connect VMs internally in a vApp. In this 
    * network you could configure only its element of /. bridged - this is a vApp network 
    * which is directly connected to an external organization network. In this network you 
    * should configure only the element of /. In this case the element is inherit from the 
    * parent network. natRouted - this is a vApp network which is NAT routed to an external 
    * organization network. In this network you could configure the and also you should 
    * specify and the element of /. When the network is NAT routed you could specify DHCP, 
    * firewall rules and NAT rules, for fine-grained configuration of your network.
    *
    * <pre>
    * PUT /vAppTemplate/{id}/networkConfigSection
    * </pre>
    *
    * @param templateUri the URI of the template
    * @param section     the new configuration to apply
    * @return the task performing the action. This operation is asynchronous and the user 
    * should monitor the returned task status in order to check when it is completed.
    */
   Task editVAppTemplateNetworkConfigSection(URI templateUri, NetworkConfigSection section);

   /**
    * Retrieves the network connection section of a VM
    *
    * <pre>
    * GET /vAppTemplate/{id}/networkConnectionSection
    * </pre>
    *
    * @param templateUri the URI of the template
    * @return the network connection section requested
    */
   NetworkConnectionSection getVAppTemplateNetworkConnectionSection(URI templateUri);

   /**
    * Modifies the network connection section of a VM.
    *
    * <pre>
    * PUT /vAppTemplate/{id}/networkConnectionSection
    * </pre>
    *
    * @param templateUri the URI of the template
    * @param section     the new configuration to apply
    * @return the task performing the action. This operation is asynchronous and the user 
    * should monitor the returned task status in order to check when it is completed.
    */
   Task editVAppTemplateNetworkConnectionSection(URI templateUri, NetworkConnectionSection section);

   /**
    * Retrieves the network section of a vApp or vApp template.
    *
    * <pre>
    * GET /vAppTemplate/{id}/networkSection
    * </pre>
    *
    * @param templateUri the URI of the template
    * @return the network section requested
    */
   NetworkSection getVAppTemplateNetworkSection(URI templateUri);

   /**
    * Retrieves an OVF descriptor of a vApp template. This OVF represents the vApp 
    * template as it is, with all vCloud specific information (like mac address, 
    * parent networks, etc). The OVF which could be downloaded by enabling for 
    * download will not contain this information. There no specific states bound 
    * to this entity.
    *
    * <pre>
    * GET /vAppTemplate/{id}/ovf
    * </pre>
    *
    * @param templateUri the URI of the template
    * @return the ovf envelope
    */
   Envelope getVAppTemplateOvf(URI templateUri);

   /**
    * Retrieves vApp template owner.
    *
    * <pre>
    * GET /vAppTemplate/{id}/owner
    * </pre>
    *
    * @param templateUri the URI of the template
    * @return the owner of the vApp template
    */
   Owner getOwnerOfVAppTemplate(URI templateUri);

   /**
    * Retrieves VAppTemplate/VM product sections
    *
    * <pre>
    * GET /vAppTemplate/{id}/productSections
    * </pre>
    *
    * @param templateUri the URI of the template
    * @return the product sections
    */
   ProductSectionList getProductSectionsForVAppTemplate(URI templateUri);

   /**
    * Modifies the product sections of a vApp or vApp template.
    *
    * <pre>
    * PUT /vAppTemplate/{id}/productSections
    * </pre>
    *
    * @param templateUri the URI of the template
    * @return the task performing the action. This operation is asynchronous and the user 
    * should monitor the returned task status in order to check when it is completed.
    */
   Task editProductSectionsForVAppTemplate(URI templateUri, ProductSectionList sections);

   // TODO ShadowVms

   /**
    * @return synchronous access to {@link Metadata} features
    */
   @Delegate
   MetadataClient.Writeable getMetadataClient();
}
