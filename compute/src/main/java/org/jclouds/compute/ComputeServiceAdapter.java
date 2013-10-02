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
package org.jclouds.compute;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.compute.domain.Template;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.javax.annotation.Nullable;

/**
 * A means of specifying the interface between the {@link ComputeService ComputeServices} and a
 * concrete compute cloud implementation, jclouds or otherwise.
 * 
 * <h3>Important</h3>
 * 
 * While implementations may cache results of calls to this interface, it is important to not cache
 * within this. Otherwise, status updates will not be visible immediately.
 * 
 * @author Adrian Cole
 * 
 */
public interface ComputeServiceAdapter<N, H, I, L> {

   /**
    * {@link ComputeService#createNodesInGroup(String, int, Template)} generates the parameters
    * passed into this method such that each node in the set has a unique name.
    * 
    * <h4>note</h4> It is intentional to return the library native node object, as generic type
    * {@code N}. If you are not using library-native objects (such as libvirt {@code Domain}) use
    * {@link JCloudsNativeComputeServiceAdapter} instead.
    * 
    * <h4>note</h4> Your responsibility is to create a node with the underlying library and return
    * after storing its credentials in the supplied map corresponding to
    * {@link ComputeServiceContext#getCredentialStore credentialStore}
    * 
    * @param group
    *           used to aggregate nodes with identical configuration
    * @param name
    *           unique supplied name for the node, which has the group encoded into it.
    * @param template
    *           includes {@code imageId}, {@code locationId}, and {@code hardwareId} used to resume
    *           the instance.
    * @return library-native representation of a node. TODO: return typed exception on
    *         createNodeFailure
    * @see ComputeService#createNodesInGroup(String, int, Template)
    */
   NodeAndInitialCredentials<N> createNodeWithGroupEncodedIntoName(String group, String name, Template template);

   public static class NodeAndInitialCredentials<N> {
      private final N node;
      private final String nodeId;
      private final LoginCredentials credentials;

      public NodeAndInitialCredentials(N node, String nodeId, @Nullable LoginCredentials credentials) {
         this.node = checkNotNull(node, "node");
         this.nodeId = checkNotNull(nodeId, "nodeId");
         this.credentials = credentials;
      }

      /**
       * 
       * @return cloud specific representation of the newly created node
       */
      public N getNode() {
         return node;
      }

      /** @return Stringified version of the new node's id. */
      public String getNodeId() {
         return nodeId;
      }

      /**
       * 
       * @return credentials given by the api for the node, or null if this information is not
       *         available
       */
      @Nullable
      public LoginCredentials getCredentials() {
         return credentials;
      }
   }

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
    * Images are the available configured operating systems that someone can run a node with.
    * <p/>
    * To implement this method, return the library native images available to the user. These will
    * be used to launch nodes as a part of the template.
    * 
    * @return a non-null iterable of available images.
    * @see ComputeService#listImages()
    */
   Iterable<I> listImages();

   /**
    * get a specific image by id
    * 
    * @param id
    *           {@link Image#getId}, which is not necessarily {@link Image#getProviderId}
    * @return image or null if not exists.
    */
   @Nullable
   I getImage(String id);

   Iterable<L> listLocations();

   N getNode(String id);

   // TODO consider making reboot/resume/suspend return the node they affected
   void destroyNode(String id);

   void rebootNode(String id);

   void resumeNode(String id);

   void suspendNode(String id);

   Iterable<N> listNodes();

   Iterable<N> listNodesByIds(Iterable<String> ids);
}
