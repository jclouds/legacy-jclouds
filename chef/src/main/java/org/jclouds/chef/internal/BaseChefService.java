package org.jclouds.chef.internal;

import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.chef.ChefService;
import org.jclouds.chef.domain.Node;
import org.jclouds.chef.reference.ChefConstants;
import org.jclouds.chef.strategy.CleanupStaleNodesAndClients;
import org.jclouds.chef.strategy.CreateNodeAndPopulateAutomaticAttributes;
import org.jclouds.chef.strategy.DeleteAllClientsAndNodesInList;
import org.jclouds.chef.strategy.GetNodes;
import org.jclouds.chef.strategy.UpdateAutomaticAttributesOnNode;
import org.jclouds.logging.Logger;

import com.google.common.base.Predicate;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class BaseChefService implements ChefService {

   @Resource
   @Named(ChefConstants.CHEF_LOGGER)
   protected Logger logger = Logger.NULL;

   private final CleanupStaleNodesAndClients cleanupStaleNodesAndClients;
   private final CreateNodeAndPopulateAutomaticAttributes createNodeAndPopulateAutomaticAttributes;
   private final DeleteAllClientsAndNodesInList deleteAllClientsAndNodesInList;
   private final GetNodes getNodes;
   private final UpdateAutomaticAttributesOnNode updateAutomaticAttributesOnNode;

   @Inject
   protected BaseChefService(CleanupStaleNodesAndClients cleanupStaleNodesAndClients,
         CreateNodeAndPopulateAutomaticAttributes createNodeAndPopulateAutomaticAttributes,
         DeleteAllClientsAndNodesInList deleteAllClientsAndNodesInList, GetNodes getNodes,
         UpdateAutomaticAttributesOnNode updateAutomaticAttributesOnNode) {
      this.cleanupStaleNodesAndClients = cleanupStaleNodesAndClients;
      this.createNodeAndPopulateAutomaticAttributes = createNodeAndPopulateAutomaticAttributes;
      this.deleteAllClientsAndNodesInList = deleteAllClientsAndNodesInList;
      this.getNodes = getNodes;
      this.updateAutomaticAttributesOnNode = updateAutomaticAttributesOnNode;
   }

   @Override
   public void cleanupStaleNodesAndClients(String prefix, int minutesStale) {
      cleanupStaleNodesAndClients.execute(prefix, minutesStale);
   }

   @Override
   public void createNodeAndPopulateAutomaticAttributes(String nodeName, Iterable<String> runList) {
      createNodeAndPopulateAutomaticAttributes.execute(nodeName, runList);
   }

   @Override
   public void deleteAllClientsAndNodesInList(Iterable<String> names) {
      deleteAllClientsAndNodesInList.execute(names);
   }

   @Override
   public Set<Node> getNodes() {
      return getNodes.execute();
   }

   @Override
   public Set<Node> getNodesWithNamesMatching(Predicate<String> nodeNameSelector) {
      return getNodes.execute(nodeNameSelector);
   }

   @Override
   public Set<Node> getNodesNamed(Iterable<String> names) {
      return getNodes.execute(names);
   }

   @Override
   public void updateAutomaticAttributesOnNode(String nodeName) {
      updateAutomaticAttributesOnNode.execute(nodeName);
   }

}