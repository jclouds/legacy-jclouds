/**
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
package org.jclouds.vcloud.director.v1_5.domain;

import static org.jclouds.vcloud.director.v1_5.domain.Checks.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.net.URI;
import java.util.Set;
import java.util.UUID;

import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

/**
 * @author grkvlt@apache.org
 */
public class Checks {

   public static void checkEntityType(EntityType<?> entity) {
      // Check required fields
      assertNotNull(entity.getName(), "The Name attribute of an EntityType must be set");

      // Check optional fields
      // NOTE description cannot be checked
      TasksInProgress tasksInProgress = entity.getTasksInProgress();
      if (tasksInProgress != null && tasksInProgress.getTasks() != null && !tasksInProgress.getTasks().isEmpty()) {
         for (Task task : tasksInProgress.getTasks()) checkTask(task);
      }
      
      // Check parent type
      checkResourceType(entity);
   }

   public static void checkReferenceType(ReferenceType<?> reference) {
      // Check required fields
      assertNotNull(reference.getHref(), "The Href attribute of a ReferenceType must be set");

      // Check optional fields
      String id = reference.getId();
      if (id != null) checkId(id);
      String type = reference.getType();
      if (type != null) checkType(type);
      // NOTE name cannot be checked
   }

   public static void checkResourceType(ResourceType<?> resource) {
      // Check optional fields
      URI href = resource.getHref();
      if (href != null) checkHref(href);
      String type = resource.getType();
      if (type != null) checkType(type);
      Set<Link> links = resource.getLinks();
      if (links != null && !links.isEmpty()) {
         for (Link link : links) checkLink(link);
      }
   }

   public static void checkId(String id) {
      Iterable<String> parts = Splitter.on(':').split(id);
      assertEquals(Iterables.size(parts), 4, "The Id must be well formed");
      assertEquals(Iterables.get(parts, 0), "urn", "The Id must start with 'urn'");
      assertEquals(Iterables.get(parts, 1), "vcloud", "The Id must include 'vcloud'");
      try {
         UUID uuid = UUID.fromString(Iterables.get(parts, 3));
         assertNotNull(uuid, "The UUID part of an Id must be well formed");
      } catch (IllegalArgumentException iae) {
         fail("The UUID part of an Id must be well formed");
      }
   }

   public static void checkType(String type) {
      assertTrue(VCloudDirectorMediaType.ALL.contains(type), 
            String.format("The Type (%s) must be a valid media type  - %s", type, 
                  Iterables.toString(VCloudDirectorMediaType.ALL)));
   }

   // NOTE this does not currently check anything
   public static void checkHref(URI href) {
      String uri = href.toASCIIString();
      String auth = href.getAuthority();
      String host = href.getHost();
      String path = href.getPath();
      // TODO inject the endpoint of the provider here for rudimentary checks as below
      // assertEquals(auth + "://" + host + path, endpoint, "The Href must contain the provider endpoint");
      // assertTrue(uri.startsWith(endpoint), "The Href must contain the provider endpoint");
   }

   public static void checkLink(Link link) {
      // Check required fields
      assertNotNull(link.getRel(), "The Rel attribute of a Link must be set");
      // XXX choose one
      assertTrue(Link.Rel.ALL.contains(link.getRel()), String.format("The Rel attribute of a Link must be from the allowed list: %s", Iterables.toString(Link.Rel.ALL)));
      assertTrue(Link.Rel.ALL.contains(link.getRel()), String.format("The Rel attribute of a Link cannot be '%s'", link.getRel()));

      // Check parent type
      checkReferenceType(link);
   }

   public static void checkTask(Task task) {
      // Check required fields
      assertNotNull(task.getStatus(), "The Status attribute of a Task must be set");
      // XXX choose one
      assertTrue(Task.Status.ALL.contains(task.getStatus()), String.format("The Status of a Task must be from the allowed list: %s", Iterables.toString(Task.Status.ALL)));
      assertTrue(Task.Status.ALL.contains(task.getStatus()), String.format("The Status of a Task cannot be '%s'", task.getStatus()));

      // Check optional fields
      // NOTE operation cannot be checked
      // NOTE operationName cannot be checked
      // NOTE startTime cannot be checked
      // NOTE endTime cannot be checked
      // NOTE expiryTimecannot be checked
      ReferenceType<?> owner = task.getOwner();
      if (owner != null) checkReferenceType(owner);
      Error error = task.getError();
      if (error != null) checkError(error);
      ReferenceType<?> user = task.getUser();
      if (user != null) checkReferenceType(user);
      ReferenceType<?> org = task.getOrg();
      if (org != null) checkReferenceType(org);
      Integer progress = task.getProgress();
      if (progress != null) checkProgress(progress);
      // NOTE params cannot be checked

      // Check parent type
      checkEntityType(task);
   }

   public static void checkMetadata(Metadata metadata) {
      Set<MetadataEntry> metadataEntries = metadata.getMetadataEntries();
      if (metadataEntries != null && !metadataEntries.isEmpty()) {
         for (MetadataEntry metadataEntry : metadataEntries) checkMetadataEntry(metadataEntry);
      }

      // Check parent type
      checkResourceType(metadata);
   }

   public static void checkMetadataEntry(MetadataEntry metadataEntry) {
      // Check required fields
      assertNotNull(metadataEntry.getKey(), "The Key attribute of a MetadataEntry must be set");
      assertNotNull(metadataEntry.getValue(), "The Value attribute of a MetadataEntry must be set");

      // Check parent type
      checkResourceType(metadataEntry);
   }

   public static void checkProgress(Integer progress) {
      assertTrue(progress >= 0 && progress <= 100, "The Progress attribute must be between 0 and 100");
   }

   public static void checkError(Error error) {
      // Check required fields
      assertNotNull(error.getMessage(), "The Message attribute of an Error must be set");
      assertNotNull(error.getMajorErrorCode(), "The MajorErrorCode attribute of an Error must be set");
      assertNotNull(error.getMinorErrorCode(), "The MinorErrorCode attribute of an Error must be set");
      
      // NOTE vendorSpecificErrorCode cannot be checked
      // NOTE stackTrace cannot be checked
   }

   public static void checkCatalog(Catalog catalog) {
      // Check optional elements/attributes
      Entity owner = catalog.getOwner();
      if (owner != null) checkEntityType(owner);
      CatalogItems catalogItems = catalog.getCatalogItems();
      if (catalogItems != null) {
         for (Reference catalogItemReference : catalogItems.getCatalogItems()) {
            checkReferenceType(catalogItemReference);
         }
      }
      // NOTE isPublished cannot be checked
      
      // Check parent type
      checkEntityType(catalog);
   }

   public static void checkCatalogItem(CatalogItem catalogItem) {
      // Check parent type
      checkEntityType(catalogItem);
   }
}
