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
package org.jclouds.atmosonline.saas.functions;

import static org.testng.Assert.assertEquals;

import org.jclouds.atmosonline.saas.domain.FileType;
import org.jclouds.atmosonline.saas.domain.SystemMetadata;
import org.jclouds.date.DateService;
import org.jclouds.encryption.EncryptionService;
import org.jclouds.http.HttpResponse;
import org.jclouds.io.Payloads;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code ParseSystemMetadataFromHeaders}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "atmossaas.ParseSystemMetadataFromHeadersTest")
public class ParseSystemMetadataFromHeadersTest {

   public void test() {
      Injector injector = Guice.createInjector();
      ParseSystemMetadataFromHeaders parser = injector
               .getInstance(ParseSystemMetadataFromHeaders.class);
      DateService dateService = injector.getInstance(DateService.class);
      EncryptionService encryptionService = injector.getInstance(EncryptionService.class);

      HttpResponse response = new HttpResponse(200, "ok", Payloads.newStringPayload(""));
      response
               .getHeaders()
               .put(
                        "x-emc-meta",
                        "content-md5=1f3870be274f6c49b3e31a0c6728957f, atime=2009-10-12T16:09:42Z, mtime=2009-10-19T04:37:00Z,"
                                 + " ctime=2009-10-19T04:37:00Z, itime=2009-10-12T16:09:42Z, type=directory, uid=root, "
                                 + "gid=rootr, objectid=4980cdb2b010109b04a44f7bb83f5f04ad354c638ae5, "
                                 + "objname=e913e09366364e9ba384b8fead643d43, size=4096, nlink=1, policyname=default");
      SystemMetadata expected = new SystemMetadata(encryptionService
               .fromHex("1f3870be274f6c49b3e31a0c6728957f"),

      dateService.iso8601SecondsDateParse("2009-10-12T16:09:42Z"), dateService
               .iso8601SecondsDateParse("2009-10-19T04:37:00Z"), "rootr", dateService
               .iso8601SecondsDateParse("2009-10-12T16:09:42Z"), dateService
               .iso8601SecondsDateParse("2009-10-19T04:37:00Z"), 1,
               "4980cdb2b010109b04a44f7bb83f5f04ad354c638ae5", "e913e09366364e9ba384b8fead643d43",
               "default", 4096l, FileType.DIRECTORY, "root"

      );
      SystemMetadata data = parser.apply(response);

      assertEquals(data, expected);
   }
}
