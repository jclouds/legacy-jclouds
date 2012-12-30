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
import org.jclouds.cloudstack.domain.ISO;
import org.jclouds.cloudstack.domain.ISOPermissions;
import org.jclouds.cloudstack.options.AccountInDomainOptions;
import org.jclouds.cloudstack.options.DeleteISOOptions;
import org.jclouds.cloudstack.options.ExtractISOOptions;
import org.jclouds.cloudstack.options.ListISOsOptions;
import org.jclouds.cloudstack.options.RegisterISOOptions;
import org.jclouds.cloudstack.options.UpdateISOOptions;
import org.jclouds.cloudstack.options.UpdateISOPermissionsOptions;

/**
 * 
 * <p/>
 * 
 * @see ISOAsyncClient
 * @see http://download.cloud.com/releases/2.2.12/api/TOC_User.html
 * @author Richard Downer
 */
public interface ISOClient {

   /**
    * Attaches an ISO to a virtual machine.
    *
    * @param isoId the ID of the ISO file
    * @param vmId the ID of the virtual machine
    * @return an asynchronous job response.
    */
   AsyncCreateResponse attachISO(String isoId, String vmId);

   /**
    * Detaches any ISO file (if any) currently attached to a virtual machine.
    *
    * @param vmId The ID of the virtual machine
    * @return an asynchronous job response.
    */
   AsyncCreateResponse detachISO(String vmId);

   /**
    * Gets information about an ISO by its ID.
    *
    * @param id the ID of the ISO file
    * @return the ISO object matching the ID
    */
   ISO getISO(String id);

   /**
    * Lists all available ISO files.
    *
    * @param options optional arguments
    * @return a set of ISO objects the match the filter
    */
   Set<ISO> listISOs(ListISOsOptions... options);

   /**
    * Registers an existing ISO into the Cloud.com Cloud.
    *
    * @param name the name of the ISO
    * @param displayText the display text of the ISO. This is usually used for display purposes.
    * @param url the URL to where the ISO is currently being hosted
    * @param zoneId the ID of the zone you wish to register the ISO to.
    * @param options optional arguments
    * @return the newly-added ISO
    */
   ISO registerISO(String name, String displayText, String url, String zoneId, RegisterISOOptions... options);

   /**
    * 
    *
    * @param id the ID of the ISO file
    * @param options optional arguments
    * @return the ISO object matching the ID
    */
   ISO updateISO(String id, UpdateISOOptions... options);

   /**
    * Deletes an ISO file.
    *
    * @param id the ID of the ISO file
    * @param options optional arguments
    * @return an asynchronous job response.
    */
   AsyncCreateResponse deleteISO(String id, DeleteISOOptions... options);

   /**
    * Copies a template from one zone to another.
    *
    * @param isoId Template ID.
    * @param sourceZoneId ID of the zone the template is currently hosted on.
    * @param destZoneId ID of the zone the template is being copied to.
    * @return an asynchronous job response.
    */
   AsyncCreateResponse copyISO(String isoId, String sourceZoneId, String destZoneId);

   /**
    * Updates iso permissions
    *
    * @param id the template ID
    * @param options optional arguments
    * @return 
    */
   void updateISOPermissions(String id, UpdateISOPermissionsOptions... options);

   /**
    * List template visibility and all accounts that have permissions to view this template.
    *
    * @param id the template ID
    * @param options optional arguments
    * @return A set of the permissions on this ISO
    */
   ISOPermissions listISOPermissions(String id, AccountInDomainOptions... options);

   /**
    * Extracts an ISO
    *
    * @param id the ID of the ISO file
    * @param mode the mode of extraction - HTTP_DOWNLOAD or FTP_UPLOAD
    * @param zoneId the ID of the zone where the ISO is originally located
    * @param options optional arguments
    * @return an asynchronous job response.
    */
   AsyncCreateResponse extractISO(String id, ExtractMode mode, String zoneId, ExtractISOOptions... options);

}
