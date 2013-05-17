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

import java.util.Date;
import java.util.List;
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
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.rackspace.clouddns.v1.CloudDNSApi;
import org.jclouds.rackspace.clouddns.v1.binders.FormatAndContentsToJSON;
import org.jclouds.rackspace.clouddns.v1.binders.UpdateDomainsToJSON;
import org.jclouds.rackspace.clouddns.v1.config.CloudDNS;
import org.jclouds.rackspace.clouddns.v1.domain.CreateDomain;
import org.jclouds.rackspace.clouddns.v1.domain.Domain;
import org.jclouds.rackspace.clouddns.v1.domain.DomainChange;
import org.jclouds.rackspace.clouddns.v1.domain.Job;
import org.jclouds.rackspace.clouddns.v1.domain.Subdomain;
import org.jclouds.rackspace.clouddns.v1.domain.UpdateDomain;
import org.jclouds.rackspace.clouddns.v1.functions.DomainsToPagedIterable;
import org.jclouds.rackspace.clouddns.v1.functions.ParseDomain;
import org.jclouds.rackspace.clouddns.v1.functions.ParseDomains;
import org.jclouds.rackspace.clouddns.v1.functions.ParseJob;
import org.jclouds.rackspace.clouddns.v1.functions.ParseSubdomains;
import org.jclouds.rackspace.clouddns.v1.functions.SubdomainsToPagedIterable;
import org.jclouds.rackspace.clouddns.v1.predicates.JobPredicates;
import org.jclouds.rackspace.cloudidentity.v2_0.CloudIdentityFallbacks.EmptyPaginatedCollectionOnNotFoundOr404;
import org.jclouds.rackspace.cloudidentity.v2_0.domain.PaginatedCollection;
import org.jclouds.rackspace.cloudidentity.v2_0.functions.DateParser;
import org.jclouds.rackspace.cloudidentity.v2_0.options.PaginationOptions;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Endpoint;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.ParamParser;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.QueryParams;
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
public interface DomainApi {
   /**
    * Provisions one or more new DNS domains based on the configuration defined in CreateDomain. If the domain
    * creation cannot be fulfilled due to insufficient or invalid data, Job with an ERROR status will
    * be returned with information regarding the nature of the failure in the body of the Job. Failures in the
    * validation process are non-recoverable and require the caller to correct the cause of the failure.
    * This is an atomic operation: if there is a failure in creation of even a single record, the entire process
    * will fail.
    * </p>
    * When a domain is created, and no Time To Live (TTL) is specified, the SOA minTTL (3600 seconds) is used as the
    * default. When a record is added without a specified TTL, it will receive the domain TTL by default. When the
    * domain and/or record TTL is supplied by the user, either via a create or update call, the TTL values must be 300
    * seconds or more.
    * </p>
    * To wait for this call to complete use {@link JobPredicates#awaitComplete(CloudDNSApi, Job)}.
    */
   @Named("domain:create")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @ResponseParser(ParseJob.class)
   @Path("/domains")
   Job<Set<Domain>> create(@WrapWith("domains") Iterable<CreateDomain> createDomains);

