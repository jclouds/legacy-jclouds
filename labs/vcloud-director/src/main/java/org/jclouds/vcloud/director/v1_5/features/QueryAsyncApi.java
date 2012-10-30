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
import javax.ws.rs.QueryParam;

import org.jclouds.rest.annotations.JAXBResponseParser;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.vcloud.director.v1_5.domain.query.CatalogReferences;
import org.jclouds.vcloud.director.v1_5.domain.query.QueryList;
import org.jclouds.vcloud.director.v1_5.domain.query.QueryResultRecords;
import org.jclouds.vcloud.director.v1_5.domain.query.VAppReferences;
import org.jclouds.vcloud.director.v1_5.filters.AddVCloudAuthorizationAndCookieToRequest;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * @see QueryApi
 * @author grkvlt@apache.org
 */
@RequestFilters(AddVCloudAuthorizationAndCookieToRequest.class)
@SkipEncoding({ '=' })
public interface QueryAsyncApi {

   /**
    * REST API General queries handler.
    */
   @GET
   @Path("/query")
   @Consumes
   @JAXBResponseParser
   ListenableFuture<QueryList> queryList();

   @GET
   @Path("/query")
   @Consumes
   @JAXBResponseParser
   ListenableFuture<QueryResultRecords> queryAll(@QueryParam("type") String type);

   @GET
   @Path("/query")
   @Consumes
   @JAXBResponseParser
   ListenableFuture<QueryResultRecords> query(@QueryParam("type") String type, @QueryParam("filter") String filter);

   @GET
   @Path("/query")
   @Consumes
   @JAXBResponseParser
   ListenableFuture<QueryResultRecords> query(@QueryParam("page") Integer page, @QueryParam("pageSize") Integer pageSize,
         @QueryParam("format") String format, @QueryParam("type") String type, @QueryParam("filter") String filter);

   /**
    * Retrieves a list of {@link Catalog}s by using REST API general QueryHandler.
    */
   @GET
   @Path("/catalogs/query")
   @Consumes
   @JAXBResponseParser
   ListenableFuture<QueryResultRecords> catalogsQueryAll();

   @GET
   @Path("/catalogs/query")
   @Consumes
   @JAXBResponseParser
   ListenableFuture<QueryResultRecords> catalogsQuery(@QueryParam("filter") String filter);

   @GET
   @Path("/catalogs/query")
   @Consumes
   @JAXBResponseParser
   ListenableFuture<QueryResultRecords> catalogsQuery(@QueryParam("page") Integer page, @QueryParam("pageSize") Integer pageSize,
         @QueryParam("filter") String filter);

   @GET
   @Path("/catalogs/query")
   @Consumes
   @QueryParams(keys = { "format" }, values = { "references" })
   @JAXBResponseParser
   ListenableFuture<CatalogReferences> catalogReferencesQueryAll();

   @GET
   @Path("/catalogs/query")
   @Consumes
   @QueryParams(keys = { "format" }, values = { "references" })
   @JAXBResponseParser
   ListenableFuture<CatalogReferences> catalogReferencesQuery(@QueryParam("filter") String filter);

   @GET
   @Path("/catalogs/query")
   @Consumes
   @QueryParams(keys = { "format" }, values = { "references" })
   @JAXBResponseParser
   ListenableFuture<CatalogReferences> catalogReferencesQuery(@QueryParam("page") Integer page, @QueryParam("pageSize") Integer pageSize,
         @QueryParam("filter") String filter);

   @GET
   @Path("/vAppTemplates/query")
   @Consumes
   @JAXBResponseParser
   ListenableFuture<QueryResultRecords> vAppTemplatesQueryAll();

   @GET
   @Path("/vAppTemplates/query")
   @Consumes
   @JAXBResponseParser
   ListenableFuture<QueryResultRecords> vAppTemplatesQuery(@QueryParam("filter") String filter);

   /**
    * Retrieves a list of {@link VApp}s by using REST API general QueryHandler.
    */
   @GET
   @Path("/vApps/query")
   @Consumes
   @JAXBResponseParser
   ListenableFuture<QueryResultRecords> vAppsQueryAll();

   @GET
   @Path("/vApps/query")
   @Consumes
   @JAXBResponseParser
   ListenableFuture<QueryResultRecords> vAppsQuery(@QueryParam("filter") String filter);

   @GET
   @Path("/vApps/query")
   @Consumes
   @JAXBResponseParser
   ListenableFuture<QueryResultRecords> vAppsQuery(@QueryParam("page") Integer page, @QueryParam("pageSize") Integer pageSize,
         @QueryParam("filter") String filter);

   @GET
   @Path("/vApps/query")
   @Consumes
   @QueryParams(keys = { "format" }, values = { "references" })
   @JAXBResponseParser
   ListenableFuture<VAppReferences> vAppReferencesQueryAll();

   @GET
   @Path("/vApps/query")
   @Consumes
   @QueryParams(keys = { "format" }, values = { "references" })
   @JAXBResponseParser
   ListenableFuture<VAppReferences> vAppReferencesQuery(@QueryParam("filter") String filter);

   @GET
   @Path("/vApps/query")
   @Consumes
   @QueryParams(keys = { "format" }, values = { "references" })
   @JAXBResponseParser
   ListenableFuture<VAppReferences> vAppReferencesQuery(@QueryParam("page") Integer page, @QueryParam("pageSize") Integer pageSize,
         @QueryParam("filter") String filter);
   
   @GET
   @Path("/vms/query")
   @Consumes
   @JAXBResponseParser
   ListenableFuture<QueryResultRecords> vmsQueryAll();

   @GET
   @Path("/vms/query")
   @Consumes
   @JAXBResponseParser
   ListenableFuture<QueryResultRecords> vmsQuery(@QueryParam("filter") String filter);
   
   @GET
   @Path("/mediaList/query")
   @Consumes
   @JAXBResponseParser
   ListenableFuture<QueryResultRecords> mediaListQueryAll();

   @GET
   @Path("/mediaList/query")
   @Consumes
   @JAXBResponseParser
   ListenableFuture<QueryResultRecords> mediaListQuery(@QueryParam("filter") String filter);
}
