/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, String 2.0 (the
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
package org.jclouds.rackspace.clouddns.v1.features;

import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.openstack.v2_0.domain.Limits;
import org.jclouds.rest.annotations.RequestFilters;

/**
 * All accounts, by default, have a preconfigured set of thresholds (or limits) to manage capacity and prevent abuse
 * of the system. The system recognizes two kinds of limits: rate limits and absolute limits. Rate limits are 
 * thresholds that are reset after a certain amount of time passes. Absolute limits are fixed.
 * 
 * @see LimitApi
 * @author Everett Toews
 */
@RequestFilters(AuthenticateRequest.class)
public interface LimitApi {
   /**
    * Provides a list of all applicable limits.
    */
   Limits list();
   
   /**
    * All applicable limit types.
    */
   Iterable<String> listTypes();
}
