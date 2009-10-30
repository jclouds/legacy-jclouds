/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.rackspace.cloudfiles.functions;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.URI;
import java.util.List;

import org.jclouds.http.functions.config.ParserModule;
import org.jclouds.rackspace.cloudfiles.domain.ContainerCDNMetadata;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.inject.Guice;
import com.google.inject.Injector;

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

      InputStream is = getClass().getResourceAsStream("/test_list_cdn.json");
      List<ContainerCDNMetadata> expects = ImmutableList.of(

      new ContainerCDNMetadata("adriancole-blobstore.testCDNOperationsContainerWithCDN", false,
               3600, URI.create("http://c0354712.cdn.cloudfiles.rackspacecloud.com")),
               new ContainerCDNMetadata("adriancole-blobstore5", true, 28800, URI
                        .create("http://c0404671.cdn.cloudfiles.rackspacecloud.com")),
               new ContainerCDNMetadata("adriancole-cfcdnint.testCDNOperationsContainerWithCDN",
                        false, 3600, URI
                                 .create("http://c0320431.cdn.cloudfiles.rackspacecloud.com")));
      ParseContainerCDNMetadataListFromJsonResponse parser = new ParseContainerCDNMetadataListFromJsonResponse(
               i.getInstance(Gson.class));
      assertEquals(parser.apply(is), expects);
   }

}
