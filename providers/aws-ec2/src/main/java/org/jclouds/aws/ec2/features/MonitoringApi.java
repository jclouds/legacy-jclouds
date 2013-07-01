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
package org.jclouds.aws.ec2.features;

import static org.jclouds.aws.reference.FormParameters.ACTION;

import java.util.Map;

import javax.inject.Named;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.jclouds.aws.ec2.domain.MonitoringState;
import org.jclouds.aws.ec2.xml.MonitoringStateHandler;
import org.jclouds.aws.filters.FormSigner;
import org.jclouds.ec2.binders.BindInstanceIdsToIndexedFormParams;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.location.functions.RegionToEndpointOrProviderIfNull;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.FormParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.VirtualHost;
import org.jclouds.rest.annotations.XMLResponseParser;

/**
 * Provides access to EC2 Monitoring Services via their REST API.
 * <p/>
 * 
 * @author Adrian Cole
 */
@RequestFilters(FormSigner.class)
@VirtualHost
public interface MonitoringApi {

   /**
    * Enables monitoring for a running instance. For more information, refer to the Amazon
    * CloudWatch Developer Guide.
    * 
    * @param region
    *           Instances are tied to Availability Zones. However, the instance ID is tied to the
    *           Region.
    * @see InstanceApi#runInstances
    * @see #unmonitorInstances
    * 
    * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-MonitorInstances.html"
    *      />
    */
   @Named("MonitorInstances")
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "MonitorInstances")
   @XMLResponseParser(MonitoringStateHandler.class)
   Map<String, MonitoringState> monitorInstancesInRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
            @FormParam("InstanceId.0") String instanceId,
            @BinderParam(BindInstanceIdsToIndexedFormParams.class) String... instanceIds);

   /**
    * Disables monitoring for a running instance. For more information, refer to the Amazon
    * CloudWatch Developer Guide.
    * 
    * @param region
    *           Instances are tied to Availability Zones. However, the instance ID is tied to the
    *           Region.
    * 
    * @see InstanceApi#runInstances
    * @see #monitorInstances
    * 
    * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-UnmonitorInstances.html"
    *      />
    */
   @Named("UnmonitorInstances")
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "UnmonitorInstances")
   @XMLResponseParser(MonitoringStateHandler.class)
   Map<String, MonitoringState> unmonitorInstancesInRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
            @FormParam("InstanceId.0") String instanceId,
            @BinderParam(BindInstanceIdsToIndexedFormParams.class) String... instanceIds);
}
