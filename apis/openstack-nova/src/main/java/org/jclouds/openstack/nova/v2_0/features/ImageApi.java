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

import java.util.Map;
import org.jclouds.collect.PagedIterable;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.openstack.keystone.v2_0.domain.PaginatedCollection;
import org.jclouds.openstack.nova.v2_0.domain.Image;
import org.jclouds.openstack.v2_0.domain.Resource;
import org.jclouds.openstack.v2_0.options.PaginationOptions;

/**
 * Provides synchronous access to Images.
 * <p/>
 * 
 * @see ImageAsyncApi
 * @see <a href=
 *      "http://docs.openstack.org/api/openstack-compute/1.1/content/Servers-d1e2073.html"
 *      />
 * @author Adrian Cole
 */
public interface ImageApi {

   /**
    * List all images (IDs, names, links)
    * 
    * @return all images (IDs, names, links)
    */
   PagedIterable<? extends Resource> list();

   PaginatedCollection<? extends Resource> list(PaginationOptions options);

   /**
    * List all images (all details)
    * 
    * @return all images (all details)
    */
   PagedIterable<? extends Image> listInDetail();

   PaginatedCollection<? extends Image> listInDetail(PaginationOptions options);

   /**
    * List details of the specified image
    * 
    * @param id
    *           id of the server
    * @return server or null if not found
    */
   Image get(String id);

   /**
    * Delete the specified image
    * 
    * @param id
    *           id of the image
    * @return server or null if not found
    */
   void delete(String id);
   
   /**
    * List all metadata for an image.
    * 
    * @param id
    *           id of the image
    * @return the metadata as a Map<String, String> 
    */
   Map<String, String> getMetadata(String id);

   /**
    * Sets the metadata for an image.
    * 
    * @param id
    *           id of the image
    * @param metadata
    *           a Map containing the metadata
    * @return the metadata as a Map<String, String> 
    */
   Map<String, String> setMetadata(String id, Map<String, String> metadata);

   /**
    * Update the metadata for a server.
    * 
    * @param id
    *           id of the image
    * @param metadata
    *           a Map containing the metadata
    * @return the metadata as a Map<String, String> 
    */
   Map<String, String> updateMetadata(String id, Map<String, String> metadata);
   
   /**
    * Update the metadata for an image.
    * 
    * @param id
    *           id of the image
    * @param metadata
    *           a Map containing the metadata
    * @return the value or null if not present
    */
   @Nullable
   String getMetadata(String id, String key);

   
   /**
    * Set a metadata item for an image.
    * 
    * @param id
    *           id of the image
    * @param key
    *           the name of the metadata item
    * @param value
    *           the value of the metadata item
    * @return the value you updated
    */
   String updateMetadata(String id, String key, String value);

   /**
    * Delete a metadata item from an image.
    * 
    * @param id
    *           id of the image
    * @param key
    *           the name of the metadata item
    */
   void deleteMetadata(String id, String key);

}
