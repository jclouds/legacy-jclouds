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

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.openstack.v2_0.domain.Limits;
import org.jclouds.rackspace.clouddns.v1.config.CloudDNS;
import org.jclouds.rest.annotations.Endpoint;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;

/**
 * All accounts, by default, have a preconfigured set of thresholds (or limits) to manage capacity and prevent abuse
 * of the system. The system recognizes two kinds of limits: rate limits and absolute limits. Rate limits are 
 * thresholds that are reset after a certain amount of time passes. Absolute limits are fixed.
 * 
 * @author Everett Toews
 */
@Endpoint(CloudDNS.class)
@RequestFilters(AuthenticateRequest.class)
public interface LimitApi {
   /**
    * Provides a list of all applicable limits.
    */
   @Named("limits:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @SelectJson("limits")
   @Path("/limits")
   Limits list();

   /**
    * All applicable limit types.
    */
   @Named("limits:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @SelectJson("limitTypes")
   @Path("/limits/types")
   Iterable<String> listTypes();
}
