package org.jclouds.nodepool.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;

import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.json.Json;
import org.jclouds.util.Strings2;

import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
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

   private Map<String, InputStream> storage;
   private final Json json;

   private static class JsonUserNodeMetadata {
      private String userGroup;
      private Set<String> userTags;
      private Map<String, String> userMetadata;
      private String user;
      private String password;
      private String privateKey;
      private Boolean authenticateSudo;
   }

   @Inject
   public JsonNodeMetadataStore(Map<String, InputStream> storage, Json json) {
      this.storage = storage;
      this.json = json;
   }

   @Override
   public NodeMetadata store(NodeMetadata backendNodeMetadata, TemplateOptions userOptions, String userGroup) {
      checkNotNull(backendNodeMetadata);
      checkNotNull(userGroup);
      checkNotNull(userOptions);
      JsonUserNodeMetadata jsonMetadata = new JsonUserNodeMetadata();
      jsonMetadata.user = userOptions.getLoginUser();
      jsonMetadata.password = userOptions.getLoginPassword();
      jsonMetadata.privateKey = userOptions.getLoginPrivateKey();
      jsonMetadata.authenticateSudo = userOptions.shouldAuthenticateSudo();
      jsonMetadata.userMetadata = userOptions.getUserMetadata();
      jsonMetadata.userTags = userOptions.getTags();
      jsonMetadata.userGroup = userGroup;
      storage.put(backendNodeMetadata.getId(), Strings2.toInputStream(json.toJson(jsonMetadata)));
      return buildFromJsonAndBackendMetadata(backendNodeMetadata, jsonMetadata);
   }

   @Override
   public NodeMetadata load(NodeMetadata backendNodeMetadata) {
      try {
         InputStream storedMetadata = storage.get(checkNotNull(backendNodeMetadata).getId());
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

   private NodeMetadata buildFromJsonAndBackendMetadata(NodeMetadata backendNodeMetadata,
            JsonUserNodeMetadata jsonMetadata) {
      return NodeMetadataBuilder
               .fromNodeMetadata(backendNodeMetadata)
               .tags(jsonMetadata.userTags)
               .group(jsonMetadata.userGroup)
               .userMetadata(jsonMetadata.userMetadata)
               .credentials(
                        new LoginCredentials(jsonMetadata.user, jsonMetadata.password, jsonMetadata.privateKey,
                                 jsonMetadata.authenticateSudo)).build();
   }

   @Override
   public void deleteAllMappings() {
      storage.clear();
   }

   @Override
   public void deleteMapping(String backendNodeId) {
      storage.remove(backendNodeId);
   }

   public Set<NodeMetadata> loadAll(Set<NodeMetadata> backendNodes) {
      return ImmutableSet.copyOf(Iterables.transform(backendNodes, new Function<NodeMetadata, NodeMetadata>() {
         @Override
         public NodeMetadata apply(NodeMetadata input) {
            return load(input);
         }
      }));
   }
}
