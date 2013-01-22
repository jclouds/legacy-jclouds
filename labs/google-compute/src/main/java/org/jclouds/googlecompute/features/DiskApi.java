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
import org.jclouds.googlecompute.domain.Disk;
import org.jclouds.googlecompute.domain.ListPage;
import org.jclouds.googlecompute.domain.Operation;
import org.jclouds.googlecompute.options.ListOptions;
import org.jclouds.javax.annotation.Nullable;

import java.net.URI;

/**
 * Provides synchronous access to Disks via their REST API.
 *
 * @author David Alves
 * @see DiskAsyncApi
 * @see <a href="https://developers.google.com/compute/docs/reference/v1beta13/disks"/>
 */
public interface DiskApi {

   /**
    * Returns the specified persistent disk resource.
    *
    * @param diskName name of the persistent disk resource to return.
    * @return a Disk resource.
    */
   Disk get(String diskName);

   /**
    * Creates a persistent disk resource in the specified project specifying the size of the disk.
    *
    *
    * @param diskName the name of disk.
    * @param sizeGb   the size of the disk
    * @param zone     the URi of the zone where the disk is to be created.
    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
    *         you, and look for the status field.
    */
   Operation createInZone(String diskName, int sizeGb, URI zone);

   /**
    * Deletes the specified persistent disk resource.
    *
    * @param diskName name of the persistent disk resource to delete.
    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
    *         you, and look for the status field.
    */
   Operation delete(String diskName);

   /**
    * @see DiskApi#listAtMarker(String, org.jclouds.googlecompute.options.ListOptions)
    */
   public ListPage<Disk> listFirstPage();

   /**
    * @see DiskApi#listAtMarker(String, org.jclouds.googlecompute.options.ListOptions)
    */
   public ListPage<Disk> listAtMarker(@Nullable String marker);


   /**
    * Retrieves the listPage of persistent disk resources contained within the specified project.
    * By default the listPage as a maximum size of 100, if no options are provided or ListOptions#getMaxResults() has
    * not been set.
    *
    * @param marker      marks the beginning of the next list page
    * @param listOptions listing options
    * @return a page of the listPage
    * @see ListOptions
    * @see org.jclouds.googlecompute.domain.ListPage
    */
   ListPage<Disk> listAtMarker(@Nullable String marker, ListOptions listOptions);

   /**
    * @see DiskApi#list(org.jclouds.googlecompute.options.ListOptions)
    */
   public PagedIterable<Disk> list();

   /**
    * A paged version of DiskApi#listPage()
    *
    * @return a Paged, Fluent Iterable that is able to fetch additional pages when required
    * @see PagedIterable
    * @see DiskApi#listAtMarker(String, org.jclouds.googlecompute.options.ListOptions)
    */
   PagedIterable<Disk> list(ListOptions listOptions);
}
