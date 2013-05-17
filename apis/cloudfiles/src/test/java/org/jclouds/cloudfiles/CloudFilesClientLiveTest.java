/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.cloudfiles;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.net.URI;
import java.util.Set;

import org.jclouds.cloudfiles.domain.ContainerCDNMetadata;
import org.jclouds.cloudfiles.options.ListCdnContainerOptions;
import org.jclouds.openstack.swift.CommonSwiftClientLiveTest;
import org.jclouds.openstack.swift.domain.ContainerMetadata;
import org.jclouds.openstack.swift.domain.SwiftObject;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

/**
 * Tests behavior of {@code JaxrsAnnotationProcessor}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live")
public class CloudFilesClientLiveTest extends CommonSwiftClientLiveTest<CloudFilesClient> {

   public CloudFilesClientLiveTest(){
      provider = "cloudfiles";
   }
   
   @Override
   public CloudFilesClient getApi() {
      return view.unwrap(CloudFilesApiMetadata.CONTEXT_TOKEN).getApi();
   }
   
   @Override
   protected void testGetObjectContentType(SwiftObject getBlob) {
      //lovely new bug.. should be text/plain
      assertEquals(getBlob.getInfo().getContentType(), "application/x-www-form-urlencoded");
   }

   @Test
   public void testCDNOperations() throws Exception {
      final long minimumTTL = 60 * 60; // The minimum TTL is 1 hour

      // Create two new containers for testing
      final String containerNameWithCDN = getContainerName();
      final String containerNameWithoutCDN = getContainerName();
      try {
         try {
            getApi().disableCDN(containerNameWithCDN);
            getApi().disableCDN(containerNameWithoutCDN);
         } catch (Exception e) {

         }
         ContainerCDNMetadata cdnMetadata = null;

         // Enable CDN with PUT for one container
         final URI cdnUri = getApi().enableCDN(containerNameWithCDN);
         assertNotNull(cdnUri);

         // Confirm CDN is enabled via HEAD request and has default TTL
         cdnMetadata = getApi().getCDNMetadata(containerNameWithCDN);

         assertTrue(cdnMetadata.isCDNEnabled());
         assertEquals(cdnMetadata.getCDNUri(), cdnUri);
         
         // Test static website metadata
         getApi().setCDNStaticWebsiteIndex(containerNameWithCDN, "index.html");
         getApi().setCDNStaticWebsiteError(containerNameWithCDN, "error.html");
         
         ContainerMetadata containerMetadata = getApi().getContainerMetadata(containerNameWithCDN);
         
         assertEquals(containerMetadata.getMetadata().get("web-index"), "index.html");
         assertEquals(containerMetadata.getMetadata().get("web-error"), "error.html");
         
         cdnMetadata = getApi().getCDNMetadata(containerNameWithoutCDN);
         assert cdnMetadata == null || !cdnMetadata.isCDNEnabled() : containerNameWithoutCDN
                  + " should not have metadata";

         assert getApi().getCDNMetadata("DoesNotExist") == null;
         
         // List CDN metadata for containers, and ensure all CDN info is
         // available for enabled
         // container
         Set<ContainerCDNMetadata> cdnMetadataList = getApi().listCDNContainers();
         assertTrue(cdnMetadataList.size() >= 1);

         cdnMetadata = getApi().getCDNMetadata(containerNameWithCDN);
         final boolean cdnEnabled = cdnMetadata.isCDNEnabled();
         final boolean logRetention = cdnMetadata.isLogRetention();
         final long initialTTL = cdnMetadata.getTTL();
         final URI cdnSslUri = cdnMetadata.getCDNSslUri();
         final URI cdnStreamingUri = cdnMetadata.getCDNStreamingUri();
         assertTrue(cdnMetadataList.contains(new ContainerCDNMetadata(
            containerNameWithCDN, cdnEnabled, logRetention, initialTTL, cdnUri, cdnSslUri, cdnStreamingUri)));

         
         
         // Test listing with options
         cdnMetadataList = getApi().listCDNContainers(ListCdnContainerOptions.Builder.enabledOnly());
         assertTrue(Iterables.all(cdnMetadataList, new Predicate<ContainerCDNMetadata>() {
            public boolean apply(ContainerCDNMetadata cdnMetadata) {
               return cdnMetadata.isCDNEnabled();
            }
         }));

         cdnMetadataList = getApi().listCDNContainers(
                  ListCdnContainerOptions.Builder.afterMarker(
                           containerNameWithCDN.substring(0, containerNameWithCDN.length() - 1)).maxResults(1));
         assertEquals(cdnMetadataList.size(), 1);

         // Enable CDN with PUT for the same container, this time with a custom TTL and Log Retention
         long ttl = 4000;
         getApi().enableCDN(containerNameWithCDN, ttl, true);

         cdnMetadata = getApi().getCDNMetadata(containerNameWithCDN);

         assertTrue(cdnMetadata.isCDNEnabled());

         assertEquals(cdnMetadata.getTTL(), ttl);

         // Check POST by updating TTL settings
         ttl = minimumTTL;
         getApi().updateCDN(containerNameWithCDN, minimumTTL, false);

         cdnMetadata = getApi().getCDNMetadata(containerNameWithCDN);
         assertTrue(cdnMetadata.isCDNEnabled());

         assertEquals(cdnMetadata.getTTL(), minimumTTL);

         // Confirm that minimum allowed value for TTL is 3600, lower values are
         // ignored.
         getApi().updateCDN(containerNameWithCDN, 3599L, false);
         cdnMetadata = getApi().getCDNMetadata(containerNameWithCDN);
         assertEquals(cdnMetadata.getTTL(), 3599L);
         
         // Test purging an object from a CDN container
         SwiftObject swiftObject = newSwiftObject("hello", "hello.txt");
         getApi().putObject(containerNameWithCDN, swiftObject);
         
         assertTrue(getApi().purgeCDNObject(containerNameWithCDN, swiftObject.getInfo().getName()));

         // Disable CDN with POST
         assertTrue(getApi().disableCDN(containerNameWithCDN));

         cdnMetadata = getApi().getCDNMetadata(containerNameWithCDN);
         assertEquals(cdnMetadata.isCDNEnabled(), false);
      } finally {
         recycleContainer(containerNameWithCDN);
         recycleContainer(containerNameWithoutCDN);
      }
   }

}
