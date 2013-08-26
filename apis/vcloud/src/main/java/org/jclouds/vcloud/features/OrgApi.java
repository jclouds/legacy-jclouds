/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.vcloud.features;

import static org.jclouds.vcloud.VCloudMediaType.ORG_XML;

import java.net.URI;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;

import org.jclouds.Fallbacks;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.rest.annotations.Endpoint;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.vcloud.VCloudMediaType;
import org.jclouds.vcloud.domain.Org;
import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.endpoints.OrgList;
import org.jclouds.vcloud.filters.AddVCloudAuthorizationAndCookieToRequest;
import org.jclouds.vcloud.functions.OrgNameToEndpoint;
import org.jclouds.vcloud.xml.OrgHandler;
import org.jclouds.vcloud.xml.OrgListHandler;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides access to Org functionality in vCloud
 * <p/>
 * 
 * @author Adrian Cole
 */
@RequestFilters(AddVCloudAuthorizationAndCookieToRequest.class)
public interface OrgApi {

   /**
    * The response to a login request includes a list of the organizations to which the
    * authenticated user has access.
    * 
    * @return organizations indexed by name
    */
   @GET
   @Endpoint(OrgList.class)
   @XMLResponseParser(OrgListHandler.class)
   @Consumes(VCloudMediaType.ORGLIST_XML)
   Map<String, ReferenceType> listOrgs();

   @GET
   @XMLResponseParser(OrgHandler.class)
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   @Consumes(ORG_XML)
   Org getOrg(@EndpointParam URI orgId);

   /**
    * This call returns a list of all vCloud Data Centers (vdcs), catalogs, and task lists within
    * the organization.
    * 
    * @param name
    *           organization name, or null for the default
    * @throws NoSuchElementException
    *            if you specified an org name that isn't present
    */
   @GET
   @XMLResponseParser(OrgHandler.class)
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   @Consumes(ORG_XML)
   Org findOrgNamed(@Nullable @EndpointParam(parser = OrgNameToEndpoint.class) String orgName);

}
