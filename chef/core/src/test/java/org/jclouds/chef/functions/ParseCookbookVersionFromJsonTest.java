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
import java.util.Set;

import org.jclouds.chef.config.ChefParserModule;
import org.jclouds.chef.domain.Attribute;
import org.jclouds.chef.domain.CookbookVersion;
import org.jclouds.chef.domain.Metadata;
import org.jclouds.chef.domain.Resource;
import org.jclouds.crypto.CryptoStreams;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.io.Payloads;
import org.jclouds.json.Json;
import org.jclouds.json.config.GsonModule;
import org.jclouds.util.Utils;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code ParseCookbookVersionFromJson}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", sequential = true, testName = "chef.ParseCookbookVersionFromJsonTest")
public class ParseCookbookVersionFromJsonTest {

   private ParseJson<CookbookVersion> handler;
   private Injector injector;
   private Json json;

   @BeforeTest
   protected void setUpInjector() throws IOException {
      injector = Guice.createInjector(new ChefParserModule(), new GsonModule());
      json = injector.getInstance(Json.class);
      handler = injector.getInstance(Key.get(new TypeLiteral<ParseJson<CookbookVersion>>() {
      }));
   }

   @Test(enabled = false)
   public void testBrew() throws IOException {
      CookbookVersion cookbook = handler.apply(new HttpResponse(200, "ok", Payloads
               .newPayload(ParseCookbookVersionFromJsonTest.class.getResourceAsStream("/brew-cookbook.json"))));

      assertEquals(cookbook, handler.apply(new HttpResponse(200, "ok", Payloads.newPayload(Utils.toInputStream(json
               .toJson(cookbook))))));
   }

   @Test(enabled = false)
   public void testTomcat() {
      CookbookVersion cookbook = handler.apply(new HttpResponse(200, "ok", Payloads
               .newPayload(ParseCookbookVersionFromJsonTest.class.getResourceAsStream("/tomcat-cookbook.json"))));

      assertEquals(cookbook, handler.apply(new HttpResponse(200, "ok", Payloads.newPayload(Utils.toInputStream(json
               .toJson(cookbook))))));
   }

   @Test(enabled = false)
   public void testMysql() throws IOException {
      CookbookVersion cookbook = handler.apply(new HttpResponse(200, "ok", Payloads
               .newPayload(ParseCookbookVersionFromJsonTest.class.getResourceAsStream("/mysql-cookbook.json"))));

      assertEquals(cookbook, handler.apply(new HttpResponse(200, "ok", Payloads.newPayload(Utils.toInputStream(json
               .toJson(cookbook))))));
   }

   @Test(enabled = false)
   public void testApache() {

      assertEquals(
               handler.apply(new HttpResponse(200, "ok", Payloads.newPayload(ParseCookbookVersionFromJsonTest.class
                        .getResourceAsStream("/apache-chef-demo-cookbook.json")))),
               new CookbookVersion(
                        "apache-chef-demo-0.0.0",
                        ImmutableSet.<Resource> of(),
                        ImmutableSet.<Attribute> of(),
                        ImmutableSet.<Resource> of(),
                        new Metadata("Apache v2.0", "Your Name", ImmutableMap.<String, String> of(), ImmutableMap
                                 .<String, Set<String>> of(), "youremail@example.com", ImmutableMap
                                 .<String, Set<String>> of(), "A fabulous new cookbook", ImmutableMap
                                 .<String, Set<String>> of(), ImmutableMap.<String, Set<String>> of(), "0.0.0",
                                 ImmutableMap.<String, String> of(), ImmutableMap.<String, Set<String>> of(),
                                 "apache-chef-demo", ImmutableMap.<String, String> of(), "", ImmutableMap
                                          .<String, Attribute> of(), ImmutableMap.<String, String> of()),
                        ImmutableSet.<Resource> of(),
                        "apache-chef-demo",
                        ImmutableSet.<Resource> of(),
                        ImmutableSet.<Resource> of(),
                        ImmutableSet.<Resource> of(),
                        "0.0.0",
                        ImmutableSet.<Resource> of(),
                        ImmutableSet
                                 .<Resource> of(
                                          new Resource(
                                                   "README",
                                                   URI
                                                            .create("https://s3.amazonaws.com/opscode-platform-production-data/organization-486ca3ac66264fea926aa0b4ff74341c/checksum-11637f98942eafbf49c71b7f2f048b78?AWSAccessKeyId=AKIAJOZTD2N26S7W6APA&Expires=1277766181&Signature=zgpNl6wSxjTNovqZu2nJq0JztU8%3D"),
                                                   CryptoStreams.hex("11637f98942eafbf49c71b7f2f048b78"), "README",
                                                   "default"),
                                          new Resource(
                                                   "Rakefile",
                                                   URI
                                                            .create("https://s3.amazonaws.com/opscode-platform-production-data/organization-486ca3ac66264fea926aa0b4ff74341c/checksum-ebcf925a1651b4e04b9cd8aac2bc54eb?AWSAccessKeyId=AKIAJOZTD2N26S7W6APA&Expires=1277766181&Signature=EFzzDSKKytTl7b%2FxrCeNLh05zj4%3D"),
                                                   CryptoStreams.hex("ebcf925a1651b4e04b9cd8aac2bc54eb"), "Rakefile",
                                                   "default"))));

   }
}
