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
package org.jclouds.mezeo.pcs2.functions;

import static org.easymock.classextension.EasyMock.createNiceMock;
import static org.testng.Assert.assertEquals;

import java.net.URI;

import javax.ws.rs.core.MediaType;

import org.jclouds.blobstore.KeyNotFoundException;
import org.jclouds.mezeo.pcs2.PCSBlobStore;
import org.jclouds.mezeo.pcs2.domain.FileMetadata;
import org.jclouds.util.DateService;
import org.testng.annotations.Test;

import com.google.inject.internal.ImmutableList;

/**
 * Tests behavior of {@code ContainerResourceId}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "pcs2.FindIdInFileListTest")
public class FindIdInFileListIdTest {
   private DateService dateService = new DateService();

   private final ImmutableList<FileMetadata> OF = ImmutableList.of(new FileMetadata("more", URI
            .create("https://pcsbeta.mezeo.net/v2/files/5C81DADC-AAEE-11DE-9D55-B39340AEFF3A"),
            dateService.fromSeconds(1254005157), dateService.fromSeconds(1254005158), dateService
                     .fromSeconds(1254005159), "adrian@jclouds.org", false, false, 1, 254288,
            MediaType.APPLICATION_OCTET_STREAM, true),

   new FileMetadata("testfile.txt", URI
            .create("https://pcsbeta.mezeo.net/v2/files/9E4C5AFA-A98B-11DE-8B4C-C3884B4A2DA3"),
            dateService.fromSeconds(1254000180), dateService.fromSeconds(1254000181), dateService
                     .fromSeconds(1254000182), "adrian@jclouds.org", false, true, 3, 5,
            MediaType.TEXT_PLAIN, false));

   @Test(expectedExceptions = KeyNotFoundException.class)
   public void testBad() {
      FindIdInFileList binder = new FindIdInFileList(createNiceMock(PCSBlobStore.class));
      binder.idForNameInListOrException("bob", "hello", OF);
   }

   public void testGood() {
      FindIdInFileList binder = new FindIdInFileList(createNiceMock(PCSBlobStore.class));
      assertEquals(binder.idForNameInListOrException("bob", "testfile.txt", OF),
               "9E4C5AFA-A98B-11DE-8B4C-C3884B4A2DA3");
   }
}
