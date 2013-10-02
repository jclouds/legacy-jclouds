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
package org.jclouds.rackspace.clouddns.v1;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.io.Closeable;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.rackspace.clouddns.v1.config.CloudDNS;
import org.jclouds.rackspace.clouddns.v1.domain.Job;
import org.jclouds.rackspace.clouddns.v1.features.DomainApi;
import org.jclouds.rackspace.clouddns.v1.features.LimitApi;
import org.jclouds.rackspace.clouddns.v1.features.RecordApi;
import org.jclouds.rackspace.clouddns.v1.features.ReverseDNSApi;
import org.jclouds.rackspace.clouddns.v1.functions.ParseJob;
import org.jclouds.rackspace.clouddns.v1.predicates.JobPredicates;
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.rest.annotations.Endpoint;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;

/**
 * Provides access to the Rackspace Cloud DNS API.
 * <p/>
 * See <a href="http://docs.rackspace.com/cdns/api/v1.0/cdns-devguide/content/index.html">Cloud DNS Developer Guide</a>
 *  
 * @author Everett Toews
 */
public interface CloudDNSApi extends Closeable {

   /**
    * Returns the current status of a job.
    * </p>
    * Operations that create, update, or delete resources may take some time to process. Therefore they return 
    * a Job containing information, which allows the status and response information of the job to be 
    * retrieved at a later point in time.
    * </p>
    * You likely won't need to use this method directly. Use {@link JobPredicates#awaitComplete(CloudDNSApi, Job)}. 
    *
    * @return null, if not found.
    */
   @Named("job:get")
   @Endpoint(CloudDNS.class)
   @RequestFilters(AuthenticateRequest.class)
   @GET
   @Consumes(APPLICATION_JSON)
   @ResponseParser(ParseJob.class)
   @Fallback(NullOnNotFoundOr404.class)
   @QueryParams(keys = "showDetails", values = "true")
   @Path("/status/{jobId}")
   @Nullable
   <T> Job<T> getJob(@PathParam("jobId") String jobId);

   /**
    * Provides access to Limit features.
    */
   @Delegate
   LimitApi getLimitApi();

   /**
    * Provides access to Domain features.
    */
   @Delegate
   DomainApi getDomainApi();

   /**
    * Provides access to Record features.
    */
   @Delegate
   @Path("/domains/{domainId}")
   RecordApi getRecordApiForDomain(@PathParam("domainId") int domainId);

   /**
    * Provides access to Reverse DNS features.
    */
   @Delegate
   ReverseDNSApi getReverseDNSApiForService(@PayloadParam("serviceName") @PathParam("serviceName") String serviceName);
}
