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

import org.jclouds.compute.domain.Image.Status;
import org.jclouds.compute.domain.internal.ImageImpl;
import org.jclouds.domain.Location;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.javax.annotation.Nullable;

/**
 * @author Adrian Cole
 */
public class ImageBuilder extends ComputeMetadataBuilder {
   private OperatingSystem operatingSystem;
   private Status status;
   private String backendStatus;
   private String version;
   private String description;
   private LoginCredentials defaultLoginCredentials;

   public ImageBuilder() {
      super(ComputeType.IMAGE);
   }

   public ImageBuilder operatingSystem(OperatingSystem operatingSystem) {
      this.operatingSystem = checkNotNull(operatingSystem, "operatingSystem");
      return this;
   }
   
   public ImageBuilder status(Status status) {
      this.status = checkNotNull(status, "status");
      return this;
   }
   
   public ImageBuilder backendStatus(@Nullable String backendStatus) {
      this.backendStatus = backendStatus;
      return this;
   }
   
   public ImageBuilder version(@Nullable String version) {
      this.version = version;
      return this;
   }

   public ImageBuilder description(String description) {
      this.description = checkNotNull(description, "description");
      return this;
   }

   public ImageBuilder defaultCredentials(@Nullable LoginCredentials defaultLoginCredentials) {
      this.defaultLoginCredentials = defaultLoginCredentials;
      return this;
   }

   @Override
   public ImageBuilder id(String id) {
      return ImageBuilder.class.cast(super.id(id));
   }

   public ImageBuilder tags(Iterable<String> tags) {
      return ImageBuilder.class.cast(super.tags(tags));
   }

   @Override
   public ImageBuilder ids(String id) {
      return ImageBuilder.class.cast(super.ids(id));
   }

   @Override
   public ImageBuilder providerId(String providerId) {
      return ImageBuilder.class.cast(super.providerId(providerId));
   }

   @Override
   public ImageBuilder name(String name) {
      return ImageBuilder.class.cast(super.name(name));
   }

   @Override
   public ImageBuilder location(Location location) {
      return ImageBuilder.class.cast(super.location(location));
   }

   @Override
   public ImageBuilder uri(URI uri) {
      return ImageBuilder.class.cast(super.uri(uri));
   }

   @Override
   public ImageBuilder userMetadata(Map<String, String> userMetadata) {
      return ImageBuilder.class.cast(super.userMetadata(userMetadata));
   }

   @Override
   public Image build() {
      return new ImageImpl(providerId, name, id, location, uri, userMetadata, tags, operatingSystem, status,
               backendStatus, description, version, defaultLoginCredentials);
   }

   public static ImageBuilder fromImage(Image image) {
      return new ImageBuilder().providerId(image.getProviderId()).name(image.getName()).id(image.getId()).location(
               image.getLocation()).uri(image.getUri()).userMetadata(image.getUserMetadata()).tags(image.getTags())
               .version(image.getVersion()).description(image.getDescription()).operatingSystem(
                        image.getOperatingSystem()).status(image.getStatus()).backendStatus(image.getBackendStatus())
               .defaultCredentials(image.getDefaultCredentials());
   }

}
