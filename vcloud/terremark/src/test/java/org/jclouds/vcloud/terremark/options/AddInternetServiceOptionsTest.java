package org.jclouds.vcloud.terremark.options;

import static org.jclouds.vcloud.terremark.options.AddInternetServiceOptions.Builder.disabled;
import static org.jclouds.vcloud.terremark.options.AddInternetServiceOptions.Builder.withDescription;
import static org.testng.Assert.assertEquals;

import org.jclouds.http.functions.config.ParserModule;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code CreateInternetServiceOptions}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "vcloud.CreateInternetServiceOptionsTest")
public class AddInternetServiceOptionsTest {

   Injector injector = Guice.createInjector(new ParserModule());

   @Test
   public void testWithDescription() {
      AddInternetServiceOptions options = new AddInternetServiceOptions();
      options.withDescription("yallo");
      assertEquals(options.description, "yallo");
   }

   @Test
   public void testWithDescriptionStatic() {
      AddInternetServiceOptions options = withDescription("yallo");
      assertEquals(options.description, "yallo");
   }

   @Test
   public void testDisabled() {
      AddInternetServiceOptions options = new AddInternetServiceOptions();
      options.disabled();
      assertEquals(options.enabled, "false");
   }

   @Test
   public void testDisabledStatic() {
      AddInternetServiceOptions options = disabled();
      assertEquals(options.enabled, "false");
   }

}
