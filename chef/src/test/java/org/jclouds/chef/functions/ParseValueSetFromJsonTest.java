package org.jclouds.chef.functions;

import static org.testng.Assert.assertEquals;

import java.io.IOException;

import org.jclouds.http.HttpResponse;
import org.jclouds.http.functions.config.ParserModule;
import org.jclouds.util.Utils;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code ParseValueSetFromJson}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", sequential = true, testName = "chef.ParseValueSetFromJsonTest")
public class ParseValueSetFromJsonTest {

   private ParseValueSetFromJson handler;

   @BeforeTest
   protected void setUpInjector() throws IOException {
      Injector injector = Guice.createInjector(new ParserModule());
      handler = injector.getInstance(ParseValueSetFromJson.class);
   }

   public void testRegex() {
      assertEquals(handler.apply(new HttpResponse(Utils
            .toInputStream("{\"runit\":[\"0.7.0\",\"0.7.1\"]}"))), ImmutableSet
            .of("0.7.0", "0.7.1"));
   }
}
