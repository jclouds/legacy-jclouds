/*
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
package org.jclouds.nodepool;

import static org.jclouds.nodepool.config.PoolingComputeServiceProperties.NODEPOOL_BACKING_GROUP_PROPERTY;
import static org.jclouds.nodepool.config.PoolingComputeServiceProperties.NODEPOOL_MAX_SIZE_PROPERTY;
import static org.jclouds.nodepool.config.PoolingComputeServiceProperties.NODEPOOL_MIN_SIZE_PROPERTY;
import static org.jclouds.nodepool.config.PoolingComputeServiceProperties.NODEPOOL_REMOVE_DESTROYED_PROPERTY;

import java.net.URI;
import java.util.Properties;

import org.jclouds.apis.internal.BaseApiMetadata;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.rest.internal.BaseRestApiMetadata;

public class PoolingApiMetadata extends BaseApiMetadata {

   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return Builder.class.cast(builder().fromApiMetadata(this));
   }

   public PoolingApiMetadata() {
      super(builder());
   }

   protected PoolingApiMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = BaseRestApiMetadata.defaultProperties();
      properties.setProperty(NODEPOOL_BACKING_GROUP_PROPERTY, "nodepool");
      properties.setProperty(NODEPOOL_MAX_SIZE_PROPERTY, 10 + "");
      properties.setProperty(NODEPOOL_MIN_SIZE_PROPERTY, 5 + "");
      properties.setProperty(NODEPOOL_REMOVE_DESTROYED_PROPERTY, "false");
      return properties;
   }

   public static class Builder extends BaseApiMetadata.Builder {
      protected Builder() {
         id("pooled").name("node pool provider wrapper").identityName("Unused").defaultIdentity("pooled")
                  .defaultEndpoint("pooled")
                  .documentation(URI.create("http://www.jclouds.org/documentation/userguide/compute"))
                  .view(ComputeServiceContext.class).defaultProperties(PoolingApiMetadata.defaultProperties());
      }

      @Override
      public PoolingApiMetadata build() {
         return new PoolingApiMetadata(this);
      }

   }

}
