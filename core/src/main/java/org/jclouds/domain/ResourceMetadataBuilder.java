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
package org.jclouds.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

/**
 * 
 * @author Adrian Cole
 */
public abstract class ResourceMetadataBuilder<T extends Enum<T>> {
   protected String providerId;
   protected String name;
   protected Location location;
   protected URI uri;
   protected Map<String, String> userMetadata = Maps.newLinkedHashMap();

   public ResourceMetadataBuilder<T> providerId(String providerId) {
      this.providerId = providerId;
      return this;
   }

   public ResourceMetadataBuilder<T> name(String name) {
      this.name = name;
      return this;
   }

   public ResourceMetadataBuilder<T> location(Location location) {
      this.location = location;
      return this;
   }

   public ResourceMetadataBuilder<T> uri(URI uri) {
      this.uri = uri;
      return this;
   }

   public ResourceMetadataBuilder<T> userMetadata(Map<String, String> userMetadata) {
      this.userMetadata = ImmutableMap.copyOf(checkNotNull(userMetadata, "userMetadata"));
      return this;
   }
}
