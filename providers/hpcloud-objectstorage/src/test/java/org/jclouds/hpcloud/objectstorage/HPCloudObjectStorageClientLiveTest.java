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
package org.jclouds.hpcloud.objectstorage;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.net.URI;

import org.jclouds.hpcloud.objectstorage.domain.CDNContainer;
import org.jclouds.hpcloud.objectstorage.options.ListCDNContainerOptions;
import org.jclouds.openstack.swift.CommonSwiftClientLiveTest;
import org.jclouds.openstack.swift.domain.SwiftObject;
import org.testng.annotations.Test;

import com.google.common.collect.FluentIterable;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "HPCloudObjectStorageClientLiveTest")
public class HPCloudObjectStorageClientLiveTest extends CommonSwiftClientLiveTest<HPCloudObjectStorageApi> {
   
   public HPCloudObjectStorageClientLiveTest(){
      provider = "hpcloud-objectstorage";
   }
   
   @Override
   public HPCloudObjectStorageApi getApi() {
      return view.unwrap(HPCloudObjectStorageApiMetadata.CONTEXT_TOKEN).getApi();
   }

   @Override
   protected void testGetObjectContentType(SwiftObject getBlob) {
      assertEquals(getBlob.getInfo().getContentType(), "application/x-www-form-urlencoded");
   }

   // CDN service due to go live Q1 2012
   @Test(enabled = true)
   public void testCDNOperations() throws Exception {
      final long minimumTTL = 60 * 60; // The minimum TTL is 1 hour

      // Create two new containers for testing
      final String containerNameWithCDN = getContainerName();
      final String containerNameWithoutCDN = getContainerName();
      try {
         try {
            getApi().getCDNExtension().get().disable(containerNameWithCDN);
            getApi().getCDNExtension().get().disable(containerNameWithoutCDN);
         } catch (Exception e) {
            e.printStackTrace();
         }
         CDNContainer cdnMetadata = null;

         // Enable CDN with PUT for one container
         final URI cdnUri = getApi().getCDNExtension().get().enable(containerNameWithCDN);
         assertNotNull(cdnUri);

         // Confirm CDN is enabled via HEAD request and has default TTL
         cdnMetadata = getApi().getCDNExtension().get().get(containerNameWithCDN);

         assertTrue(cdnMetadata.isCDNEnabled());

         assertEquals(cdnMetadata.getCDNUri(), cdnUri);

         cdnMetadata = getApi().getCDNExtension().get().get(containerNameWithoutCDN);
         assert cdnMetadata == null || !cdnMetadata.isCDNEnabled() : containerNameWithoutCDN
                  + " should not have metadata";

         assert getApi().getCDNExtension().get().get("DoesNotExist") == null;

         // List CDN metadata for containers, and ensure all CDN info is
         // available for enabled
         // container
         FluentIterable<CDNContainer> cdnMetadataList = getApi().getCDNExtension().get().list();
         assertTrue(cdnMetadataList.size() >= 1);

         final long initialTTL = cdnMetadata.getTTL();
         assertTrue(cdnMetadataList.contains(CDNContainer.builder().name(containerNameWithCDN)
               .CDNEnabled(true).ttl(initialTTL).CDNUri(cdnUri).build()));

         /*
          * Test listing with options FIXFIX cdnMetadataList =
          * getApi().list(ListCDNContainerOptions.Builder.enabledOnly());
          * assertTrue(Iterables.all(cdnMetadataList, new Predicate<CDNContainer>() { public
          * boolean apply(CDNContainer cdnMetadata) { return cdnMetadata.isCDNEnabled(); }
          * }));
          */

         cdnMetadataList = getApi().getCDNExtension().get().list(
                  ListCDNContainerOptions.Builder.afterMarker(
                           containerNameWithCDN.substring(0, containerNameWithCDN.length() - 1)).maxResults(1));
         assertEquals(cdnMetadataList.size(), 1);

         // Enable CDN with PUT for the same container, this time with a custom
         // TTL
         long ttl = 4000;
         getApi().getCDNExtension().get().enable(containerNameWithCDN, ttl);

         cdnMetadata = getApi().getCDNExtension().get().get(containerNameWithCDN);

         assertTrue(cdnMetadata.isCDNEnabled());
   
         assertEquals(cdnMetadata.getTTL(), ttl);

         // Check POST by updating TTL settings
         ttl = minimumTTL;
         getApi().getCDNExtension().get().update(containerNameWithCDN, minimumTTL);

         cdnMetadata = getApi().getCDNExtension().get().get(containerNameWithCDN);
         assertTrue(cdnMetadata.isCDNEnabled());

         assertEquals(cdnMetadata.getTTL(), minimumTTL);

         // Confirm that minimum allowed value for TTL is 3600, lower values are
         // ignored.
         getApi().getCDNExtension().get().update(containerNameWithCDN, 3599L);
         cdnMetadata = getApi().getCDNExtension().get().get(containerNameWithCDN);
         assertEquals(cdnMetadata.getTTL(), 3599L);

         // Disable CDN with POST
         assertTrue(getApi().getCDNExtension().get().disable(containerNameWithCDN));

         cdnMetadata = getApi().getCDNExtension().get().get(containerNameWithCDN);
         assertEquals(cdnMetadata.isCDNEnabled(), false);
      } catch (Exception e) {
         e.printStackTrace();
      } finally {
         recycleContainer(containerNameWithCDN);
         recycleContainer(containerNameWithoutCDN);
      }
   }

}
