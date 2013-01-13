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
import org.jclouds.googlecompute.domain.MachineType;
import org.jclouds.googlecompute.options.ListOptions;
import org.jclouds.javax.annotation.Nullable;

/**
 * Provides synchronous access to MachineTypes via their REST API.
 * <p/>
 *
 * @author David Alves
 * @see <a href="https://developers.google.com/compute/docs/reference/v1beta13/machineTypes"/>
 */
public interface MachineTypeApi {

   /**
    * Returns the specified machine type resource
    *
    * @param machineTypeName name of the machine type resource to return.
    * @return If successful, this method returns a MachineType resource
    */
   MachineType get(String machineTypeName);

   /**
    * @see MachineTypeApi#listAtMarker(String, org.jclouds.googlecompute.options.ListOptions)
    */
   ListPage<MachineType> listFirstPage();

   /**
    * @see MachineTypeApi#listAtMarker(String, org.jclouds.googlecompute.options.ListOptions)
    */
   ListPage<MachineType> listAtMarker(@Nullable String marker);

   /**
    * Retrieves the list of machine type resources available to the specified project.
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
   ListPage<MachineType> listAtMarker(@Nullable String marker, ListOptions listOptions);

   /**
    * @see MachineTypeApi#list(org.jclouds.googlecompute.options.ListOptions)
    */
   PagedIterable<MachineType> list();

   /**
    * A paged version of MachineTypeApi#list()
    *
    * @return a Paged, Fluent Iterable that is able to fetch additional pages when required
    * @see PagedIterable
    * @see MachineTypeApi#listAtMarker(String, org.jclouds.googlecompute.options.ListOptions)
    */
   PagedIterable<MachineType> list(ListOptions listOptions);

}
