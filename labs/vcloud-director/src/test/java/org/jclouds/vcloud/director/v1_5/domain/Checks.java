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

import static org.testng.Assert.*;

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

   protected void checkEntityType(EntityType<?> entity) {
      // Check required fields
      assertNotNull(entity.getName(), "The Name attribute of an EntityType must be set");

      // Check optional fields
//      String description = entity.getDescription(); // ???
      TasksInProgress tasksInProgress = entity.getTasksInProgress();
      if (tasksInProgress != null && tasksInProgress.getTasks() != null && !tasksInProgress.getTasks().isEmpty()) {
         for (Task task : tasksInProgress.getTasks()) checkTask(task);
      }
      
      // Check parent type
      checkResourceType(entity);
   }

   protected void checkReferenceType(ReferenceType<?> reference) {
      // Check required fields
      assertNotNull(reference.getHref(), "The Href attribute of a ReferenceType must be set");

      // Check optional fields
      String id = reference.getId();
      if (id != null) checkId(id);
      String type = reference.getType();
      if (type != null) checkType(type);
//      String name = reference.getName(); // ???
   }

   protected void checkResourceType(ResourceType<?> resource) {
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

   protected void checkId(String id) {
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

   protected void checkType(String type) {
      assertTrue(VCloudDirectorMediaType.ALL.contains(type), "The Type must be a valid media type");
   }

   protected void checkHref(URI href) {
      String uri = href.toASCIIString();
      String auth = href.getAuthority();
      String host = href.getHost();
      String path = href.getPath();
//      assertEquals(auth + "://" + host + path, endpoint, "The Href must contain the provider endpoint");
//      assertTrue(uri.startsWith(endpoint), "The Href must contain the provider endpoint");
   }

   protected void checkLink(Link link) {
      // Check required fields
      assertNotNull(link.getRel(), "The Rel attribute of a Link must be set");
      assertTrue(Link.Rel.ALL.contains(link.getRel()), "The Rel attribute of a Link must be one of the allowed list");

      // Check parent type
      checkReferenceType(link);
   }

   protected void checkTask(Task task) {
      // Check required fields
      assertNotNull(task.getStatus(), "The Status attribute of a Task must be set");
      assertTrue(Task.Status.ALL.contains(task.getStatus()), "The Status of a Task must be one of the allowed list");

      // Check optional fields
//      String operation = task.getOperation(); // ???
//      String operationName = task.getOperationName(); // ???;
//      Date startTime = task.getStartTime(); // ???
//      Date endTime = task.getEndTime(); // ???
//      Date expiryTime = task.getExpiryTime(); // ???
//      ReferenceType<?> owner = task.getOwner(); // ???
      Error error = task.getError();
      if (error != null) checkError(error);
//      ReferenceType<?> user = task.getUser(); // ???
//      ReferenceType<?> organization = task.getOrg(); // ???
      Integer progress = task.getProgress();
      if (progress != null) checkProgress(progress);
//      Object params = task.getParams(); // ???

      // Check parent type
      checkEntityType(task);
   }

   protected void checkProgress(Integer progress) {
      assertTrue(progress >= 0 && progress <= 100, "The Progress attribute must be between 0 and 100");
   }

   protected void checkError(Error error) {
      // Check required fields
      assertNotNull(error.getMessage(), "The Message attribute of an Error must be set");
      assertNotNull(error.getMajorErrorCode(), "The MajorErrorCode attribute of an Error must be set");
      assertNotNull(error.getMinorErrorCode(), "The MinorErrorCode attribute of an Error must be set");
      
//      String vendorSpecificErrorCode = error.getVendorSpecificErrorCode(); // ???
//      String stackTrace = error.getStackTrace(); // ???
   }
}
