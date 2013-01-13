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
import org.jclouds.googlecompute.domain.Kernel;
import org.jclouds.googlecompute.domain.ListPage;
import org.jclouds.googlecompute.options.ListOptions;
import org.jclouds.javax.annotation.Nullable;

/**
 * Provides synchronous access to MachineTypes via their REST API.
 * <p/>
 *
 * @author David Alves
 * @see <a href="https://developers.google.com/compute/docs/reference/v1beta13/kernels"/>
 */
public interface KernelApi {

   /**
    * Returns the specified kernel resource
    *
    * @param kernelName name of the kernel resource to return.
    * @return If successful, this method returns a Kernel resource
    */
   public Kernel get(String kernelName);

   /**
    * @see KernelApi#listAtMarker(String, org.jclouds.googlecompute.options.ListOptions)
    */
   public ListPage<Kernel> listFirstPage();

   /**
    * @see KernelApi#listAtMarker(String, org.jclouds.googlecompute.options.ListOptions)
    */
   public ListPage<Kernel> listAtMarker(@Nullable String marker);

   /**
    * Retrieves the list of kernel resources available to the specified project.
    * By default the list as a maximum size of 100, if no options are provided or ListOptions#getMaxResults() has not
    * been set.
    *
    * @param marker      marks the beginning of the next list page
    * @param listOptions listing options
    * @return a page of the list
    * @see ListOptions
    * @see org.jclouds.googlecompute.domain.ListPage
    */
   public ListPage<Kernel> listAtMarker(String marker, ListOptions listOptions);

   /**
    * @see KernelApi#list(org.jclouds.googlecompute.options.ListOptions)
    */
   public PagedIterable<Kernel> list();

   /**
    * A paged version of KernelApi#list()
    *
    * @return a Paged, Fluent Iterable that is able to fetch additional pages when required
    * @see PagedIterable
    * @see KernelApi#listAtMarker(String, org.jclouds.googlecompute.options.ListOptions)
    */
   public PagedIterable<Kernel> list(ListOptions listOptions);

}
