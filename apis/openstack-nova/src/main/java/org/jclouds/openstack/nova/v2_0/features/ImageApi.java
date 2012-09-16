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
package org.jclouds.openstack.nova.v2_0.features;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jclouds.concurrent.Timeout;
import org.jclouds.openstack.nova.v2_0.domain.Image;
import org.jclouds.openstack.v2_0.domain.Resource;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.Payload;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.binders.BindToJsonPayload;
import org.jclouds.rest.functions.ReturnEmptyMapOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnVoidOnNotFoundOr404;

import com.google.common.util.concurrent.ListenableFuture;

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
@Timeout(duration = 180, timeUnit = TimeUnit.SECONDS)
public interface ImageApi {

   /**
    * List all images (IDs, names, links)
    * 
    * @return all images (IDs, names, links)
    */
   Set<? extends Resource> listImages();

   /**
    * List all images (all details)
    * 
    * @return all images (all details)
    */
   Set<? extends Image> listImagesInDetail();

   /**
    * List details of the specified image
    * 
    * @param id
    *           id of the server
    * @return server or null if not found
    */
   Image getImage(String id);

   /**
    * Delete the specified image
    * 
    * @param id
    *           id of the image
    * @return server or null if not found
    */
   void deleteImage(String id);
   
   /**
    * List all metadata for an image.
    * 
    * @param id
    *           id of the image
    * @return the metadata as a Map<String, String> 
    */
   Map<String, String> listMetadata(String id);

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
    * @return the metadata as a Map<String, String> 
    */
   Map<String, String> getMetadataItem(String id, String key);

   
   /**
    * Set a metadata item for an image.
    * 
    * @param id
    *           id of the image
    * @param key
    *           the name of the metadata item
    * @param value
    *           the value of the metadata item
    * @return the metadata as a Map<String, String> 
    */
   Map<String, String> setMetadataItem(String id, String key, String value);

   /**
    * Delete a metadata item from an image.
    * 
    * @param id
    *           id of the image
    * @param key
    *           the name of the metadata item
    */
   void deleteMetadataItem(String id, String key);

}
