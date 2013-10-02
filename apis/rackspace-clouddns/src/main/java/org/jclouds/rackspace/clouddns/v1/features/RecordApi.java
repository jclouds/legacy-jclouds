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
package org.jclouds.rackspace.clouddns.v1.features;

import java.util.Map;
import java.util.Set;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.EmptyPagedIterableOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.Fallbacks.VoidOnNotFoundOr404;
import org.jclouds.collect.PagedIterable;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.rackspace.clouddns.v1.CloudDNSApi;
import org.jclouds.rackspace.clouddns.v1.binders.UpdateRecordsToJSON;
import org.jclouds.rackspace.clouddns.v1.config.CloudDNS;
import org.jclouds.rackspace.clouddns.v1.domain.Job;
import org.jclouds.rackspace.clouddns.v1.domain.Record;
import org.jclouds.rackspace.clouddns.v1.domain.RecordDetail;
import org.jclouds.rackspace.clouddns.v1.functions.ParseJob;
import org.jclouds.rackspace.clouddns.v1.functions.ParseOnlyRecord;
import org.jclouds.rackspace.clouddns.v1.functions.ParseRecord;
import org.jclouds.rackspace.clouddns.v1.functions.ParseRecords;
import org.jclouds.rackspace.clouddns.v1.functions.RecordsToPagedIterable;
import org.jclouds.rackspace.clouddns.v1.predicates.JobPredicates;
import org.jclouds.rackspace.cloudidentity.v2_0.CloudIdentityFallbacks.EmptyPaginatedCollectionOnNotFoundOr404;
import org.jclouds.rackspace.cloudidentity.v2_0.domain.PaginatedCollection;
import org.jclouds.rackspace.cloudidentity.v2_0.options.PaginationOptions;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Endpoint;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.Transform;
import org.jclouds.rest.annotations.WrapWith;
import org.jclouds.rest.binders.BindToJsonPayload;

/**
 * @author Everett Toews
 */
@Endpoint(CloudDNS.class)
@RequestFilters(AuthenticateRequest.class)
public interface RecordApi {

   /**
    * Create Records for a Domain or Subdomain.
    * </p>
    * See <a href="http://docs.rackspace.com/cdns/api/v1.0/cdns-devguide/content/supported_record_types.html">
    * Supported Record Types</a>
    * </p>
    * To wait for this call to complete use {@link JobPredicates#awaitComplete(CloudDNSApi, Job)}.
    */
   @Named("record:create")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @ResponseParser(ParseJob.class)
   @Path("/records")
   Job<Set<RecordDetail>> create(@WrapWith("records") Iterable<Record> createRecords);

   /**
    * This call lists all records configured for the specified domain.
    */
   @Named("record:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @ResponseParser(ParseRecords.class)
   @Transform(RecordsToPagedIterable.class)
   @Path("/records")
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   PagedIterable<RecordDetail> list();

   /**
    * RecordDetails filtered by type.
    */
   @Named("record:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @ResponseParser(ParseRecords.class)
   @Transform(RecordsToPagedIterable.class)
   @Path("/records")
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   PagedIterable<RecordDetail> listByType(
         @QueryParam("type") String typeFilter);

   /**
    * RecordDetails filtered by type and data.
    */
   @Named("record:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @ResponseParser(ParseRecords.class)
   @Transform(RecordsToPagedIterable.class)
   @Path("/records")
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   PagedIterable<RecordDetail> listByTypeAndData(
         @QueryParam("type") String typeFilter,
         @QueryParam("data") String dataFilter);

   /**
    * RecordDetails filtered by name and type.
    */
   @Named("record:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @ResponseParser(ParseRecords.class)
   @Transform(RecordsToPagedIterable.class)
   @Path("/records")
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   PagedIterable<RecordDetail> listByNameAndType(
         @QueryParam("name") String nameFilter,
         @QueryParam("type") String typeFilter);

   /**
    * Use PaginationOptions to manually control the list of RecordDetail pages returned.
    */
   @Named("record:list")
   @GET
   @ResponseParser(ParseRecords.class)
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(EmptyPaginatedCollectionOnNotFoundOr404.class)
   @Path("/records")
   PaginatedCollection<RecordDetail> list(PaginationOptions options);

   /**
    * RecordDetails filtered by name and type and data.
    */
   @Named("record:get")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @ResponseParser(ParseOnlyRecord.class)
   @Path("/records")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   RecordDetail getByNameAndTypeAndData(
         @QueryParam("name") String nameFilter,
         @QueryParam("type") String typeFilter,
         @QueryParam("data") String dataFilter);

   /**
    * Get the details for the specified record in the specified domain.
    */
   @Named("record:get")
   @GET
   @ResponseParser(ParseRecord.class)
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/records/{recordId}")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   RecordDetail get(@PathParam("recordId") String recordId);

   /**
    * Update the configuration of the specified record in the specified domain.
    * </p>
    * To wait for this call to complete use {@link JobPredicates#awaitComplete(CloudDNSApi, Job)}.
    */
   @Named("record:update")
   @PUT
   @Consumes(MediaType.APPLICATION_JSON)
   @ResponseParser(ParseJob.class)
   @Path("/records/{recordId}")
   Job<Void> update(
         @PathParam("recordId") String recordId,
         @BinderParam(BindToJsonPayload.class) Record record);

   /**
    * Update the configuration of the specified records in the specified domain.
    * </p>
    * To wait for this call to complete use {@link JobPredicates#awaitComplete(CloudDNSApi, Job)}.
    */
   @Named("record:update")
   @PUT
   @Consumes(MediaType.APPLICATION_JSON)
   @ResponseParser(ParseJob.class)
   @Path("/records")
   Job<Void> update(
         @BinderParam(UpdateRecordsToJSON.class) Map<String, Record> idsToRecords);

   /**
    * Delete the specified record in the specified domain.
    * </p>
    * To wait for this call to complete use {@link JobPredicates#awaitComplete(CloudDNSApi, Job)}.
    */
   @Named("record:delete")
   @DELETE
   @Fallback(VoidOnNotFoundOr404.class)
   @ResponseParser(ParseJob.class)
   @Path("/records/{recordId}")
   @Consumes("*/*")
   Job<Void> delete(@PathParam("recordId") String recordId);

   /**
    * Delete the specified records in the specified domain.
    * </p>
    * To wait for this call to complete use {@link JobPredicates#awaitComplete(CloudDNSApi, Job)}.
    */
   @Named("record:delete")
   @DELETE
   @Fallback(VoidOnNotFoundOr404.class)
   @ResponseParser(ParseJob.class)
   @Path("/records")
   @Consumes("*/*")
   Job<Void> delete(@QueryParam("id") Iterable<String> recordId);
}
