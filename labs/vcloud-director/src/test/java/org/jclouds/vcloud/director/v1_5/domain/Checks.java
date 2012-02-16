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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.net.URI;
import java.util.Set;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

/**
 * @author grkvlt@apache.org
 */
public class Checks {

   public static void checkResourceEntityType(ResourceEntityType<?> resourceEntity) {
      // Check optional fields
      // NOTE status cannot be checked (TODO: doesn't status have a range of valid values?)
      FilesList files = resourceEntity.getFiles();
      if (files != null && files.getFiles() != null && !files.getFiles().isEmpty()) {
         for (File file : files.getFiles()) checkFile(file);
      }
      
      // Check parent type
      checkEntityType(resourceEntity);
   }
   
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
      assertTrue(Link.Rel.ALL.contains(link.getRel()), 
            String.format("The Rel attribute (%s) of a Link must be one of the allowed list - %s", 
                  link.getRel(), Iterables.toString(Link.Rel.ALL)));

      // Check parent type
      checkReferenceType(link);
   }

   public static void checkTask(Task task) {
      // Check required fields
      assertNotNull(task.getStatus(), "The Status attribute of a Task must be set");
      assertTrue(Task.Status.ALL.contains(task.getStatus().toString()), 
            String.format("The Status of a Task (%s) must be one of the allowed list - %s", 
            task.getStatus().toString(), Iterables.toString(Task.Status.ALL)));

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
   
   public static void checkFile(File file) {
      // Check optional fields
      // NOTE checksum be checked
      Long size = file.getSize();
      if(size != null) {
         assertTrue(file.size >= 0, "File size must be greater than or equal to 0");
      }
      Long bytesTransferred = file.getBytesTransferred();
      if(bytesTransferred != null) {
         assertTrue(bytesTransferred >= 0, "Bytes transferred must be greater than or equal to 0");
      }
      
      // Check parent type
      checkEntityType(file);
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

   public static void checkImageType(String imageType) {
      assertTrue(Media.ImageType.ALL.contains(imageType), 
            "The Image type of a Media must be one of the allowed list");
   }
}
