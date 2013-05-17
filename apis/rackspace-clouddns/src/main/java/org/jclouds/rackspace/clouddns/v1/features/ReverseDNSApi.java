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

import java.net.URI;
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
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.EmptyPagedIterableOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.Fallbacks.VoidOnNotFoundOr404;
import org.jclouds.collect.PagedIterable;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.rackspace.clouddns.v1.CloudDNSApi;
import org.jclouds.rackspace.clouddns.v1.binders.CreateReverseDNSToJSON;
import org.jclouds.rackspace.clouddns.v1.binders.UpdateReverseDNSToJSON;
import org.jclouds.rackspace.clouddns.v1.config.CloudDNS;
import org.jclouds.rackspace.clouddns.v1.domain.Job;
import org.jclouds.rackspace.clouddns.v1.domain.Record;
import org.jclouds.rackspace.clouddns.v1.domain.RecordDetail;
import org.jclouds.rackspace.clouddns.v1.functions.ParseJob;
import org.jclouds.rackspace.clouddns.v1.functions.ParseRecord;
import org.jclouds.rackspace.clouddns.v1.functions.ParseRecords;
import org.jclouds.rackspace.clouddns.v1.functions.RecordsToPagedIterable;
import org.jclouds.rackspace.clouddns.v1.predicates.JobPredicates;
import org.jclouds.rest.annotations.Endpoint;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.Transform;

/**
 * Cloud DNS supports the management of reverse DNS (PTR) records for Rackspace Cloud devices such as Cloud Load
 * Balancers and Cloud Servers (both first generation and next generation). In order to manage the PTR records for
 * Rackspace Cloud devices, the service as well as the device resource URI must be specified along with record details.
 * 
 * @author Everett Toews
 */
@Endpoint(CloudDNS.class)
@RequestFilters(AuthenticateRequest.class)
public interface ReverseDNSApi {
   /**
    * List all of the Reverse DNS (PTR) records for a device.
    */
   @Named("rdns:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @ResponseParser(ParseRecords.class)
   @Transform(RecordsToPagedIterable.class)
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   @Path("/rdns/{serviceName}")
   PagedIterable<RecordDetail> list(
         @QueryParam("href") URI deviceURI);

   /**
    * List all of the Reverse DNS (PTR) records for a device.
    */
   @Named("rdns:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @ResponseParser(ParseRecord.class)
   @Fallback(NullOnNotFoundOr404.class)
   @Path("/rdns/{serviceName}/{recordId}")
   RecordDetail get(
         @QueryParam("href") URI deviceURI,
         @PathParam("recordId") String recordId);

   /**
    * Create Reverse DNS (PTR) records for a device.
    * </p>
    * To wait for this call to complete use {@link JobPredicates#awaitComplete(CloudDNSApi, Job)}.
    */
   @Named("rdns:create")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_JSON)
   @ResponseParser(ParseJob.class)
   @MapBinder(CreateReverseDNSToJSON.class)
   @Path("/rdns")
   Job<Set<RecordDetail>> create(
         @PayloadParam("href") URI deviceURI,
         @PayloadParam("records") Iterable<Record> records);

   /**
    * Update Reverse DNS (PTR) records for a device.
    * </p>
    * To wait for this call to complete use {@link JobPredicates#awaitComplete(CloudDNSApi, Job)}.
    */
   @Named("rdns:update")
   @PUT
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_JSON)
   @ResponseParser(ParseJob.class)
   @MapBinder(UpdateReverseDNSToJSON.class)
   @Path("/rdns")
   Job<Void> update(
         @PayloadParam("href") URI deviceURI,
         @PayloadParam("idsToRecords") Map<String, Record> idsToRecords);

   /**
    * Delete the Reverse DNS (PTR) record with the specified IP address for a device.
    * </p>
    * To wait for this call to complete use {@link JobPredicates#awaitComplete(CloudDNSApi, Job)}.
    */
   @Named("rdns:delete")
   @DELETE
   @Fallback(VoidOnNotFoundOr404.class)
   @ResponseParser(ParseJob.class)
   @Path("/rdns/{serviceName}")
   @Consumes("*/*")
   Job<Void> delete(@QueryParam("href") URI deviceURI, @QueryParam("ip") String ipAddress);

   /**
    * Delete all Reverse DNS (PTR) records for a device.
    * </p>
    * To wait for this call to complete use {@link JobPredicates#awaitComplete(CloudDNSApi, Job)}.
    */
   @Named("rdns:delete")
   @DELETE
   @Fallback(VoidOnNotFoundOr404.class)
   @ResponseParser(ParseJob.class)
   @Path("/rdns/{serviceName}")
   @Consumes("*/*")
   Job<Void> deleteAll(@QueryParam("href") URI deviceURI);
}
