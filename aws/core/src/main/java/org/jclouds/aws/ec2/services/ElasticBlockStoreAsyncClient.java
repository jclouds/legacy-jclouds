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

import java.util.Set;
import java.util.concurrent.Future;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.jclouds.aws.ec2.EC2;
import org.jclouds.aws.ec2.binders.BindVolumeIdsToIndexedFormParams;
import org.jclouds.aws.ec2.domain.AvailabilityZone;
import org.jclouds.aws.ec2.domain.Region;
import org.jclouds.aws.ec2.domain.Volume;
import org.jclouds.aws.ec2.filters.FormSigner;
import org.jclouds.aws.ec2.functions.RegionToEndpoint;
import org.jclouds.aws.ec2.xml.CreateVolumeResponseHandler;
import org.jclouds.aws.ec2.xml.DescribeVolumesResponseHandler;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Endpoint;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.FormParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.VirtualHost;
import org.jclouds.rest.annotations.XMLResponseParser;

/**
 * Provides access to EC2 Elastic Block Store services via their REST API.
 * <p/>
 * 
 * @author Adrian Cole
 */
@RequestFilters(FormSigner.class)
@FormParams(keys = VERSION, values = "2009-11-30")
@VirtualHost
public interface ElasticBlockStoreAsyncClient {

   /**
    * @see ElasticBlockStoreClient#createVolumeFromSnapshotInAvailabilityZone
    */
   @POST
   @Path("/")
   @Endpoint(EC2.class)
   // TODO: remove
   @FormParams(keys = ACTION, values = "CreateVolume")
   @XMLResponseParser(CreateVolumeResponseHandler.class)
   Future<Volume> createVolumeFromSnapshotInAvailabilityZone(
            @FormParam("AvailabilityZone") AvailabilityZone availabilityZone,
            @FormParam("SnapshotId") String snapshotId);

   /**
    * @see ElasticBlockStoreClient#createVolumeInAvailabilityZone
    */
   @POST
   @Path("/")
   @Endpoint(EC2.class)
   // TODO: remove
   @FormParams(keys = ACTION, values = "CreateVolume")
   @XMLResponseParser(CreateVolumeResponseHandler.class)
   Future<Volume> createVolumeInAvailabilityZone(
            @FormParam("AvailabilityZone") AvailabilityZone availabilityZone,
            @FormParam("Size") int size);

   /**
    * @see ElasticBlockStoreClient#describeVolumesInRegion
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "DescribeVolumes")
   @XMLResponseParser(DescribeVolumesResponseHandler.class)
   Future<? extends Set<Volume>> describeVolumesInRegion(
            @EndpointParam(parser = RegionToEndpoint.class) Region region,
            @BinderParam(BindVolumeIdsToIndexedFormParams.class) String... volumeIds);

   /**
    * @see ElasticBlockStoreClient#deleteVolumeInRegion
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "DeleteVolume")
   Future<Void> deleteVolumeInRegion(@EndpointParam(parser = RegionToEndpoint.class) Region region,
            @FormParam("VolumeId") String volumeId);

}
