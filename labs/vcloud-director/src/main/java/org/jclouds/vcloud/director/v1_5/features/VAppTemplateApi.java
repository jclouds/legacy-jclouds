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

import org.jclouds.dmtf.ovf.NetworkSection;
import org.jclouds.vcloud.director.v1_5.domain.Owner;
import org.jclouds.vcloud.director.v1_5.domain.ProductSectionList;
import org.jclouds.vcloud.director.v1_5.domain.References;
import org.jclouds.vcloud.director.v1_5.domain.Task;
import org.jclouds.vcloud.director.v1_5.domain.VAppTemplate;
import org.jclouds.vcloud.director.v1_5.domain.dmtf.Envelope;
import org.jclouds.vcloud.director.v1_5.domain.section.CustomizationSection;
import org.jclouds.vcloud.director.v1_5.domain.section.LeaseSettingsSection;
import org.jclouds.vcloud.director.v1_5.domain.section.NetworkConfigSection;

/**
 * Provides synchronous access to {@link VAppTemplate} objects.
 * 
 * @author Adam Lowe, Adrian Cole
 * @see VAppTemplateAsyncApi
 */
public interface VAppTemplateApi {

   /**
    * Retrieves a vApp template (can be used also to retrieve a VM from a vApp Template).
    * 
    * The vApp could be in one of these statues:
    * <ul>
    * <li>{@link org.jclouds.vcloud.director.v1_5.domain.ResourceEntityType.Status#FAILED_CREATION
    * FAILED_CREATION(-1)} - Transient entity state, e.g., model object is addd but the
    * corresponding VC backing does not exist yet. This is further sub-categorized in the respective
    * entities.
    * <li>{@link org.jclouds.vcloud.director.v1_5.domain.ResourceEntityType.Status#UNRESOLVED
    * UNRESOLVED(0)} - Entity is whole, e.g., VM creation is complete and all the required model
    * objects and VC backings are addd.
    * <li>{@link org.jclouds.vcloud.director.v1_5.domain.ResourceEntityType.Status#RESOLVED
    * RESOLVED(1)} - Entity is resolved.
    * <li>{@link org.jclouds.vcloud.director.v1_5.domain.ResourceEntityType.Status#UNKNOWN
    * UNKNOWN(6)} - Entity state could not be retrieved from the inventory, e.g., VM power state is
    * null.
    * <li>{@link org.jclouds.vcloud.director.v1_5.domain.ResourceEntityType.Status#POWERED_OFF
    * POWERED_OFF(8)} - All VMs of the vApp template are powered off.
    * <li>{@link org.jclouds.vcloud.director.v1_5.domain.ResourceEntityType.Status#MIXED MIXED(10)}
    * - vApp template status is set to {@code MIXED} when the VMs in the vApp are in different power
    * states.
    * </ul>
    * 
    * <pre>
    * GET /vAppTemplate/{id}
    * </pre>
    * 
    * @param templateUrn
    *           the String of the template
    * @return the requested template
    */
   VAppTemplate get(String templateUrn);

   VAppTemplate get(URI templateHref);

   /**
    * Modifies only the name/description of a vApp template.
    * 
    * <pre>
    * PUT /vAppTemplate/{id}
    * </pre>
    * 
    * @param templateUrn
    *           the String of the template
    * @param template
    *           the template containing the new name and/or description
    * @return the task performing the action. This operation is asynchronous and the user should
    *         monitor the returned task status in order to check when it is completed.
    */
   Task edit(String templateUrn, VAppTemplate template);

   Task edit(URI templateHref, VAppTemplate template);

   /**
    * Deletes a vApp template.
    * 
    * <pre>
    * DELETE /vAppTemplate/{id}
    * </pre>
    * 
    * @param templateUrn
    *           the String of the template
    * @return the task performing the action. This operation is asynchronous and the user should
    *         monitor the returned task status in order to check when it is completed.
    */
   Task remove(String templateUrn);

   Task remove(URI templateHref);

   /**
    * Disables the download link to the ovf of a vApp template.
    * 
    * <pre>
    * POST /vAppTemplate/{id}/action/disableDownload
    * </pre>
    * 
    * @param templateUrn
    *           the String of the template
    */
   void disableDownload(String templateUrn);

   void disableDownload(URI templateHref);

