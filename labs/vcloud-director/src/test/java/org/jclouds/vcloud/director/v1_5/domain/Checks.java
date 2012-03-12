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
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.OBJ_FIELD_ATTRB_REQ;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.OBJ_FIELD_EQ;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.OBJ_FIELD_GTE_0;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.OBJ_FIELD_REQ;
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
import org.jclouds.vcloud.director.v1_5.domain.CustomOrgLdapSettings.AuthenticationMechanism;
import org.jclouds.vcloud.director.v1_5.domain.CustomOrgLdapSettings.ConnectorType;
import org.jclouds.vcloud.director.v1_5.domain.OrgLdapSettings.LdapMode;

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
      Set<Task> tasks = entity.getTasks();
      if (tasks != null && tasks != null && !tasks.isEmpty()) {
         for (Task task : tasks) checkTask(task);
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
   
   public static void checkAdminOrg(AdminOrg org) {
      // required
      assertNotNull(org.getSettings(), String.format(NOT_NULL_OBJECT_FMT, "settings", "AdminOrg"));
      
      // optional
      if (org.getGroups() != null) {
         checkGroupsList(org.getGroups());
      }
      if (org.getCatalogs() != null) {
         checkCatalogsList(org.getCatalogs());
      }
      if (org.getVdcs() != null) {
         checkVdcs(org.getVdcs());
      }
      if (org.getNetworks() != null) {
         checkNetworks(org.getNetworks());
      }
      
      // Check parent type
      checkOrg(org);
   }
   
   public static void checkCatalogsList(CatalogsList catalogList) {
      for (Reference catalogItem : catalogList.getCatalogItems()) {
         checkReferenceType(catalogItem);
      }
   }
   
   public static void checkVdcs(Vdcs vdcs) {
      for (Reference vdc : vdcs.getVdcs()) {
         checkReferenceType(vdc);
      }
   }
   
   public static void checkNetworks(Networks networks) {
      for (Reference network : networks.getNetwork()) {
         checkReferenceType(network);
      }
   }
   
   public static void checkAdminCatalog(AdminCatalog catalog) {
      // Check parent type
      checkCatalogType(catalog);
   }

   public static void checkCatalogType(CatalogType<?> catalog) {
      // Check optional elements/attributes
      Owner owner = catalog.getOwner();
      if (owner != null) checkOwner(owner);
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

   public static void checkOwner(Owner owner) {
       // Check optional elements/attributes
      if (owner.getUser() != null) {
         checkReferenceType(owner.getUser());
      }
      
     // Check parent type
      checkResourceType(owner);
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
         for (NetworkServiceType service : features.getNetworkServices()) {
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
      InetAddresses.isInetAddress(ip);
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

   public static void checkResourceEntities(ResourceEntities resourceEntities) {
      for (Reference resourceEntity : resourceEntities.getResourceEntities()) {
         checkReferenceType(resourceEntity);
      }
   }

   public static void checkAvailableNetworks(AvailableNetworks availableNetworks) {
      for (Reference network : availableNetworks.getNetworks()) {
         checkReferenceType(network);
      }
   }
   
   public static void checkCapabilities(Capabilities capabilities) {
      // Check optional fields
      if (capabilities.getSupportedHardwareVersions() != null) {
         checkSupportedHardwareVersions(capabilities.getSupportedHardwareVersions());
      }
   }
   public static void checkSupportedHardwareVersions(SupportedHardwareVersions supportedHardwareVersions) {
      for (String supportedHardwareVersion : supportedHardwareVersions.getSupportedHardwareVersions()) {
         // NOTE supportedHardwareVersion cannot be checked?
      }
   }

   public static void checkMetadataFor(String client, Metadata metadata) {
      for (MetadataEntry entry : metadata.getMetadataEntries()) {
         // Check required fields
         assertNotNull(entry.getKey(), 
               String.format(OBJ_FIELD_ATTRB_REQ, client, "MetadataEntry", entry.getKey(), "key"));
         assertNotNull(entry.getValue(), 
               String.format(OBJ_FIELD_ATTRB_REQ, client, "MetadataEntry", entry.getValue(), "value"));
          
         // Check parent type
         checkResourceType(entry);
      }
      
      // Check parent type
      checkResourceType(metadata);
   }

   public static void checkMetadataValueFor(String client, MetadataValue metadataValue) {
      // Check required fields
      String value = metadataValue.getValue();
      assertNotNull(value, 
            String.format(OBJ_FIELD_ATTRB_REQ, client, "MetadataEntry", 
                  metadataValue.toString(), "value"));
      assertEquals(value, "value", 
            String.format(OBJ_FIELD_EQ, client, "metadataEntry.value", "value", value));
      
      // Check parent type
      checkResourceType(metadataValue);
   }

   public static void checkVApp(VApp vApp) {
      // TODO Auto-generated method stub
      
   }

   public static void checkVAppTemplate(VAppTemplate template) {
      // TODO Auto-generated method stub
      
   }

   public static void checkMediaFor(String client, Media media) {
      // required
      assertNotNull(media.getImageType(), String.format(OBJ_FIELD_REQ, client, "imageType"));
      checkImageType(media.getImageType());
      assertNotNull(media.getSize(), String.format(OBJ_FIELD_REQ, client, "size"));
      assertTrue(media.getSize() >= 0, String.format(OBJ_FIELD_GTE_0, client, "size", media.getSize()));
      
      // parent type
      checkResourceEntityType(media);
   }
   
   public static void checkGroupsList(GroupsList groupsList) {
      // Check optional fields
      if (groupsList.getGroups() != null) {
         for (Reference group : groupsList.getGroups()) {
            checkReferenceType(group);
         }
      }
   }
   
   public static void checkGroup(Group group) {
      // Check optional fields
      // NOTE nameInSource cannot be checked
      if (group.getUsersList() != null) {
         checkUsersList(group.getUsersList());
      }
      if (group.getRole() != null) {
         checkReferenceType(group.getRole());
      }
      
      // parent type
      checkEntityType(group);
   }

   public static void checkUsersList(UsersList usersList) {
      // Check optional fields
      if (usersList.getUsers() != null) {
         for (Reference user : usersList.getUsers()) {
            checkReferenceType(user);
         }
      }
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
      checkResourceType(settings);
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
      checkResourceType(settings);
   }
   
   public static void checkEmailAddress(String email) {
      // TODO: validate email addresses
   }
   
   public static void checkGeneralSettings(OrgGeneralSettings settings) {
      // Check optional fields
      // NOTE canPublishCatalogs cannot be checked
      // NOTE useServerBootSequence cannot be checked
      if (settings.getDeployedVMQuota() != null) {
         assertTrue(settings.getDeployedVMQuota() >= 0, String.format(
               OBJ_FIELD_GTE_0, "deployedVMQuota", "port", settings.getDeployedVMQuota()));
      }
      if (settings.getStoredVmQuota() != null) {
         assertTrue(settings.getStoredVmQuota() >= 0, String.format(
               OBJ_FIELD_GTE_0, "storedVmQuota", "port", settings.getStoredVmQuota()));
      }
      if (settings.getDelayAfterPowerOnSeconds() != null) {
         assertTrue(settings.getDelayAfterPowerOnSeconds() >= 0, String.format(
               OBJ_FIELD_GTE_0, "delayAfterPowerOnSeconds", "port", settings.getDelayAfterPowerOnSeconds()));
      }
      
      // parent type
      checkResourceType(settings);
   }
   
   public static void checkLdapSettings(OrgLdapSettings settings) {
      // Check optional fields
      // NOTE customUsersOu cannot be checked
      if (settings.getLdapMode() != null) {
         assertTrue(LdapMode.ALL.contains(settings.getLdapMode()), String.format(REQUIRED_VALUE_OBJECT_FMT, 
               "LdapMode", "OrdLdapSettings", settings.getLdapMode(), Iterables.toString(OrgLdapSettings.LdapMode.ALL)));
      }
      if (settings.getCustomOrgLdapSettings() != null) {
         checkCustomOrgLdapSettings(settings.getCustomOrgLdapSettings());
      }
      
      // parent type
      checkResourceType(settings);
   }
   
   public static void checkCustomOrgLdapSettings(CustomOrgLdapSettings settings) {
      // required
      assertNotNull(settings.getHostName(), String.format(OBJ_FIELD_REQ, "CustomOrgLdapSettings", "hostName"));
      assertNotNull(settings.getPort(), String.format(OBJ_FIELD_REQ, "CustomOrgLdapSettings", "port"));
      assertTrue(settings.getPort() >= 0, String.format(
            OBJ_FIELD_GTE_0, "CustomOrgLdapSettings", "port", settings.getPort()));
      assertNotNull(settings.getAuthenticationMechanism(), String.format(OBJ_FIELD_REQ, "CustomOrgLdapSettings", "authenticationMechanism"));
      assertTrue(AuthenticationMechanism.ALL.contains(settings.getAuthenticationMechanism()), String.format(REQUIRED_VALUE_OBJECT_FMT, 
            "AuthenticationMechanism", "CustomOrdLdapSettings", settings.getAuthenticationMechanism(), 
            Iterables.toString(CustomOrgLdapSettings.AuthenticationMechanism.ALL)));
      assertNotNull(settings.isGroupSearchBaseEnabled(), String.format(OBJ_FIELD_REQ, "CustomOrgLdapSettings", "isGroupSearchBaseEnabled"));
      assertNotNull(settings.getConnectorType(), String.format(OBJ_FIELD_REQ, "CustomOrgLdapSettings", "connectorType"));
      assertTrue(ConnectorType.ALL.contains(settings.getConnectorType()), String.format(REQUIRED_VALUE_OBJECT_FMT, 
            "ConnectorType", "CustomOrdLdapSettings", settings.getConnectorType(), 
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
   
   public static void checkUserAttributes(String client, OrgLdapUserAttributes attributes) {
      // required
      assertNotNull(attributes.getObjectClass(), String.format(OBJ_FIELD_REQ, client, "objectClass"));
      assertNotNull(attributes.getObjectIdentifier(), String.format(OBJ_FIELD_REQ, client, "objectIdentifier"));
      assertNotNull(attributes.getUserName(), String.format(OBJ_FIELD_REQ, client, "userName"));
      assertNotNull(attributes.getEmail(), String.format(OBJ_FIELD_REQ, client, "email"));
      assertNotNull(attributes.getFullName(), String.format(OBJ_FIELD_REQ, client, "fullName"));
      assertNotNull(attributes.getGivenName(), String.format(OBJ_FIELD_REQ, client, "givenName"));
      assertNotNull(attributes.getSurname(), String.format(OBJ_FIELD_REQ, client, "surname"));
      assertNotNull(attributes.getTelephone(), String.format(OBJ_FIELD_REQ, client, "telephone"));
      assertNotNull(attributes.getGroupMembershipIdentifier(), String.format(OBJ_FIELD_REQ, client, "groupMembershipIdentifier"));
      
      // optional
      // NOTE groupBackLinkIdentifier cannot be checked
   }
   
   public static void checkGroupAttributes(String client, OrgLdapGroupAttributes attributes) {
      // required
      assertNotNull(attributes.getObjectClass(), String.format(OBJ_FIELD_REQ, client, "objectClass"));
      assertNotNull(attributes.getObjectIdentifier(), String.format(OBJ_FIELD_REQ, client, "objectIdentifier"));
      assertNotNull(attributes.getGroupName(), String.format(OBJ_FIELD_REQ, client, "groupName"));
      assertNotNull(attributes.getMembership(), String.format(OBJ_FIELD_REQ, client, "membership"));
      assertNotNull(attributes.getMembershipIdentifier(), String.format(OBJ_FIELD_REQ, client, "membershipIdentifier"));
      
      // optional
      // NOTE backLinkIdentifier cannot be checked
   }

   public static void checkPasswordPolicySettings(OrgPasswordPolicySettings settings) {
      // required
      assertNotNull(settings.isAccountLockoutEnabled(), String.format(OBJ_FIELD_REQ, "OrgPasswordPolicySettings", "isAccountLockoutEnabled"));
      assertNotNull(settings.getInvalidLoginsBeforeLockout(), String.format(OBJ_FIELD_REQ, "OrgPasswordPolicySettings", "invalidLoginsBeforeLockout"));
      assertTrue(settings.getInvalidLoginsBeforeLockout() >= 0, String.format(
            OBJ_FIELD_GTE_0, "OrgPasswordPolicySettings", "storageLeaseSeconds", settings.getInvalidLoginsBeforeLockout()));
      assertNotNull(settings.getAccountLockoutIntervalMinutes(), String.format(OBJ_FIELD_REQ, "OrgPasswordPolicySettings", "accountLockoutIntervalMinutes"));
      assertTrue(settings.getAccountLockoutIntervalMinutes() >= 0, String.format(
            OBJ_FIELD_GTE_0, "OrgPasswordPolicySettings", "accountLockoutIntervalMinutes", settings.getAccountLockoutIntervalMinutes()));
      
      // parent type
      checkResourceType(settings);
   }
   
   public static void checkVAppLeaseSettings(OrgLeaseSettings settings) {
      // Check optional fields
      // NOTE deleteOnStorageLeaseExpiration cannot be checked
      if (settings.getStorageLeaseSeconds() != null) {
         assertTrue(settings.getStorageLeaseSeconds() >= 0, String.format(
               OBJ_FIELD_GTE_0, "OrgLeaseSettings", "storageLeaseSeconds", settings.getStorageLeaseSeconds()));
      }
      if (settings.getDeploymentLeaseSeconds() != null) {
         assertTrue(settings.getDeploymentLeaseSeconds() >= 0, String.format(
               OBJ_FIELD_GTE_0, "OrgLeaseSettings", "deploymentLeaseSeconds", settings.getDeploymentLeaseSeconds()));
      }
      
      // parent type
      checkResourceType(settings);
   }

   public static void checkVAppTemplateLeaseSettings(OrgVAppTemplateLeaseSettings settings) {
      // Check optional fields
      // NOTE deleteOnStorageLeaseExpiration cannot be checked
      if (settings.getStorageLeaseSeconds() != null) {
         assertTrue(settings.getStorageLeaseSeconds() >= 0, String.format(
               OBJ_FIELD_GTE_0, "OrgVAppTemplateLeaseSettings", "storageLeaseSeconds", settings.getStorageLeaseSeconds()));
      }
      
      // parent type
      checkResourceType(settings);
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
         assertTrue(user.getStoredVmQuota() >= 0, String.format(OBJ_FIELD_GTE_0, 
               "User", "storedVmQuota", user.getStoredVmQuota()));
      }
      if (user.getDeployedVmQuota() != null) {
         assertTrue(user.getDeployedVmQuota() >= 0, String.format(OBJ_FIELD_GTE_0, 
               "User", "deployedVmQuota", user.getDeployedVmQuota()));
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
      // TODO: regex validate telephone 
   }
}
