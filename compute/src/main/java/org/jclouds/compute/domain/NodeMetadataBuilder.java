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
package org.jclouds.compute.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Map;
import java.util.Set;

import org.jclouds.compute.domain.NodeMetadata.Status;
import org.jclouds.compute.domain.internal.NodeMetadataImpl;
import org.jclouds.domain.Location;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.javax.annotation.Nullable;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

/**
 * @author Adrian Cole
 */
public class NodeMetadataBuilder extends ComputeMetadataBuilder {
   private Status status;
   private String backendStatus;
   private Set<String> publicAddresses = Sets.newLinkedHashSet();
   private Set<String> privateAddresses = Sets.newLinkedHashSet();
   @Nullable
   private LoginCredentials credentials;
   @Nullable
   private String group;
   private int loginPort = 22;
   @Nullable
   private String imageId;
   @Nullable
   private Hardware hardware;
   @Nullable
   private OperatingSystem os;
   @Nullable
   private String hostname;

   public NodeMetadataBuilder() {
      super(ComputeType.NODE);
   }

   public NodeMetadataBuilder loginPort(int loginPort) {
      this.loginPort = loginPort;
      return this;
   }
  
   public NodeMetadataBuilder status(Status status) {
      this.status = checkNotNull(status, "status");
      return this;
   }
   
   public NodeMetadataBuilder backendStatus(@Nullable String backendStatus) {
      this.backendStatus = backendStatus;
      return this;
   }

   public NodeMetadataBuilder publicAddresses(Iterable<String> publicAddresses) {
      this.publicAddresses = ImmutableSet.copyOf(checkNotNull(publicAddresses, "publicAddresses"));
      return this;
   }

   public NodeMetadataBuilder privateAddresses(Iterable<String> privateAddresses) {
      this.privateAddresses = ImmutableSet.copyOf(checkNotNull(privateAddresses, "privateAddresses"));
      return this;
   }

   public NodeMetadataBuilder credentials(@Nullable LoginCredentials credentials) {
      this.credentials = credentials;
      return this;
   }

   public NodeMetadataBuilder group(@Nullable String group) {
      this.group = group;
      return this;
   }

   public NodeMetadataBuilder imageId(@Nullable String imageId) {
      this.imageId = imageId;
      return this;
   }

   public NodeMetadataBuilder hardware(@Nullable Hardware hardware) {
      this.hardware = hardware;
      return this;
   }

   public NodeMetadataBuilder operatingSystem(@Nullable OperatingSystem os) {
      this.os = os;
      return this;
   }

   public NodeMetadataBuilder hostname(String hostname) {
      this.hostname = hostname;
      return this;
   }

   @Override
   public NodeMetadataBuilder id(String id) {
      return NodeMetadataBuilder.class.cast(super.id(id));
   }

   @Override
   public NodeMetadataBuilder tags(Iterable<String> tags) {
      return NodeMetadataBuilder.class.cast(super.tags(tags));
   }

   @Override
   public NodeMetadataBuilder ids(String id) {
      return NodeMetadataBuilder.class.cast(super.ids(id));
   }

   @Override
   public NodeMetadataBuilder providerId(String providerId) {
      return NodeMetadataBuilder.class.cast(super.providerId(providerId));
   }

   @Override
   public NodeMetadataBuilder name(String name) {
      return NodeMetadataBuilder.class.cast(super.name(name));
   }

   @Override
   public NodeMetadataBuilder location(Location location) {
      return NodeMetadataBuilder.class.cast(super.location(location));
   }

   @Override
   public NodeMetadataBuilder uri(URI uri) {
      return NodeMetadataBuilder.class.cast(super.uri(uri));
   }

   @Override
   public NodeMetadataBuilder userMetadata(Map<String, String> userMetadata) {
      return NodeMetadataBuilder.class.cast(super.userMetadata(userMetadata));
   }

   @Override
   public NodeMetadata build() {
      return new NodeMetadataImpl(providerId, name, id, location, uri, userMetadata, tags, group, hardware, imageId,
               os, status, backendStatus, loginPort, publicAddresses, privateAddresses, credentials, hostname);
   }

   public static NodeMetadataBuilder fromNodeMetadata(NodeMetadata node) {
      return new NodeMetadataBuilder().providerId(node.getProviderId()).name(node.getName()).id(node.getId()).location(
               node.getLocation()).uri(node.getUri()).userMetadata(node.getUserMetadata()).tags(node.getTags()).group(
               node.getGroup()).hardware(node.getHardware()).imageId(node.getImageId()).operatingSystem(
               node.getOperatingSystem()).status(node.getStatus()).backendStatus(node.getBackendStatus()).loginPort(
               node.getLoginPort()).publicAddresses(node.getPublicAddresses()).privateAddresses(
               node.getPrivateAddresses()).credentials(node.getCredentials()).hostname(node.getHostname());
   }

}
