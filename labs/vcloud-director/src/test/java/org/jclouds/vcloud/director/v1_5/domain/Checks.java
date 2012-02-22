/*
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

import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.CONDITION_FMT;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.MUST_BE_WELL_FORMED_FMT;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.MUST_CONTAIN_FMT;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.NOT_NULL_OBJECT_FMT;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.REQUIRED_VALUE_FMT;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.REQUIRED_VALUE_OBJECT_FMT;
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
import com.google.common.net.InetAddresses;

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
      assertNotNull(entity.getName(), String.format(NOT_NULL_OBJECT_FMT, "Name", "EntityType"));

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
      assertNotNull(reference.getHref(), String.format(NOT_NULL_OBJECT_FMT, "Href", "ReferenceType"));

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
      assertEquals(Iterables.size(parts), 4, String.format(MUST_BE_WELL_FORMED_FMT, "Id", id));
      assertEquals(Iterables.get(parts, 0), "urn", String.format(MUST_CONTAIN_FMT, "Id", "urn", id));
      assertEquals(Iterables.get(parts, 1), "vcloud", String.format(MUST_CONTAIN_FMT, "Id", "vcloud", id));
      try {
         UUID.fromString(Iterables.get(parts, 3));
      } catch (IllegalArgumentException iae) {
          fail(String.format(MUST_BE_WELL_FORMED_FMT, "Id", id));
      }
   }

   public static void checkType(String type) {
      assertTrue(VCloudDirectorMediaType.ALL.contains(type), String.format(REQUIRED_VALUE_FMT, "Type", type, Iterables.toString(VCloudDirectorMediaType.ALL)));
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
      assertNotNull(link.getRel(), String.format(NOT_NULL_OBJECT_FMT, "Rel", "Link"));
      assertTrue(Link.Rel.ALL.contains(link.getRel()), String.format(REQUIRED_VALUE_OBJECT_FMT, "Rel", "Link", link.getRel(), Iterables.toString(Link.Rel.ALL)));

      // Check parent type
      checkReferenceType(link);
   }

   public static void checkTask(Task task) {
      // Check required fields
      assertNotNull(task.getStatus(), String.format(NOT_NULL_OBJECT_FMT, "Status", "Task"));
      assertTrue(Task.Status.ALL.contains(task.getStatus()), String.format(REQUIRED_VALUE_OBJECT_FMT, "Status", "Task", task.getStatus(), Iterables.toString(Task.Status.ALL)));

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

   public static void checkMetadata(Metadata metadata) {
      Set<MetadataEntry> metadataEntries = metadata.getMetadataEntries();
      if (metadataEntries != null && !metadataEntries.isEmpty()) {
         for (MetadataEntry metadataEntry : metadataEntries) {
            checkMetadataEntry(metadataEntry);
         }
      }

      // Check parent type
      checkResourceType(metadata);
   }

   public static void checkMetadataEntry(MetadataEntry metadataEntry) {
      // Check required fields
      assertNotNull(metadataEntry.getKey(), String.format(NOT_NULL_OBJECT_FMT, "Key", "MetadataEntry"));
      assertNotNull(metadataEntry.getValue(), String.format(NOT_NULL_OBJECT_FMT, "Value", "MetadataEntry"));

      // Check parent type
      checkResourceType(metadataEntry);
   }

   public static void checkMetadataValue(MetadataValue metadataValue) {
      // Check required elements and attributes
      assertNotNull(metadataValue.getValue(), String.format(NOT_NULL_OBJECT_FMT, "Value", "MetadataValue"));
      
      // Check parent type
      checkResourceType(metadataValue);
   }

   public static void checkProgress(Integer progress) {
      assertTrue(progress >= 0 && progress <= 100, String.format(CONDITION_FMT, "Progress", "between 0 and 100", Integer.toString(progress)));
   }

   public static void checkError(Error error) {
      // Check required fields
      assertNotNull(error.getMessage(), String.format(NOT_NULL_OBJECT_FMT, "Message", "Error"));
      assertNotNull(error.getMajorErrorCode(), String.format(NOT_NULL_OBJECT_FMT, "MajorErrorCode", "Error"));
      assertNotNull(error.getMinorErrorCode(), String.format(NOT_NULL_OBJECT_FMT, "MinorErrorCode", "Error"));
      
      // NOTE vendorSpecificErrorCode cannot be checked
      // NOTE stackTrace cannot be checked
   }

   public static void checkOrg(Org org) {
      // Check required elements and attributes
      assertNotNull(org.getFullName(), String.format(NOT_NULL_OBJECT_FMT, "FullName", "Org"));

      // Check parent type
      checkEntityType(org);
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

   public static void checkImageType(String imageType) {
      assertTrue(Media.ImageType.ALL.contains(imageType), 
            "The Image type of a Media must be one of the allowed list");
   }

   public static void checkNetworkType(NetworkType<?> network) {
      // Check optional fields
      NetworkConfiguration config = network.getConfiguration();
      if (config != null) {
         checkNetworkConfiguration(config);
      }
      
      // Check parent type
      checkEntityType(network);
   }
   
   public static void checkNetworkConfiguration(NetworkConfiguration config) {
      // Check optional fields
      if (config.getIpScope() != null) {
         checkIpScope(config.getIpScope());
      }
      
      if (config.getParentNetwork() != null) {
         checkReferenceType(config.getParentNetwork());
      }
      
      if (config.getNetworkFeatures() != null) {
         checkNetworkFeatures(config.getNetworkFeatures());
      }
      
      if (config.getSyslogServerSettings() != null) {
         checkSyslogServerSettings(config.getSyslogServerSettings());
      }
      
      if (config.getRouterInfo() != null) {
         checkRouterInfo(config.getRouterInfo());
      }
   }
   
   public static void checkIpScope(IpScope ipScope) {
      // Check required fields
      assertNotNull(ipScope.isInherited(), "isInherited attribute of IpScope must be set");
      
      // Check optional fields
      // NOTE dnsSuffix cannot be checked
      if (ipScope.getGateway() != null) {
         checkIpAddress(ipScope.getGateway());
      }
      if (ipScope.getNetmask() != null) {
         checkIpAddress(ipScope.getNetmask());
      }
      if (ipScope.getDns1() != null) {
         checkIpAddress(ipScope.getDns1());
      }
      if (ipScope.getDns2() != null) {
         checkIpAddress(ipScope.getDns2());
      }
      if (ipScope.getIpRanges() != null) {
         checkIpRanges(ipScope.getIpRanges());
      }
      if (ipScope.getAllocatedIpAddresses() != null) {
         checkIpAddresses(ipScope.getAllocatedIpAddresses());
      }
   }
   
   public static void checkNetworkFeatures(NetworkFeatures features) {
      // Check optional fields
      if (features.getNetworkServices() != null) {
         for (NetworkService service : features.getNetworkServices()) {
            checkNetworkService(service);
         }
      }
   }
   
   public static void checkSyslogServerSettings(SyslogServerSettings settings) {
      // Check optional fields
      if (settings.getSyslogServerIp1() != null) {
         checkIpAddress(settings.getSyslogServerIp1());
      }
      
      if (settings.getSyslogServerIp2() != null) {
         checkIpAddress(settings.getSyslogServerIp2());
      }
      
   }

   public static void checkRouterInfo(RouterInfo routerInfo) {
      // Check required fields
      assertNotNull(routerInfo.getExternalIp(), "The external IP attribute of a Router Info must be set");
      checkIpAddress(routerInfo.getExternalIp());
   }
   
   public static void checkNetworkService(NetworkService service) {
      // NOTE isEnabled cannot be checked
   }
   
   public static void checkIpRanges(IpRanges ipRanges) {
      // Check optional fields
      for (IpRange range : ipRanges.getIpRanges()) {
         checkIpRange(range);
      }
   }
   
   public static void checkIpRange(IpRange range) {
      // Check required fields
      assertNotNull(range.getStartAddress(), "The start address attribute of an IP Range must be set");
      checkIpAddress(range.getStartAddress());
      
      assertNotNull(range.getEndAddress(), "The end address attribute of an IP Range must be set");
      checkIpAddress(range.getEndAddress());
   }
   
   public static void checkIpAddresses(IpAddresses ipAddresses) {
      // Check optional fields
      for (String address : ipAddresses.getIpAddresses()) {
         checkIpAddress(address);
      }
   }
   
   public static void checkIpAddress(String ip) {
      InetAddresses.isInetAddress(ip);
   }
}
