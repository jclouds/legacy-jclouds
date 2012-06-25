package org.jclouds.nodepool.internal;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.nodepool.config.NodePoolComputServiceContextModule.Internal;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

@Singleton
public class NodeMetadataStoreCache implements NodeMetadataStore {

   private Map<String, NodeMetadata> frontendMetadataCache = new HashMap<String, NodeMetadata>();
   private NodeMetadataStore backing;

   @Inject
   public NodeMetadataStoreCache(@Internal NodeMetadataStore backing) {
      this.backing = backing;
   }

   @Override
   public synchronized NodeMetadata store(NodeMetadata backendNode, TemplateOptions userOptions, String userGroup) {
      NodeMetadata frontEndNode = backing.store(backendNode, userOptions, userGroup);
      frontendMetadataCache.put(backendNode.getId(), frontEndNode);
      return frontEndNode;
   }

   @Override
   public synchronized void deleteMapping(String backendNodeId) {
      frontendMetadataCache.remove(backendNodeId);
      backing.deleteMapping(backendNodeId);

   }

   @Override
   public synchronized void deleteAllMappings() {
      frontendMetadataCache.clear();
      backing.deleteAllMappings();
   }

   @Override
   public synchronized NodeMetadata load(NodeMetadata backendNode) {
      NodeMetadata frontendNode = frontendMetadataCache.get(backendNode.getId());
      if (frontendNode == null) {
         frontendNode = backing.load(backendNode);
         if (frontendNode != null) {
            frontendMetadataCache.put(backendNode.getId(), frontendNode);
         }
      }
      return frontendNode;
   }

   @Override
   public synchronized Set<NodeMetadata> loadAll(Set<NodeMetadata> backendNodes) {
      return ImmutableSet.copyOf(Iterables.transform(backendNodes, new Function<NodeMetadata, NodeMetadata>() {
         @Override
         public NodeMetadata apply(NodeMetadata input) {
            return load(input);
         }
      }));
   }

}
