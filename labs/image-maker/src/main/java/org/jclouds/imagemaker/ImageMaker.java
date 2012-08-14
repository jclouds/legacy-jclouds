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

package org.jclouds.imagemaker;

import java.util.Map;
import java.util.Set;

import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.extensions.ImageExtension;
import org.jclouds.imagemaker.internal.ImageMakerImpl;

import com.google.inject.ImplementedBy;

/**
 * An ImageMake takes a node, applies a series of {@link PackageProcessor}s that are adequate to
 * that node's OS (each doing stuff like pre-caching apt packages or installing pip packages) and
 * outputs a ready to use image. Requires a {@link ComputeServiceContext} that has an
 * {@link ImageExtension}.
 * 
 * @author David Alves
 */
@ImplementedBy(ImageMakerImpl.class)
public interface ImageMaker {

   /**
    * Creates an {@link Image} based on a node by applied all compatible, registered
    * {@link PackageProcessor}s and registering the node as a new Image with {@link ImageExtension}.
    * 
    * @param node
    *           the node upon to base the new image
    * @param imageDescriptorId
    *           the id of the image descriptor (in the yaml file)
    * @param newImageName
    *           the name of the newly created image
    * @return the newly created and available {@link Image}
    */
   public Image makeImage(NodeMetadata node, String imageDescriptorId, String newImageName);

   /**
    * Returns the set of registered {@link PackageProcessor}s
    */
   public Map<PackageProcessor.Type, Set<PackageProcessor>> registeredProcessors();

}
