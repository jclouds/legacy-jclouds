package org.jclouds.chef;

import org.jclouds.chef.domain.Node;
import org.jclouds.chef.internal.BaseChefService;

import com.google.common.base.Predicate;
import com.google.inject.ImplementedBy;

/**
 * Provides high level chef operations
 * 
 * @author Adrian Cole
 */
@ImplementedBy(BaseChefService.class)
public interface ChefService {
   /**
    * @return a reference to the context that created this.
    */
   ChefContext getContext();

   void cleanupStaleNodesAndClients(String prefix, int secondsStale);

   void createNodeAndPopulateAutomaticAttributes(String nodeName, Iterable<String> runList);

   void deleteAllClientsAndNodesInList(Iterable<String> names);

   Iterable<? extends Node> getNodes();

   Iterable<? extends Node> getNodesWithNamesMatching(Predicate<String> nodeNameSelector);

   Iterable<? extends Node> getNodesNamed(Iterable<String> names);

   void updateAutomaticAttributesOnNode(String nodeName);
}
