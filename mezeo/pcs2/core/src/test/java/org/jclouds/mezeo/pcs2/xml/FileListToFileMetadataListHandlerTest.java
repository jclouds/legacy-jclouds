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
package org.jclouds.mezeo.pcs2.xml;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.URI;
import java.util.SortedSet;

import javax.ws.rs.core.MediaType;

import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.mezeo.pcs2.domain.FileMetadata;
import org.jclouds.util.DateService;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSortedSet;

/**
 * Tests behavior of {@code FileSortedSetToFileMetadataSortedSetHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "pcs2.FileListToFileMetadataListHandlerTest")
public class FileListToFileMetadataListHandlerTest extends BaseHandlerTest {

   private DateService dateService;

   @BeforeTest
   @Override
   protected void setUpInjector() {
      super.setUpInjector();
      dateService = injector.getInstance(DateService.class);
      assert dateService != null;
   }

   @SuppressWarnings("unchecked")
   public void testApplyInputStream() {
      InputStream is = getClass().getResourceAsStream("/test_file_list.xml");
      SortedSet<FileMetadata> list = ImmutableSortedSet.of(new FileMetadata("more", URI
               .create("https://pcsbeta.mezeo.net/v2/files/5C81DADC-AAEE-11DE-9D55-B39340AEFF3A"),
               dateService.fromSeconds(1254005157), dateService.fromSeconds(1254005158),
               dateService.fromSeconds(1254005159), "adrian@jclouds.org", false, false, 1, 254288,
               MediaType.APPLICATION_OCTET_STREAM, true),

      new FileMetadata("testfile.txt", URI
               .create("https://pcsbeta.mezeo.net/v2/files/9E4C5AFA-A98B-11DE-8B4C-C3884B4A2DA3"),
               dateService.fromSeconds(1254000180), dateService.fromSeconds(1254000181),
               dateService.fromSeconds(1254000182), "adrian@jclouds.org", false, true, 3, 5,
               MediaType.TEXT_PLAIN, false));

      SortedSet<FileMetadata> result = (SortedSet<FileMetadata>) factory.create(
               injector.getInstance(FileListToFileMetadataListHandler.class)).parse(is);

      assertEquals(result, list);
   }

}
