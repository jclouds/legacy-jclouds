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
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.MAC_ADDRESS_PATTERN;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.MATCHES_STRING_FMT;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.MUST_BE_WELL_FORMED_FMT;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.MUST_CONTAIN_FMT;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.NOT_EMPTY_STRING_FMT;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.NOT_NULL_OBJ_FIELD_FMT;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.NOT_NULL_OBJ_FMT;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.OBJ_DEL;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.OBJ_FIELD_ATTRB_REQ;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.OBJ_FIELD_EQ;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.OBJ_FIELD_GTE_0;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.OBJ_FIELD_GTE_1;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.OBJ_FIELD_REQ;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.REQUIRED_VALUE_FMT;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.REQUIRED_VALUE_OBJECT_FMT;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.jclouds.dmtf.cim.ResourceAllocationSettingData;
import org.jclouds.dmtf.cim.VirtualSystemSettingData;
import org.jclouds.dmtf.ovf.Disk;
import org.jclouds.dmtf.ovf.DiskSection;
import org.jclouds.dmtf.ovf.NetworkSection;
import org.jclouds.dmtf.ovf.ProductSection;
import org.jclouds.dmtf.ovf.SectionType;
import org.jclouds.dmtf.ovf.StartupSection;
import org.jclouds.dmtf.ovf.environment.EnvironmentType;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;
import org.jclouds.vcloud.director.v1_5.domain.dmtf.Envelope;
import org.jclouds.vcloud.director.v1_5.domain.dmtf.RasdItem;
import org.jclouds.vcloud.director.v1_5.domain.dmtf.VirtualSystem;
import org.jclouds.vcloud.director.v1_5.domain.network.ExternalNetwork;
import org.jclouds.vcloud.director.v1_5.domain.network.IpAddresses;
import org.jclouds.vcloud.director.v1_5.domain.network.IpRange;
import org.jclouds.vcloud.director.v1_5.domain.network.IpRanges;
import org.jclouds.vcloud.director.v1_5.domain.network.IpScope;
import org.jclouds.vcloud.director.v1_5.domain.network.Network;
import org.jclouds.vcloud.director.v1_5.domain.network.NetworkConfiguration;
import org.jclouds.vcloud.director.v1_5.domain.network.NetworkConnection;
import org.jclouds.vcloud.director.v1_5.domain.network.NetworkFeatures;
import org.jclouds.vcloud.director.v1_5.domain.network.NetworkServiceType;
import org.jclouds.vcloud.director.v1_5.domain.network.RouterInfo;
import org.jclouds.vcloud.director.v1_5.domain.network.SyslogServerSettings;
import org.jclouds.vcloud.director.v1_5.domain.network.VAppNetworkConfiguration;
import org.jclouds.vcloud.director.v1_5.domain.org.AdminOrg;
import org.jclouds.vcloud.director.v1_5.domain.org.CustomOrgLdapSettings;
import org.jclouds.vcloud.director.v1_5.domain.org.CustomOrgLdapSettings.AuthenticationMechanism;
import org.jclouds.vcloud.director.v1_5.domain.org.CustomOrgLdapSettings.ConnectorType;
import org.jclouds.vcloud.director.v1_5.domain.org.Org;
import org.jclouds.vcloud.director.v1_5.domain.org.OrgEmailSettings;
import org.jclouds.vcloud.director.v1_5.domain.org.OrgGeneralSettings;
import org.jclouds.vcloud.director.v1_5.domain.org.OrgLdapGroupAttributes;
import org.jclouds.vcloud.director.v1_5.domain.org.OrgLdapSettings;
import org.jclouds.vcloud.director.v1_5.domain.org.OrgLdapSettings.LdapMode;
import org.jclouds.vcloud.director.v1_5.domain.org.OrgLdapUserAttributes;
import org.jclouds.vcloud.director.v1_5.domain.org.OrgLeaseSettings;
import org.jclouds.vcloud.director.v1_5.domain.org.OrgNetwork;
import org.jclouds.vcloud.director.v1_5.domain.org.OrgPasswordPolicySettings;
import org.jclouds.vcloud.director.v1_5.domain.org.OrgSettings;
import org.jclouds.vcloud.director.v1_5.domain.org.OrgVAppTemplateLeaseSettings;
import org.jclouds.vcloud.director.v1_5.domain.params.ControlAccessParams;
import org.jclouds.vcloud.director.v1_5.domain.query.ContainerType;
import org.jclouds.vcloud.director.v1_5.domain.query.QueryResultRecordType;
import org.jclouds.vcloud.director.v1_5.domain.section.CustomizationSection;
import org.jclouds.vcloud.director.v1_5.domain.section.GuestCustomizationSection;
import org.jclouds.vcloud.director.v1_5.domain.section.LeaseSettingsSection;
import org.jclouds.vcloud.director.v1_5.domain.section.NetworkConfigSection;
import org.jclouds.vcloud.director.v1_5.domain.section.NetworkConnectionSection;
import org.jclouds.vcloud.director.v1_5.domain.section.OperatingSystemSection;
import org.jclouds.vcloud.director.v1_5.domain.section.RuntimeInfoSection;
import org.jclouds.vcloud.director.v1_5.domain.section.VirtualHardwareSection;

import com.beust.jcommander.internal.Maps;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.net.InetAddresses;

/**
 * @author grkvlt@apache.org
 */
public class Checks {

   public static void checkResourceEntityType(ResourceEntity resourceEntity) {
      checkResourceEntityType(resourceEntity, true);
   }

   public static void checkResourceEntityType(ResourceEntity resourceEntity, boolean ready) {
      // Check optional fields
      // NOTE status cannot be checked (TODO: doesn't status have a range of valid values?)
      Set<File> files = resourceEntity.getFiles();
      if (files != null && !files.isEmpty()) {
         for (File file : files) checkFile(file, ready);
      }
      
      // Check parent type
      checkEntityType(resourceEntity);
   }
   
   public static void checkEntityType(Entity entity) {
      // Check required fields
      assertNotNull(entity.getName(), String.format(NOT_NULL_OBJ_FIELD_FMT, "Name", "EntityType"));

      // Check optional fields
      // NOTE description cannot be checked
      List<Task> tasks = entity.getTasks();
      if (tasks != null && tasks != null && !tasks.isEmpty()) {
         for (Task task : tasks) checkTask(task);
      }
      
      // Check parent type
      checkResource(entity);
   }

   /**
    * Assumes the validTypes to be vcloud-specific types.
    * 
    * @see #checkReferenceType(Reference, Collection)
    */
   public static void checkReferenceType(Reference reference) {
      checkReferenceType(reference, VCloudDirectorMediaType.ALL);
   }

   /**
    * @see #checkReferenceType(Reference, Collection)
    */
   public static void checkReferenceType(Reference reference, String type) {
      checkReferenceType(reference, ImmutableSet.of(type));
   }
   
   public static void checkReferenceType(Reference reference, Collection<String> validTypes) {
      // Check required fields
      assertNotNull(reference.getHref(), String.format(NOT_NULL_OBJ_FIELD_FMT, "Href", "ReferenceType"));

      // Check optional fields
      String type = reference.getType();
      if (type != null) checkType(type, validTypes);
      // NOTE name cannot be checked
   }

   /**
    * Assumes the validTypes to be vcloud-specific types.
    * 
    * @see #checkResource(Resource, Collection)
    */
   public static void checkResource(Resource resource) {
      checkResource(resource, VCloudDirectorMediaType.ALL);
   }

