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

import org.jclouds.cloudstack.domain.AsyncCreateResponse;
import org.jclouds.cloudstack.domain.ExtractMode;
import org.jclouds.cloudstack.domain.Iso;
import org.jclouds.cloudstack.domain.IsoPermissions;
import org.jclouds.cloudstack.options.AccountInDomainOptions;
import org.jclouds.cloudstack.options.DeleteIsoOptions;
import org.jclouds.cloudstack.options.ExtractIsoOptions;
import org.jclouds.cloudstack.options.ListIsosOptions;
import org.jclouds.cloudstack.options.RegisterIsoOptions;
import org.jclouds.cloudstack.options.UpdateIsoOptions;
import org.jclouds.cloudstack.options.UpdateIsoPermissionsOptions;
import org.jclouds.concurrent.Timeout;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 
 * <p/>
 * 
 * @see IsoAsyncClient
 * @see http://download.cloud.com/releases/2.2.12/api/TOC_User.html
 * @author Richard Downer
 */
@Timeout(duration = 60, timeUnit = TimeUnit.SECONDS)
public interface IsoClient {

   /**
    * Attaches an ISO to a virtual machine.
    *
    * @param isoId the ID of the ISO file
    * @param vmId the ID of the virtual machine
    * @return an asynchronous job response.
    */
   AsyncCreateResponse attachIso(long isoId, long vmId);

   /**
    * Detaches any ISO file (if any) currently attached to a virtual machine.
    *
    * @param vmId The ID of the virtual machine
    * @return an asynchronous job response.
    */
   AsyncCreateResponse detachIso(long vmId);

   /**
    * Gets information about an ISO by its ID.
    *
    * @param id the ID of the ISO file
    * @return the ISO object matching the ID
    */
   Iso getIso(long id);

   /**
    * Lists all available ISO files.
    *
    * @param options optional arguments
    * @return a set of ISO objects the match the filter
    */
   Set<Iso> listIsos(ListIsosOptions... options);

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
   Iso registerIso(String name, String displayText, String url, long zoneId, RegisterIsoOptions... options);

   /**
    * 
    *
    * @param id the ID of the ISO file
    * @param options optional arguments
    * @return the ISO object matching the ID
    */
   Iso updateIso(long id, UpdateIsoOptions... options);

   /**
    * Deletes an ISO file.
    *
    * @param id the ID of the ISO file
    * @param options optional arguments
    * @return an asynchronous job response.
    */
   AsyncCreateResponse deleteIso(long id, DeleteIsoOptions... options);

   /**
    * Copies a template from one zone to another.
    *
    * @param isoId Template ID.
    * @param sourceZoneId ID of the zone the template is currently hosted on.
    * @param destZoneId ID of the zone the template is being copied to.
    * @return an asynchronous job response.
    */
   AsyncCreateResponse copyIso(long isoId, long sourceZoneId, long destZoneId);

   /**
    * Updates iso permissions
    *
    * @param id the template ID
    * @param options optional arguments
    * @return 
    */
   void updateIsoPermissions(long id, UpdateIsoPermissionsOptions... options);

   /**
    * List template visibility and all accounts that have permissions to view this template.
    *
    * @param id the template ID
    * @param options optional arguments
    * @return A set of the permissions on this ISO
    */
   Set<IsoPermissions> listIsoPermissions(long id, AccountInDomainOptions... options);

   /**
    * Extracts an ISO
    *
    * @param id the ID of the ISO file
    * @param mode the mode of extraction - HTTP_DOWNLOAD or FTP_UPLOAD
    * @param zoneId the ID of the zone where the ISO is originally located
    * @param options optional arguments
    * @return an asynchronous job response.
    */
   AsyncCreateResponse extractIso(long id, ExtractMode mode, long zoneId, ExtractIsoOptions... options);

}
