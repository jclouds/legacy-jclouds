package org.jclouds.chef.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.security.PrivateKey;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.chef.ChefContext;
import org.jclouds.chef.ChefService;
import org.jclouds.chef.domain.Node;
import org.jclouds.chef.reference.ChefConstants;
import org.jclouds.chef.strategy.CleanupStaleNodesAndClients;
import org.jclouds.chef.strategy.CreateNodeAndPopulateAutomaticAttributes;
import org.jclouds.chef.strategy.DeleteAllNodesInList;
import org.jclouds.chef.strategy.GetNodes;
import org.jclouds.chef.strategy.UpdateAutomaticAttributesOnNode;
import org.jclouds.io.Payloads;
import org.jclouds.io.payloads.RSADecryptingPayload;
import org.jclouds.io.payloads.RSAEncryptingPayload;
import org.jclouds.logging.Logger;

import com.google.common.base.Predicate;
import com.google.common.io.ByteStreams;
import com.google.common.io.InputSupplier;

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
   private final DeleteAllNodesInList deleteAllNodesInList;
   private final GetNodes getNodes;
   private final UpdateAutomaticAttributesOnNode updateAutomaticAttributesOnNode;
   private final Provider<PrivateKey> privateKey;

   @Inject
   protected BaseChefService(ChefContext chefContext, CleanupStaleNodesAndClients cleanupStaleNodesAndClients,
         CreateNodeAndPopulateAutomaticAttributes createNodeAndPopulateAutomaticAttributes,
         DeleteAllNodesInList deleteAllNodesInList, GetNodes getNodes,
         UpdateAutomaticAttributesOnNode updateAutomaticAttributesOnNode, Provider<PrivateKey> privateKey) {
      this.chefContext = checkNotNull(chefContext, "chefContext");
      this.cleanupStaleNodesAndClients = checkNotNull(cleanupStaleNodesAndClients, "cleanupStaleNodesAndClients");
      this.createNodeAndPopulateAutomaticAttributes = checkNotNull(createNodeAndPopulateAutomaticAttributes,
            "createNodeAndPopulateAutomaticAttributes");
      this.deleteAllNodesInList = checkNotNull(deleteAllNodesInList, "deleteAllNodesInList");
      this.getNodes = checkNotNull(getNodes, "getNodes");
      this.updateAutomaticAttributesOnNode = checkNotNull(updateAutomaticAttributesOnNode,
            "updateAutomaticAttributesOnNode");
      this.privateKey = checkNotNull(privateKey, "privateKey");
   }

   @Override
   public void cleanupStaleNodesAndClients(String prefix, int secondsStale) {
      cleanupStaleNodesAndClients.execute(prefix, secondsStale);
   }

   @Override
   public Node createNodeAndPopulateAutomaticAttributes(String nodeName, Iterable<String> runList) {
      return createNodeAndPopulateAutomaticAttributes.execute(nodeName, runList);
   }

   @Override
   public void deleteAllNodesInList(Iterable<String> names) {
      deleteAllNodesInList.execute(names);
   }

   @Override
   public Iterable<? extends Node> listNodesDetails() {
      return getNodes.execute();
   }

   @Override
   public Iterable<? extends Node> listNodesDetailsMatching(Predicate<String> nodeNameSelector) {
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

   @Override
   public byte[] decrypt(InputSupplier<? extends InputStream> supplier) throws IOException {
      return ByteStreams.toByteArray(new RSADecryptingPayload(Payloads.newPayload(supplier.getInput()), privateKey
            .get()));
   }

   @Override
   public byte[] encrypt(InputSupplier<? extends InputStream> supplier) throws IOException {
      return ByteStreams.toByteArray(new RSAEncryptingPayload(Payloads.newPayload(supplier.getInput()), privateKey
            .get()));
   }

}