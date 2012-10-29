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
package org.jclouds.slicehost.compute.strategy;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.domain.Template;
import org.jclouds.domain.Location;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.slicehost.SlicehostClient;
import org.jclouds.slicehost.domain.Flavor;
import org.jclouds.slicehost.domain.Image;
import org.jclouds.slicehost.domain.Slice;

import com.google.common.collect.ImmutableSet;

/**
 * defines the connection between the {@link SlicehostClient} implementation and the jclouds
 * {@link ComputeService}
 * 
 */
@Singleton
public class SlicehostComputeServiceAdapter implements ComputeServiceAdapter<Slice, Flavor, Image, Location> {

   protected final SlicehostClient client;

   @Inject
   protected SlicehostComputeServiceAdapter(SlicehostClient client) {
      this.client = checkNotNull(client, "client");
   }

   @Override
   public NodeAndInitialCredentials<Slice> createNodeWithGroupEncodedIntoName(String group, String name,
            Template template) {
      Slice server = client
               .createSlice(name, Integer.parseInt(template.getImage().getProviderId()), Integer.parseInt(template
                        .getHardware().getProviderId()));

      return new NodeAndInitialCredentials<Slice>(server, server.getId() + "", LoginCredentials.builder().password(
               server.getRootPassword()).build());
   }

   @Override
   public Iterable<Flavor> listHardwareProfiles() {
      return client.listFlavors();
   }

   @Override
   public Iterable<Image> listImages() {
      return client.listImages();
   }

   @Override
   public Iterable<Slice> listNodes() {
      return client.listSlices();
   }

   @Override
   public Iterable<Location> listLocations() {
      // Not using the adapter to determine locations
      return ImmutableSet.<Location>of();
   }

   @Override
   public Slice getNode(String id) {
      int serverId = Integer.parseInt(id);
      return client.getSlice(serverId);
   }
   
   @Override
   public Image getImage(String id) {
      int imageId = Integer.parseInt(id);
      return client.getImage(imageId);
   }
   
   @Override
   public void destroyNode(String id) {
      int serverId = Integer.parseInt(id);
      // if false server wasn't around in the first place
      client.destroySlice(serverId);
   }

   @Override
   public void rebootNode(String id) {
      int sliceId = Integer.parseInt(id);
      client.hardRebootSlice(sliceId);
   }

   @Override
   public void resumeNode(String id) {
      throw new UnsupportedOperationException("suspend not supported");
   }

   @Override
   public void suspendNode(String id) {
      throw new UnsupportedOperationException("suspend not supported");
   }

}
