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

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.JAXBResponseParser;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.binders.BindToXMLPayload;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;
import org.jclouds.vcloud.director.v1_5.domain.AdminOrg;
import org.jclouds.vcloud.director.v1_5.domain.OrgEmailSettings;
import org.jclouds.vcloud.director.v1_5.domain.OrgGeneralSettings;
import org.jclouds.vcloud.director.v1_5.domain.OrgLdapSettings;
import org.jclouds.vcloud.director.v1_5.domain.OrgLeaseSettings;
import org.jclouds.vcloud.director.v1_5.domain.OrgPasswordPolicySettings;
import org.jclouds.vcloud.director.v1_5.domain.OrgSettings;
import org.jclouds.vcloud.director.v1_5.domain.OrgVAppTemplateLeaseSettings;
import org.jclouds.vcloud.director.v1_5.filters.AddVCloudAuthorizationToRequest;
import org.jclouds.vcloud.director.v1_5.functions.ThrowVCloudErrorOn4xx;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * @see GroupClient
 * @author danikov
 */
@RequestFilters(AddVCloudAuthorizationToRequest.class)
public interface AdminOrgAsyncClient extends OrgAsyncClient {
   
   /**
    * @see AdminOrgClient#getOrg(URI)
    */
   @GET
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ThrowVCloudErrorOn4xx.class)
   ListenableFuture<AdminOrg> getOrg(
         @EndpointParam URI orgRef);
 
   /**
    * @see AdminOrgClient#getSettings(URI)
    */
   @GET
   @Path("/settings")
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ThrowVCloudErrorOn4xx.class)
   ListenableFuture<OrgSettings> getSettings(
         @EndpointParam URI orgRef);

   /**
    * @see AdminOrgClient#updateSettings(URI, OrgSettings)
    */
   @PUT
   @Path("/settings")
   @Consumes(VCloudDirectorMediaType.ORG_SETTINGS)
   @Produces(VCloudDirectorMediaType.ORG_SETTINGS)
   @JAXBResponseParser
   @ExceptionParser(ThrowVCloudErrorOn4xx.class)
   ListenableFuture<OrgSettings> updateSettings(
         @EndpointParam URI orgRef, 
         @BinderParam(BindToXMLPayload.class) OrgSettings settings);
 
   /**
    * @see AdminOrgClient#getEmailSettings(URI)
    */
   @GET
   @Path("/settings/email")
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ThrowVCloudErrorOn4xx.class)
   ListenableFuture<OrgEmailSettings> getEmailSettings(
         @EndpointParam URI orgRef);

   /**
    * @see AdminOrgClient#updateEmailSettings(URI, OrgEmailSettings)
    */
   @PUT
   @Path("/settings/email")
   @Consumes(VCloudDirectorMediaType.ORG_EMAIL_SETTINGS)
   @Produces(VCloudDirectorMediaType.ORG_EMAIL_SETTINGS)
   @JAXBResponseParser
   @ExceptionParser(ThrowVCloudErrorOn4xx.class)
   ListenableFuture<OrgEmailSettings> updateEmailSettings(
         @EndpointParam URI orgRef, 
         @BinderParam(BindToXMLPayload.class) OrgEmailSettings settings);
 
   /**
    * @see AdminOrgClient#getGeneralSettings(URI)
    */
   @GET
   @Path("/settings/general")
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ThrowVCloudErrorOn4xx.class)
   ListenableFuture<OrgGeneralSettings> getGeneralSettings(
         @EndpointParam URI orgRef);

   /**
    * @see AdminOrgClient#updateGeneralSettings(URI, OrgGeneralSettings)
    */
   @PUT
   @Path("/settings/general")
   @Consumes(VCloudDirectorMediaType.ORG_GENERAL_SETTINGS)
   @Produces(VCloudDirectorMediaType.ORG_GENERAL_SETTINGS)
   @JAXBResponseParser
   @ExceptionParser(ThrowVCloudErrorOn4xx.class)
   ListenableFuture<OrgGeneralSettings> updateGeneralSettings(
         @EndpointParam URI orgRef, 
         @BinderParam(BindToXMLPayload.class) OrgGeneralSettings settings);
   
   /**
    * @see AdminOrgClient#getPasswordPolicy(URI)
    */
   @GET
   @Path("/settings/ldap")
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ThrowVCloudErrorOn4xx.class)
   ListenableFuture<OrgLdapSettings> getLdapSettings(@EndpointParam URI orgRef);
 
   /**
    * @see AdminOrgClient#getPasswordPolicy(URI)
    */
   @GET
   @Path("/settings/passwordPolicy")
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ThrowVCloudErrorOn4xx.class)
   ListenableFuture<OrgPasswordPolicySettings> getPasswordPolicy(
         @EndpointParam URI orgRef);

   /**
    * @see AdminOrgClient#updatePasswordPolicy(URI, OrgPasswordPolicySettings)
    */
   @PUT
   @Path("/settings/passwordPolicy")
   @Consumes(VCloudDirectorMediaType.ORG_PASSWORD_POLICY_SETTINGS)
   @Produces(VCloudDirectorMediaType.ORG_PASSWORD_POLICY_SETTINGS)
   @JAXBResponseParser
   @ExceptionParser(ThrowVCloudErrorOn4xx.class)
   ListenableFuture<OrgPasswordPolicySettings> updatePasswordPolicy(
         @EndpointParam URI orgRef, 
         @BinderParam(BindToXMLPayload.class) OrgPasswordPolicySettings settings);
   
   /**
    * @see AdminOrgClient#getVAppLeaseSettings(URI)
    */
   @GET
   @Path("/settings/vAppLeaseSettings")
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ThrowVCloudErrorOn4xx.class)
   ListenableFuture<OrgLeaseSettings> getVAppLeaseSettings(
         @EndpointParam URI orgRef);

   /**
    * @see AdminOrgClient#updateVAppLeaseSettings(URI, OrgVAppLeaseSettings)
    */
   @PUT
   @Path("/settings/vAppLeaseSettings")
   @Consumes(VCloudDirectorMediaType.ORG_LEASE_SETTINGS)
   @Produces(VCloudDirectorMediaType.ORG_LEASE_SETTINGS)
   @JAXBResponseParser
   @ExceptionParser(ThrowVCloudErrorOn4xx.class)
   ListenableFuture<OrgLeaseSettings> updateVAppLeaseSettings(
         @EndpointParam URI orgRef, 
         @BinderParam(BindToXMLPayload.class) OrgLeaseSettings settings);
 
   /**
    * @see AdminOrgClient#getVAppTemplateLeaseSettings(URI)
    */
   @GET
   @Path("/settings/vAppTemplateLeaseSettings")
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ThrowVCloudErrorOn4xx.class)
   ListenableFuture<OrgVAppTemplateLeaseSettings> getVAppTemplateLeaseSettings(
         @EndpointParam URI orgRef);

   /**
    * @see AdminOrgClient#updateVAppTemplateLeaseSettings(URI, OrgVAppTemplateLeaseSettings)
    */
   @PUT
   @Path("/settings/vAppTemplateLeaseSettings")
   @Consumes(VCloudDirectorMediaType.ORG_VAPP_TEMPLATE_LEASE_SETTINGS)
   @Produces(VCloudDirectorMediaType.ORG_VAPP_TEMPLATE_LEASE_SETTINGS)
   @JAXBResponseParser
   @ExceptionParser(ThrowVCloudErrorOn4xx.class)
   ListenableFuture<OrgVAppTemplateLeaseSettings> updateVAppTemplateLeaseSettings(
         @EndpointParam URI orgRef, 
         @BinderParam(BindToXMLPayload.class) OrgVAppTemplateLeaseSettings settings);
 
}
