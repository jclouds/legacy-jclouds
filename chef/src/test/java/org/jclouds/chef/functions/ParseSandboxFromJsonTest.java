package org.jclouds.chef.functions;

import static org.testng.Assert.assertEquals;

import java.io.IOException;

import org.jclouds.chef.domain.Sandbox;
import org.jclouds.date.DateService;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.Payloads;
import org.jclouds.http.functions.config.ParserModule;
import org.jclouds.http.functions.config.ParserModule.DateAdapter;
import org.jclouds.http.functions.config.ParserModule.Iso8601DateAdapter;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code ParseSandboxFromJson}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", sequential = true, testName = "chef.ParseSandboxFromJsonTest")
public class ParseSandboxFromJsonTest {

   private ParseSandboxFromJson handler;
   private DateService dateService;

   @BeforeTest
   protected void setUpInjector() throws IOException {
      Injector injector = Guice.createInjector(new ParserModule(), new AbstractModule() {

         @Override
         protected void configure() {
            bind(DateAdapter.class).to(Iso8601DateAdapter.class);
         }

      });
      handler = injector.getInstance(ParseSandboxFromJson.class);
      dateService = injector.getInstance(DateService.class);
   }

   public void test() {
      assertEquals(handler.apply(new HttpResponse(200, "ok", Payloads
               .newPayload(ParseSandboxFromJsonTest.class.getResourceAsStream("/sandbox.json")))),
               new Sandbox("1-8c27b0ea4c2b7aaedbb44cfbdfcc11b2", false, dateService
                        .iso8601SecondsDateParse("2010-07-07T03:36:00+00:00"), ImmutableSet
                        .<String> of(), "f9d6d9b72bae465890aae87969f98a9c",
                        "f9d6d9b72bae465890aae87969f98a9c"));
   }
}
