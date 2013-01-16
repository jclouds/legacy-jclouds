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
import org.jclouds.googlecompute.domain.Image;
import org.jclouds.googlecompute.domain.ListPage;
import org.jclouds.googlecompute.domain.Operation;
import org.jclouds.googlecompute.options.ListOptions;
import org.jclouds.javax.annotation.Nullable;

/**
 * Provides synchronous access to Images via their REST API.
 * <p/>
 *
 * @author David Alves
 * @see ImageAsyncApi
 * @see <a href="https://developers.google.com/compute/docs/reference/v1beta13/images"/>
 */
public interface ImageApi {

   /**
    * Returns the specified image resource.
    *
    * @param imageName name of the image resource to return.
    * @return an Image resource
    */
   Image get(String imageName);

   /**
    * @see ImageApi#listAtMarker(String, org.jclouds.googlecompute.options.ListOptions)
    */
   public ListPage<Image> listFirstPage();

   /**
    * @see ImageApi#listAtMarker(String, org.jclouds.googlecompute.options.ListOptions)
    */
   public ListPage<Image> listAtMarker(@Nullable String marker);

   /**
    * Deletes the specified image resource.
    *
    * @param imageName name of the image resource to delete.
    * @return an Operation resource. To check on the status of an operation, poll the Operations resource returned to
    *         you, and look for the status field.  If the image did not exist the result is null.
    */
   Operation delete(String imageName);

   /**
    * Retrieves the list of image resources available to the specified project.
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
   ListPage<Image> listAtMarker(String marker, @Nullable ListOptions listOptions);

   /**
    * @see ImageApi#list(org.jclouds.googlecompute.options.ListOptions)
    */
   public PagedIterable<Image> list();

   /**
    * A paged version of ImageApi#list()
    *
    * @return a Paged, Fluent Iterable that is able to fetch additional pages when required
    * @see PagedIterable
    * @see ImageApi#listAtMarker(String, org.jclouds.googlecompute.options.ListOptions)
    */
   PagedIterable<Image> list(@Nullable ListOptions listOptions);
}