   /**
    * @see #checkResource(Resource, Collection)
    */
   public static void checkResource(Resource resource, String type) {
      checkResource(resource, ImmutableSet.of(type));
   }

   public static void checkResource(Resource resource, Collection<String> validTypes) {
      // Check optional fields
      URI href = resource.getHref();
      if (href != null) checkHref(href);
      String type = resource.getType();
      if (type != null) checkType(type, validTypes);
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
      checkType(type, VCloudDirectorMediaType.ALL);
   }

   public static void checkType(String type, Collection<String> validTypes) {
      assertTrue(validTypes.contains(type), String.format(REQUIRED_VALUE_FMT, "Type", type, Iterables.toString(validTypes)));
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
      assertNotNull(link.getRel(), String.format(NOT_NULL_OBJ_FIELD_FMT, "Rel", "Link"));
      assertTrue(Link.Rel.ALL.contains(link.getRel()), String.format(REQUIRED_VALUE_OBJECT_FMT, "Rel", "Link", link.getRel(), Iterables.toString(Link.Rel.ALL)));

      // Check parent type
      checkReferenceType(link);
   }

   public static void checkTask(Task task) {
      // Check required fields
      assertNotNull(task.getStatus(), String.format(NOT_NULL_OBJ_FIELD_FMT, "Status", "Task"));
      assertTrue(Task.Status.ALL.contains(task.getStatus()), String.format(REQUIRED_VALUE_OBJECT_FMT, "Status", "Task", task.getStatus(), Iterables.toString(Task.Status.ALL)));

      // Check optional fields
      // NOTE operation cannot be checked
      // NOTE operationName cannot be checked
      // NOTE startTime cannot be checked
      // NOTE endTime cannot be checked
      // NOTE expiryTimecannot be checked
      Reference owner = task.getOwner();
      if (owner != null) checkReferenceType(owner);
      Error error = task.getError();
      if (error != null) checkError(error);
      Reference user = task.getUser();
      if (user != null) checkReferenceType(user);
      Reference org = task.get();
      if (org != null) checkReferenceType(org);
      Integer progress = task.getProgress();
      if (progress != null) checkProgress(progress);
      // NOTE params cannot be checked

      // Check parent type
      checkEntityType(task);
   }

   public static void checkFile(File file) {
      checkFile(file, true);
   }
   
