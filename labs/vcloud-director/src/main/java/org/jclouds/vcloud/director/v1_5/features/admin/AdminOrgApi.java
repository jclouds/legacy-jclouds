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
package org.jclouds.vcloud.director.v1_5.features.admin;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.vcloud.director.v1_5.domain.org.AdminOrg;
import org.jclouds.vcloud.director.v1_5.domain.org.OrgEmailSettings;
import org.jclouds.vcloud.director.v1_5.domain.org.OrgGeneralSettings;
import org.jclouds.vcloud.director.v1_5.domain.org.OrgLdapSettings;
import org.jclouds.vcloud.director.v1_5.domain.org.OrgLeaseSettings;
import org.jclouds.vcloud.director.v1_5.domain.org.OrgPasswordPolicySettings;
import org.jclouds.vcloud.director.v1_5.domain.org.OrgSettings;
import org.jclouds.vcloud.director.v1_5.domain.org.OrgVAppTemplateLeaseSettings;
import org.jclouds.vcloud.director.v1_5.features.MetadataApi;
import org.jclouds.vcloud.director.v1_5.features.OrgApi;
import org.jclouds.vcloud.director.v1_5.functions.href.OrgURNToAdminHref;

/**
 * Provides synchronous access to {@link Org} objects.
 * 
 * @see AdminOrgAsyncApi
 * @author danikov, Adrian Cole
 */
@Timeout(duration = 180, timeUnit = TimeUnit.SECONDS)
public interface AdminOrgApi extends OrgApi {

   /**
    * Retrieves an admin view of an organization. The organization might be enabled or disabled. If
    * enabled, the organization allows login and all other operations.
    * 
    * <pre>
    * GET /admin/org/{id}
    * </pre>
    * 
    * @param orgUrn
    *           the reference for the admin org
    * @return the admin org
    */
   @Override
   AdminOrg get(String orgUrn);

   @Override
   AdminOrg get(URI orgAdminHref);

   /**
    * Gets organizational settings for this organization.
    * 
    * <pre>
    * GET /admin/org/{id}/settings
    * </pre>
    * 
    * @param orgUrn
    *           the reference for the admin org
    * @return the settings
    */
   OrgSettings getSettings(String orgUrn);

   OrgSettings getSettings(URI orgAdminHref);

   /**
    * Updates organizational settings for this organization.
    * 
    * <pre>
    * PUT /admin/org/{id}/settings
    * </pre>
    * 
    * @param orgUrn
    *           the reference for the admin org
    * @param newSettings
    *           the requested edited settings
    * @return the resultant settings
    */
   OrgSettings editSettings(String orgUrn, OrgSettings newSettings);

   OrgSettings editSettings(URI orgAdminHref, OrgSettings newSettings);

   /**
    * Retrieves email settings for an organization.
    * 
    * <pre>
    * GET /admin/org/{id}/settings/email
    * </pre>
    * 
    * @param orgUrn
    *           the reference for the admin org
    * @return the email settings
    */
   OrgEmailSettings getEmailSettings(String orgUrn);

   OrgEmailSettings getEmailSettings(URI orgAdminHref);

   /**
    * Updates email policy settings for organization.
    * 
    * <pre>
    * PUT /admin/org/{id}/settings/email
    * </pre>
    * 
    * @param orgUrn
    *           the reference for the admin org
    * @param newSettings
    *           the requested edited settings
    * @return the resultant settings
    */
   OrgEmailSettings editEmailSettings(String orgUrn, OrgEmailSettings newSettings);

   OrgEmailSettings editEmailSettings(URI orgAdminHref, OrgEmailSettings newSettings);

   /**
    * Gets general organization settings.
    * 
    * <pre>
    * GET /admin/org/{id}/settings/general
    * </pre>
    * 
    * @param orgUrn
    *           the reference for the admin org
    * @return the lease settings
    */
   OrgGeneralSettings getGeneralSettings(String orgUrn);

   OrgGeneralSettings getGeneralSettings(URI orgAdminHref);

   /**
    * Updates general organization settings.
    * 
    * <pre>
    * PUT /admin/org/{id}/settings/general
    * </pre>
    * 
    * @param orgUrn
    *           the reference for the admin org
    * @param newSettings
    *           the requested edited settings
    * @return the resultant settings
    */
   OrgGeneralSettings editGeneralSettings(String orgUrn, OrgGeneralSettings newSettings);

