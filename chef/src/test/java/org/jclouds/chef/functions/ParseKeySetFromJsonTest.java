package org.jclouds.chef.functions;

import static org.testng.Assert.assertEquals;

import java.io.IOException;

import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.config.ParserModule;
import org.jclouds.io.Payloads;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code ParseKeySetFromJson}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", sequential = true, testName = "chef.ParseKeySetFromJsonTest")
public class ParseKeySetFromJsonTest {

   private ParseKeySetFromJson handler;

   @BeforeTest
   protected void setUpInjector() throws IOException {
      Injector injector = Guice.createInjector(new ParserModule());
      handler = injector.getInstance(ParseKeySetFromJson.class);
   }

   public void testRegex() {
      assertEquals(
            handler
                  .apply(new HttpResponse(
                        200,
                        "ok",
                        Payloads
                              .newStringPayload("{\n\"opscode-validator\": \"https://api.opscode.com/...\", \"pimp-validator\": \"https://api.opscode.com/...\"}"))),
            ImmutableSet.of("opscode-validator", "pimp-validator"));
   }
}
