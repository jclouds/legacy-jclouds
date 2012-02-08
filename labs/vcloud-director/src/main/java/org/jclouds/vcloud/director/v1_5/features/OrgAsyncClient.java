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

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.JAXBResponseParser;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.vcloud.director.v1_5.domain.Metadata;
import org.jclouds.vcloud.director.v1_5.domain.MetadataEntry;
import org.jclouds.vcloud.director.v1_5.domain.Org;
import org.jclouds.vcloud.director.v1_5.domain.OrgList;
import org.jclouds.vcloud.director.v1_5.filters.AddVCloudAuthorizationToRequest;
import org.jclouds.vcloud.director.v1_5.functions.ThrowVCloudErrorOn4xxOrNull;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * @see OrgClient
 * @author Adrian Cole
 */
@RequestFilters(AddVCloudAuthorizationToRequest.class)
public interface OrgAsyncClient {

   /**
    * @see OrgClient#getOrgList
    */
   @GET
   @Path("/org")
   @Consumes
   @JAXBResponseParser
   ListenableFuture<OrgList> getOrgList();

   /**
    * @see OrgClient#getOrg
    */
   @GET
   @Path("/org/{id}")
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ThrowVCloudErrorOn4xxOrNull.class)
   ListenableFuture<Org> getOrg(@PathParam("id") String orgId);
   
   /**
    * @see OrgClient#getMetadata
    */
   @GET
   @Path("/org/{id}/metadata")
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ThrowVCloudErrorOn4xxOrNull.class)
   ListenableFuture<Metadata> getMetadata(@PathParam("id") String orgId);

   /**
    * @see OrgClient#getMetadataEntry
    */
   @GET
   @Path("/org/{id}/metadata/{key}")
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ThrowVCloudErrorOn4xxOrNull.class)
   ListenableFuture<MetadataEntry> getMetadataEntry(@PathParam("id") String orgId, @PathParam("key") String key);
}
