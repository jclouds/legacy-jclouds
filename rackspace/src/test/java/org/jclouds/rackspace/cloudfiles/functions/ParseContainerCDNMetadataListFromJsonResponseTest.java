/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.rackspace.cloudfiles.functions;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.URI;
import java.util.Set;
import java.util.SortedSet;

import org.jclouds.http.HttpResponse;
import org.jclouds.http.Payloads;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.http.functions.config.ParserModule;
import org.jclouds.rackspace.cloudfiles.domain.ContainerCDNMetadata;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSortedSet;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code ParseContainerCDNMetadataListFromJsonResponse}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "cloudfiles.ParseContainerCDNMetadataListFromJsonResponseTest")
public class ParseContainerCDNMetadataListFromJsonResponseTest {
   Injector i = Guice.createInjector(new ParserModule());

   @Test
   public void testApplyInputStream() {

      InputStream is = getClass().getResourceAsStream(
            "/cloudfiles/test_list_cdn.json");
      Set<ContainerCDNMetadata> expects = ImmutableSortedSet
            .of(

                  new ContainerCDNMetadata(
                        "adriancole-blobstore.testCDNOperationsContainerWithCDN",
                        false,
                        3600,
                        URI
                              .create("http://c0354712.cdn.cloudfiles.rackspacecloud.com")),
                  new ContainerCDNMetadata(
                        "adriancole-blobstore5",
                        true,
                        28800,
                        URI
                              .create("http://c0404671.cdn.cloudfiles.rackspacecloud.com")),
                  new ContainerCDNMetadata(
                        "adriancole-cfcdnint.testCDNOperationsContainerWithCDN",
                        false,
                        3600,
                        URI
                              .create("http://c0320431.cdn.cloudfiles.rackspacecloud.com")));
      ParseJson<SortedSet<ContainerCDNMetadata>> parser = i
            .getInstance(Key
                  .get(new TypeLiteral<ParseJson<SortedSet<ContainerCDNMetadata>>>() {
                  }));
      assertEquals(parser.apply(new HttpResponse(200, "ok", Payloads
            .newInputStreamPayload(is))), expects);
   }
}
