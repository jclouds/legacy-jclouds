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
package org.jclouds.compute.extensions;

import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageTemplate;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * An extension to compute service to allow for the manipulation of {@link Image}s. Implementation
 * is optional by providers.
 * 
 * @author David Alves
 */
public interface ImageExtension {

   /**
    * Build an ImageTemplate from a running node, to use later to create a new {@link Image}.
    * 
    * @param name
    *           name to give the new image
    * 
    * @param id
    *           node to base the template on
    * @return an image template that can be used to create a new image
    */
   ImageTemplate buildImageTemplateFromNode(String name, String id);

   /**
    * Transform the {@link ImageTemplate} on an {@link Image} that can be used to create nodes.
    * 
    * @param template
    *           template to base the new image on
    * @return the image that was just built *after* it is registered on the provider
    */
   ListenableFuture<Image> createImage(ImageTemplate template);

   /**
    * Delete an {@link Image} on the provider.
    * 
    * @param id
    *           the id of the image to delete
    * @return
    */
   boolean deleteImage(String id);

}
