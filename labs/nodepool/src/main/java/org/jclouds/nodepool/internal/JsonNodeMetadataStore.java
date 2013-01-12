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
package org.jclouds.nodepool.internal;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;

import javax.inject.Named;

import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.json.Json;
import org.jclouds.util.Strings2;

import com.google.common.base.Supplier;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * An implementation of {@link NodeMetadataStore} that stores all that is needed by building a json
 * string.
 * 
 * @author David Alves
 * 
 */
@Singleton
public class JsonNodeMetadataStore implements NodeMetadataStore {

   private Supplier<Map<String, InputStream>> storage;
   private final Json json;

   private static class JsonUserNodeMetadata {
      private String group;
      private Set<String> tags;
      private Map<String, String> userMetadata;
      private String user;
      private String password;
      private String privateKey;
      private boolean authenticateSudo;
   }

   @Inject
   public JsonNodeMetadataStore(@Named("METADATA") Supplier<Map<String, InputStream>> storage, Json json) {
      this.storage = storage;
      this.json = json;
   }

   @Override
   public NodeMetadata store(NodeMetadata backendNodeMetadata, TemplateOptions userOptions, String userGroup) {
      checkNotNull(backendNodeMetadata);
      checkNotNull(userGroup);
      checkNotNull(userOptions);
      checkNotNull(userOptions.getLoginUser());
      checkState(userOptions.getLoginPassword() != null || userOptions.getLoginPrivateKey() != null);
      JsonUserNodeMetadata jsonMetadata = new JsonUserNodeMetadata();
      jsonMetadata.user = userOptions.getLoginUser();
      jsonMetadata.password = userOptions.getLoginPassword();
      jsonMetadata.privateKey = userOptions.getLoginPrivateKey();
      jsonMetadata.authenticateSudo = userOptions.shouldAuthenticateSudo() != null ? userOptions
               .shouldAuthenticateSudo().booleanValue() : false;
      jsonMetadata.userMetadata = userOptions.getUserMetadata();
      jsonMetadata.tags = userOptions.getTags();
      jsonMetadata.group = userGroup;
      storage.get().put(backendNodeMetadata.getId(), Strings2.toInputStream(json.toJson(jsonMetadata)));
      return buildFromJsonAndBackendMetadata(backendNodeMetadata, jsonMetadata);
   }

   @Override
   public NodeMetadata load(NodeMetadata backendNodeMetadata) {
      try {
         InputStream storedMetadata = storage.get().get(checkNotNull(backendNodeMetadata).getId());
         if (storedMetadata == null) {
            return null;
         }
         String jsonMetadataAsString = Strings2.toStringAndClose(storedMetadata);
         JsonUserNodeMetadata jsonMetadata = json.fromJson(jsonMetadataAsString, JsonUserNodeMetadata.class);
         return buildFromJsonAndBackendMetadata(backendNodeMetadata, jsonMetadata);
      } catch (IOException e) {
         throw Throwables.propagate(e);
      }
   }

   @Override
   public Set<NodeMetadata> loadAll(Set<NodeMetadata> backendNodes) {
      if (backendNodes == null || backendNodes.isEmpty()) {
         return ImmutableSet.of();
      }
      final Set<NodeMetadata> loadedSet = Sets.newLinkedHashSet();
      for (NodeMetadata input : backendNodes) {
         NodeMetadata loaded = load(input);
         if (loaded != null) {
            loadedSet.add(loaded);
         }

      }
      return loadedSet;
   }

   private NodeMetadata buildFromJsonAndBackendMetadata(NodeMetadata backendNodeMetadata,
            JsonUserNodeMetadata jsonMetadata) {
      return NodeMetadataBuilder
               .fromNodeMetadata(backendNodeMetadata)
               .tags(jsonMetadata.tags)
               .group(jsonMetadata.group)
               .userMetadata(jsonMetadata.userMetadata)
               .credentials(LoginCredentials.builder()
                                            .user(jsonMetadata.user)
                                            .privateKey(jsonMetadata.privateKey)
                                            .password(jsonMetadata.password)
                                            .authenticateSudo(jsonMetadata.authenticateSudo).build()).build();
   }

   @Override
   public void deleteAllMappings() {
      storage.get().clear();
   }

   @Override
   public void deleteMapping(String backendNodeId) {
      storage.get().remove(backendNodeId);
   }

}
