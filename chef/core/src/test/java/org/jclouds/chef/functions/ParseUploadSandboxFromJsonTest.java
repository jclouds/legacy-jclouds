/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.chef.functions;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import org.jclouds.chef.config.ChefParserModule;
import org.jclouds.chef.domain.ChecksumStatus;
import org.jclouds.chef.domain.UploadSandbox;
import org.jclouds.crypto.CryptoStreams;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.io.Payloads;
import org.jclouds.json.config.GsonModule;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.primitives.Bytes;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code ParseUploadSiteFromJson}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", sequential = true, testName = "chef.ParseUploadSiteFromJsonTest")
public class ParseUploadSandboxFromJsonTest {

   private ParseJson<UploadSandbox> handler;
   private Injector injector;

   @BeforeTest
   protected void setUpInjector() throws IOException {
      injector = Guice.createInjector(new ChefParserModule(), new GsonModule());
      handler = injector.getInstance(Key.get(new TypeLiteral<ParseJson<UploadSandbox>>() {
      }));
   }

   public void test() {
      assertEquals(
               handler.apply(new HttpResponse(200, "ok", Payloads.newPayload(ParseUploadSandboxFromJsonTest.class
                        .getResourceAsStream("/upload-site.json")))),
               new UploadSandbox(
                        URI
                                 .create("https://api.opscode.com/organizations/jclouds/sandboxes/d454f71e2a5f400c808d0c5d04c2c88c"),
                        ImmutableMap
                                 .<List<Byte>, ChecksumStatus> of(
                                          Bytes.asList(CryptoStreams.hex("0c5ecd7788cf4f6c7de2a57193897a6c")),
                                          new ChecksumStatus(
                                                   URI
                                                            .create("https://s3.amazonaws.com/opscode-platform-production-data/organization-486ca3ac66264fea926aa0b4ff74341c/sandbox-d454f71e2a5f400c808d0c5d04c2c88c/checksum-0c5ecd7788cf4f6c7de2a57193897a6c?AWSAccessKeyId=AKIAJOZTD2N26S7W6APA&Expires=1277344702&Signature=FtKyqvYEjhhEKmRY%2B0M8aGPMM7g%3D"),
                                                   true), Bytes.asList(CryptoStreams
                                                   .hex("0189e76ccc476701d6b374e5a1a27347")), new ChecksumStatus(),
                                          Bytes.asList(CryptoStreams.hex("1dda05ed139664f1f89b9dec482b77c0")),
                                          new ChecksumStatus()), "d454f71e2a5f400c808d0c5d04c2c88c"));
   }
}
