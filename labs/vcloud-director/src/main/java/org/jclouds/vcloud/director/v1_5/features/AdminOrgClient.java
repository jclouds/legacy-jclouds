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
import org.jclouds.vcloud.director.v1_5.domain.Group;
import org.jclouds.vcloud.director.v1_5.domain.OrgEmailSettings;
import org.jclouds.vcloud.director.v1_5.domain.OrgGeneralSettings;
import org.jclouds.vcloud.director.v1_5.domain.OrgLdapSettings;
import org.jclouds.vcloud.director.v1_5.domain.OrgLeaseSettings;
import org.jclouds.vcloud.director.v1_5.domain.OrgPasswordPolicySettings;
import org.jclouds.vcloud.director.v1_5.domain.OrgSettings;
import org.jclouds.vcloud.director.v1_5.domain.OrgVAppTemplateLeaseSettings;

/**
 * Provides synchronous access to {@link Group} objects.
 * 
 * @see GroupAsyncClient
 * @author danikov
 */
@Timeout(duration = 180, timeUnit = TimeUnit.SECONDS)
public interface AdminOrgClient extends OrgClient {
   
//   GET /admin/org/{id}
   
//   POST /admin/org/{id}/catalogs
   
//   POST /admin/org/{id}/groups
   
   /**
    * Gets organizational settings for this organization.
    *
    * <pre>
    * GET /admin/org/{id}/settings
    * </pre>
    *
    * @param orgRef the reference for the admin org
    * @return the settings
    */
   OrgSettings getSettings(URI orgRef);
   
   /**
    * Updates organizational settings for this organization.
    * 
    * <pre>
    * PUT /admin/org/{id}/settings
    * </pre>
    * @param orgRef the reference for the admin org
    * @param newSettings the requested updated settings
    * @return the resultant settings
    */
   OrgSettings updateSettings(URI orgRef, OrgSettings newSettings);
   
   /**
    * Retrieves email settings for an organization.
    *
    * <pre>
    * GET /admin/org/{id}/settings/email
    * </pre>
    *
    * @param orgRef the reference for the admin org
    * @return the email settings
    */
   OrgEmailSettings getEmailSettings(URI orgRef);
   
   /**
    * Updates email policy settings for organization.
    * 
    * <pre>
    * PUT /admin/org/{id}/settings/email
    * </pre>
    * @param orgRef the reference for the admin org
    * @param newSettings the requested updated settings
    * @return the resultant settings
    */
   OrgEmailSettings updateEmailSettings(URI orgRef, 
         OrgEmailSettings newSettings);
   
   /**
    * Gets general organization settings.
    *
    * <pre>
    * GET /admin/org/{id}/settings/general
    * </pre>
    *
    * @param orgRef the reference for the admin org
    * @return the lease settings
    */
   OrgGeneralSettings getGeneralSettings(URI orgRef);
   
   /**
    * Updates general organization settings.
    * 
    * <pre>
    * PUT /admin/org/{id}/settings/general
    * </pre>
    * @param orgRef the reference for the admin org
    * @param newSettings the requested updated settings
    * @return the resultant settings
    */
   OrgGeneralSettings updateGeneralSettings(URI orgRef, 
         OrgGeneralSettings newSettings);
   
   /**
    * Retrieves LDAP settings for an organization.
    *
    * <pre>
    * GET /admin/org/{id}/settings/ldap
    * </pre>
    *
    * @param orgRef the reference for the admin org
    * @return the ldap settings
    */
   OrgLdapSettings getLdapSettings(URI orgRef);
   
   /**
    * Retrieves password policy settings for an organization.
    *
    * <pre>
    * GET /admin/org/{id}/settings/passwordPolicy
    * </pre>
    *
    * @param orgRef the reference for the admin org
    * @return the lease settings
    */
   OrgPasswordPolicySettings getPasswordPolicy(URI orgRef);
   
   /**
    * Updates password policy settings for organization.
    * 
    * <pre>
    * PUT /admin/org/{id}/settings/passwordPolicy
    * </pre>
    * @param orgRef the reference for the admin org
    * @param newSettings the requested updated settings
    * @return the resultant settings
    */
   OrgPasswordPolicySettings updatePasswordPolicy(URI orgRef, 
         OrgPasswordPolicySettings newSettings);
   
   /**
    * Gets organization resource cleanup settings on the level of vApp.
    *
    * <pre>
    * GET /admin/org/{id}/settings/vAppLeaseSettings
    * </pre>
    *
    * @param orgRef the reference for the admin org
    * @return the lease settings
    */
   OrgLeaseSettings getVAppLeaseSettings(URI orgRef);
   
   /**
    * Updates organization resource cleanup settings on the level of vApp.
    * 
    * <pre>
    * PUT /admin/org/{id}/settings/vAppLeaseSettings
    * </pre>
    * @param orgRef the reference for the admin org
    * @param newSettings the requested updated settings
    * @return the resultant settings
    */
   OrgLeaseSettings updateVAppLeaseSettings(URI orgRef, 
         OrgLeaseSettings newSettings);
   
   /**
    * Retrieves expiration and storage policy for vApp templates in an organization.
    *
    * <pre>
    * GET /admin/org/{id}/settings/vAppTemplateLeaseSettings
    * </pre>
    *
    * @param orgRef the reference for the admin org
    * @return the lease settings
    */
   OrgVAppTemplateLeaseSettings getVAppTemplateLeaseSettings(URI orgRef);
   
   /**
    * Updates vApp template policy settings for organization.
    * 
    * <pre>
    * PUT /admin/org/{id}/settings/vAppTemplateLeaseSettings
    * </pre>
    * @param orgRef the reference for the admin org
    * @param newSettings the requested updated settings
    * @return the resultant settings
    */
   OrgVAppTemplateLeaseSettings updateVAppTemplateLeaseSettings(URI orgRef, 
         OrgVAppTemplateLeaseSettings newSettings);

}
