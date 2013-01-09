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

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.JAXBResponseParser;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.binders.BindToXMLPayload;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;
import org.jclouds.vcloud.director.v1_5.domain.org.AdminOrg;
import org.jclouds.vcloud.director.v1_5.domain.org.OrgEmailSettings;
import org.jclouds.vcloud.director.v1_5.domain.org.OrgGeneralSettings;
import org.jclouds.vcloud.director.v1_5.domain.org.OrgLdapSettings;
import org.jclouds.vcloud.director.v1_5.domain.org.OrgLeaseSettings;
import org.jclouds.vcloud.director.v1_5.domain.org.OrgPasswordPolicySettings;
import org.jclouds.vcloud.director.v1_5.domain.org.OrgSettings;
import org.jclouds.vcloud.director.v1_5.domain.org.OrgVAppTemplateLeaseSettings;
import org.jclouds.vcloud.director.v1_5.features.OrgAsyncApi;
import org.jclouds.vcloud.director.v1_5.filters.AddVCloudAuthorizationAndCookieToRequest;
import org.jclouds.vcloud.director.v1_5.functions.URNToAdminHref;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * @see AdminOrgApi
 * @author danikov, Adrian Cole
 */
@RequestFilters(AddVCloudAuthorizationAndCookieToRequest.class)
public interface AdminOrgAsyncApi extends OrgAsyncApi {

   /**
    * @see AdminOrgApi#get(String)
    */
   @Override
   @GET
   @Consumes
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<AdminOrg> get(@EndpointParam(parser = URNToAdminHref.class) String orgUrn);

   /**
    * @see AdminOrgApi#get(URI)
    */
   @Override
   @GET
   @Consumes
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<AdminOrg> get(@EndpointParam URI adminOrgHref);

   /**
    * @see AdminOrgApi#getSettings(String)
    */
   @GET
   @Path("/settings")
   @Consumes
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<OrgSettings> getSettings(@EndpointParam(parser = URNToAdminHref.class) String orgUrn);

   /**
    * @see AdminOrgApi#getSettings(URI)
    */
   @GET
   @Path("/settings")
   @Consumes
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<OrgSettings> getSettings(@EndpointParam URI adminOrgHref);

   /**
    * @see AdminOrgApi#editSettings(String, OrgSettings)
    */
   @PUT
   @Path("/settings")
   @Consumes(VCloudDirectorMediaType.ORG_SETTINGS)
   @Produces(VCloudDirectorMediaType.ORG_SETTINGS)
   @JAXBResponseParser
   ListenableFuture<OrgSettings> editSettings(@EndpointParam(parser = URNToAdminHref.class) String orgUrn,
            @BinderParam(BindToXMLPayload.class) OrgSettings settings);

   /**
    * @see AdminOrgApi#editSettings(URI, OrgSettings)
    */
   @PUT
   @Path("/settings")
   @Consumes(VCloudDirectorMediaType.ORG_SETTINGS)
   @Produces(VCloudDirectorMediaType.ORG_SETTINGS)
   @JAXBResponseParser
   ListenableFuture<OrgSettings> editSettings(@EndpointParam URI adminOrgHref,
            @BinderParam(BindToXMLPayload.class) OrgSettings settings);

   /**
    * @see AdminOrgApi#getEmailSettings(String)
    */
   @GET
   @Path("/settings/email")
   @Consumes
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<OrgEmailSettings> getEmailSettings(@EndpointParam(parser = URNToAdminHref.class) String orgUrn);

   /**
    * @see AdminOrgApi#getEmailSettings(URI)
    */
   @GET
   @Path("/settings/email")
   @Consumes
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<OrgEmailSettings> getEmailSettings(@EndpointParam URI adminOrgHref);

   /**
    * @see AdminOrgApi#editEmailSettings(String, OrgEmailSettings)
    */
   @PUT
   @Path("/settings/email")
   @Consumes(VCloudDirectorMediaType.ORG_EMAIL_SETTINGS)
   @Produces(VCloudDirectorMediaType.ORG_EMAIL_SETTINGS)
   @JAXBResponseParser
   ListenableFuture<OrgEmailSettings> editEmailSettings(
            @EndpointParam(parser = URNToAdminHref.class) String orgUrn,
            @BinderParam(BindToXMLPayload.class) OrgEmailSettings settings);

