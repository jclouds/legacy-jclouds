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
package org.jclouds.openstack.nova.v2_0.features;

import org.jclouds.collect.PagedIterable;
import org.jclouds.openstack.keystone.v2_0.domain.PaginatedCollection;
import org.jclouds.openstack.nova.v2_0.domain.Flavor;
import org.jclouds.openstack.v2_0.domain.Resource;
import org.jclouds.openstack.v2_0.options.PaginationOptions;

/**
 * Provides asynchronous access to Flavors via their REST API.
 * <p/>
 * 
 * @see FlavorAsyncApi
 * @see <a href=
 *      "http://docs.openstack.org/api/openstack-compute/2/content/List_Flavors-d1e4188.html"
 *      />
 * @author Jeremy Daggett, Ilja Bobkevic
 */
public interface FlavorApi {

   /**
    * List all flavors (IDs, names, links)
    * 
    * @return all flavors (IDs, names, links)
    */
   PagedIterable<? extends Resource> list();

   PaginatedCollection<? extends Resource> list(PaginationOptions options);

   /**
    * List all flavors (all details)
    * 
    * @return all flavors (all details)
    */
   PagedIterable<? extends Flavor> listInDetail();

   PaginatedCollection<? extends Flavor> listInDetail(PaginationOptions options);

   /**
    * List details of the specified flavor
    * 
    * @param id
    *           id of the flavor
    * @return flavor or null if not found
    */
   Flavor get(String id);

	/**
	 * Create flavor according to the provided object
	 * 
	 * @param flavor - flavor object
	 * @return newly created flavor
	 */
	Flavor create(Flavor flavor);

	/**
	 * Delete flavor with a given id
	 * 
	 * @param id - flavor id
	 */
	void delete(String id);
}
