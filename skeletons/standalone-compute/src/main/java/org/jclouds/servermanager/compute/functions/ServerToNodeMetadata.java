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
package org.jclouds.servermanager.compute.functions;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.FluentIterable.from;

import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.collect.Memoized;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadata.Status;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.functions.GroupNamingConvention;
import org.jclouds.compute.predicates.HardwarePredicates;
import org.jclouds.compute.predicates.ImagePredicates;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.location.predicates.LocationPredicates;
import org.jclouds.servermanager.Server;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * @author Adrian Cole
 */
@Singleton
public class ServerToNodeMetadata implements Function<Server, NodeMetadata> {

   public static final Map<Server.Status, Status> serverStatusToNodeStatus = ImmutableMap
         .<Server.Status, Status> builder().put(Server.Status.ACTIVE, Status.RUNNING)//
         .put(Server.Status.BUILD, Status.PENDING)//
         .put(Server.Status.TERMINATED, Status.TERMINATED)//
         .put(Server.Status.UNRECOGNIZED, Status.UNRECOGNIZED)//
         .build();

   private final Supplier<Set<? extends Hardware>> hardware;
   private final Supplier<Set<? extends Location>> locations;
   private final Supplier<Set<? extends Image>> images;
   private final Map<String, Credentials> credentialStore;
   private final GroupNamingConvention nodeNamingConvention;

   @Inject
   ServerToNodeMetadata(Map<String, Credentials> credentialStore, @Memoized Supplier<Set<? extends Hardware>> hardware,
         @Memoized Supplier<Set<? extends Location>> locations, @Memoized Supplier<Set<? extends Image>> images,
         GroupNamingConvention.Factory namingConvention) {
      this.nodeNamingConvention = checkNotNull(namingConvention, "namingConvention").createWithoutPrefix();
      this.credentialStore = checkNotNull(credentialStore, "credentialStore");
      this.hardware = checkNotNull(hardware, "hardware");
      this.locations = checkNotNull(locations, "locations");
      this.images = checkNotNull(images, "images");
   }

   @Override
   public NodeMetadata apply(Server from) {
      // convert the result object to a jclouds NodeMetadata
      NodeMetadataBuilder builder = new NodeMetadataBuilder();
      builder.ids(from.id + "");
      builder.name(from.name);
      builder.location(from(locations.get()).firstMatch(LocationPredicates.idEquals(from.datacenter)).orNull());
      builder.group(nodeNamingConvention.groupInUniqueNameOrNull(from.name));
      builder.imageId(from.imageId + "");
      Image image = from(images.get()).firstMatch(ImagePredicates.idEquals(from.imageId + "")).orNull();
      if (image != null)
         builder.operatingSystem(image.getOperatingSystem());
      builder.hardware(from(hardware.get()).firstMatch(HardwarePredicates.idEquals(from.hardwareId + "")).orNull());
      builder.status(serverStatusToNodeStatus.get(from.status));
      builder.publicAddresses(ImmutableSet.<String> of(from.publicAddress));
      builder.privateAddresses(ImmutableSet.<String> of(from.privateAddress));
      builder.credentials(LoginCredentials.fromCredentials(credentialStore.get(from.id + "")));
      return builder.build();
   }
}
