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
package org.jclouds.mezeo.pcs2.xml;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.URI;

import org.jclouds.date.DateService;
import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.mezeo.pcs2.domain.FileInfoWithMetadata;
import org.jclouds.mezeo.pcs2.domain.internal.FileInfoWithMetadataImpl;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;

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
      dateService = injector.getInstance(DateService.class);
      assert dateService != null;
   }

   public void testFileMetadata() {
      InputStream is = getClass().getResourceAsStream("/test_file_metadata.xml");

      FileInfoWithMetadata expects = new FileInfoWithMetadataImpl(
               URI
                        .create("https://pcsbeta.mezeo.net/v2/files/9E4C5AFA-A98B-11DE-8B4C-C3884B4A2DA3"),
               "testfile.txt",
               dateService.fromSeconds(1254000180),
               true,
               dateService.fromSeconds(1254000181),
               "adrian@jclouds.org",
               3,
               false,
               dateService.fromSeconds(1254000182),
               false,
               "text/plain",
               5,
               URI
                        .create("https://pcsbeta.mezeo.net/v2/files/9E4C5AFA-A98B-11DE-8B4C-C3884B4A2DA3/content"),
               URI
                        .create("https://pcsbeta.mezeo.net/v2/containers/C4DA95C2-B298-11DE-8D7C-2B1FE4F2B99C"),
               URI
                        .create("https://pcsbeta.mezeo.net/v2/files/9E4C5AFA-A98B-11DE-8B4C-C3884B4A2DA3/permissions"),
               URI
                        .create("https://pcsbeta.mezeo.net/v2/files/9E4C5AFA-A98B-11DE-8B4C-C3884B4A2DA3/tags"),
               URI
                        .create("https://pcsbeta.mezeo.net/v2/files/9E4C5AFA-A98B-11DE-8B4C-C3884B4A2DA3/metadata"),
               // Note that we should convert uppercase to lowercase, since most clouds do anyway

               ImmutableMap
                        .<String, URI> of(
                                 "foo",
                                 URI
                                          .create("https://pcsbeta.mezeo.net/v2/files/9E4C5AFA-A98B-11DE-8B4C-C3884B4A2DA3/metadata/Foo")),
               URI
                        .create("https://pcsbeta.mezeo.net/v2/files/9E4C5AFA-A98B-11DE-8B4C-C3884B4A2DA3/thumbnail"));

      FileInfoWithMetadata result = (FileInfoWithMetadata) factory.create(
               injector.getInstance(FileHandler.class)).parse(is);

      assertEquals(result, expects);
   }
}
