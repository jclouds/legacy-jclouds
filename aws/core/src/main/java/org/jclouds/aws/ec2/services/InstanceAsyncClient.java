/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.aws.ec2.services;

import static org.jclouds.aws.ec2.reference.EC2Parameters.ACTION;
import static org.jclouds.aws.ec2.reference.EC2Parameters.VERSION;

import java.util.SortedSet;
import java.util.concurrent.Future;

import javax.annotation.Nullable;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.jclouds.aws.ec2.EC2;
import org.jclouds.aws.ec2.binders.BindInstanceIdsToIndexedFormParams;
import org.jclouds.aws.ec2.binders.IfNotNullBindAvailabilityZoneToFormParam;
import org.jclouds.aws.ec2.domain.AvailabilityZone;
import org.jclouds.aws.ec2.domain.Region;
import org.jclouds.aws.ec2.domain.Reservation;
import org.jclouds.aws.ec2.domain.TerminatedInstance;
import org.jclouds.aws.ec2.filters.FormSigner;
import org.jclouds.aws.ec2.functions.RegionToEndpoint;
import org.jclouds.aws.ec2.options.RunInstancesOptions;
import org.jclouds.aws.ec2.xml.DescribeInstancesResponseHandler;
import org.jclouds.aws.ec2.xml.RunInstancesResponseHandler;
import org.jclouds.aws.ec2.xml.TerminateInstancesResponseHandler;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Endpoint;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.FormParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.VirtualHost;
import org.jclouds.rest.annotations.XMLResponseParser;

/**
 * Provides access to EC2 via their REST API.
 * <p/>
 * 
 * @author Adrian Cole
 */
@Endpoint(EC2.class)
@RequestFilters(FormSigner.class)
@FormParams(keys = VERSION, values = "2009-11-30")
@VirtualHost
public interface InstanceAsyncClient {

   /**
    * @see BaseEC2Client#describeInstancesInRegion
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "DescribeInstances")
   @XMLResponseParser(DescribeInstancesResponseHandler.class)
   Future<? extends SortedSet<Reservation>> describeInstancesInRegion(
            @EndpointParam(parser = RegionToEndpoint.class) Region region,
            @BinderParam(BindInstanceIdsToIndexedFormParams.class) String... instanceIds);

   /**
    * @see BaseEC2Client#runInstancesInRegion
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "RunInstances")
   @XMLResponseParser(RunInstancesResponseHandler.class)
   Future<Reservation> runInstancesInRegion(
            @EndpointParam(parser = RegionToEndpoint.class) Region region,
            @Nullable @BinderParam(IfNotNullBindAvailabilityZoneToFormParam.class) AvailabilityZone nullableAvailabilityZone,
            @FormParam("ImageId") String imageId, @FormParam("MinCount") int minCount,
            @FormParam("MaxCount") int maxCount, RunInstancesOptions... options);

   /**
    * @see BaseEC2Client#terminateInstancesInRegion
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "TerminateInstances")
   @XMLResponseParser(TerminateInstancesResponseHandler.class)
   Future<? extends SortedSet<TerminatedInstance>> terminateInstancesInRegion(
            @EndpointParam(parser = RegionToEndpoint.class) Region region,
            @FormParam("InstanceId.0") String instanceId,
            @BinderParam(BindInstanceIdsToIndexedFormParams.class) String... instanceIds);

}
