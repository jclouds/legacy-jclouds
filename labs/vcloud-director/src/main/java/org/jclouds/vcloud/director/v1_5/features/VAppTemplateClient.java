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
import org.jclouds.vcloud.director.v1_5.domain.GuestCustomizationSection;
import org.jclouds.vcloud.director.v1_5.domain.LeaseSettingsSection;
import org.jclouds.vcloud.director.v1_5.domain.Metadata;
import org.jclouds.vcloud.director.v1_5.domain.MetadataValue;
import org.jclouds.vcloud.director.v1_5.domain.NetworkConfigSection;
import org.jclouds.vcloud.director.v1_5.domain.NetworkConnectionSection;
import org.jclouds.vcloud.director.v1_5.domain.Owner;
import org.jclouds.vcloud.director.v1_5.domain.ProductSectionList;
import org.jclouds.vcloud.director.v1_5.domain.RelocateParams;
import org.jclouds.vcloud.director.v1_5.domain.Task;
import org.jclouds.vcloud.director.v1_5.domain.VAppTemplate;
import org.jclouds.vcloud.director.v1_5.domain.ovf.CustomizationSection;
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
    *
    * @param templateUri the URI of the template
    * @return the requested template
    */
   VAppTemplate getVAppTemplate(URI templateUri);

   /**
    * Modifies only the name/description of a vApp template.
    *
    * @param templateUri the URI of the template
    * @param template    the template containing the new name and/or description
    * @return the task performing the action
    */
   Task editVAppTemplate(URI templateUri, VAppTemplate template);

   /**
    * Deletes a vApp template.
    *
    * @param templateUri the URI of the template
    * @return the task performing the action
    */
   Task deleteVappTemplate(URI templateUri);

   /**
    * Consolidates a VM
    *
    * @param templateUri the URI of the template
    * @return the task performing the action
    */
   Task consolidateVappTemplate(URI templateUri);

   /**
    * Consolidates a VM
    *
    * @param templateUri the URI of the template
    * @return the task performing the action
    */
   Task disableDownloadVappTemplate(URI templateUri);

   /**
    * Consolidates a VM,
    *
    * @param templateUri the URI of the template
    * @return the task performing the action
    */
   Task enableDownloadVappTemplate(URI templateUri);

   /**
    * Relocates a virtual machine in a vApp template to a different datastore.    *
    *
    * @param templateUri the URI of the template
    * @param params      contains the reference to the new datastore
    * @return the task performing the action
    */
   Task relocateVappTemplate(URI templateUri, RelocateParams params);

   /**
    * Retrieves the customization section of a vApp template.
    *
    * @param templateUri the URI of the template
    * @return the task performing the action
    */
   CustomizationSection getVAppTemplateCustomizationSection(URI templateUri);

   /**
    * Modifies the vApp template customization information.
    *
    * @param templateUri the URI of the template
    * @param section     the new configuration to apply
    * @return the task performing the action
    */
   Task editVAppTemplateCustomizationSection(URI templateUri, CustomizationSection section);

   /**
    * Retrieves the Guest Customization Section of a VM
    *
    * @param templateUri the URI of the template
    * @return the task performing the action
    */
   GuestCustomizationSection getVAppTemplateGuestCustomizationSection(URI templateUri);

   /**
    * Consolidates a VM
    *
    * @param templateUri the URI of the template
    * @param section     the new configuration to apply
    * @return the task performing the action
    */
   Task editVAppTemplateGuestCustomizationSection(URI templateUri, GuestCustomizationSection section);

   /**
    * Consolidates a VM
    *
    * @param templateUri the URI of the template
    * @return the task performing the action
    */
   LeaseSettingsSection getVappTemplateLeaseSettingsSection(URI templateUri);

   /**
    * Consolidates a VM
    *
    * @param templateUri the URI of the template
    * @param section     the new configuration to apply
    * @return the task performing the action
    */
   Task editVappTemplateLeaseSettingsSection(URI templateUri, LeaseSettingsSection section);

   /**
    * Retrieves the metadata associated with a vApp Template.
    *
    * @param templateUri the URI of the template
    * @return the requested metadata
    */
   Metadata getVAppTemplateMetadata(URI templateUri);

   /**
    * Merges the metadata for a vApp Template with the information provided.
    *
    * @param templateUri the URI of the template
    * @return the task performing the action
    */
   Task editVAppTemplateMetadata(URI templateUri, Metadata metadata);

   /**
    * Consolidates a VM
    *
    * @param templateUri the URI of the template
    * @return the task performing the action
    */
   MetadataValue getVAppTemplateMetadataValue(URI templateUri, String key);

   /**
    * Consolidates a VM
    *
    * @param templateUri the URI of the template
    * @return the task performing the action
    */
   Task editVAppTemplateMetadataValue(URI templateUri, String key, MetadataValue value);

   /**
    * Consolidates a VM
    *
    * @param templateUri the URI of the template
    * @return the task performing the action
    */
   Task deleteVAppTemplateMetadataValue(URI templateUri, String key);

   /**
    * Retrieves the network config section of a vApp or vApp template.
    *
    * @param templateUri the URI of the template
    * @return the network config section requested
    */
   NetworkConfigSection getVAppTemplateNetworkConfigSection(URI templateUri);

   /**
    * Modifies the network config section of a vApp or vApp template.
    *
    * @param templateUri the URI of the template
    * @param section     the new configuration to apply
    * @return the task performing the action
    */
   Task editVAppTemplateNetworkConfigSection(URI templateUri, NetworkConfigSection section);

   /**
    * Retrieves the network connection section of a vApp or vApp template.
    *
    * @param templateUri the URI of the template
    * @return the network connection section requested
    */
   NetworkConnectionSection getVAppTemplateNetworkConnectionSection(URI templateUri);

   /**
    * Modifies the network connection section of a vApp or vApp template.
    *
    * @param templateUri the URI of the template
    * @param section     the new configuration to apply
    * @return the task performing the action
    */
   Task editVAppTemplateNetworkConnectionSection(URI templateUri, NetworkConnectionSection section);

   /**
    * Retrieves the network section of a vApp or vApp template.
    *
    * @param templateUri the URI of the template
    * @return the network section requested
    */
   NetworkSection getVAppTemplateNetworkSection(URI templateUri);

   /**
    * Modifies the network section of a vApp or vApp template.
    *
    * @param templateUri the URI of the template
    * @param section     the new configuration to apply
    * @return the task performing the action
    */
   Task editVAppTemplateNetworkSection(URI templateUri, NetworkSection section);

   /**
    * Retrieves an OVF descriptor of a vApp template.
    *
    * @param templateUri the URI of the template
    * @return the task performing the action
    */
   Envelope getVAppTemplateOvf(URI templateUri);

   /**
    * Retrieves vApp template owner.
    *
    * @param templateUri the URI of the template
    * @return the owner of the vApp template
    */
   Owner getOwnerOfVAppTemplate(URI templateUri);

   /**
    * Retrieves VAppTemplate/VM product sections
    *
    * @param templateUri the URI of the template
    * @return the product sections
    */
   ProductSectionList getProductSectionsForVAppTemplate(URI templateUri);

   /**
    * Modifies the product sections of a vApp or vApp template.
    *
    * @param templateUri the URI of the template
    * @return the task performing the action
    */
   Task editProductSectionsForVAppTemplate(URI templateUri, ProductSectionList sections);

   // TODO ShadowVms???
}
