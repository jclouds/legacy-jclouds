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
package org.jclouds.compute.domain.internal;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.compute.util.ComputeServiceUtils.formatStatus;

import java.net.URI;
import java.util.Map;
import java.util.Set;

import org.jclouds.compute.domain.ComputeType;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.domain.Location;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableSet;

/**
 * @author Adrian Cole
 * @author Ivan Meredith
 */
public class NodeMetadataImpl extends ComputeMetadataImpl implements NodeMetadata {

   private final Status status;
   private final String backendStatus;
   private final int loginPort;
   private final Set<String> publicAddresses;
   private final Set<String> privateAddresses;
   @Nullable
   private final LoginCredentials credentials;
   @Nullable
   private final String group;
   @Nullable
   private final String imageId;
   @Nullable
   private final Hardware hardware;
   @Nullable
   private final OperatingSystem os;
   @Nullable
   private final String hostname;

   public NodeMetadataImpl(String providerId, String name, String id, Location location, URI uri,
            Map<String, String> userMetadata, Set<String> tags, @Nullable String group, @Nullable Hardware hardware,
            @Nullable String imageId, @Nullable OperatingSystem os, Status status, @Nullable String backendStatus,
            int loginPort, Iterable<String> publicAddresses, Iterable<String> privateAddresses,
            @Nullable LoginCredentials credentials, String hostname) {
      super(ComputeType.NODE, providerId, name, id, location, uri, userMetadata, tags);
      this.group = group;
      this.hardware = hardware;
      this.imageId = imageId;
      this.os = os;
      this.status = checkNotNull(status, "status");
      this.backendStatus = backendStatus;
      this.loginPort = loginPort;
      this.publicAddresses = ImmutableSet.copyOf(checkNotNull(publicAddresses, "publicAddresses"));
      this.privateAddresses = ImmutableSet.copyOf(checkNotNull(privateAddresses, "privateAddresses"));
      this.credentials = credentials;
      this.hostname = hostname;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getGroup() {
      return group;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Hardware getHardware() {
      return hardware;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public LoginCredentials getCredentials() {
      return credentials;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Set<String> getPublicAddresses() {
      return publicAddresses;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Set<String> getPrivateAddresses() {
      return privateAddresses;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Status getStatus() {
      return status;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getBackendStatus() {
      return backendStatus;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int getLoginPort() {
      return this.loginPort;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getImageId() {
      return imageId;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public OperatingSystem getOperatingSystem() {
      return os;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getHostname() {
      return hostname;
   }


   // equals and toString from super are sufficient to establish identity equivalence

   protected ToStringHelper string() {
      ToStringHelper helper = computeToStringPrefix();
      helper.add("group", getGroup()).add("imageId", getImageId()).add("os", getOperatingSystem())
               .add("status", formatStatus(this)).add("loginPort", getLoginPort()).add("hostname", getHostname());
      if (getPrivateAddresses().size() > 0)
         helper.add("privateAddresses", getPrivateAddresses());
      if (getPublicAddresses().size() > 0)
         helper.add("publicAddresses", getPublicAddresses());
      helper.add("hardware", getHardware()).add("loginUser", credentials != null ? credentials.identity : null);
      return addComputeToStringSuffix(helper);
   }
}