   /**
    * @see AdminOrgApi#editEmailSettings(URI, OrgEmailSettings)
    */
   @PUT
   @Path("/settings/email")
   @Consumes(VCloudDirectorMediaType.ORG_EMAIL_SETTINGS)
   @Produces(VCloudDirectorMediaType.ORG_EMAIL_SETTINGS)
   @JAXBResponseParser
   ListenableFuture<OrgEmailSettings> editEmailSettings(@EndpointParam URI adminOrgHref,
            @BinderParam(BindToXMLPayload.class) OrgEmailSettings settings);

   /**
    * @see AdminOrgApi#getGeneralSettings(String)
    */
   @GET
   @Path("/settings/general")
   @Consumes
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<OrgGeneralSettings> getGeneralSettings(
            @EndpointParam(parser = URNToAdminHref.class) String orgUrn);

   /**
    * @see AdminOrgApi#getGeneralSettings(URI)
    */
   @GET
   @Path("/settings/general")
   @Consumes
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<OrgGeneralSettings> getGeneralSettings(@EndpointParam URI adminOrgHref);

   /**
    * @see AdminOrgApi#editGeneralSettings(String, OrgGeneralSettings)
    */
   @PUT
   @Path("/settings/general")
   @Consumes(VCloudDirectorMediaType.ORG_GENERAL_SETTINGS)
   @Produces(VCloudDirectorMediaType.ORG_GENERAL_SETTINGS)
   @JAXBResponseParser
   ListenableFuture<OrgGeneralSettings> editGeneralSettings(
            @EndpointParam(parser = URNToAdminHref.class) String orgUrn,
            @BinderParam(BindToXMLPayload.class) OrgGeneralSettings settings);

   /**
    * @see AdminOrgApi#editGeneralSettings(URI, OrgGeneralSettings)
    */
   @PUT
   @Path("/settings/general")
   @Consumes(VCloudDirectorMediaType.ORG_GENERAL_SETTINGS)
   @Produces(VCloudDirectorMediaType.ORG_GENERAL_SETTINGS)
   @JAXBResponseParser
   ListenableFuture<OrgGeneralSettings> editGeneralSettings(@EndpointParam URI adminOrgHref,
            @BinderParam(BindToXMLPayload.class) OrgGeneralSettings settings);

   /**
    * @see AdminOrgApi#getPasswordPolicy(String)
    */
   @GET
   @Path("/settings/ldap")
   @Consumes
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<OrgLdapSettings> getLdapSettings(@EndpointParam(parser = URNToAdminHref.class) String orgUrn);

   /**
    * @see AdminOrgApi#getPasswordPolicy(URI)
    */
   @GET
   @Path("/settings/ldap")
   @Consumes
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<OrgLdapSettings> getLdapSettings(@EndpointParam URI adminOrgHref);

   /**
    * @see AdminOrgApi#getPasswordPolicy(String)
    */
   @GET
   @Path("/settings/passwordPolicy")
   @Consumes
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<OrgPasswordPolicySettings> getPasswordPolicy(
            @EndpointParam(parser = URNToAdminHref.class) String orgUrn);

   /**
    * @see AdminOrgApi#getPasswordPolicy(URI)
    */
   @GET
   @Path("/settings/passwordPolicy")
   @Consumes
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<OrgPasswordPolicySettings> getPasswordPolicy(@EndpointParam URI adminOrgHref);

   /**
    * @see AdminOrgApi#editPasswordPolicy(String, OrgPasswordPolicySettings)
    */
   @PUT
   @Path("/settings/passwordPolicy")
   @Consumes(VCloudDirectorMediaType.ORG_PASSWORD_POLICY_SETTINGS)
   @Produces(VCloudDirectorMediaType.ORG_PASSWORD_POLICY_SETTINGS)
   @JAXBResponseParser
   ListenableFuture<OrgPasswordPolicySettings> editPasswordPolicy(
            @EndpointParam(parser = URNToAdminHref.class) String orgUrn,
            @BinderParam(BindToXMLPayload.class) OrgPasswordPolicySettings settings);

   /**
    * @see AdminOrgApi#editPasswordPolicy(URI, OrgPasswordPolicySettings)
    */
   @PUT
   @Path("/settings/passwordPolicy")
   @Consumes(VCloudDirectorMediaType.ORG_PASSWORD_POLICY_SETTINGS)
   @Produces(VCloudDirectorMediaType.ORG_PASSWORD_POLICY_SETTINGS)
   @JAXBResponseParser
   ListenableFuture<OrgPasswordPolicySettings> editPasswordPolicy(@EndpointParam URI adminOrgHref,
            @BinderParam(BindToXMLPayload.class) OrgPasswordPolicySettings settings);

