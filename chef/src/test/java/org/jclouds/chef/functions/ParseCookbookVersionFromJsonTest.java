package org.jclouds.chef.functions;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.net.URI;
import java.util.Set;

import org.jclouds.chef.domain.Attribute;
import org.jclouds.chef.domain.CookbookVersion;
import org.jclouds.chef.domain.Metadata;
import org.jclouds.chef.domain.Resource;
import org.jclouds.encryption.EncryptionService;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.ParseJson;
import org.jclouds.http.functions.config.ParserModule;
import org.jclouds.io.Payloads;
import org.jclouds.util.Utils;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.gson.Gson;
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

   @BeforeTest
   protected void setUpInjector() throws IOException {
      injector = Guice.createInjector(new ParserModule());
      handler = injector.getInstance(Key
            .get(new TypeLiteral<ParseJson<CookbookVersion>>() {
            }));
   }

   @Test(enabled = false)
   public void testBrew() throws IOException {
      CookbookVersion cookbook = handler.apply(new HttpResponse(200, "ok",
            Payloads.newPayload(ParseCookbookVersionFromJsonTest.class
                  .getResourceAsStream("/brew-cookbook.json"))));

      assertEquals(cookbook, handler.apply(new HttpResponse(200, "ok", Payloads
            .newPayload(Utils.toInputStream(new Gson().toJson(cookbook))))));
   }

   @Test(enabled = false)
   public void testTomcat() {
      CookbookVersion cookbook = handler.apply(new HttpResponse(200, "ok",
            Payloads.newPayload(ParseCookbookVersionFromJsonTest.class
                  .getResourceAsStream("/tomcat-cookbook.json"))));

      assertEquals(cookbook, handler.apply(new HttpResponse(200, "ok", Payloads
            .newPayload(Utils.toInputStream(new Gson().toJson(cookbook))))));
   }

   @Test(enabled = false)
   public void testMysql() throws IOException {
      CookbookVersion cookbook = handler.apply(new HttpResponse(200, "ok",
            Payloads.newPayload(ParseCookbookVersionFromJsonTest.class
                  .getResourceAsStream("/mysql-cookbook.json"))));

      assertEquals(cookbook, handler.apply(new HttpResponse(200, "ok", Payloads
            .newPayload(Utils.toInputStream(new Gson().toJson(cookbook))))));
   }

   @Test(enabled = false)
   public void testApache() {
      EncryptionService encryptionService = injector
            .getInstance(EncryptionService.class);

      assertEquals(
            handler
                  .apply(new HttpResponse(
                        200,
                        "ok",
                        Payloads
                              .newPayload(ParseCookbookVersionFromJsonTest.class
                                    .getResourceAsStream("/apache-chef-demo-cookbook.json")))),
            new CookbookVersion(
                  "apache-chef-demo-0.0.0",
                  ImmutableSet.<Resource> of(),
                  ImmutableSet.<Resource> of(),
                  ImmutableSet.<Resource> of(),
                  new Metadata("Apache v2.0", "Your Name", ImmutableMap
                        .<String, String> of(), ImmutableMap
                        .<String, Set<String>> of(), "youremail@example.com",
                        ImmutableMap.<String, Set<String>> of(),
                        "A fabulous new cookbook", ImmutableMap
                              .<String, Set<String>> of(), ImmutableMap
                              .<String, Set<String>> of(), "0.0.0",
                        ImmutableMap.<String, String> of(), ImmutableMap
                              .<String, Set<String>> of(), "apache-chef-demo",
                        ImmutableMap.<String, String> of(), "", ImmutableMap
                              .<String, Attribute> of(), ImmutableMap
                              .<String, String> of()),
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
                                    encryptionService
                                          .fromHex("11637f98942eafbf49c71b7f2f048b78"),
                                    "README", "default"),
                              new Resource(
                                    "Rakefile",
                                    URI
                                          .create("https://s3.amazonaws.com/opscode-platform-production-data/organization-486ca3ac66264fea926aa0b4ff74341c/checksum-ebcf925a1651b4e04b9cd8aac2bc54eb?AWSAccessKeyId=AKIAJOZTD2N26S7W6APA&Expires=1277766181&Signature=EFzzDSKKytTl7b%2FxrCeNLh05zj4%3D"),
                                    encryptionService
                                          .fromHex("ebcf925a1651b4e04b9cd8aac2bc54eb"),
                                    "Rakefile", "default"))));

   }
}
