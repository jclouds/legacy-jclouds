/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.chef.internal;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.chef.reference.ChefConstants.CHEF_BOOTSTRAP_DATABAG;

import java.io.IOException;
import java.io.InputStream;
import java.security.PrivateKey;
import java.util.List;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.chef.ChefContext;
import org.jclouds.chef.ChefService;
import org.jclouds.chef.domain.Client;
import org.jclouds.chef.domain.CookbookVersion;
import org.jclouds.chef.domain.DatabagItem;
import org.jclouds.chef.domain.Node;
import org.jclouds.chef.functions.RunListForTag;
import org.jclouds.chef.functions.TagToBootScript;
import org.jclouds.chef.reference.ChefConstants;
import org.jclouds.chef.strategy.CleanupStaleNodesAndClients;
import org.jclouds.chef.strategy.CreateNodeAndPopulateAutomaticAttributes;
import org.jclouds.chef.strategy.DeleteAllClientsInList;
import org.jclouds.chef.strategy.DeleteAllNodesInList;
import org.jclouds.chef.strategy.ListClients;
import org.jclouds.chef.strategy.ListCookbookVersions;
import org.jclouds.chef.strategy.ListNodes;
import org.jclouds.chef.strategy.UpdateAutomaticAttributesOnNode;
import org.jclouds.io.Payload;
import org.jclouds.io.Payloads;
import org.jclouds.io.payloads.RSADecryptingPayload;
import org.jclouds.io.payloads.RSAEncryptingPayload;
import org.jclouds.logging.Logger;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
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
   private final ListNodes listNodes;
   private final DeleteAllClientsInList deleteAllClientsInList;
   private final ListClients listClients;
   private final UpdateAutomaticAttributesOnNode updateAutomaticAttributesOnNode;
   private final Provider<PrivateKey> privateKey;
   private final TagToBootScript tagToBootScript;
   private final String databag;
   private final RunListForTag runListForTag;
   private final ListCookbookVersions listCookbookVersions;

   @Inject
   protected BaseChefService(ChefContext chefContext, CleanupStaleNodesAndClients cleanupStaleNodesAndClients,
            CreateNodeAndPopulateAutomaticAttributes createNodeAndPopulateAutomaticAttributes,
            DeleteAllNodesInList deleteAllNodesInList, ListNodes listNodes,
            DeleteAllClientsInList deleteAllClientsInList, ListClients listClients,
            ListCookbookVersions listCookbookVersions, UpdateAutomaticAttributesOnNode updateAutomaticAttributesOnNode,
            Provider<PrivateKey> privateKey, @Named(CHEF_BOOTSTRAP_DATABAG) String databag,
            TagToBootScript tagToBootScript, RunListForTag runListForTag) {
      this.chefContext = checkNotNull(chefContext, "chefContext");
      this.cleanupStaleNodesAndClients = checkNotNull(cleanupStaleNodesAndClients, "cleanupStaleNodesAndClients");
      this.createNodeAndPopulateAutomaticAttributes = checkNotNull(createNodeAndPopulateAutomaticAttributes,
               "createNodeAndPopulateAutomaticAttributes");
      this.deleteAllNodesInList = checkNotNull(deleteAllNodesInList, "deleteAllNodesInList");
      this.listNodes = checkNotNull(listNodes, "listNodes");
      this.deleteAllClientsInList = checkNotNull(deleteAllClientsInList, "deleteAllClientsInList");
      this.listClients = checkNotNull(listClients, "listClients");
      this.listCookbookVersions = checkNotNull(listCookbookVersions, "listCookbookVersions");
      this.updateAutomaticAttributesOnNode = checkNotNull(updateAutomaticAttributesOnNode,
               "updateAutomaticAttributesOnNode");
      this.privateKey = checkNotNull(privateKey, "privateKey");
      this.tagToBootScript = checkNotNull(tagToBootScript, "tagToBootScript");
      this.databag = checkNotNull(databag, "databag");
      this.runListForTag = checkNotNull(runListForTag, "runListForTag");
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
   public Iterable<? extends Node> listNodes() {
      return listNodes.execute();
   }

   @Override
   public Iterable<? extends Node> listNodesMatching(Predicate<String> nodeNameSelector) {
      return listNodes.execute(nodeNameSelector);
   }

   @Override
   public Iterable<? extends Node> listNodesNamed(Iterable<String> names) {
      return listNodes.execute(names);
   }

   @Override
   public void deleteAllClientsInList(Iterable<String> names) {
      deleteAllClientsInList.execute(names);
   }

   @Override
   public Iterable<? extends Client> listClientsDetails() {
      return listClients.execute();
   }

   @Override
   public Iterable<? extends Client> listClientsDetailsMatching(Predicate<String> clientNameSelector) {
      return listClients.execute(clientNameSelector);
   }

   @Override
   public Iterable<? extends Client> listClientsNamed(Iterable<String> names) {
      return listClients.execute(names);
   }

   @Override
   public Iterable<? extends CookbookVersion> listCookbookVersions() {
      return listCookbookVersions.execute();
   }

   @Override
   public Iterable<? extends CookbookVersion> listCookbookVersionsMatching(Predicate<String> cookbookNameSelector) {
      return listCookbookVersions.execute(cookbookNameSelector);
   }

   @Override
   public Iterable<? extends CookbookVersion> listCookbookVersionsNamed(Iterable<String> names) {
      return listCookbookVersions.execute(names);
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
   public Payload createClientAndBootstrapScriptForTag(String tag) {
      return tagToBootScript.apply(tag);
   }

   @Override
   public void updateRunListForTag(Iterable<String> runList, String tag) {
      try {
         chefContext.getApi().createDatabag(databag);
      } catch (IllegalStateException e) {

      }
      chefContext.getApi().updateDatabagItem(
               databag,
               new DatabagItem(tag, chefContext.utils().json().toJson(
                        ImmutableMap.<String, List<String>> of("run_list", Lists.newArrayList(runList)),
                        RunListForTag.RUN_LIST_TYPE)));
   }

   @Override
   public List<String> getRunListForTag(String tag) {
      return runListForTag.apply(tag);
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