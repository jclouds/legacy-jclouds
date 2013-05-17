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
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.domain.Location;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects.ToStringHelper;

/**
 * @author Adrian Cole
 */
public class ImageImpl extends ComputeMetadataImpl implements Image {

   private final OperatingSystem operatingSystem;
   private final Status status;
   private final String backendStatus;
   private final String version;
   private final String description;
   private final LoginCredentials defaultCredentials;

   public ImageImpl(String providerId, String name, String id, Location location, URI uri,
            Map<String, String> userMetadata, Set<String> tags, OperatingSystem operatingSystem, Image.Status status,
            @Nullable String backendStatus, String description, @Nullable String version,
            @Nullable LoginCredentials defaultCredentials) {
      super(ComputeType.IMAGE, providerId, name, id, location, uri, userMetadata, tags);
      this.operatingSystem = checkNotNull(operatingSystem, "operatingSystem");
      this.status = checkNotNull(status, "status");
      this.backendStatus = backendStatus;
      this.version = version;
      this.description = checkNotNull(description, "description");
      this.defaultCredentials = defaultCredentials;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public OperatingSystem getOperatingSystem() {
      return operatingSystem;
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
   public String getVersion() {
      return version;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getDescription() {
      return description;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public LoginCredentials getDefaultCredentials() {
      return defaultCredentials;
   }

   // equals and toString from super are sufficient to establish identity equivalence

   protected ToStringHelper string() {
      ToStringHelper helper = computeToStringPrefix();
      helper.add("os", getOperatingSystem()).add("description", getDescription()).add("version", getVersion())
               .add("status", formatStatus(this))
               .add("loginUser", defaultCredentials != null ? defaultCredentials.identity : null);
      return addComputeToStringSuffix(helper);
   }

}
