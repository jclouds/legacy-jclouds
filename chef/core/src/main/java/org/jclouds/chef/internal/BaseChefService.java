package org.jclouds.chef.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.chef.ChefContext;
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

   private final ChefContext chefContext;
   private final CleanupStaleNodesAndClients cleanupStaleNodesAndClients;
   private final CreateNodeAndPopulateAutomaticAttributes createNodeAndPopulateAutomaticAttributes;
   private final DeleteAllClientsAndNodesInList deleteAllClientsAndNodesInList;
   private final GetNodes getNodes;
   private final UpdateAutomaticAttributesOnNode updateAutomaticAttributesOnNode;

   @Inject
   protected BaseChefService(ChefContext chefContext, CleanupStaleNodesAndClients cleanupStaleNodesAndClients,
         CreateNodeAndPopulateAutomaticAttributes createNodeAndPopulateAutomaticAttributes,
         DeleteAllClientsAndNodesInList deleteAllClientsAndNodesInList, GetNodes getNodes,
         UpdateAutomaticAttributesOnNode updateAutomaticAttributesOnNode) {
      this.chefContext = checkNotNull(chefContext, "chefContext");
      this.cleanupStaleNodesAndClients = checkNotNull(cleanupStaleNodesAndClients, "cleanupStaleNodesAndClients");
      this.createNodeAndPopulateAutomaticAttributes = checkNotNull(createNodeAndPopulateAutomaticAttributes,
            "createNodeAndPopulateAutomaticAttributes");
      this.deleteAllClientsAndNodesInList = checkNotNull(deleteAllClientsAndNodesInList,
            "deleteAllClientsAndNodesInList");
      this.getNodes = checkNotNull(getNodes, "getNodes");
      this.updateAutomaticAttributesOnNode = checkNotNull(updateAutomaticAttributesOnNode,
            "updateAutomaticAttributesOnNode");
   }

   @Override
   public void cleanupStaleNodesAndClients(String prefix, int secondsStale) {
      cleanupStaleNodesAndClients.execute(prefix, secondsStale);
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
   public Iterable<? extends Node> getNodes() {
      return getNodes.execute();
   }

   @Override
   public Iterable<? extends Node> getNodesWithNamesMatching(Predicate<String> nodeNameSelector) {
      return getNodes.execute(nodeNameSelector);
   }

   @Override
   public Iterable<? extends Node> getNodesNamed(Iterable<String> names) {
      return getNodes.execute(names);
   }

   @Override
   public void updateAutomaticAttributesOnNode(String nodeName) {
      updateAutomaticAttributesOnNode.execute(nodeName);
   }

   @Override
   public ChefContext getContext() {
      return chefContext;
   }

}