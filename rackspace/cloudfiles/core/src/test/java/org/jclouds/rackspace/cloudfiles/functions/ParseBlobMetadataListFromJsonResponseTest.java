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
package org.jclouds.rackspace.cloudfiles.functions;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.util.List;

import org.jclouds.blobstore.domain.BlobMetadata;
import org.jclouds.http.HttpUtils;
import org.jclouds.http.functions.config.ParserModule;
import org.joda.time.DateTime;
import org.testng.annotations.Test;
import org.testng.collections.Lists;

import com.google.gson.Gson;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code ParseBlobMetadataListFromJsonResponseTest}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "cloudfiles.ParseBlobMetadataListFromJsonResponseTest")
public class ParseBlobMetadataListFromJsonResponseTest {

   Injector i = Guice.createInjector(new ParserModule());

   public void testApplyInputStream() {
      InputStream is = getClass().getResourceAsStream("/test_list_container.json");
      List<BlobMetadata> expects = Lists.newArrayList();
      BlobMetadata one = new BlobMetadata("test_obj_1");
      one.setETag(HttpUtils.fromHexString("4281c348eaf83e70ddce0e07221c3d28"));
      one.setSize(14);
      one.setContentType("application/octet-stream");
      one.setLastModified(new DateTime("2009-02-03T05:26:32.612278"));
      expects.add(one);
      BlobMetadata two = new BlobMetadata("test_obj_2");
      two.setETag(HttpUtils.fromHexString("b039efe731ad111bc1b0ef221c3849d0"));
      two.setSize(64);
      two.setContentType("application/octet-stream");
      two.setLastModified(new DateTime("2009-02-03T05:26:32.612278"));
      expects.add(two);
      ParseBlobMetadataListFromJsonResponse parser = new ParseBlobMetadataListFromJsonResponse(i
               .getInstance(Gson.class));
      assertEquals(parser.apply(is), expects);
   }
}
