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
package org.jclouds.elb.features;

import static org.jclouds.aws.reference.FormParameters.ACTION;

import java.util.Set;

import javax.inject.Named;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.jclouds.aws.filters.FormSigner;
import org.jclouds.elb.binders.BindAvailabilityZonesToIndexedFormParams;
import org.jclouds.elb.xml.AvailabilityZonesResultHandler;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.FormParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.VirtualHost;
import org.jclouds.rest.annotations.XMLResponseParser;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides access to Amazon ELB via the Query API
 * <p/>
 * 
 * @see <a href="http://docs.amazonwebservices.com/ElasticLoadBalancing/latest/APIReference"
 *      >doc</a>
 * @see AvailabilityZoneApi
 * @author Adrian Cole
 */
@RequestFilters(FormSigner.class)
@VirtualHost
public interface AvailabilityZoneAsyncApi {

   /**
    * @see AvailabilityZoneApi#addAvailabilityZonesToLoadBalancer
    */
   @Named("EnableAvailabilityZonesForLoadBalancer")
   @POST
   @Path("/")
   @XMLResponseParser(AvailabilityZonesResultHandler.class)
   @FormParams(keys = ACTION, values = "EnableAvailabilityZonesForLoadBalancer")
   ListenableFuture<Set<String>> addAvailabilityZonesToLoadBalancer(
            @BinderParam(BindAvailabilityZonesToIndexedFormParams.class) Iterable<String> zones,
            @FormParam("LoadBalancerName") String loadBalancerName);
   

   /**
    * @see AvailabilityZoneApi#addAvailabilityZoneToLoadBalancer
    */
   @Named("EnableAvailabilityZonesForLoadBalancer")
   @POST
   @Path("/")
   @XMLResponseParser(AvailabilityZonesResultHandler.class)
   @FormParams(keys = ACTION, values = "EnableAvailabilityZonesForLoadBalancer")
   ListenableFuture<Set<String>> addAvailabilityZoneToLoadBalancer(
            @FormParam("AvailabilityZones.member.1") String zone,
            @FormParam("LoadBalancerName") String loadBalancerName);

   /**
    * @see AvailabilityZoneApi#removeAvailabilityZonesFromLoadBalancer
    */
   @Named("DisableAvailabilityZonesForLoadBalancer")
   @POST
   @Path("/")
   @XMLResponseParser(AvailabilityZonesResultHandler.class)
   @FormParams(keys = ACTION, values = "DisableAvailabilityZonesForLoadBalancer")
   ListenableFuture<Set<String>> removeAvailabilityZonesFromLoadBalancer(
            @BinderParam(BindAvailabilityZonesToIndexedFormParams.class) Iterable<String> zones,
            @FormParam("LoadBalancerName") String loadBalancerName);

   /**
    * @see AvailabilityZoneApi#removeAvailabilityZoneFromLoadBalancer
    */
   @Named("DisableAvailabilityZonesForLoadBalancer")
   @POST
   @Path("/")
   @XMLResponseParser(AvailabilityZonesResultHandler.class)
   @FormParams(keys = ACTION, values = "DisableAvailabilityZonesForLoadBalancer")
   ListenableFuture<Set<String>> removeAvailabilityZoneFromLoadBalancer(
            @FormParam("AvailabilityZones.member.1") String zone,
            @FormParam("LoadBalancerName") String loadBalancerName);
}