   OrgGeneralSettings editGeneralSettings(URI orgAdminHref, OrgGeneralSettings newSettings);

   /**
    * Retrieves LDAP settings for an organization.
    * 
    * <pre>
    * GET /admin/org/{id}/settings/ldap
    * </pre>
    * 
    * @param orgUrn
    *           the reference for the admin org
    * @return the ldap settings
    */
   OrgLdapSettings getLdapSettings(String orgUrn);

   OrgLdapSettings getLdapSettings(URI orgAdminHref);

   /**
    * Retrieves password policy settings for an organization.
    * 
    * <pre>
    * GET /admin/org/{id}/settings/passwordPolicy
    * </pre>
    * 
    * @param orgUrn
    *           the reference for the admin org
    * @return the lease settings
    */
   OrgPasswordPolicySettings getPasswordPolicy(String orgUrn);

   OrgPasswordPolicySettings getPasswordPolicy(URI orgAdminHref);

   /**
    * Updates password policy settings for organization.
    * 
    * <pre>
    * PUT /admin/org/{id}/settings/passwordPolicy
    * </pre>
    * 
    * @param orgUrn
    *           the reference for the admin org
    * @param newSettings
    *           the requested edited settings
    * @return the resultant settings
    */
   OrgPasswordPolicySettings editPasswordPolicy(String orgUrn, OrgPasswordPolicySettings newSettings);

   OrgPasswordPolicySettings editPasswordPolicy(URI orgAdminHref, OrgPasswordPolicySettings newSettings);

   /**
    * Gets organization resource cleanup settings on the level of vApp.
    * 
    * <pre>
    * GET /admin/org/{id}/settings/vAppLeaseSettings
    * </pre>
    * 
    * @param orgUrn
    *           the reference for the admin org
    * @return the lease settings
    */
   OrgLeaseSettings getVAppLeaseSettings(String orgUrn);

   OrgLeaseSettings getVAppLeaseSettings(URI orgAdminHref);

   /**
    * Updates organization resource cleanup settings on the level of vApp.
    * 
    * <pre>
    * PUT /admin/org/{id}/settings/vAppLeaseSettings
    * </pre>
    * 
    * @param orgUrn
    *           the reference for the admin org
    * @param newSettings
    *           the requested edited settings
    * @return the resultant settings
    */
   OrgLeaseSettings editVAppLeaseSettings(String orgUrn, OrgLeaseSettings newSettings);

   OrgLeaseSettings editVAppLeaseSettings(URI orgAdminHref, OrgLeaseSettings newSettings);

   /**
    * Retrieves expiration and storage policy for vApp templates in an organization.
    * 
    * <pre>
    * GET /admin/org/{id}/settings/vAppTemplateLeaseSettings
    * </pre>
    * 
    * @param orgUrn
    *           the reference for the admin org
    * @return the lease settings
    */
   OrgVAppTemplateLeaseSettings getVAppTemplateLeaseSettings(String orgUrn);

   OrgVAppTemplateLeaseSettings getVAppTemplateLeaseSettings(URI orgAdminHref);

   /**
    * Updates vApp template policy settings for organization.
    * 
    * <pre>
    * PUT /admin/org/{id}/settings/vAppTemplateLeaseSettings
    * </pre>
    * 
    * @param orgUrn
    *           the reference for the admin org
    * @param newSettings
    *           the requested edited settings
    * @return the resultant settings
    */
   OrgVAppTemplateLeaseSettings editVAppTemplateLeaseSettings(String orgUrn, OrgVAppTemplateLeaseSettings newSettings);

   OrgVAppTemplateLeaseSettings editVAppTemplateLeaseSettings(URI orgAdminHref, OrgVAppTemplateLeaseSettings newSettings);

   /**
    * @return synchronous access to admin {@link MetadataApi.Writeable} features
    */
   @Override
   @Delegate
   MetadataApi.Writeable getMetadataApi(@EndpointParam(parser = OrgURNToAdminHref.class) String orgUrn);

   @Override
   @Delegate
   MetadataApi.Writeable getMetadataApi(@EndpointParam URI orgAdminHref);

}