   /**
    * @see AdminOrgApi#getVAppLeaseSettings(String)
    */
   @GET
   @Path("/settings/vAppLeaseSettings")
   @Consumes
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<OrgLeaseSettings> getVAppLeaseSettings(
            @EndpointParam(parser = URNToAdminHref.class) String orgUrn);

   /**
    * @see AdminOrgApi#getVAppLeaseSettings(URI)
    */
   @GET
   @Path("/settings/vAppLeaseSettings")
   @Consumes
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<OrgLeaseSettings> getVAppLeaseSettings(@EndpointParam URI adminOrgHref);

   /**
    * @see AdminOrgApi#editVAppLeaseSettings(String, OrgVAppLeaseSettings)
    */
   @PUT
   @Path("/settings/vAppLeaseSettings")
   @Consumes(VCloudDirectorMediaType.ORG_LEASE_SETTINGS)
   @Produces(VCloudDirectorMediaType.ORG_LEASE_SETTINGS)
   @JAXBResponseParser
   ListenableFuture<OrgLeaseSettings> editVAppLeaseSettings(
            @EndpointParam(parser = URNToAdminHref.class) String orgUrn,
            @BinderParam(BindToXMLPayload.class) OrgLeaseSettings settings);

   /**
    * @see AdminOrgApi#editVAppLeaseSettings(URI, OrgVAppLeaseSettings)
    */
   @PUT
   @Path("/settings/vAppLeaseSettings")
   @Consumes(VCloudDirectorMediaType.ORG_LEASE_SETTINGS)
   @Produces(VCloudDirectorMediaType.ORG_LEASE_SETTINGS)
   @JAXBResponseParser
   ListenableFuture<OrgLeaseSettings> editVAppLeaseSettings(@EndpointParam URI adminOrgHref,
            @BinderParam(BindToXMLPayload.class) OrgLeaseSettings settings);

   /**
    * @see AdminOrgApi#getVAppTemplateLeaseSettings(String)
    */
   @GET
   @Path("/settings/vAppTemplateLeaseSettings")
   @Consumes
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<OrgVAppTemplateLeaseSettings> getVAppTemplateLeaseSettings(
            @EndpointParam(parser = URNToAdminHref.class) String orgUrn);

   /**
    * @see AdminOrgApi#getVAppTemplateLeaseSettings(URI)
    */
   @GET
   @Path("/settings/vAppTemplateLeaseSettings")
   @Consumes
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<OrgVAppTemplateLeaseSettings> getVAppTemplateLeaseSettings(@EndpointParam URI adminOrgHref);

   /**
    * @see AdminOrgApi#editVAppTemplateLeaseSettings(String, OrgVAppTemplateLeaseSettings)
    */
   @PUT
   @Path("/settings/vAppTemplateLeaseSettings")
   @Consumes(VCloudDirectorMediaType.ORG_VAPP_TEMPLATE_LEASE_SETTINGS)
   @Produces(VCloudDirectorMediaType.ORG_VAPP_TEMPLATE_LEASE_SETTINGS)
   @JAXBResponseParser
   ListenableFuture<OrgVAppTemplateLeaseSettings> editVAppTemplateLeaseSettings(
            @EndpointParam(parser = URNToAdminHref.class) String orgUrn,
            @BinderParam(BindToXMLPayload.class) OrgVAppTemplateLeaseSettings settings);

   /**
    * @see AdminOrgApi#editVAppTemplateLeaseSettings(URI, OrgVAppTemplateLeaseSettings)
    */
   @PUT
   @Path("/settings/vAppTemplateLeaseSettings")
   @Consumes(VCloudDirectorMediaType.ORG_VAPP_TEMPLATE_LEASE_SETTINGS)
   @Produces(VCloudDirectorMediaType.ORG_VAPP_TEMPLATE_LEASE_SETTINGS)
   @JAXBResponseParser
   ListenableFuture<OrgVAppTemplateLeaseSettings> editVAppTemplateLeaseSettings(@EndpointParam URI adminOrgHref,
            @BinderParam(BindToXMLPayload.class) OrgVAppTemplateLeaseSettings settings);
}