   public static void checkFile(File file, boolean checkSize) {
      // Check optional fields
      // NOTE checksum be checked
      Long size = file.getSize();
      if(size != null && checkSize) {
         assertTrue(size >= 0, "File size must be greater than or equal to 0, but was "+size);
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
      checkResource(metadata);
   }

   public static void checkMetadataEntry(MetadataEntry metadataEntry) {
      // Check required fields
      assertNotNull(metadataEntry.getKey(), String.format(NOT_NULL_OBJ_FIELD_FMT, "Key", "MetadataEntry"));
      assertNotNull(metadataEntry.getValue(), String.format(NOT_NULL_OBJ_FIELD_FMT, "Value", "MetadataEntry"));

      // Check parent type
      checkResource(metadataEntry);
   }

   public static void checkProgress(Integer progress) {
      assertTrue(progress >= 0 && progress <= 100, String.format(CONDITION_FMT, "Progress", "between 0 and 100", Integer.toString(progress)));
   }

   public static void checkError(Error error) {
      // Check required fields
      assertNotNull(error.getMessage(), String.format(NOT_NULL_OBJ_FIELD_FMT, "Message", "Error"));
      assertNotNull(error.getMajorErrorCode(), String.format(NOT_NULL_OBJ_FIELD_FMT, "MajorErrorCode", "Error"));
      assertNotNull(error.getMinorErrorCode(), String.format(NOT_NULL_OBJ_FIELD_FMT, "MinorErrorCode", "Error"));
      
      // NOTE vendorSpecificErrorCode cannot be checked
      // NOTE stackTrace cannot be checked
   }

   public static void checkOrg(Org org) {
      // Check required elements and attributes
      assertNotNull(org.getFullName(), String.format(NOT_NULL_OBJ_FIELD_FMT, "FullName", "Org"));

      // Check parent type
      checkEntityType(org);
   }
   
   public static void checkAdminOrg(AdminOrg org) {
      // required
      assertNotNull(org.getSettings(), String.format(NOT_NULL_OBJ_FIELD_FMT, "settings", "AdminOrg"));
      checkResource(org, VCloudDirectorMediaType.ADMIN_ORG);
      
      // optional
      for (Reference user : org.getUsers()) {
         checkReferenceType(user, VCloudDirectorMediaType.ADMIN_USER);
      }
      for (Reference group : org.getGroups()) {
         checkReferenceType(group, VCloudDirectorMediaType.GROUP);
      }
      for (Reference catalog : org.getCatalogs()) {
         checkReferenceType(catalog, VCloudDirectorMediaType.ADMIN_CATALOG);
      }
      for (Reference vdc : org.getVdcs()) {
         checkReferenceType(vdc, VCloudDirectorMediaType.VDC);
      }
      for (Reference network : org.getNetworks()) {
         checkReferenceType(network, VCloudDirectorMediaType.ADMIN_NETWORK);
      }
      
      // Check parent type
      checkOrg(org);
   }
   
   public static void checkAdminCatalog(AdminCatalog catalog) {
      // Check parent type
      checkCatalogType(catalog);
   }

   public static void checkCatalogType(Catalog catalog) {
      // Check optional elements/attributes
      Owner owner = catalog.getOwner();
      if (owner != null) checkOwner(owner);
      for (Reference catalogItemReference : catalog.getCatalogItems()) {
         checkReferenceType(catalogItemReference, VCloudDirectorMediaType.CATALOG_ITEM);
      }
      // NOTE isPublished cannot be checked
      
      // Check parent type
      checkEntityType(catalog);
   }

   public static void checkOwner(Owner owner) {
       // Check optional elements/attributes
      if (owner.getUser() != null) {
         checkReferenceType(owner.getUser());
      }
      
     // Check parent type
      checkResource(owner);
   }

   public static void checkCatalogItem(CatalogItem catalogItem) {
      // Check parent type
      checkEntityType(catalogItem);
   }

   public static void checkNetwork(Network network) {
      // Check optional fields
      NetworkConfiguration config = network.getConfiguration();
      if (config != null) {
         checkNetworkConfiguration(config);
      }
      
      // Check parent type
      checkEntityType(network);
   }
   
   public static void checkNetworkConfiguration(NetworkConfiguration config) {
      // required
      assertNotNull(config.getFenceMode(), String.format(OBJ_FIELD_REQ, 
            "NetworkConfiguration", "fenceMode"));
      assertTrue(Network.FenceMode.ALL.contains(config.getFenceMode()), String.format(REQUIRED_VALUE_OBJECT_FMT, 
            "fenceMode", "NetworkConfiguration", config.getFenceMode(), Iterables.toString(Network.FenceMode.ALL)));
      
      // Check optional fields
      // NOTE retainNetInfoAcrossDeployments cannot be checked
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
         for (NetworkServiceType<?> service : features.getNetworkServices()) {
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
   
   public static void checkNetworkService(NetworkServiceType service) {
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
      // Check the string is a valid IP address
      assertTrue(InetAddresses.isInetAddress(ip), String.format(CONDITION_FMT, "IpAddress", "a valid IP address", ip));
   }
   
   public static void checkMacAddress(String macAddress) {
      // Check the string is a valid MAC address
      assertNotNull(macAddress, String.format(NOT_EMPTY_STRING_FMT, "macAddress"));
      assertTrue(macAddress.matches(MAC_ADDRESS_PATTERN), String.format(MATCHES_STRING_FMT, "macAddress", MAC_ADDRESS_PATTERN, macAddress));
   }

   public static void checkComputeCapacity(ComputeCapacity computeCapacity) {
      // Check required fields
      assertNotNull(computeCapacity.getCpu(), "The cpu attribute of a ComputeCapacity must be set");
      checkCapacityWithUsage(computeCapacity.getCpu());
      
      assertNotNull(computeCapacity.getMemory(), "The memory attribute of a ComputeCapacity must be set");
      checkCapacityWithUsage(computeCapacity.getMemory());
   }

   public static void checkCapacityWithUsage(CapacityWithUsage capacityWithUsage) {
      // Check optional fields
      if (capacityWithUsage.getUsed() != null) {
         assertTrue(capacityWithUsage.getUsed() >= 0, "used must be greater than or equal to 0");
      }
      if (capacityWithUsage.getOverhead() != null) {
         assertTrue(capacityWithUsage.getOverhead() >= 0, "overhead must be greater than or equal to 0");
      }
      
      // Check parent type
      checkCapacityType(capacityWithUsage);
   }

   public static void checkCapacityType(CapacityType<?> capacity) {
      // Check required fields
      assertNotNull(capacity.getUnits(), "The unit attribute of a CapacityWithUsage must be set");
      
      assertNotNull(capacity.getLimit(), "The limit attribute of a CapacityWithUsage must be set");
      assertTrue(capacity.getLimit() >= 0, "Limit must be greater than or equal to 0");
      
      // Check optional fields
      if (capacity.getAllocated() != null) {
         assertTrue(capacity.getAllocated() >= 0, "allocated must be greater than or equal to 0");
      }
   }
   
   public static void checkCapabilities(Capabilities capabilities) {
      // Check optional fields
      for (String supportedHardwareVersion : capabilities.getSupportedHardwareVersions()) {
         // NOTE supportedHardwareVersion cannot be checked?
      }
   }

   public static void checkMetadataFor(String api, Metadata metadata) {
      for (MetadataEntry entry : metadata.getMetadataEntries()) {
         // Check required fields
         assertNotNull(entry.getKey(), 
               String.format(OBJ_FIELD_ATTRB_REQ, api, "MetadataEntry", entry.getKey(), "key"));
         assertNotNull(entry.getValue(), 
               String.format(OBJ_FIELD_ATTRB_REQ, api, "MetadataEntry", entry.getValue(), "value"));
          
         // Check parent type
         checkResource(entry);
      }
      
      // Check parent type
      checkResource(metadata);
   }


   public static void checkMetadataKeyAbsentFor(String api, Metadata metadata, String key) {
      Map<String,String> metadataMap = metadataToMap(metadata);
      assertFalse(metadataMap.containsKey(key), 
               String.format(OBJ_DEL, api+" metadata key", key));
   }

   public static void checkMetadataFor(String api, Metadata metadata, Map<String, String> expectedMap) {
      Map<String,String> actualMap = Checks.metadataToMap(metadata);
      assertEquals(actualMap, expectedMap,
               String.format(OBJ_FIELD_EQ, api, "metadata entries", expectedMap, actualMap));
   }

   public static Map<String,String> metadataToMap(Metadata metadata) {
      Map<String,String> result = Maps.newLinkedHashMap();
      for (MetadataEntry entry : metadata.getMetadataEntries()) {
         result.put(entry.getKey(), entry.getValue());
      }
      return result;
   }

   public static void checkVmPendingQuestion(VmPendingQuestion question) {
      assertNotNull(question, String.format(NOT_NULL_OBJ_FMT, "VmPendingQuestion"));

      // Check required fields
      assertNotNull(question.getQuestion(), String.format(OBJ_FIELD_REQ, "VmPendingQuestion", "Question"));
      assertNotNull(question.getQuestionId(), String.format(OBJ_FIELD_REQ, "VmPendingQuestion", "QuestionId"));
      for (VmQuestionAnswerChoice choice : question.getChoices()) {
         checkVmQuestionAnswerChoice(choice);
      }
      
      // Check parent type
      checkResource(question);
   }

   public static void checkVmQuestionAnswerChoice(VmQuestionAnswerChoice choice) {
      assertNotNull(choice, String.format(NOT_NULL_OBJ_FMT, "VmQuestionAnswerChoice"));
      
      // NOTE the Id field cannot be checked
      // NOTE the Text field cannot be checked
   }
   
   public static void checkVApp(VApp vApp) {
      // Check optional fields
      Owner owner = vApp.getOwner();
      if (owner != null) checkOwner(owner);
      // NOTE inMaintenanceMode cannot be checked
      VAppChildren children = vApp.getChildren();
      if (children != null) checkVAppChildren(children);
      // NOTE ovfDescriptorUploaded cannot be checked
      
      // Check parent type
      checkAbstractVAppType(vApp);
   }

   public static void checkVAppChildren(VAppChildren vAppChildren) {
      // Check optional fields
      for (VApp vApp : vAppChildren.getVApps()) {
         checkVApp(vApp);
      }
      for (Vm vm : vAppChildren.getVms()) {
         checkVm(vm);
      }
   }

   public static void checkAbstractVAppType(AbstractVAppType abstractVAppType) {
      // Check optional fields
      Reference vAppParent = abstractVAppType.getVAppParent();
      if (vAppParent != null) checkReferenceType(vAppParent);
      // NOTE deployed cannot be checked
      for (SectionType section : abstractVAppType.getSections()) {
         checkSectionType(section);
      }
      
      // Check parent type
      checkResourceEntityType(abstractVAppType);
   }
   
   public static void checkVAppTemplate(VAppTemplate template) {
      checkVAppTemplate(template, true);
   }
   
   public static void checkVAppTemplateWhenNotReady(VAppTemplate template) {
      checkVAppTemplate(template, false);
   }
   
   public static void checkVAppTemplate(VAppTemplate template, boolean ready) {
      // Check required fields
      assertNotNull(template.getName(), String.format(NOT_NULL_OBJ_FIELD_FMT, "Name", "VAppTemplate"));
      
      // Check optional fields
      Owner owner = template.getOwner();
      if (owner != null) checkOwner(owner);
      for (Vm child : template.getChildren()) {
         checkVm(child);
      }
      for (SectionType section : template.getSections()) {
         checkSectionType(section);
      }
      if (template.getTasks() != null) {
         for (Task task : template.getTasks()) {
            checkTask(task);
         }
      }
      if (template.getFiles() != null) {
         for (File file : template.getFiles()) {
            checkFile(file, ready);
         }
      }
      
      // NOTE vAppScopedLocalId cannot be checked
      // NOTE ovfDescriptorUploaded cannot be checked
      // NOTE goldMaster cannot be checked
      
      // Check parent type
      checkResourceEntityType(template, ready);
   }

   public static void checkVm(Vm vm) {
      // Check optional fields
      EnvironmentType environment = vm.getEnvironment();
      if (environment != null) checkEnvironmentType(environment);
      // NOTE vAppScopedLocalId cannot be checked
      // NOTE needsCustomization cannot be checked
      
      // Check parent type
      checkAbstractVAppType(vm);
   }

   public static void checkControlAccessParams(ControlAccessParams params) {
      // Check required fields
      assertNotNull(params.isSharedToEveryone(), String.format(OBJ_FIELD_REQ, "ControlAccessParams", "IsSharedToEveryone"));
      
      // Check optional fields, dependant on IsSharedToEveryone state
      if (params.isSharedToEveryone()) {
         assertNotNull(params.getEveryoneAccessLevel(), String.format(OBJ_FIELD_REQ, "ControlAccessParams", "EveryoneAccessLevel"));
         assertNotNull(params.getAccessSettings(), String.format(OBJ_FIELD_REQ, "ControlAccessParams", "AccessSettings when isSharedToEveryone"));
      } else {
         assertTrue(params.getAccessSettings().size() >= 1, String.format(OBJ_FIELD_GTE_1, "ControlAccessParams", "AccessSettings.size", params.getAccessSettings().size()));
         for (AccessSetting setting : params.getAccessSettings()) {
            checkAccessSetting(setting);
         }
      }
   }

   public static void checkAccessSetting(AccessSetting setting) {
      // Check required fields
      assertNotNull(setting.getSubject(), String.format(OBJ_FIELD_REQ, "AccessSetting", "Subject"));
      checkReferenceType(setting.getSubject());
      assertNotNull(setting.getAccessLevel(), String.format(OBJ_FIELD_REQ, "AccessSetting", "AccessLevel"));
   }

   public static void checkEnvironmentType(EnvironmentType environment) {
      // TODO
   }

   public static void checkSectionType(SectionType section) {
      // Check optional fields
      // NOTE info cannot be checked
      // NOTE required cannot be checked
   }

   public static void checkVirtualHardwareSection(VirtualHardwareSection hardware) {
      // Check optional fields
      VirtualSystemSettingData virtualSystem = hardware.getSystem();
      if (virtualSystem != null) checkVirtualSystemSettingData(virtualSystem);
      // NOTE transport cannot be checked
      if (hardware.getItems() != null) {
	      for (ResourceAllocationSettingData item : hardware.getItems()) {
	         checkResourceAllocationSettingData(item);
	      }
      }
      
      // Check parent type
      checkSectionType(hardware);
   }

   public static void checkVirtualSystemSettingData(VirtualSystemSettingData virtualSystem) {
      assertNotNull(virtualSystem.getElementName(), String.format(OBJ_FIELD_REQ, "VirtualSystemSettingData", "elementName"));
      assertNotNull(virtualSystem.getInstanceID(),  String.format(OBJ_FIELD_REQ, "VirtualSystemSettingData", "instanceID"));
//      assertNotNull(virtualSystem.getCaption(), String.format(OBJ_FIELD_REQ, "VirtualSystemSettingData", "caption"));
//      assertNotNull(virtualSystem.getDescription(), String.format(OBJ_FIELD_REQ, "VirtualSystemSettingData", "description"));
//      assertNotNull(virtualSystem.getAutomaticRecoveryAction(), String.format(OBJ_FIELD_REQ, "VirtualSystemSettingData", "automaticRecoveryAction"));
//      assertNotNull(virtualSystem.getAutomaticShutdownAction(), String.format(OBJ_FIELD_REQ, "VirtualSystemSettingData", "automaticShutdownAction"));
//      assertNotNull(virtualSystem.getAutomaticStartupAction(), String.format(OBJ_FIELD_REQ, "VirtualSystemSettingData", "automaticStartupAction"));
//      assertNotNull(virtualSystem.getAutomaticStartupActionDelay(), String.format(OBJ_FIELD_REQ, "VirtualSystemSettingData", "automaticStartupActionDelay"));
//      assertNotNull(virtualSystem.getAutomaticStartupActionSequenceNumber(), String.format(OBJ_FIELD_REQ, "VirtualSystemSettingData", "automaticStartupActionSequenceNumber"));
//      assertNotNull(virtualSystem.getConfigurationDataRoot(), String.format(OBJ_FIELD_REQ, "VirtualSystemSettingData", "configurationDataRoot"));
//      assertNotNull(virtualSystem.getConfigurationFile(), String.format(OBJ_FIELD_REQ, "VirtualSystemSettingData", "configurationFile"));
//      assertNotNull(virtualSystem.getConfigurationID(), String.format(OBJ_FIELD_REQ, "VirtualSystemSettingData", "configurationID"));
//      assertNotNull(virtualSystem.getCreationTime(), String.format(OBJ_FIELD_REQ, "VirtualSystemSettingData", "creationTime"));
//      assertNotNull(virtualSystem.getLogDataRoot(), String.format(OBJ_FIELD_REQ, "VirtualSystemSettingData", "logDataRoot"));
//      assertNotNull(virtualSystem.getRecoveryFile(), String.format(OBJ_FIELD_REQ, "VirtualSystemSettingData", "recoveryFile"));
//      assertNotNull(virtualSystem.getSnapshotDataRoot(), String.format(OBJ_FIELD_REQ, "VirtualSystemSettingData", "snapshotDataRoot"));
//      assertNotNull(virtualSystem.getSuspendDataRoot(), String.format(OBJ_FIELD_REQ, "VirtualSystemSettingData", "suspendDataRoot"));
//      assertNotNull(virtualSystem.getSwapFileDataRoot(), String.format(OBJ_FIELD_REQ, "VirtualSystemSettingData", "swapFileDataRoot"));
//      assertNotNull(virtualSystem.getVirtualSystemIdentifier(), String.format(OBJ_FIELD_REQ, "VirtualSystemSettingData", "virtualSystemIdentifier"));
//      assertNotNull(virtualSystem.getVirtualSystemType(), String.format(OBJ_FIELD_REQ, "VirtualSystemSettingData", "virtualSystemType"));
   }

   public static void checkResourceAllocationSettingData(ResourceAllocationSettingData item) {
      // TODO
   }
   
   public static void checkMediaFor(String api, Media media) {
      // required
      assertNotNull(media.getImageType(), String.format(OBJ_FIELD_REQ, api, "imageType"));
      assertTrue(Media.ImageType.ALL.contains(media.getImageType()), 
            "The Image type of a Media must be one of the allowed list");
      assertNotNull(media.getSize(), String.format(OBJ_FIELD_REQ, api, "size"));
      assertTrue(media.getSize() >= 0, String.format(OBJ_FIELD_GTE_0, api, "size", media.getSize()));
      
      // parent type
      checkResourceEntityType(media);
   }
   
   public static void checkGroup(Group group) {
      // Check optional fields
      // NOTE nameInSource cannot be checked
      for (Reference user : group.getUsersList()) {
         checkReferenceType(user, VCloudDirectorMediaType.USER);
      }
      if (group.getRole() != null) {
         checkReferenceType(group.getRole(), VCloudDirectorMediaType.ROLE);
      }
      
      // parent type
      checkEntityType(group);
   }
   
   public static void checkOrgSettings(OrgSettings settings) {
      // Check optional fields
      if (settings.getGeneralSettings() != null) {
         checkGeneralSettings(settings.getGeneralSettings());
      }
      if (settings.getVAppLeaseSettings() != null) {
         checkVAppLeaseSettings(settings.getVAppLeaseSettings());
      }
      if (settings.getVAppTemplateLeaseSettings() != null) {
         checkVAppTemplateLeaseSettings(settings.getVAppTemplateLeaseSettings());
      }
      if (settings.getLdapSettings() != null) {
         checkLdapSettings(settings.getLdapSettings());
      }
      if (settings.getEmailSettings() != null) {
         checkEmailSettings(settings.getEmailSettings());
      }
      if (settings.getPasswordPolicy() != null) {
         checkPasswordPolicySettings(settings.getPasswordPolicy());
      }
      
      // parent type
      checkResource(settings);
   }
   
   public static void checkEmailSettings(OrgEmailSettings settings) {
      // required
      assertNotNull(settings.isDefaultSmtpServer(), String.format(OBJ_FIELD_REQ, "OrgEmailSettings", "isDefaultSmtpServer"));
      assertNotNull(settings.isDefaultOrgEmail(), String.format(OBJ_FIELD_REQ, "OrgEmailSettings", "isDefaultOrgEmail"));
      assertNotNull(settings.getFromEmailAddress(), String.format(OBJ_FIELD_REQ, "OrgEmailSettings", "fromEmailAddress"));
      checkEmailAddress(settings.getFromEmailAddress());
      assertNotNull(settings.getDefaultSubjectPrefix(), String.format(OBJ_FIELD_REQ, "OrgEmailSettings", "defaultSubjectPrefix"));
      assertNotNull(settings.isAlertEmailToAllAdmins(), String.format(OBJ_FIELD_REQ, "OrgEmailSettings", "isAlertEmailToAllAdmins"));
      
      // optional
      // NOTE alertEmailsTo cannot be checked
      
      // parent type
      checkResource(settings);
   }
   
   public static void checkEmailAddress(String email) {
      // TODO: validate email addresses
   }
   
   public static void checkGeneralSettings(OrgGeneralSettings settings) {
      // Check optional fields
      // NOTE canPublishCatalogs cannot be checked
      // NOTE useServerBootSequence cannot be checked
      if (settings.getDeployedVMQuota() != null) {
         assertTrue(settings.getDeployedVMQuota() >= 0,
               String.format(OBJ_FIELD_GTE_0, "deployedVMQuota", "port", settings.getDeployedVMQuota()));
      }
      if (settings.getStoredVmQuota() != null) {
         assertTrue(settings.getStoredVmQuota() >= 0,
               String.format(OBJ_FIELD_GTE_0, "storedVmQuota", "port", settings.getStoredVmQuota()));
      }
      if (settings.getDelayAfterPowerOnSeconds() != null) {
         assertTrue(settings.getDelayAfterPowerOnSeconds() >= 0,
               String.format(OBJ_FIELD_GTE_0, "delayAfterPowerOnSeconds", "port", settings.getDelayAfterPowerOnSeconds()));
      }
      
      // parent type
      checkResource(settings);
   }
   
   public static void checkLdapSettings(OrgLdapSettings settings) {
      // Check optional fields
      // NOTE customUsersOu cannot be checked
      if (settings.getLdapMode() != null) {
         assertTrue(LdapMode.ALL.contains(settings.getLdapMode()),
               String.format(REQUIRED_VALUE_OBJECT_FMT, "LdapMode", "OrgLdapSettings", settings.getLdapMode(),
                     Iterables.toString(OrgLdapSettings.LdapMode.ALL)));
      }
      if (settings.getCustomOrgLdapSettings() != null) {
         checkCustomOrgLdapSettings(settings.getCustomOrgLdapSettings());
      }
      
      // parent type
      checkResource(settings);
   }
   
   public static void checkCustomOrgLdapSettings(CustomOrgLdapSettings settings) {
      // required
      assertNotNull(settings.getHostName(), String.format(OBJ_FIELD_REQ, "CustomOrgLdapSettings", "hostName"));
      assertNotNull(settings.getPort(), String.format(OBJ_FIELD_REQ, "CustomOrgLdapSettings", "port"));
      assertTrue(settings.getPort() >= 0,
            String.format(OBJ_FIELD_GTE_0, "CustomOrgLdapSettings", "port", settings.getPort()));
      assertNotNull(settings.getAuthenticationMechanism(), String.format(OBJ_FIELD_REQ, "CustomOrgLdapSettings", "authenticationMechanism"));
      assertTrue(AuthenticationMechanism.ALL.contains(settings.getAuthenticationMechanism()),
            String.format(REQUIRED_VALUE_OBJECT_FMT, "AuthenticationMechanism", "CustomOrdLdapSettings", settings.getAuthenticationMechanism(),
                  Iterables.toString(CustomOrgLdapSettings.AuthenticationMechanism.ALL)));
      assertNotNull(settings.isGroupSearchBaseEnabled(), String.format(OBJ_FIELD_REQ, "CustomOrgLdapSettings", "isGroupSearchBaseEnabled"));
      assertNotNull(settings.getConnectorType(), String.format(OBJ_FIELD_REQ, "CustomOrgLdapSettings", "connectorType"));
      assertTrue(ConnectorType.ALL.contains(settings.getConnectorType()),
            String.format(REQUIRED_VALUE_OBJECT_FMT, "ConnectorType", "CustomOrdLdapSettings", settings.getConnectorType(),
                  Iterables.toString(CustomOrgLdapSettings.ConnectorType.ALL)));
      assertNotNull(settings.getUserAttributes(), String.format(OBJ_FIELD_REQ, "CustomOrgLdapSettings", "userAttributes"));
      checkUserAttributes("CustomOrdLdapSettings", settings.getUserAttributes());
      assertNotNull(settings.getGroupAttributes(), String.format(OBJ_FIELD_REQ, "CustomOrgLdapSettings", "groupAttributes"));
      checkGroupAttributes("CustomOrdLdapSettings", settings.getGroupAttributes());
      
      // optional
      // NOTE isSsl cannot be checked
      // NOTE isSSlAcceptAll cannot be checked
      // NOTE realm cannot be checked
      // NOTE searchBase cannot be checked
      // NOTE userName cannot be checked
      // NOTE password cannot be checked
      // NOTE groupSearchBase cannot be checked
   }
   
   public static void checkUserAttributes(String api, OrgLdapUserAttributes attributes) {
      // required
      assertNotNull(attributes.getObjectClass(), String.format(OBJ_FIELD_REQ, api, "objectClass"));
      assertNotNull(attributes.getObjectIdentifier(), String.format(OBJ_FIELD_REQ, api, "objectIdentifier"));
      assertNotNull(attributes.getUserName(), String.format(OBJ_FIELD_REQ, api, "userName"));
      assertNotNull(attributes.getEmail(), String.format(OBJ_FIELD_REQ, api, "email"));
      assertNotNull(attributes.getFullName(), String.format(OBJ_FIELD_REQ, api, "fullName"));
      assertNotNull(attributes.getGivenName(), String.format(OBJ_FIELD_REQ, api, "givenName"));
      assertNotNull(attributes.getSurname(), String.format(OBJ_FIELD_REQ, api, "surname"));
      assertNotNull(attributes.getTelephone(), String.format(OBJ_FIELD_REQ, api, "telephone"));
      assertNotNull(attributes.getGroupMembershipIdentifier(), String.format(OBJ_FIELD_REQ, api, "groupMembershipIdentifier"));
      
      // optional
      // NOTE groupBackLinkIdentifier cannot be checked
   }
   
   public static void checkGroupAttributes(String api, OrgLdapGroupAttributes attributes) {
      // required
      assertNotNull(attributes.getObjectClass(), String.format(OBJ_FIELD_REQ, api, "objectClass"));
      assertNotNull(attributes.getObjectIdentifier(), String.format(OBJ_FIELD_REQ, api, "objectIdentifier"));
      assertNotNull(attributes.getGroupName(), String.format(OBJ_FIELD_REQ, api, "groupName"));
      assertNotNull(attributes.getMembership(), String.format(OBJ_FIELD_REQ, api, "membership"));
      assertNotNull(attributes.getMembershipIdentifier(), String.format(OBJ_FIELD_REQ, api, "membershipIdentifier"));
      
      // optional
      // NOTE backLinkIdentifier cannot be checked
   }

   public static void checkPasswordPolicySettings(OrgPasswordPolicySettings settings) {
      // required
      assertNotNull(settings.isAccountLockoutEnabled(),
            String.format(OBJ_FIELD_REQ, "OrgPasswordPolicySettings", "isAccountLockoutEnabled"));
      assertNotNull(settings.getInvalidLoginsBeforeLockout(),
            String.format(OBJ_FIELD_REQ, "OrgPasswordPolicySettings", "invalidLoginsBeforeLockout"));
      assertTrue(settings.getInvalidLoginsBeforeLockout() >= 0,
            String.format(OBJ_FIELD_GTE_0, "OrgPasswordPolicySettings", "storageLeaseSeconds", settings.getInvalidLoginsBeforeLockout()));
      assertNotNull(settings.getAccountLockoutIntervalMinutes(),
            String.format(OBJ_FIELD_REQ, "OrgPasswordPolicySettings", "accountLockoutIntervalMinutes"));
      assertTrue(settings.getAccountLockoutIntervalMinutes() >= 0,
            String.format(OBJ_FIELD_GTE_0, "OrgPasswordPolicySettings", "accountLockoutIntervalMinutes", settings.getAccountLockoutIntervalMinutes()));
      
      // parent type
      checkResource(settings);
   }
   
   public static void checkVAppLeaseSettings(OrgLeaseSettings settings) {
      // Check optional fields
      // NOTE deleteOnStorageLeaseExpiration cannot be checked
      if (settings.getStorageLeaseSeconds() != null) {
         assertTrue(settings.getStorageLeaseSeconds() >= 0,
               String.format(OBJ_FIELD_GTE_0, "OrgLeaseSettings", "storageLeaseSeconds", settings.getStorageLeaseSeconds()));
      }
      if (settings.getDeploymentLeaseSeconds() != null) {
         assertTrue(settings.getDeploymentLeaseSeconds() >= 0,
               String.format(OBJ_FIELD_GTE_0, "OrgLeaseSettings", "deploymentLeaseSeconds", settings.getDeploymentLeaseSeconds()));
      }
      
      // parent type
      checkResource(settings);
   }

   public static void checkVAppTemplateLeaseSettings(OrgVAppTemplateLeaseSettings settings) {
      // Check optional fields
      // NOTE deleteOnStorageLeaseExpiration cannot be checked
      if (settings.getStorageLeaseSeconds() != null) {
         assertTrue(settings.getStorageLeaseSeconds() >= 0,
               String.format(OBJ_FIELD_GTE_0, "OrgVAppTemplateLeaseSettings", "storageLeaseSeconds", settings.getStorageLeaseSeconds()));
      }
      
      // parent type
      checkResource(settings);
   }
   
   public static void checkUser(User user) {
      // Check optional fields
      // NOTE fullName cannot be checked
      // NOTE isEnabled cannot be checked
      // NOTE isLocked cannot be checked
      // NOTE im cannot be checked
      // NOTE nameInSource cannot be checked
      // NOTE isAlertEnabled cannot be checked
      // NOTE alterEmailPrefix cannot be checked
      // NOTE isExternal cannot be checked
      // NOTE isDefaultCached cannot be checked
      // NOTE isGroupRole cannot be checked
      // NOTE password cannot be checked
      
      if (user.getEmailAddress() != null) {
         checkEmailAddress(user.getEmailAddress());
      }
      if (user.getTelephone() != null) {
         checkTelephone(user.getTelephone());
      }
      if (user.getAlertEmail() != null) {
         checkEmailAddress(user.getAlertEmail());
      }
      if (user.getStoredVmQuota() != null) {
         assertTrue(user.getStoredVmQuota() >= 0,
               String.format(OBJ_FIELD_GTE_0, "User", "storedVmQuota", user.getStoredVmQuota()));
      }
      if (user.getDeployedVmQuota() != null) {
         assertTrue(user.getDeployedVmQuota() >= 0,
               String.format(OBJ_FIELD_GTE_0, "User", "deployedVmQuota", user.getDeployedVmQuota()));
      }
      if (user.getRole() != null) {
         checkReferenceType(user.getRole());
      }
      if (user.getGroups() != null) {
         for (Reference group : user.getGroups()) {
            checkReferenceType(group);
         }
      }
      
      // parent type
      checkEntityType(user);
   }
   
   public static void checkTelephone(String number) {
      // TODO regex validate telephone 
   }

   public static void checkScreenTicket(ScreenTicket ticket) {
      // NOTE the value field cannot be checked
   }

   public static void checkCustomizationSection(CustomizationSection section) {
      assertNotNull(section, String.format(NOT_NULL_OBJ_FMT, "CustomizationSection"));
      
      // Check optional fields
      if (section.getLinks() != null) {
         for (Link link : section.getLinks()) {
            checkLink(link);
         }
      }
      if (section.getType() != null) checkType(section.getType());
      if (section.getHref() != null) checkHref(section.getHref());
      
      // Check parent type
      checkOvfSectionType(section);
   }

   public static void checkProductSectionList(ProductSectionList sections) {
      assertNotNull(sections, String.format(NOT_NULL_OBJ_FMT, "ProductSectionList"));
      
      for (ProductSection productSection : sections) {
         checkOvfProductSection(productSection);
      }
      
      // Check parent type
      checkResource(sections);
   }

   public static void checkGuestCustomizationSection(GuestCustomizationSection section) {
      assertNotNull(section, String.format(NOT_NULL_OBJ_FMT, "NetworkConfigSection"));
      
      // TODO assertions have failed for returned GuestCustomizationSection.
      // Perhaps "invalid" values are just ignored, rather than guaranteeing they will never be returned?
//      if (!section.isJoinDomainEnabled()) {
//         assertFalse(section.isUseOrgSettings() != null && section.isUseOrgSettings());
//         assertNull(section.getDomainName());
//         assertNull(section.getDomainUserName());
//         assertNull(section.getDomainUserPassword());
//      }
//      
//      if (!section.isAdminPasswordEnabled()) {
//         assertFalse(section.isAdminPasswordAuto() != null && section.isAdminPasswordAuto());
//         assertFalse(section.isResetPasswordRequired() != null && section.isResetPasswordRequired());
//         if (section.isAdminPasswordAuto()) {
//            assertNull(section.getAdminPassword());
//         }
//      }
      
      // Check parent type
      checkOvfSectionType(section);
   }

   public static void checkLeaseSettingsSection(LeaseSettingsSection section) {
      assertNotNull(section, String.format(NOT_NULL_OBJ_FMT, "LeaseSettingsSection"));
      
      if (section.getLinks() != null) {
         for (Link link : section.getLinks()) {
            checkLink(link);
         }
      }
      
      // Check parent type
      checkOvfSectionType(section);
   }

   public static void checkNetworkConfigSection(NetworkConfigSection section) {
      assertNotNull(section, String.format(NOT_NULL_OBJ_FMT, "NetworkConfigSection"));
      
      if (section.getNetworkConfigs() != null) {
         for (VAppNetworkConfiguration networkConfig : section.getNetworkConfigs()) {
            checkVAppNetworkConfig(networkConfig);
         }
      }
      if (section.getLinks() != null) {
         for (Link link : section.getLinks()) {
            checkLink(link);
         }
      }
      if (section.getHref() != null) {
         checkHref(section.getHref());
      }
      
      // Check parent type
      checkOvfSectionType(section);
   }

   public static void checkNetworkSection(NetworkSection section) {
      assertNotNull(section, String.format(NOT_NULL_OBJ_FMT, "NetworkSection"));
      
      // Check optional fields
      if (section.getNetworks() != null) {
	      for (org.jclouds.dmtf.ovf.Network network : section.getNetworks()) {
	         checkOvfNetwork(network);
	      }
      }

      // Check parent type
      checkOvfSectionType(section);
   }

   public static void checkOvfNetwork(org.jclouds.dmtf.ovf.Network network) {
      assertNotNull(network, String.format(NOT_NULL_OBJ_FMT, "Network"));
      
      // Check optional fields
      // NOTE name field cannot be checked
      // NOTE description field cannot be checked
   }

   public static void checkOperatingSystemSection(OperatingSystemSection section) {
      assertNotNull(section, String.format(NOT_NULL_OBJ_FMT, "OperatingSystemSection"));
      
      // Check optional fields
      // NOTE id field cannot be checked
      // NOTE version field cannot be checked
      // NOTE description field cannot be checked

      // Check parent type
      checkOvfSectionType(section);
   }

   public static void checkRuntimeInfoSection(RuntimeInfoSection section) {
      assertNotNull(section, String.format(NOT_NULL_OBJ_FMT, "RuntimeInfoSection"));
      
      // Check optional fields
      VMWareTools tools = section.getVMWareTools();
      if (tools != null) checkVMWareTools(tools);

      // NOTE does this mean anything?
      for (Object any : section.getAny()) {
         assertNotNull(any);
      }

      // Check parent type
      checkOvfSectionType(section);
   }

   public static void checkVMWareTools(VMWareTools tools) {
      assertNotNull(tools, String.format(NOT_NULL_OBJ_FMT, "VMWareTools"));
      
      // Check required fields
      assertNotNull(tools.getVersion(), String.format(NOT_NULL_OBJ_FIELD_FMT, "version", "VMWareTools"));
      assertFalse(tools.getVersion().isEmpty(), String.format(NOT_EMPTY_STRING_FMT, "VMWareTools.version"));
   }

   public static void checkStartupSection(StartupSection section) {
      // TODO

      // Check parent type
      checkOvfSectionType(section);
   }

   private static void checkVAppNetworkConfig(VAppNetworkConfiguration val) {
      assertNotNull(val, String.format(NOT_NULL_OBJ_FMT, "VAppNetworkConfiguration"));
      
      // required fields
      assertNotNull(val.getNetworkName(), String.format(NOT_NULL_OBJ_FIELD_FMT, "NetworkName", "VAppNetworkConfiguration"));
      checkNetworkConfiguration(val.getConfiguration());
      
      checkResource(val);
   }

   public static void checkNetworkConnectionSection(NetworkConnectionSection val) {
      assertNotNull(val, String.format(NOT_NULL_OBJ_FMT, "VAppConnectionSection"));
      
      // Check optional fields
      if (val.getLinks() != null) {
         for (Link link : val.getLinks()) {
            checkLink(link);
         }
      }
      if (val.getHref() != null) {
         checkHref(val.getHref());
      }
      if (val.getNetworkConnections() != null) {
         for (NetworkConnection networkConnection : val.getNetworkConnections()) {
            checkNetworkConnection(networkConnection);
         }
      }
      if (val.getType() != null) {
         checkType(val.getType());
      }
      
      checkOvfSectionType(val);
   }

   private static void checkNetworkConnection(NetworkConnection val) {
      assertNotNull(val, String.format(NOT_NULL_OBJ_FMT, "NetworkConnection"));
      
      // Check required fields
      assertNotNull(val.getNetwork(), String.format(NOT_NULL_OBJ_FIELD_FMT, "Network", "NetworkConnection"));
      assertNotNull(val.getIpAddressAllocationMode(), String.format(NOT_NULL_OBJ_FIELD_FMT, "IpAddressAllocationMode", "NetworkConnection"));
      assertNotEquals(val.getIpAddressAllocationMode(), NetworkConnection.IpAddressAllocationMode.UNRECOGNIZED,
            String.format(REQUIRED_VALUE_OBJECT_FMT, "IpAddressAllocationMode", "NetworkConnection", val.getIpAddressAllocationMode(), Iterables.toString(NetworkConnection.IpAddressAllocationMode.ALL)));
      
      // Check optional fields
      if (val.getIpAddress() != null) {
         checkIpAddress(val.getIpAddress());
      }
      if (val.getExternalIpAddress() != null) {
         checkIpAddress(val.getExternalIpAddress());
      }

      if (val.getMACAddress() != null) {
         checkMacAddress(val.getMACAddress());
      }
   }

   public static void checkRasdItemsList(RasdItemsList items) {
      // Check fields
      // TODO

      for (RasdItem item : items.getItems()) {
         checkResourceAllocationSettingData(item);
      }
   }

   public static void checkOvfSectionType(SectionType section) {
      assertNotNull(section, String.format(NOT_NULL_OBJ_FMT, "SectionType"));
   }
   
   public static void checkOvfProductSection(ProductSection val) {
      assertNotNull(val, String.format(NOT_NULL_OBJ_FMT, "ProductSection"));

      if (val.getProperties() != null) {
         for (org.jclouds.dmtf.ovf.Property property : val.getProperties()) {
            checkOvfProperty(property);
         }
      }
      
      // Check parent type
      checkOvfSectionType(val);
   }

   private static void checkOvfProperty(org.jclouds.dmtf.ovf.Property val) {
      assertNotNull(val, String.format(NOT_NULL_OBJ_FMT, "Property"));
   }

   public static void checkOvfNetworkSection(NetworkSection val) {
      assertNotNull(val, String.format(NOT_NULL_OBJ_FMT, "NetworkSection"));

      if (val.getNetworks() != null) {
         for (org.jclouds.dmtf.ovf.Network network : val.getNetworks()) {
            checkOvfNetwork(network);
         }
      }
      
      checkOvfSectionType(val);
   }

   public static void checkOvfEnvelope(Envelope val) {
      assertNotNull(val, String.format(NOT_NULL_OBJ_FMT, "Envelope"));
      
      if (val.getDiskSections() != null) {
         for (DiskSection diskSection : val.getDiskSections()) {
            checkOvfDiskSection(diskSection);
         }
      }
      if (val.getNetworkSections() != null) {
         for (NetworkSection networkSection : val.getNetworkSections()) {
            checkOvfNetworkSection(networkSection);
         }
      }
      if (val.getVirtualSystem() != null) {
         checkOvfVirtualSystem(val.getVirtualSystem());
      }
   }

   private static void checkOvfVirtualSystem(VirtualSystem val) {
      assertNotNull(val, String.format(NOT_NULL_OBJ_FMT, "VirtualSystem"));
      
      if (val.getProductSections() != null) {
         for (ProductSection productSection : val.getProductSections()) {
            checkOvfProductSection(productSection);
         }
      }
      if (val.getVirtualHardwareSections() != null) {
         for (VirtualHardwareSection virtualHardwareSection : val.getVirtualHardwareSections()) {
            checkOvfVirtualHardwareSection(virtualHardwareSection);
         }
      }
      if (val.getOperatingSystemSection() != null) {
         checkOvfOperationSystemSection(val.getOperatingSystemSection());
      }
   }

   private static void checkOvfDiskSection(DiskSection section) {
      assertNotNull(section, String.format(NOT_NULL_OBJ_FMT, "DiskSection"));
      
      if (section.getDisks() != null) {
         for (Disk disk : section.getDisks()) {
            checkOvfDisk(disk);
         }
      }
      
      // Check parent type
      checkOvfSectionType(section);
   }
   
   private static void checkOvfDisk(Disk disk) {
      assertNotNull(disk, String.format(NOT_NULL_OBJ_FMT, "Disk"));
   }

   private static void checkOvfOperationSystemSection(OperatingSystemSection section) {
      assertNotNull(section, String.format(NOT_NULL_OBJ_FMT, "OperatingSystemSection"));
      
      // Check parent type
      checkOvfSectionType(section);
   }

   private static void checkOvfVirtualHardwareSection(VirtualHardwareSection section) {
      assertNotNull(section, String.format(NOT_NULL_OBJ_FMT, "VirtualHardwareSection"));
      
      if (section.getItems() != null) {
         for (ResourceAllocationSettingData item : section.getItems()) {
            checkCimResourceAllocationSettingData((RasdItem) item);
         }
      }
      if (section.getSystem() != null) {
         checkCimVirtualSystemSettingData(section.getSystem());
      }
      
      // Check parent type
      checkOvfSectionType(section);
   }

   private static void checkCimVirtualSystemSettingData(VirtualSystemSettingData val) {
      // TODO Could do more assertions...
      assertNotNull(val, String.format(NOT_NULL_OBJ_FMT, "VirtualSystemSettingData"));
   }

   private static void checkCimResourceAllocationSettingData(RasdItem val) {
      // TODO Could do more assertions...
      assertNotNull(val, String.format(NOT_NULL_OBJ_FMT, "ResouorceAllocationSettingData"));
   }

   public static void checkOrgNetwork(OrgNetwork network) {
      // optional
      Reference networkPoolRef = network.getNetworkPool();
      if (networkPoolRef != null) {
         Checks.checkReferenceType(networkPoolRef);
      }
      IpAddresses allowedExternalIpAddresses = network.getAllowedExternalIpAddresses();
      if (allowedExternalIpAddresses != null) {
         Checks.checkIpAddresses(allowedExternalIpAddresses);
      }
      
      // parent type
      checkNetwork(network);
   }
   
   public static void checkExternalNetwork(ExternalNetwork network) {
      // required
      assertNotNull(network.getProviderInfo(), String.format(OBJ_FIELD_REQ, 
            "ExternalNetwork", "providerInfo"));
      
      // parent type
      checkNetwork(network);
   }

   public static void checkAdminVdc(AdminVdc vdc) {
      // optional
      // NOTE isThinProvision cannot be checked
      // NOTE usesFastProvisioning cannot be checked
      if (vdc.getResourceGuaranteedMemory() != null) {
         // TODO: between 0 and 1 inc.
      }
      if (vdc.getResourceGuaranteedCpu() != null) {
         // TODO: between 0 and 1 inc.
      }
      if (vdc.getVCpuInMhz() != null) {
         assertTrue(vdc.getVCpuInMhz() >= 0, String.format(OBJ_FIELD_GTE_0, 
               "Vdc", "cCpuInMhz", vdc.getVCpuInMhz()));
      }
      if (vdc.getNetworkPoolReference() != null) {
         checkReferenceType(vdc.getNetworkPoolReference());
      }
      if (vdc.getProviderVdcReference() != null) {
         checkReferenceType(vdc.getProviderVdcReference());
      }
      
      // parent type
      checkVdc(vdc);
   }
   
   public static void checkVdc(Vdc vdc) {
      // required
      assertNotNull(vdc.getAllocationModel(), String.format(OBJ_FIELD_REQ, "Vdc", "allocationModel"));
      // one of: AllocationVApp, AllocationPool, ReservationPool
      assertNotNull(vdc.getStorageCapacity(), String.format(OBJ_FIELD_REQ, "Vdc", "storageCapacity"));
      checkCapacityWithUsage(vdc.getStorageCapacity());
      assertNotNull(vdc.getComputeCapacity(), String.format(OBJ_FIELD_REQ, "Vdc", "computeCapacity"));
      checkComputeCapacity(vdc.getComputeCapacity());
      assertNotNull(vdc.getNicQuota(), String.format(OBJ_FIELD_REQ, "Vdc", "nicQuota"));
      assertTrue(vdc.getNicQuota() >= 0, String.format(OBJ_FIELD_GTE_0, 
            "Vdc", "nicQuota", vdc.getNicQuota()));
      assertNotNull(vdc.getNetworkQuota(), String.format(OBJ_FIELD_REQ, "Vdc", "networkQuota"));
      assertTrue(vdc.getNetworkQuota() >= 0, String.format(OBJ_FIELD_GTE_0, 
            "Vdc", "networkQuota", vdc.getNetworkQuota()));
      
      // optional
      // NOTE isEnabled cannot be checked
      for (Reference resourceEntity : vdc.getResourceEntities()) {
         checkReferenceType(resourceEntity);
      }
      for (Reference availableNetwork : vdc.getAvailableNetworks()) {
         checkReferenceType(availableNetwork);
      }
      if (vdc.getCapabilities() != null) {
         checkCapabilities(vdc.getCapabilities());
      }
      if (vdc.getVmQuota() != null) {
         assertTrue(vdc.getVmQuota() >= 0, String.format(OBJ_FIELD_GTE_0, 
               "Vdc", "vmQuota", vdc.getVmQuota()));
      }
      
      // parent type
      checkEntityType(vdc);
   }

   public static void checkQueryResultRecord(QueryResultRecordType record) {
      checkHref(record.getHref());
      if (record.getLinks() != null) {
         for (Link link : record.getLinks()) {
            checkLink(link);
         }
      }
      if (record.getType() != null) {
         checkType(record.getType());
      }
   }

   public static void checkReferences(References references) {
      // optional
      for (Reference reference : references.getReferences()) {
         checkReferenceType(reference);
      }
      
      // parent type
      checkContainerType(references);
   }

   public static void checkContainerType(ContainerType container) {
      // optional
      // NOTE name can't be checked
      if (container.getPage() != null) {
         assertTrue(container.getPage() >= 1, "page must be >=1 ");
      }
      if (container.getPageSize() != null) {
         assertTrue(container.getPageSize() >= 1, "pageSize must be >=1 ");
      }
      if (container.getTotal() != null) {
         assertTrue(container.getTotal() >= 0, "total must be >=0 ");
      }
         
      // parent type
      checkResource(container);
   }
}
