package org.jclouds.chef.functions;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.net.URI;
import java.util.Set;

import org.jclouds.chef.domain.Attribute;
import org.jclouds.chef.domain.Cookbook;
import org.jclouds.chef.domain.Metadata;
import org.jclouds.chef.domain.Resource;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.config.ParserModule;
import org.jclouds.util.Utils;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.gson.Gson;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code ParseCookbookFromJson}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", sequential = true, testName = "chef.ParseCookbookFromJsonTest")
public class ParseCookbookFromJsonTest {

   private ParseCookbookFromJson handler;

   @BeforeTest
   protected void setUpInjector() throws IOException {
      Injector injector = Guice.createInjector(new ParserModule());
      handler = injector.getInstance(ParseCookbookFromJson.class);
   }

   public void testBrew() throws IOException {
      Cookbook cookbook = handler.apply(new HttpResponse(
            ParseCookbookFromJsonTest.class
                  .getResourceAsStream("/brew-cookbook.json")));

      assertEquals(cookbook, handler.apply(new HttpResponse(Utils
            .toInputStream(new Gson().toJson(cookbook)))));
   }

   public void testTomcat() {
      Cookbook cookbook = handler.apply(new HttpResponse(
            ParseCookbookFromJsonTest.class
                  .getResourceAsStream("/tomcat-cookbook.json")));

      assertEquals(cookbook, handler.apply(new HttpResponse(Utils
            .toInputStream(new Gson().toJson(cookbook)))));
   }

   public void testMysql() throws IOException {
      Cookbook cookbook = handler.apply(new HttpResponse(
            ParseCookbookFromJsonTest.class
                  .getResourceAsStream("/mysql-cookbook.json")));

      assertEquals(cookbook, handler.apply(new HttpResponse(Utils
            .toInputStream(new Gson().toJson(cookbook)))));
   }

   public void testApache() {
      assertEquals(
            handler.apply(new HttpResponse(ParseCookbookFromJsonTest.class
                  .getResourceAsStream("/apache-chef-demo-cookbook.json"))),
            new Cookbook(
                  "apache-chef-demo-0.0.0",
                  ImmutableSet.<Resource> of(),
                  "Chef::CookbookVersion",
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
                                    "11637f98942eafbf49c71b7f2f048b78",
                                    "README", "default"),
                              new Resource(
                                    "Rakefile",
                                    URI
                                          .create("https://s3.amazonaws.com/opscode-platform-production-data/organization-486ca3ac66264fea926aa0b4ff74341c/checksum-ebcf925a1651b4e04b9cd8aac2bc54eb?AWSAccessKeyId=AKIAJOZTD2N26S7W6APA&Expires=1277766181&Signature=EFzzDSKKytTl7b%2FxrCeNLh05zj4%3D"),
                                    "ebcf925a1651b4e04b9cd8aac2bc54eb",
                                    "Rakefile", "default")), "cookbook_version"));

   }
}
