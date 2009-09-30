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

import org.jclouds.blobstore.ContainerNotFoundException;
import org.jclouds.mezeo.pcs2.PCSBlobStore;
import org.jclouds.mezeo.pcs2.domain.ContainerMetadata;
import org.jclouds.util.DateService;
import org.testng.annotations.Test;

import com.google.inject.internal.ImmutableList;

/**
 * Tests behavior of {@code ContainerResourceId}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "pcs2.FindIdInContainerList")
public class FindIdInContainerListTest {
   private DateService dateService = new DateService();

   private final ImmutableList<ContainerMetadata> OF = ImmutableList
            .of(new ContainerMetadata(
                     "test1",
                     URI
                              .create("https://pcsbeta.mezeo.net/v2/containers/7F143552-AAF5-11DE-BBB0-0BC388ED913B"),
                     dateService.fromSeconds(1254008225), dateService.fromSeconds(1254008226),
                     dateService.fromSeconds(1254008227), "adrian@jclouds.org", true, false, 1,
                     1024));

   @Test(expectedExceptions = ContainerNotFoundException.class)
   public void testBad() {
      FindIdInContainerList binder = new FindIdInContainerList(createNiceMock(PCSBlobStore.class));
      binder.idForNameInListOrException("hello", OF);
   }

   public void testGood() {
      FindIdInContainerList binder = new FindIdInContainerList(createNiceMock(PCSBlobStore.class));
      assertEquals(binder.idForNameInListOrException("test1", OF),
               "7F143552-AAF5-11DE-BBB0-0BC388ED913B");
   }
}
