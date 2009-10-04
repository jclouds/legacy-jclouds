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

import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.mezeo.pcs2.domain.ContainerMetadata;
import org.jclouds.util.DateService;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSortedSet;

/**
 * Tests behavior of {@code FileListToContainerMetadataListHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "pcs2.FileListToContainerMetadataListHandlerTest")
public class FileListToContainerMetadataListHandlerTest extends BaseHandlerTest {

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
      SortedSet<ContainerMetadata> list = ImmutableSortedSet
               .of(new ContainerMetadata(
                        "test1",
                        URI
                                 .create("https://pcsbeta.mezeo.net/v2/containers/7F143552-AAF5-11DE-BBB0-0BC388ED913B"),
                        dateService.fromSeconds(1254008225), dateService.fromSeconds(1254008226),
                        dateService.fromSeconds(1254008227), "adrian@jclouds.org", true, false, 1,
                        1024));

      SortedSet<ContainerMetadata> result = (SortedSet<ContainerMetadata>) factory.create(
               injector.getInstance(FileListToContainerMetadataListHandler.class)).parse(is);

      assertEquals(result, list);
   }
}
