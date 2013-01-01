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

import static org.jclouds.nodepool.config.NodePoolProperties.BACKEND_GROUP;
import static org.jclouds.nodepool.config.NodePoolProperties.BACKEND_MODULES;
import static org.jclouds.nodepool.config.NodePoolProperties.MAX_SIZE;
import static org.jclouds.nodepool.config.NodePoolProperties.METADATA_CONTAINER;
import static org.jclouds.nodepool.config.NodePoolProperties.MIN_SIZE;
import static org.jclouds.nodepool.config.NodePoolProperties.POOL_ADMIN_ACCESS;
import static org.jclouds.nodepool.config.NodePoolProperties.REMOVE_DESTROYED;

import java.io.File;
import java.net.URI;
import java.util.Properties;

import org.jclouds.apis.internal.BaseApiMetadata;
import org.jclouds.nodepool.config.BindBackendComputeService;
import org.jclouds.nodepool.config.BindInputStreamToFilesystemBlobStore;
import org.jclouds.nodepool.config.NodePoolComputeServiceContextModule;
import org.jclouds.rest.internal.BaseRestApiMetadata;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

public class NodePoolApiMetadata extends BaseApiMetadata {

   @Override
   public Builder toBuilder() {
      return new Builder().fromApiMetadata(this);
   }

   public NodePoolApiMetadata() {
      super(new Builder());
   }

   protected NodePoolApiMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = BaseRestApiMetadata.defaultProperties();
      properties.setProperty(BACKEND_GROUP, "nodepool");
      properties.setProperty(METADATA_CONTAINER, "nodes");
      properties.setProperty(BACKEND_MODULES,
               "org.jclouds.logging.slf4j.config.SLF4JLoggingModule,org.jclouds.sshj.config.SshjSshClientModule");
      properties.setProperty(MAX_SIZE, 10 + "");
      properties.setProperty(MIN_SIZE, 5 + "");
      properties.setProperty(REMOVE_DESTROYED, "true");
      // by default use the current user's user and private key if one exists, if not the properties
      // will need to be set (no default passwords)
      if (new File(System.getProperty("user.home") + "/.ssh/id_rsa").exists()) {
         properties.setProperty(POOL_ADMIN_ACCESS, "adminUsername=" + System.getProperty("user.name")
                  + ",adminPrivateKeyFile=" + System.getProperty("user.home") + "/.ssh/id_rsa");
      }
      return properties;
   }

   public static class Builder extends BaseApiMetadata.Builder<Builder> {
      protected Builder() {
         id("nodepool")
                  .name("node pool provider wrapper")
                  .identityName("backend identity")
                  .endpointName("backend endpoint")
                  .defaultEndpoint("fixme")
                  .documentation(URI.create("http://www.jclouds.org/documentation/userguide/compute"))
                  .view(NodePoolComputeServiceContext.class)
                  .defaultModules(
                           ImmutableSet.<Class<? extends Module>> builder()
                                    .add(NodePoolComputeServiceContextModule.class)
                                    .add(BindInputStreamToFilesystemBlobStore.class)
                                    .add(BindBackendComputeService.class).build())
                  .defaultProperties(NodePoolApiMetadata.defaultProperties());
      }

      @Override
      public NodePoolApiMetadata build() {
         return new NodePoolApiMetadata(this);
      }

      @Override
      protected Builder self() {
         return this;
      }
   }
}
