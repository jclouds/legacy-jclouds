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
import org.jclouds.dmtf.ovf.NetworkSection;
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.vcloud.director.v1_5.domain.Owner;
import org.jclouds.vcloud.director.v1_5.domain.ProductSectionList;
import org.jclouds.vcloud.director.v1_5.domain.References;
import org.jclouds.vcloud.director.v1_5.domain.Task;
import org.jclouds.vcloud.director.v1_5.domain.VAppTemplate;
import org.jclouds.vcloud.director.v1_5.domain.dmtf.Envelope;
import org.jclouds.vcloud.director.v1_5.domain.params.RelocateParams;
import org.jclouds.vcloud.director.v1_5.domain.section.CustomizationSection;
import org.jclouds.vcloud.director.v1_5.domain.section.GuestCustomizationSection;
import org.jclouds.vcloud.director.v1_5.domain.section.LeaseSettingsSection;
import org.jclouds.vcloud.director.v1_5.domain.section.NetworkConfigSection;
import org.jclouds.vcloud.director.v1_5.domain.section.NetworkConnectionSection;

/**
 * Provides synchronous access to {@link VAppTemplate} objects.
 * 
 * @author Adam Lowe
 * @see org.jclouds.vcloud.director.v1_5.features.VAppTemplateAsyncApi
 */
@Timeout(duration = 180, timeUnit = TimeUnit.SECONDS)
public interface VAppTemplateApi {

   /**
    * Retrieves a vApp template (can be used also to retrieve a VM from a vApp Template).
    *
    * The vApp could be in one of these statues:
    * <ul>
    * <li>{@link org.jclouds.vcloud.director.v1_5.domain.ResourceEntityType.Status#FAILED_CREATION FAILED_CREATION(-1)} -
    *    Transient entity state, e.g., model object is created but the corresponding VC backing does not exist yet. This
    *    is further sub-categorized in the respective entities.
    * <li>{@link org.jclouds.vcloud.director.v1_5.domain.ResourceEntityType.Status#UNRESOLVED UNRESOLVED(0)} -
    *    Entity is whole, e.g., VM creation is complete and all the required model objects and VC backings are created.
    * <li>{@link org.jclouds.vcloud.director.v1_5.domain.ResourceEntityType.Status#RESOLVED RESOLVED(1)} -
    *    Entity is resolved.
    * <li>{@link org.jclouds.vcloud.director.v1_5.domain.ResourceEntityType.Status#UNKNOWN UNKNOWN(6)} -
    *    Entity state could not be retrieved from the inventory, e.g., VM power state is null.
    * <li>{@link org.jclouds.vcloud.director.v1_5.domain.ResourceEntityType.Status#POWERED_OFF POWERED_OFF(8)} -
    *    All VMs of the vApp template are powered off.
    * <li>{@link org.jclouds.vcloud.director.v1_5.domain.ResourceEntityType.Status#MIXED MIXED(10)} -
    *    vApp template status is set to {@code MIXED} when the VMs in the vApp are in different power states.
    * </ul>
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
    * @param template the template containing the new name and/or description
    * @return the task performing the action. This operation is asynchronous and the user should monitor the returned
    *         task status in order to check when it is completed.
    */
   Task modifyVAppTemplate(URI templateUri, VAppTemplate template);

   /**
    * Deletes a vApp template.
    * 
    * <pre>
    * DELETE /vAppTemplate/{id}
    * </pre>
    * 
    * @param templateUri the URI of the template
    * @return the task performing the action. This operation is asynchronous and the user should monitor the returned
    *         task status in order to check when it is completed.
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
    * @return the task performing the action. This operation is asynchronous and the user should monitor the returned
    *         task status in order to check when it is completed.
    */
   Task consolidateVm(URI templateUri);

   /**
    * Disables the download link to the ovf of a vApp template.
    * 
    * <pre>
    * POST /vAppTemplate/{id}/action/disableDownload
    * </pre>
    * 
    * @param templateUri the URI of the template
    */
   void disableDownload(URI templateUri);

   /**
    * Enables downloading of the ovf of a vApp template.
    * 
    * <pre>
    * POST /vAppTemplate/{id}/action/enableDownload
    * </pre>
    * 
    * @param templateUri the URI of the template
    * @return the task performing the action. This operation is asynchronous and the user should monitor the returned
    *         task status in order to check when it is completed.
    */
   Task enableDownload(URI templateUri);

   /**
    * Relocates a virtual machine in a vApp template to a different datastore.
    * 
    * <pre>
    * POST /vAppTemplate/{id}/action/relocate
    * </pre>
    * 
    * @param templateUri the URI of the template
    * @param params contains the reference to the new datastore
    * @return the task performing the action. This operation is asynchronous and the user should monitor the returned
    *         task status in order to check when it is completed.
    */
   Task relocateVm(URI templateUri, RelocateParams params);

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
   CustomizationSection getCustomizationSection(URI templateUri);

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
   GuestCustomizationSection getGuestCustomizationSection(URI templateUri);

   /**
    * Modifies the guest customization options of a VM.
    * 
    * <pre>
    * PUT /vAppTemplate/{id}/guestCustomizationSection
    * </pre>
    * 
    * @param templateUri the URI of the template
    * @param section the new configuration to apply
    * @return the task performing the action. This operation is asynchronous and the user should monitor the returned
    *         task status in order to check when it is completed.
    */
   Task modifyGuestCustomizationSection(URI templateUri, GuestCustomizationSection section);

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
   LeaseSettingsSection getLeaseSettingsSection(URI templateUri);

   /**
    * Modifies the lease settings section of a vApp or vApp template.
    * 
    * <pre>
    * PUT /vAppTemplate/{id}/leaseSettingsSection
    * </pre>
    * 
    * @param templateUri the URI of the template
    * @param section the new configuration to apply
    * @return the task performing the action. This operation is asynchronous and the user should monitor the returned
    *         task status in order to check when it is completed.
    */
   Task modifyLeaseSettingsSection(URI templateUri, LeaseSettingsSection section);

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
   NetworkConfigSection getNetworkConfigSection(URI templateUri);

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
   NetworkConnectionSection getNetworkConnectionSection(URI templateUri);

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
   NetworkSection getNetworkSection(URI templateUri);

   /**
    * Retrieves an OVF descriptor of a vApp template.
    *
    * This OVF represents the vApp template as it is, with all vCloud specific information (like mac address, parent
    * networks, etc). The OVF which could be downloaded by enabling for download will not contain this information.
    * There are no specific states bound to this entity.
    * 
    * <pre>
    * GET /vAppTemplate/{id}/ovf
    * </pre>
    * 
    * @param templateUri the URI of the template
    * @return the ovf envelope
    */
   Envelope getOvf(URI templateUri);

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
   Owner getOwner(URI templateUri);

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
   ProductSectionList getProductSections(URI templateUri);

   /**
    * Modifies the product sections of a vApp or vApp template.
    * 
    * <pre>
    * PUT /vAppTemplate/{id}/productSections
    * </pre>
    * 
    * @param templateUri the URI of the template
    * @return the task performing the action. This operation is asynchronous and the user should monitor the returned
    *         task status in order to check when it is completed.
    */
   Task modifyProductSections(URI templateUri, ProductSectionList sections);

   /**
    * <pre>
    * GET /vAppTemplate/{id}/shadowVms
    * </pre>
    * 
    * @param templateUri the URI of the template
    * @return shadowVM references
    */
   References getShadowVms(URI templateUri);

   /**
    * @return synchronous access to {@link Metadata} features
    */
   @Delegate
   MetadataApi.Writeable getMetadataApi();
}