   /**
    * The resulting list is flat, and does not break the domains down hierarchically by subdomain. All representative
    * domains are included in the list, even if a domain is conceptually a subdomain of another domain in the list.
    * Records are not included.
    */
   @Named("domain:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @ResponseParser(ParseDomains.class)
   @Transform(DomainsToPagedIterable.class)
   @Path("/domains")
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   PagedIterable<Domain> list();

   /**
    * Filtering the search to limit the results returned can be performed by using the nameFilter parameter. For
    * example, "hoola.com" matches hoola.com and similar names such as main.hoola.com and sub.hoola.com.
    * </p>
    * Filter criteria may consist of:
    * <ul>
    * <li>Any letter (A-Za-z)</li>
    * <li>Numbers (0-9)</li>
    * <li>Hyphen ("-")</li>
    * <li>1 to 63 characters</li>
    * </ul>
    * Filter criteria should not include any of the following characters: ' + , | ! " £ $ % & / ( ) = ? ^ * ç ° § ; : _
    * > ] [ @ à, é, ò
    */
   @Named("domain:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @ResponseParser(ParseDomains.class)
   @Transform(DomainsToPagedIterable.class)
   @Path("/domains")
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   PagedIterable<Domain> listWithFilterByNamesMatching(@QueryParam("name") String nameFilter);

   /**
    * The resulting list is flat, and does not break the domains down hierarchically by subdomain.
    */
   @Named("domain:list")
   @GET
   @ResponseParser(ParseDomains.class)
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(EmptyPaginatedCollectionOnNotFoundOr404.class)
   @Path("/domains")
   PaginatedCollection<Domain> list(PaginationOptions options);

   /**
    * List the subdomains of a domain.
    */
   @Named("domain:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @ResponseParser(ParseSubdomains.class)
   @Transform(SubdomainsToPagedIterable.class)
   @Path("/domains/{domainId}/subdomains")
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   @Nullable
   PagedIterable<Subdomain> listSubdomains(@PathParam("domainId") int domainId);

   /**
    * List the subdomains of a domain and manually control pagination.
    */
   @Named("domain:list")
   @GET
   @ResponseParser(ParseSubdomains.class)
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(EmptyPaginatedCollectionOnNotFoundOr404.class)
   @Path("/domains/{domainId}/subdomains")
   PaginatedCollection<Subdomain> listSubdomains(@PathParam("domainId") int domainId,
         PaginationOptions options);

   /**
    * Shows all changes to the specified domain since the specified date/time.
    */
   @Named("domain:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/domains/{id}/changes")
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   DomainChange listChanges(@PathParam("id") int id,
         @ParamParser(DateParser.class) @QueryParam("changes") Date since);

   /**
    * Get all information for a Domain, including records and subdomains.
    */
   @Named("domain:get")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("/domains/{id}")
   @QueryParams(keys = { "showRecords", "showSubdomains" }, values = { "true", "true" })
   @ResponseParser(ParseDomain.class)
   @Fallback(NullOnNotFoundOr404.class)
   @Nullable
   Domain get(@PathParam("id") int id);

   /**
    * This call modifies the domain attributes only. Records cannot be added, modified, or removed.
    * </p>
    * To wait for this call to complete use {@link JobPredicates#awaitComplete(CloudDNSApi, Job)}.
    * 
    * @see RecordApi
    */
   @Named("domain:update")
   @PUT
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_JSON)
   @ResponseParser(ParseJob.class)
   @Path("/domains/{id}")
   Job<Void> update(@PathParam("id") int id, @BinderParam(BindToJsonPayload.class) UpdateDomain updateDomain);

   /**
    * This call modifies the domain's TTL only.
    * </p>
    * To wait for this call to complete use {@link JobPredicates#awaitComplete(CloudDNSApi, Job)}.
    */
   @Named("domain:update")
   @PUT
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_JSON)
   @ResponseParser(ParseJob.class)
   @Path("/domains")
   @MapBinder(UpdateDomainsToJSON.class)
   Job<Void> updateTTL(@PayloadParam("ids") Iterable<Integer> ids, @PayloadParam("ttl") int ttl);

   /**
    * This call modifies the domain's email only.
    * </p>
    * To wait for this call to complete use {@link JobPredicates#awaitComplete(CloudDNSApi, Job)}.
    */
   @Named("domain:update")
   @PUT
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_JSON)
   @ResponseParser(ParseJob.class)
   @Path("/domains")
   @MapBinder(UpdateDomainsToJSON.class)
   Job<Void> updateEmail(@PayloadParam("ids") Iterable<Integer> ids, @PayloadParam("emailAddress") String email);

   /**
    * This call removes one or more specified domains from the account; when a domain is deleted, its immediate resource
    * records are also deleted from the account. By default, if a deleted domain had subdomains, each subdomain becomes
    * a root domain and is not deleted; this can be overridden by the optional deleteSubdomains parameter. Utilizing the
    * optional deleteSubdomains parameter on domains without subdomains does not result in a failure. When a domain is
    * deleted, any and all domain data is immediately purged and is not recoverable via the API.
    * </p>
    * To wait for this call to complete use {@link JobPredicates#awaitComplete(CloudDNSApi, Job)}.
    */
   @Named("domain:delete")
   @DELETE
   @Fallback(VoidOnNotFoundOr404.class)
   @ResponseParser(ParseJob.class)
   @Path("/domains")
   @Consumes("*/*")
   Job<Void> delete(@QueryParam("id") Iterable<Integer> ids,
         @QueryParam("deleteSubdomains") boolean deleteSubdomains);

   /**
    * This call provides the BIND (Berkeley Internet Name Domain) 9 formatted contents of the requested domain. This
    * call is for a single domain only, and as such, does not traverse up or down the domain hierarchy for details (that
    * is, no subdomain information is provided).
    * </p>
    * To wait for this call to complete use {@link JobPredicates#awaitComplete(CloudDNSApi, Job)}.
    */
   @Named("domain:export")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @ResponseParser(ParseJob.class)
   @Path("/domains/{id}/export")
   // format is ignored because the Cloud DNS API doesn't use it but other formats (e.g. BIND 10) may be supported in
   // the future and we don't want this interface to change
   Job<List<String>> exportFormat(@PathParam("id") int id, Domain.Format format);

   /**
    * This call provisions a new DNS domain under the account specified by the BIND 9 formatted file configuration
    * contents. If the corresponding request cannot be fulfilled due to insufficient or invalid data, an exception will
    * be thrown with information regarding the nature of the failure in the body of the response. Failures in the
    * validation process are non-recoverable and require the caller to correct the cause of the failure and call again.
    * </p>
    * To wait for this call to complete use {@link JobPredicates#awaitComplete(CloudDNSApi, Job)}.
    */
   @Named("domain:import")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_JSON)
   @ResponseParser(ParseJob.class)
   @MapBinder(FormatAndContentsToJSON.class)
   @Path("/domains/import")
   Job<Domain> importFormat(
         @PayloadParam("contents") List<String> contents,
         @PayloadParam("format") Domain.Format format);
}
