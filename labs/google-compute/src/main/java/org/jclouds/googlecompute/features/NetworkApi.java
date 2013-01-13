/*
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

package org.jclouds.googlecompute.features;

import org.jclouds.collect.PagedIterable;
import org.jclouds.googlecompute.domain.ListPage;
import org.jclouds.googlecompute.domain.Network;
import org.jclouds.googlecompute.domain.Operation;
import org.jclouds.googlecompute.options.ListOptions;
import org.jclouds.javax.annotation.Nullable;

/**
 * Provides synchronous access to Networks via their REST API.
 *
 * @author David Alves
 * @see NetworkAsyncApi
 * @see <a href="https://developers.google.com/compute/docs/reference/v1beta13/networks"/>
 */
public interface NetworkApi {

   /**
    * Returns the specified persistent network resource.
    *
    * @param networkName name of the persistent network resource to return.
    * @return a Network resource.
    */
   Network get(String networkName);

   /**
    * Creates a persistent network resource in the specified project with the specified range.
    *
    * @param networkName the network name
    * @param IPv4Range   the range of the network to be inserted.
    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
    *         you, and look for the status field.
    */
   Operation createInIPv4Range(String networkName, String IPv4Range);

   /**
    * Creates a persistent network resource in the specified project with the specified range and specified gateway.
    *
    * @param networkName the network name
    * @param IPv4Range   the range of the network to be inserted.
    * @param gatewayIPv4 the range of the network to be inserted.
    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
    *         you, and look for the status field.
    */
   Operation createInIPv4RangeWithGateway(String networkName, String IPv4Range, String gatewayIPv4);

   /**
    * Deletes the specified persistent network resource.
    *
    * @param networkName name of the persistent network resource to delete.
    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
    *         you, and look for the status field.
    */
   Operation delete(String networkName);

   /**
    * @see NetworkApi#listAtMarker(String, org.jclouds.googlecompute.options.ListOptions)
    */
   ListPage<Network> listFirstPage();

   /**
    * @see NetworkApi#listAtMarker(String, org.jclouds.googlecompute.options.ListOptions)
    */
   ListPage<Network> listAtMarker(@Nullable String marker);

   /**
    * Retrieves the list of persistent network resources contained within the specified project.
    * By default the list as a maximum size of 100, if no options are provided or ListOptions#getMaxResults() has not
    * been set.
    *
    *
    * @param marker      marks the beginning of the next list page
    * @param listOptions listing options
    * @return a page of the list
    * @see ListOptions
    * @see org.jclouds.googlecompute.domain.ListPage
    */
   ListPage<Network> listAtMarker(@Nullable String marker, ListOptions listOptions);

   /**
    * @see NetworkApi#list(org.jclouds.googlecompute.options.ListOptions)
    */
   PagedIterable<Network> list();

   /**
    * A paged version of NetworkApi#list()
    *
    * @return a Paged, Fluent Iterable that is able to fetch additional pages when required
    * @see PagedIterable
    * @see NetworkApi#listAtMarker(String, org.jclouds.googlecompute.options.ListOptions)
    */
   PagedIterable<Network> list(@Nullable ListOptions listOptions);
}
