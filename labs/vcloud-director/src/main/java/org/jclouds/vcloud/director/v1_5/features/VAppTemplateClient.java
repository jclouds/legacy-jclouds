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

import org.jclouds.concurrent.Timeout;
import org.jclouds.ovf.Envelope;
import org.jclouds.ovf.NetworkSection;
import org.jclouds.vcloud.director.v1_5.domain.*;

import java.net.URI;
import java.util.concurrent.TimeUnit;

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
    *
    * @param templateReference the reference to the template
    * @return the requested template
    */
   VAppTemplate getVAppTemplate(URISupplier templateReference);

   /**
    * Modifies only the name/description of a vApp template.
    *
    * @param templateReference the reference to the template
    * @param template          the template containing the new name and/or description
    * @return the task performing the action
    */
   Task editVAppTemplate(URISupplier templateReference, VAppTemplate template);

   /**
    * Deletes a vApp template.
    *
    * @param templateReference the reference to the template
    * @return the task performing the action
    */
   Task deleteVappTemplate(URISupplier templateReference);

   /**
    * Consolidates a VM
    *
    * @param templateReference the reference to the template
    * @return the task performing the action
    */
   Task consolidateVappTemplate(URISupplier templateReference);

   /**
    * Consolidates a VM
    *
    * @param templateReference the reference to the template
    * @return the task performing the action
    */
   Task disableDownloadVappTemplate(URISupplier templateReference);

   /**
    * Consolidates a VM,
    *
    * @param templateReference the reference to the template
    * @return the task performing the action
    */
   Task enableDownloadVappTemplate(URISupplier templateReference);

   /**
    * Relocates a virtual machine in a vApp template to a different datastore.    *
    *
    * @param templateReference the reference to the template
    * @return the task performing the action
    */
   Task relocateVappTemplate(URISupplier templateReference, RelocateParams params);

   /**
    * Retrieves the customization section of a vApp template.
    *
    * @param templateReference the reference to the template
    * @return the task performing the action
    */
   CustomizationSection getVAppTemplateCustomizationSection(URISupplier templateReference);

   /**
    * Modifies the vApp template customization information.
    *
    * @param templateReference the reference to the template
    * @return the task performing the action
    */
   Task editVAppTemplateCustomizationSection(URISupplier templateReference, CustomizationSection sectionType);

   /**
    * Retrieves the Guest Customization Section of a VM
    *
    * @param templateReference the reference to the template
    * @return the task performing the action
    */
   GuestCustomizationSection getVAppTemplateGuestCustomizationSection(URISupplier templateReference);

   /**
    * Consolidates a VM
    *
    * @param templateReference the reference to the template
    * @return the task performing the action
    */
   Task editVAppTemplateGuestCustomizationSection(URISupplier templateReference, GuestCustomizationSection sectionType);

   /**
    * Consolidates a VM
    *
    * @param templateReference the reference to the template
    * @return the task performing the action
    */
   LeaseSettingsSection getVappTemplateLeaseSettingsSection(URISupplier templateReference);

   /**
    * Consolidates a VM
    *
    * @param templateReference the reference to the template
    * @return the task performing the action
    */
   Task editVappTemplateLeaseSettingsSection(URISupplier templateReference, LeaseSettingsSection settingsSection);

   /**
    * Retrieves the metadata associated with a vApp Template.
    *
    * @param templateReference the reference to the template
    * @return the requested metadata
    */
   Metadata getMetadataForVappTemplate(URISupplier templateReference);

   /**
    * Merges the metadata for a vApp Template with the information provided.
    *
    * @param templateReference the reference to the template
    * @return the task performing the action
    */
   Task editMetadataForVappTemplate(URISupplier templateReference, Metadata metadata);

   /**
    * Consolidates a VM
    *
    * @param templateReference the reference to the template
    * @return the task performing the action
    */
   MetadataEntry getMetadataEntryForVAppTemplateAndKey(URISupplier templateReference, String key);

   /**
    * Consolidates a VM
    *
    * @param templateReference the reference to the template
    * @return the task performing the action
    */
   Task editMetadataEntryForVAppTemplate(URISupplier templateReference, String key, MetadataEntry entry);

   /**
    * Consolidates a VM
    *
    * @param templateReference the reference to the template
    * @return the task performing the action
    */
   Task deleteMetadataEntryForVAppTemplate(URISupplier templateReference, String key);

   /**
    * Retrieves the network config section of a vApp or vApp template.
    *
    * @param templateReference the reference to the template
    * @return the network config section requested
    */
   NetworkConfigSection getNetworkConfigSectionForVAppTemplate(URISupplier templateReference);

   /**
    * Modifies the network config section of a vApp or vApp template.
    *
    * @param templateReference the reference to the template
    * @return the task performing the action
    */
   Task editNetworkConfigSectionForVAppTemplate(URISupplier templateReference, NetworkConfigSection section);

   /**
    * Retrieves the network connection section of a vApp or vApp template.
    *
    * @param templateReference the reference to the template
    * @return the network connection section requested
    */
   NetworkConnectionSection getNetworkConnectionSectionForVAppTemplate(URISupplier templateReference);

   /**
    * Modifies the network connection section of a vApp or vApp template.
    *
    * @param templateReference the reference to the template
    * @return the task performing the action
    */
   Task editNetworkConnectionSectionForVAppTemplate(URISupplier templateReference, NetworkConnectionSection section);

   /**
    * Retrieves the network section of a vApp or vApp template.
    *
    * @param templateReference the reference to the template
    * @return the network section requested
    */
   NetworkSection getNetworkSectionForVAppTemplate(URISupplier templateReference);

   /**
    * Modifies the network section of a vApp or vApp template.
    *
    * @param templateReference the reference to the template
    * @return the task performing the action
    */
   Task editNetworkSectionForVAppTemplate(URISupplier templateReference, NetworkSection section);

   /**
    * Retrieves an OVF descriptor of a vApp template.
    *
    * @param templateReference the reference to the template
    * @return the task performing the action
    */
   Envelope getOvfForVAppTemplate(URISupplier templateReference);

   /**
    * Retrieves vApp template owner.
    *
    * @param templateReference the reference to the template
    * @return the owner of the vApp template
    */
   Owner getOwnerOfVAppTemplate(URISupplier templateReference);

   /**
    * Retrieves VAppTemplate/VM product sections
    *
    * @param templateReference the reference to the template
    * @return the product sections
    */
   ProductSectionList getProductSectionsForVAppTemplate(URISupplier templateReference);

   /**
    * Modifies the product sections of a vApp or vApp template.
    *
    * @param templateReference the reference to the template
    * @return the task performing the action
    */
   Task editProductSectionsForVAppTemplate(URISupplier templateReference, ProductSectionList sections);

   // TODO ShadowVms???
}
