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

package org.jclouds.byon.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.byon.Node;
import org.jclouds.byon.functions.NodeToNodeMetadata;
import org.jclouds.compute.JCloudsNativeComputeServiceAdapter;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import org.jclouds.location.suppliers.JustProvider;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class BYONComputeServiceAdapter implements JCloudsNativeComputeServiceAdapter {
   private final Supplier<Map<String, Node>> nodes;
   private final NodeToNodeMetadata converter;
   private final JustProvider locationSupplier;

   @Inject
   public BYONComputeServiceAdapter(Supplier<Map<String, Node>> nodes, NodeToNodeMetadata converter,
            JustProvider locationSupplier) {
      this.nodes = checkNotNull(nodes, "nodes");
      this.converter = checkNotNull(converter, "converter");
      this.locationSupplier = checkNotNull(locationSupplier, "locationSupplier");
   }

   @Override
   public NodeMetadata createNodeWithGroupEncodedIntoNameThenStoreCredentials(String tag, String name, Template template,
            Map<String, Credentials> credentialStore) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Iterable<Hardware> listHardwareProfiles() {
      return ImmutableSet.<Hardware> of();
   }

   @Override
   public Iterable<Image> listImages() {
      return ImmutableSet.<Image> of();
   }

   @Override
   public Iterable<NodeMetadata> listNodes() {
      return Iterables.transform(nodes.get().values(), converter);
   }

   @SuppressWarnings("unchecked")
   @Override
   public Iterable<Location> listLocations() {
      return (Iterable<Location>) locationSupplier.get();
   }

   @Override
   public NodeMetadata getNode(String id) {
      Node node = nodes.get().get(id);
      return node != null ? converter.apply(node) : null;
   }

   @Override
   public void destroyNode(final String id) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void rebootNode(String id) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void resumeNode(String id) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void suspendNode(String id) {
      throw new UnsupportedOperationException();
   }
}