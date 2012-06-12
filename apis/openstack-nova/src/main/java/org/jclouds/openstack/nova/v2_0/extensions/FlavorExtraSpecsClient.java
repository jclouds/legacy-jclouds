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
package org.jclouds.openstack.nova.v2_0.extensions;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.openstack.v2_0.ServiceType;
import org.jclouds.openstack.v2_0.services.Extension;
import org.jclouds.rest.annotations.RequestFilters;

/**
 * Provide access to extra metadata for Nova flavors.
 *
 * @author Adam Lowe
 * @see <a href="http://nova.openstack.org/api/nova.api.openstack.compute.contrib.flavorextraspecs.html"/>
 * @see org.jclouds.openstack.nova.v2_0.features.FlavorClient
 * @see org.jclouds.openstack.nova.v2_0.extensions.FlavorExtraSpecsAsyncClient
 */
@Extension(of = ServiceType.COMPUTE, namespace = ExtensionNamespaces.FLAVOR_EXTRA_SPECS)
@Timeout(duration = 180, timeUnit = TimeUnit.SECONDS)
@RequestFilters(AuthenticateRequest.class)
public interface FlavorExtraSpecsClient {

   /**
    * Retrieve all extra specs for a flavor
    *
    * @return the set of extra metadata for the flavor
    */
   Map<String, String> getAllExtraSpecs(String flavorId);

   /**
    * Creates or updates the extra specs for a given flavor
    *
    * @param flavorId the id of the flavor to modify
    * @param specs    the extra specs to apply
    */
   Boolean setAllExtraSpecs(String flavorId, Map<String, String> specs);

   /**
    * Return a single extra spec value
    *
    * @param flavorId the id of the flavor to modify
    * @param key      the extra spec key to retrieve
    */
   String getExtraSpec(String flavorId, String key);

   /**
    * Creates or updates a single extra spec value
    *
    * @param flavorId the id of the flavor to modify
    * @param key      the extra spec key (when creating ensure this does not include whitespace or other difficult characters)
    * @param value    the value to associate with the key
    */
   Boolean setExtraSpec(String flavorId, String key, String value);

   /**
    * Deletes an extra spec
    *
    * @param flavorId the id of the flavor to modify
    * @param key      the extra spec key to delete
    */
   Boolean deleteExtraSpec(String flavorId, String key);

}