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
package org.jclouds.ec2.features;

import static org.jclouds.aws.reference.FormParameters.ACTION;

import java.net.URI;
import java.util.Map;
import java.util.Set;

import javax.inject.Named;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.jclouds.Fallbacks.EmptySetOnNotFoundOr404;
import org.jclouds.aws.filters.FormSigner;
import org.jclouds.ec2.domain.AvailabilityZoneInfo;
import org.jclouds.ec2.options.DescribeAvailabilityZonesOptions;
import org.jclouds.ec2.options.DescribeRegionsOptions;
import org.jclouds.ec2.xml.DescribeAvailabilityZonesResponseHandler;
import org.jclouds.ec2.xml.DescribeRegionsResponseHandler;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.location.functions.RegionToEndpointOrProviderIfNull;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.FormParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.VirtualHost;
import org.jclouds.rest.annotations.XMLResponseParser;

/**
 * Provides access to EC2 Availability Zones and Regions via their REST API.
 * <p/>
 * 
 * @author Adrian Cole
 */
@RequestFilters(FormSigner.class)
@VirtualHost
public interface AvailabilityZoneAndRegionApi {

   /**
    * Displays Availability Zones that are currently available to the identity and their states.
    * 
    * @see InstanceApi#runInstances
    * @see #describeRegions
    * 
    * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-DescribeAvailabilityZones.html"
    *      />
    */
   @Named("DescribeAvailabilityZones")
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "DescribeAvailabilityZones")
   @XMLResponseParser(DescribeAvailabilityZonesResponseHandler.class)
   @Fallback(EmptySetOnNotFoundOr404.class)
   Set<AvailabilityZoneInfo> describeAvailabilityZonesInRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
            DescribeAvailabilityZonesOptions... options);

   /**
    * Describes Regions that are currently available to the identity.
    * 
    * @see InstanceApi#runInstances
    * @see #describeAvailabilityZones
    * 
    * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-DescribeRegions.html"
    *      />
    */
   @Named("DescribeRegions")
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "DescribeRegions")
   @XMLResponseParser(DescribeRegionsResponseHandler.class)
   Map<String, URI> describeRegions(DescribeRegionsOptions... options);

}
