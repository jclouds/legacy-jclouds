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
package org.jclouds.hpcloud.objectstorage.functions;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.URI;
import java.util.Set;
import java.util.SortedSet;

import org.jclouds.hpcloud.objectstorage.domain.ContainerCDNMetadata;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.json.config.GsonModule;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code ParseContainerCDNMetadataListFromJsonResponse}
 * 
 * @author Jeremy Daggett
 */
@Test(groups = "unit")
public class ParseContainerCDNMetadataListFromJsonResponseTest {
   Injector i = Guice.createInjector(new GsonModule());

   @Test
   public void testApplyInputStream() {

      InputStream is = getClass().getResourceAsStream("/test_list_cdn.json");
      
      Set<ContainerCDNMetadata> expects = ImmutableSet.of(
         ContainerCDNMetadata.builder().name("hpcloud-blobstore.testCDNOperationsContainerWithCDN").CDNEnabled(false).ttl(3600)
                  .CDNUri(URI.create("https://cdnmgmt.hpcloud.net:8080/v1/AUTH_test/")).build(),
            ContainerCDNMetadata.builder().name("hpcloud-blobstore5").CDNEnabled(true).ttl(28800)
                  .CDNUri(URI.create("https://cdnmgmt.hpcloud.net:8080/v1/AUTH_test/")).build(),
            ContainerCDNMetadata.builder().name("hpcloud-cfcdnint.testCDNOperationsContainerWithCDN").CDNEnabled(false).ttl(3600)
                  .CDNUri(URI.create("https://cdnmgmt.hpcloud.net:8080/v1/AUTH_test/")).build());
      
      ParseJson<SortedSet<ContainerCDNMetadata>> parser = i.getInstance(
            Key.get(new TypeLiteral<ParseJson<SortedSet<ContainerCDNMetadata>>>() {
            }));
      
      assertEquals(parser.apply(HttpResponse.builder().statusCode(200).message("ok").payload(is).build()), expects);
   }
}