   /**
    * Enables downloading of the ovf of a vApp template.
    * 
    * <pre>
    * POST /vAppTemplate/{id}/action/enableDownload
    * </pre>
    * 
    * @param templateUrn
    *           the String of the template
    * @return the task performing the action. This operation is asynchronous and the user should
    *         monitor the returned task status in order to check when it is completed.
    */
   Task enableDownload(String templateUrn);

   Task enableDownload(URI templateHref);

   /**
    * Retrieves the customization section of a vApp template.
    * 
    * <pre>
    * GET /vAppTemplate/{id}/customizationSection
    * </pre>
    * 
    * @param templateUrn
    *           the String of the template
    * @return the customization section
    */
   CustomizationSection getCustomizationSection(String templateUrn);

   CustomizationSection getCustomizationSection(URI templateHref);

   /**
    * Retrieves the lease settings section of a vApp or vApp template
    * 
    * <pre>
    * GET /vAppTemplate/{id}/leaseSettingsSection
    * </pre>
    * 
    * @param templateUrn
    *           the String of the template
    * @return the lease settings
    */
   LeaseSettingsSection getLeaseSettingsSection(String templateUrn);

   LeaseSettingsSection getLeaseSettingsSection(URI templateHref);

   /**
    * Modifies the lease settings section of a vApp or vApp template.
    * 
    * <pre>
    * PUT /vAppTemplate/{id}/leaseSettingsSection
    * </pre>
    * 
    * @param templateUrn
    *           the String of the template
    * @param section
    *           the new configuration to apply
    * @return the task performing the action. This operation is asynchronous and the user should
    *         monitor the returned task status in order to check when it is completed.
    */
   Task editLeaseSettingsSection(String templateUrn, LeaseSettingsSection section);

   Task editLeaseSettingsSection(URI templateHref, LeaseSettingsSection section);

   /**
    * Retrieves the network config section of a vApp or vApp template.
    * 
    * <pre>
    * GET /vAppTemplate/{id}/networkConfigSection
    * </pre>
    * 
    * @param templateUrn
    *           the String of the template
    * @return the network config section requested
    */
   NetworkConfigSection getNetworkConfigSection(String templateUrn);

   NetworkConfigSection getNetworkConfigSection(URI templateHref);

   /**
    * Retrieves the network section of a vApp or vApp template.
    * 
    * <pre>
    * GET /vAppTemplate/{id}/networkSection
    * </pre>
    * 
    * @param templateUrn
    *           the String of the template
    * @return the network section requested
    */
   NetworkSection getNetworkSection(String templateUrn);

   NetworkSection getNetworkSection(URI templateHref);

   /**
    * Retrieves an OVF descriptor of a vApp template.
    * 
    * This OVF represents the vApp template as it is, with all vCloud specific information (like mac
    * address, parent networks, etc). The OVF which could be downloaded by enabling for download
    * will not contain this information. There are no specific states bound to this entity.
    * 
    * <pre>
    * GET /vAppTemplate/{id}/ovf
    * </pre>
    * 
    * @param templateUrn
    *           the String of the template
    * @return the ovf envelope
    */
   Envelope getOvf(String templateUrn);

   Envelope getOvf(URI templateHref);

   /**
    * Retrieves vApp template owner.
    * 
    * <pre>
    * GET /vAppTemplate/{id}/owner
    * </pre>
    * 
    * @param templateUrn
    *           the String of the template
    * @return the owner of the vApp template
    */
   Owner getOwner(String templateUrn);

   Owner getOwner(URI templateHref);

   /**
    * Retrieves VAppTemplate/VM product sections
    * 
    * <pre>
    * GET /vAppTemplate/{id}/productSections
    * </pre>
    * 
    * @param templateUrn
    *           the String of the template
    * @return the product sections
    */
   ProductSectionList getProductSections(String templateUrn);

   ProductSectionList getProductSections(URI templateHref);

   /**
    * Modifies the product sections of a vApp or vApp template.
    * 
    * <pre>
    * PUT /vAppTemplate/{id}/productSections
    * </pre>
    * 
    * @param templateUrn
    *           the String of the template
    * @return the task performing the action. This operation is asynchronous and the user should
    *         monitor the returned task status in order to check when it is completed.
    */
   Task editProductSections(String templateUrn, ProductSectionList sections);

   Task editProductSections(URI templateHref, ProductSectionList sections);

   /**
    * <pre>
    * GET /vAppTemplate/{id}/shadowVms
    * </pre>
    * 
    * @param templateUrn
    *           the String of the template
    * @return shadowVM references
    */
   References getShadowVms(String templateUrn);

   References getShadowVms(URI templateHref);
}
