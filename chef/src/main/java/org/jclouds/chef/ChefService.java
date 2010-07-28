package org.jclouds.chef;

import java.util.Set;

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

   void cleanupStaleNodesAndClients(String prefix, int minutesStale);

   void createNodeAndPopulateAutomaticAttributes(String nodeName, Iterable<String> runList);

   void deleteAllClientsAndNodesInList(Iterable<String> names);

   Set<Node> getNodes();

   Set<Node> getNodesWithNamesMatching(Predicate<String> nodeNameSelector);

   Set<Node> getNodesNamed(Iterable<String> names);

   void updateAutomaticAttributesOnNode(String nodeName);
}
