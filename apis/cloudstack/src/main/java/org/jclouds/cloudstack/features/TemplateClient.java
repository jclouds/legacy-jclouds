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
package org.jclouds.cloudstack.features;

import java.util.Set;
import org.jclouds.cloudstack.domain.AsyncCreateResponse;
import org.jclouds.cloudstack.domain.ExtractMode;
import org.jclouds.cloudstack.domain.Template;
import org.jclouds.cloudstack.domain.TemplateMetadata;
import org.jclouds.cloudstack.domain.TemplatePermission;
import org.jclouds.cloudstack.options.AccountInDomainOptions;
import org.jclouds.cloudstack.options.CreateTemplateOptions;
import org.jclouds.cloudstack.options.DeleteTemplateOptions;
import org.jclouds.cloudstack.options.ExtractTemplateOptions;
import org.jclouds.cloudstack.options.ListTemplatesOptions;
import org.jclouds.cloudstack.options.RegisterTemplateOptions;
import org.jclouds.cloudstack.options.UpdateTemplateOptions;
import org.jclouds.cloudstack.options.UpdateTemplatePermissionsOptions;

/**
 * Provides synchronous access to CloudStack template features.
 * <p/>
 * 
 * @see TemplateAsyncClient
 * @see <a
 *      href="http://download.cloud.com/releases/2.2.0/api_2.2.12/TOC_User.html"
 *      />
 * @author Adrian Cole
 */
public interface TemplateClient {
   /**
    * Creates a template of a virtual machine. The virtual machine must be in a
    * STOPPED state. A template created from this command is automatically
    * designated as a private template visible to the account that created it.
    * 
    * @see http
    *      ://download.cloud.com/releases/2.2.0/api_2.2.8/user/createTemplate
    *      .html
    * @param templateMetadata
    *           overall description of the template
    * @param options
    *           optional arguments
    * @return an asynchronous job response
    */
   AsyncCreateResponse createTemplate(TemplateMetadata templateMetadata, CreateTemplateOptions... options);

   /**
    * Registers an existing template into the Cloud.com cloud.
    * 
    * @see http 
    *      ://download.cloud.com/releases/2.2.0/api_2.2.8/user/registerTemplate
    *      .html
    * @param templateMetadata
    *           overall description of the template
    * @param format
    *           the format for the template. Possible values include QCOW2, RAW,
    *           and VHD.
    * 
    * @param url
    *           the URL of where the template is hosted. Possible URL include
    *           http:// and https://
    * @param zoneId
    *           the ID of the zone the template is to be hosted on
    * @param options
    *           optional arguments
    * @return data about the newly-registered template
    */
   Set<Template> registerTemplate(TemplateMetadata templateMetadata, String format, String hypervisor, String url,
         String zoneId, RegisterTemplateOptions... options);

   /**
    * Updates attributes of a template.
    * 
    * @see http
    *      ://download.cloud.com/releases/2.2.0/api_2.2.8/user/updateTemplate
    *      .html
    * @param id
    *           the ID of the image file
    * @param options
    *           optional arguments
    * @return updated data about the template
    */
   Template updateTemplate(String id, UpdateTemplateOptions... options);

   /**
    * Copies a template from one zone to another.
    * 
    * @see http 
    *      ://download.cloud.com/releases/2.2.0/api_2.2.8/user/copyTemplate.html
    * @param id
    *           Template ID.
    * @param sourceZoneId
    *           ID of the zone the template is currently hosted on.
    * @param destZoneId
    *           ID of the zone the template is being copied to.
    * @return an asynchronous job response
    */
   AsyncCreateResponse copyTemplateToZone(String id, String sourceZoneId, String destZoneId);

   /**
    * Deletes a template from the system. All virtual machines using the deleted
    * template will not be affected.
    * 
    * @see http
    *      ://download.cloud.com/releases/2.2.0/api_2.2.8/user/deleteTemplate
    *      .html
    * @param id
    *           the ID of the template
    * @param options
    *           optional arguments
    */
   AsyncCreateResponse deleteTemplate(String id, DeleteTemplateOptions... options);

   /**
    * List all executable templates.
    * 
    * @see http
    *      ://download.cloud.com/releases/2.2.0/api_2.2.8/user/listTemplates.
    *      html
    * @return all executable templates, or empty set, if no templates are found
    */
   Set<Template> listTemplates();

   /**
    * List all public, private, and privileged templates.
    * 
    * @see http
    *      ://download.cloud.com/releases/2.2.0/api_2.2.8/user/listTemplates.
    *      html
    * @param options
    *           if present, how to constrain the list, defaults to all
    *           executable templates
    * @return templates matching query, or empty set, if no templates are found
    * @see TemplateFilter
    */
   Set<Template> listTemplates(ListTemplatesOptions options);

   /**
    * get a specific template by id
    * 
    * 
    * @param templateId
    * @param zoneId
    *           zone template is defined in
    * @return template or null if not found
    */
   Template getTemplateInZone(String templateId, String zoneId);

   /**
    * Updates a template visibility permissions. A public template is visible to
    * all accounts within the same domain. A private template is visible only to
    * the owner of the template. A privileged template is a private template
    * with account permissions added. Only accounts specified under the template
    * permissions are visible to them.
    * 
    * @see http://download.cloud.com/releases/2.2.0/api_2.2.8/user/
    *      updateTemplatePermissions.html
    * @param id
    *           the template ID
    * @param options
    *           optional arguments
    */
   void updateTemplatePermissions(String id, UpdateTemplatePermissionsOptions... options);

   /**
    * List template visibility and all accounts that have permissions to view
    * this template.
    * 
    * @see http://download.cloud.com/releases/2.2.0/api_2.2.8/user/
    *      listTemplatePermissions.html
    * @param id
    *           the template ID
    * @param options
    *           optional arguments
    * @return the list of permissions that apply to the template
    */
   Set<TemplatePermission> listTemplatePermissions(String id, AccountInDomainOptions... options);

   /**
    * 
    * @see http 
    *      ://download.cloud.com/releases/2.2.0/api_2.2.8/user/extractTemplate
    *      .html
    * @param id
    *           the ID of the template
    * @param mode
    *           FIXME the mode of extraction - HTTP_DOWNLOAD or FTP_UPLOAD
    * @param zoneId
    *           the ID of the zone where the ISO is originally located
    * @param options
    *           optional arguments
    * @return an asynchronous job response
    */
   AsyncCreateResponse extractTemplate(String id, ExtractMode mode, String zoneId,
         ExtractTemplateOptions... options);
}
