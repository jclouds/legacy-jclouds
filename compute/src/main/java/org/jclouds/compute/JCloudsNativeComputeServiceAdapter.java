/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.compute;

import java.util.Map;

import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;

/**
 * A means of specifying the implementation of a service that uses jclouds types.
 * 
 * @author Adrian Cole
 * 
 */
public interface JCloudsNativeComputeServiceAdapter extends
      ComputeServiceAdapter<NodeMetadata, Hardware, Image, Location> {
   /**
    * {@inheritDoc}
    */
   @Override
   NodeMetadata createNodeWithGroupEncodedIntoNameThenStoreCredentials(String tag, String name, Template template,
         Map<String, Credentials> credentialStore);

   /**
    * {@inheritDoc}
    */
   @Override
   Iterable<NodeMetadata> listNodes();

   /**
    * {@inheritDoc}
    */
   @Override
   Iterable<Image> listImages();

   /**
    * {@inheritDoc}
    */
   @Override
   Iterable<Hardware> listHardwareProfiles();

   /**
    * {@inheritDoc}
    */
   @Override
   Iterable<Location> listLocations();

   /**
    * {@inheritDoc}
    */
   @Override
   NodeMetadata getNode(String id);

   /**
    * {@inheritDoc}
    */
   @Override
   void destroyNode(String id);

   /**
    * {@inheritDoc}
    */
   @Override
   void rebootNode(String id);

}