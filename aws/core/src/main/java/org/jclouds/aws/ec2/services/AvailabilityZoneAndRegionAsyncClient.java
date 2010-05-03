/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.aws.ec2.services;

import static org.jclouds.aws.ec2.reference.EC2Parameters.ACTION;
import static org.jclouds.aws.ec2.reference.EC2Parameters.VERSION;

import java.net.URI;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.jclouds.aws.ec2.EC2;
import org.jclouds.aws.ec2.domain.AvailabilityZoneInfo;
import org.jclouds.aws.ec2.functions.RegionToEndpoint;
import org.jclouds.aws.ec2.options.DescribeAvailabilityZonesOptions;
import org.jclouds.aws.ec2.options.DescribeRegionsOptions;
import org.jclouds.aws.ec2.xml.DescribeAvailabilityZonesResponseHandler;
import org.jclouds.aws.ec2.xml.DescribeRegionsResponseHandler;
import org.jclouds.aws.filters.FormSigner;
import org.jclouds.rest.annotations.Endpoint;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.FormParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.VirtualHost;
import org.jclouds.rest.annotations.XMLResponseParser;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides access to EC2 Availability Zones and Regions via their REST API.
 * <p/>
 * 
 * @author Adrian Cole
 */
@RequestFilters(FormSigner.class)
@FormParams(keys = VERSION, values = "2009-11-30")
@VirtualHost
public interface AvailabilityZoneAndRegionAsyncClient {

   /**
    * @see AvailabilityZoneAndRegionClient#describeAvailabilityZonesInRegion
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "DescribeAvailabilityZones")
   @XMLResponseParser(DescribeAvailabilityZonesResponseHandler.class)
   ListenableFuture<? extends Set<AvailabilityZoneInfo>> describeAvailabilityZonesInRegion(
            @EndpointParam(parser = RegionToEndpoint.class) @Nullable String region,
            DescribeAvailabilityZonesOptions... options);

   /**
    * @see AvailabilityZoneAndRegionClient#describeRegions
    */
   @POST
   @Endpoint(EC2.class)
   @Path("/")
   @FormParams(keys = ACTION, values = "DescribeRegions")
   @XMLResponseParser(DescribeRegionsResponseHandler.class)
   ListenableFuture<? extends Map<String, URI>> describeRegions(DescribeRegionsOptions... options);

}
