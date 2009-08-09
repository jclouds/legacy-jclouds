/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.rackspace.cloudfiles;

import static org.jclouds.rackspace.reference.RackspaceConstants.PROPERTY_RACKSPACE_KEY;
import static org.jclouds.rackspace.reference.RackspaceConstants.PROPERTY_RACKSPACE_USER;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.rackspace.cloudfiles.domain.ContainerCDNMetadata;
import org.jclouds.rackspace.cloudfiles.options.ListCdnContainerOptions;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

/**
 * Tests behavior of {@code JaxrsAnnotationProcessor} for Cloud Files CDN service.
 * 
 * @author James Murty
 */
@Test(groups = "live", testName = "cloudfiles.CloudFilesAuthenticationLiveTest")
public class CloudFilesCDNConnectionLiveTest {

   protected static final String sysRackspaceUser = System.getProperty(PROPERTY_RACKSPACE_USER);
   protected static final String sysRackspaceKey = System.getProperty(PROPERTY_RACKSPACE_KEY);

   private String bucketPrefix = System.getProperty("user.name") + ".cfcdnint";
   CloudFilesCDNConnection cdnConnection;
   CloudFilesConnection filesConnection;

   @BeforeGroups(groups = { "live" })
   public void setupConnection() {
      cdnConnection = CloudFilesCDNContextBuilder.newBuilder(sysRackspaceUser, sysRackspaceKey)
               .withModule(new Log4JLoggingModule()).withJsonDebug().buildContext().getConnection();
      filesConnection = CloudFilesContextBuilder.newBuilder(sysRackspaceUser, sysRackspaceKey)
      .withModule(new Log4JLoggingModule()).withJsonDebug().buildContext().getConnection();
   }

   @Test
   public void testCDNOperations() throws Exception {      
      final long minimumTTL = 60 * 60; // The minimum TTL is 1 hour
      
      // Create two new containers for testing      
      final String containerNameWithCDN = bucketPrefix + ".testCDNOperationsContainerWithCDN";
      final String containerNameWithoutCDN = bucketPrefix + ".testCDNOperationsContainerWithoutCDN";
      assertTrue(filesConnection.putContainer(containerNameWithCDN));
      assertTrue(filesConnection.putContainer(containerNameWithoutCDN));
      
      ContainerCDNMetadata cdnMetadata = null;
      
      // Enable CDN with PUT for one container
      final String cdnUri = cdnConnection.enableCDN(containerNameWithCDN);
      assertTrue(cdnUri != null);
      assertTrue(cdnUri.startsWith("http://"));
      
      // Confirm CDN is enabled via HEAD request and has default TTL
      cdnMetadata = cdnConnection.getCDNMetadata(containerNameWithCDN);
      assertTrue(cdnMetadata.isCdnEnabled());
      assertEquals(cdnMetadata.getCdnUri(), cdnUri);
      final long initialTTL = cdnMetadata.getTtl();
      
      // Check HEAD responses for non-existent container, and container with no CDN metadata
      cdnMetadata = cdnConnection.getCDNMetadata(containerNameWithoutCDN);
      assertEquals(cdnMetadata, ContainerCDNMetadata.NOT_FOUND);
      cdnMetadata = cdnConnection.getCDNMetadata("DoesNotExist");
      assertEquals(cdnMetadata, ContainerCDNMetadata.NOT_FOUND);
      
      // List CDN metadata for containers, and ensure all CDN info is available for enabled container
      List<ContainerCDNMetadata> cdnMetadataList = cdnConnection.listCDNContainers();
      assertTrue(cdnMetadataList.size() >= 1);
      assertTrue(Iterables.any(cdnMetadataList, new Predicate<ContainerCDNMetadata>() {
          public boolean apply(ContainerCDNMetadata cdnMetadata) {
             return (
                cdnMetadata.getName().equals(containerNameWithCDN) &&                  
                cdnMetadata.isCdnEnabled() &&
                cdnMetadata.getTtl() == initialTTL &&
                cdnMetadata.getCdnUri().equals(cdnUri));
          }  
      }));
      
      // Test listing with options
      cdnMetadataList = cdnConnection.listCDNContainers(
            ListCdnContainerOptions.Builder.enabledOnly());
      assertTrue(Iterables.all(cdnMetadataList, new Predicate<ContainerCDNMetadata>() {
         public boolean apply(ContainerCDNMetadata cdnMetadata) {
            return cdnMetadata.isCdnEnabled();
         }  
      }));

      cdnMetadataList = cdnConnection.listCDNContainers(ListCdnContainerOptions.Builder
            .afterMarker(containerNameWithCDN.substring(0, containerNameWithCDN.length() - 1)) 
            .maxResults(1));
      assertEquals(cdnMetadataList.size(), 1);
      assertEquals(cdnMetadataList.get(0).getName(), containerNameWithCDN);

      // Enable CDN with PUT for the same container, this time with a custom TTL
      long ttl = 4000;
      cdnConnection.enableCDN(containerNameWithCDN, ttl);
      
      cdnMetadata = cdnConnection.getCDNMetadata(containerNameWithCDN);
      assertTrue(cdnMetadata.isCdnEnabled());
      assertEquals(cdnMetadata.getTtl(), ttl);      
      
      // Check POST by updating TTL settings
      ttl = minimumTTL;
      cdnConnection.updateCDN(containerNameWithCDN, minimumTTL);
      
      cdnMetadata = cdnConnection.getCDNMetadata(containerNameWithCDN);
      assertTrue(cdnMetadata.isCdnEnabled());
      assertEquals(cdnMetadata.getTtl(), minimumTTL);

      // Confirm that minimum allowed value for TTL is 3600, lower values are ignored.
      cdnConnection.updateCDN(containerNameWithCDN, 3599L);      
      cdnMetadata = cdnConnection.getCDNMetadata(containerNameWithCDN);
      assertEquals(cdnMetadata.getTtl(), minimumTTL); // Note that TTL is 3600 here, not 3599

      // Disable CDN with POST
      assertTrue(cdnConnection.disableCDN(containerNameWithCDN));
      
      cdnMetadata = cdnConnection.getCDNMetadata(containerNameWithCDN);
      assertEquals(cdnMetadata.isCdnEnabled(), false);
            
      // Delete test containers
      assertTrue(filesConnection.deleteContainerIfEmpty(containerNameWithCDN));
      assertTrue(filesConnection.deleteContainerIfEmpty(containerNameWithoutCDN));
   }

}
