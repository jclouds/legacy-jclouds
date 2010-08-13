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

package org.jclouds.chef.test;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Throwables.propagate;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Sets.newLinkedHashSet;
import static org.jclouds.concurrent.Futures.compose;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.blobstore.TransientAsyncBlobStore;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.PageSet;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.chef.ChefAsyncClient;
import org.jclouds.chef.domain.Client;
import org.jclouds.chef.domain.CookbookVersion;
import org.jclouds.chef.domain.DatabagItem;
import org.jclouds.chef.domain.Node;
import org.jclouds.chef.domain.Role;
import org.jclouds.chef.domain.Sandbox;
import org.jclouds.chef.domain.SearchResult;
import org.jclouds.chef.domain.UploadSandbox;
import org.jclouds.util.Utils;

import com.google.common.base.Function;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * In-memory chef simulator.
 * 
 * @author Adrian Cole
 */

public class TransientChefAsyncClient implements ChefAsyncClient {
   @Singleton
   private static class StorageMetadataToName implements Function<PageSet<? extends StorageMetadata>, Set<String>> {
      @Override
      public Set<String> apply(PageSet<? extends StorageMetadata> from) {
         return newLinkedHashSet(transform(from, new Function<StorageMetadata, String>() {

            @Override
            public String apply(StorageMetadata from) {
               return from.getName();
            }
         }));
      }
   }

   @Singleton
   private static class BlobToDatabagItem implements Function<Blob, DatabagItem> {
      @Override
      public DatabagItem apply(Blob from) {
         try {
            return from == null ? null : new DatabagItem(from.getMetadata().getName(), Utils.toStringAndClose(from
                  .getPayload().getInput()));
         } catch (IOException e) {
            propagate(e);
            return null;
         }
      }
   }

   private final TransientAsyncBlobStore databags;
   private final ExecutorService executor;
   private final BlobToDatabagItem blobToDatabagItem;
   private final StorageMetadataToName storageMetadataToName;

   @Inject
   TransientChefAsyncClient(@Named("databags") TransientAsyncBlobStore databags,
         StorageMetadataToName storageMetadataToName, BlobToDatabagItem blobToDatabagItem,
         @Named(Constants.PROPERTY_USER_THREADS) ExecutorService executor) {
      this.databags = checkNotNull(databags, "databags");
      this.storageMetadataToName = checkNotNull(storageMetadataToName, "storageMetadataToName");
      this.blobToDatabagItem = checkNotNull(blobToDatabagItem, "blobToDatabagItem");
      this.executor = checkNotNull(executor, "executor");
   }

   @Override
   public ListenableFuture<Boolean> clientExists(String clientname) {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public ListenableFuture<Sandbox> commitSandbox(String id, boolean isCompleted) {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public ListenableFuture<Client> createClient(String clientname) {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public ListenableFuture<Void> createDatabag(String databagName) {
      return databags.createContainerInLocationIfAbsent(null, databagName);
   }

   @Override
   public ListenableFuture<DatabagItem> createDatabagItem(String databagName, DatabagItem databagItem) {
      Blob blob = databags.newBlob(databagItem.getId());
      blob.setPayload(databagItem.toString());
      databags.putBlobAndReturnOld(databagName, blob);
      return Futures.immediateFuture(databagItem);
   }

   @Override
   public ListenableFuture<Void> createNode(Node node) {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public ListenableFuture<Void> createRole(Role role) {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public ListenableFuture<Boolean> databagExists(String databagName) {
      return databags.containerExists(databagName);
   }

   @Override
   public ListenableFuture<Boolean> databagItemExists(String databagName, String databagItemId) {
      return databags.blobExists(databagName, databagItemId);
   }

   @Override
   public ListenableFuture<Client> deleteClient(String clientname) {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public ListenableFuture<CookbookVersion> deleteCookbook(String cookbookName, String version) {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public ListenableFuture<Void> deleteDatabag(String databagName) {
      return databags.deleteContainer(databagName);
   }

   @Override
   public ListenableFuture<DatabagItem> deleteDatabagItem(String databagName, String databagItemId) {
      return compose(databags.removeBlobAndReturnOld(databagName, databagItemId), blobToDatabagItem, executor);
   }

   @Override
   public ListenableFuture<Node> deleteNode(String nodename) {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public ListenableFuture<Role> deleteRole(String rolename) {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public ListenableFuture<Client> generateKeyForClient(String clientname) {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public ListenableFuture<Client> getClient(String clientname) {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public ListenableFuture<CookbookVersion> getCookbook(String cookbookName, String version) {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public ListenableFuture<DatabagItem> getDatabagItem(String databagName, String databagItemId) {
      return compose(databags.getBlob(databagName, databagItemId), blobToDatabagItem, executor);
   }

   @Override
   public ListenableFuture<Node> getNode(String nodename) {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public ListenableFuture<Role> getRole(String rolename) {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public ListenableFuture<UploadSandbox> getUploadSandboxForChecksums(Set<List<Byte>> md5s) {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public ListenableFuture<Set<String>> getVersionsOfCookbook(String cookbookName) {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public ListenableFuture<Set<String>> listClients() {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public ListenableFuture<Set<String>> listCookbooks() {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public ListenableFuture<Set<String>> listDatabagItems(String databagName) {
      return compose(databags.list(databagName), storageMetadataToName, executor);
   }

   @Override
   public ListenableFuture<Set<String>> listDatabags() {
      return compose(databags.list(), storageMetadataToName, executor);
   }

   @Override
   public ListenableFuture<Set<String>> listNodes() {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public ListenableFuture<Set<String>> listRoles() {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public ListenableFuture<Set<String>> listSearchIndexes() {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public ListenableFuture<Boolean> nodeExists(String nodename) {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public ListenableFuture<Boolean> roleExists(String rolename) {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public ListenableFuture<? extends SearchResult<? extends Client>> searchClients() {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public ListenableFuture<? extends SearchResult<? extends DatabagItem>> searchDatabag(String databagName) {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public ListenableFuture<? extends SearchResult<? extends Node>> searchNodes() {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public ListenableFuture<? extends SearchResult<? extends Role>> searchRoles() {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public ListenableFuture<CookbookVersion> updateCookbook(String cookbookName, String version, CookbookVersion cookbook) {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public ListenableFuture<DatabagItem> updateDatabagItem(String databagName, DatabagItem item) {
      return createDatabagItem(databagName, item);
   }

   @Override
   public ListenableFuture<Node> updateNode(Node node) {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public ListenableFuture<Role> updateRole(Role role) {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public ListenableFuture<Void> uploadContent(Set<List<Byte>> md5s) {
      // TODO Auto-generated method stub
      return null;
   }

}
