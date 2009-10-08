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

import static org.easymock.classextension.EasyMock.createMock;
import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.URI;
import java.util.concurrent.Future;

import javax.inject.Singleton;
import javax.ws.rs.core.MediaType;

import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.mezeo.pcs2.PCSUtil;
import org.jclouds.mezeo.pcs2.domain.FileMetadata;
import org.jclouds.util.DateService;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.collect.Multimap;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/**
 * Tests behavior of {@code FileListToFileMetadataListHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "pcs2.FileMetadataHandlerTest")
public class FileMetadataHandlerTest extends BaseHandlerTest {

   private DateService dateService;

   @BeforeTest
   @Override
   protected void setUpInjector() {
      super.setUpInjector();
      injector = injector.createChildInjector(new AbstractModule() {

         @Override
         protected void configure() {
         }

         @SuppressWarnings( { "unused", "unchecked" })
         @Singleton
         @Provides
         PCSUtil provideUtil() {
            final Future<Void> voidF = createMock(Future.class);
            return new PCSUtil() {
               public Future<Void> addEntryToMultiMap(Multimap<String, String> map, String key,
                        URI value) {
                  map.put(key.toLowerCase(), "bar");
                  return voidF;
               }

               public Future<Void> putMetadata(String resourceId, String key, String value) {
                  return null;
               }
            };
         }

      });
      dateService = injector.getInstance(DateService.class);
      assert dateService != null;
   }

   public void testFileMetadata() {
      InputStream is = getClass().getResourceAsStream("/test_file_metadata.xml");

      FileMetadata expects = new FileMetadata("testfile.txt", URI
               .create("https://pcsbeta.mezeo.net/v2/files/9E4C5AFA-A98B-11DE-8B4C-C3884B4A2DA3"),
               dateService.fromSeconds(1254000180), dateService.fromSeconds(1254000181),
               dateService.fromSeconds(1254000182), "adrian@jclouds.org", false, true, 3, 5,
               MediaType.TEXT_PLAIN, false);
      // Note that we should convert uppercase to lowercase, since most clouds do anyway
      expects.getUserMetadata().put("foo", "bar");
      FileMetadata result = (FileMetadata) factory.create(
               injector.getInstance(FileMetadataHandler.class)).parse(is);

      assertEquals(result, expects);
   }
}
