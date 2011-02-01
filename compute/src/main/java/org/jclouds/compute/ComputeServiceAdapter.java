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

import org.jclouds.compute.domain.Template;
import org.jclouds.domain.Credentials;

/**
 * A means of specifying the interface between the {@link ComputeService ComputeServices} and a concrete compute
 * cloud implementation, jclouds or otherwise.
 * 
 * @author Adrian Cole
 * 
 */
public interface ComputeServiceAdapter<N, H, I, L> {

   /**
    * {@link ComputeService#runNodesWithTag(String, int, Template)} generates the parameters passed
    * into this method such that each node in the set has a unique name.
    * 
    * <h4>note</h4> It is intentional to return the library native node object, as generic type
    * {@code N}. If you are not using library-native objects (such as libvirt {@code Domain}) use
    * {@link JCloudsNativeComputeServiceAdapter} instead.
    * 
    * <h4>note</h4> Your responsibility is to create a node with the underlying library and return
    * after storing its credentials in the supplied map corresponding to
    * {@link ComputeServiceContext#getCredentialStore credentialStore}
    * 
    * @param tag
    *           used to aggregate nodes with identical configuration
    * @param name
    *           unique supplied name for the node, which has the tag encoded into it.
    * @param template
    *           includes {@code imageId}, {@code locationId}, and {@code hardwareId} used to resume
    *           the instance.
    * @param credentialStore
    *           once the node is resumeed, its login user and password must be stored keyed on
    *           {@code node#id}.
    * @return library-native representation of a node.
    * 
    * @see ComputeService#runNodesWithTag(String, int, Template)
    * @see ComputeServiceContext#getCredentialStore
    */
   N createNodeWithGroupEncodedIntoNameThenStoreCredentials(String tag, String name, Template template,
            Map<String, Credentials> credentialStore);

   /**
    * Hardware profiles describe available cpu, memory, and disk configurations that can be used to
    * run a node.
    * <p/>
    * To implement this method, return the library native hardware profiles available to the user.
    * These will be used to launch nodes as a part of the template.
    * 
    * @return a non-null iterable of available hardware profiles.
    * @see ComputeService#listHardwareProfiles()
    */
   Iterable<H> listHardwareProfiles();

   /**
    * Images are the available configured operating systems that someone can run a node with. *
    * <p/>
    * To implement this method, return the library native images available to the user. These will
    * be used to launch nodes as a part of the template.
    * 
    * @return a non-null iterable of available images.
    * @see ComputeService#listImages()
    */
   Iterable<I> listImages();

   Iterable<L> listLocations();

   N getNode(String id);

   void destroyNode(String id);

   void rebootNode(String id);

   void resumeNode(String id);

   void suspendNode(String id);

   Iterable<N> listNodes();

}