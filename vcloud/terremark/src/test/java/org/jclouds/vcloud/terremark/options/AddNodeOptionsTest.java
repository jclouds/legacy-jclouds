package org.jclouds.vcloud.terremark.options;

import static org.jclouds.vcloud.terremark.options.AddNodeOptions.Builder.disabled;
import static org.jclouds.vcloud.terremark.options.AddNodeOptions.Builder.withDescription;
import static org.testng.Assert.assertEquals;

import org.jclouds.http.functions.config.ParserModule;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code CreateNodeOptions}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "vcloud.AddNodeOptionsTest")
public class AddNodeOptionsTest {

   Injector injector = Guice.createInjector(new ParserModule());

   @Test
   public void testWithDescription() {
      AddNodeOptions options = new AddNodeOptions();
      options.withDescription("yallo");
      assertEquals(options.description, "yallo");
   }

   @Test
   public void testWithDescriptionStatic() {
      AddNodeOptions options = withDescription("yallo");
      assertEquals(options.description, "yallo");
   }

   @Test
   public void testDisabled() {
      AddNodeOptions options = new AddNodeOptions();
      options.disabled();
      assertEquals(options.enabled, "false");
   }

   @Test
   public void testDisabledStatic() {
      AddNodeOptions options = disabled();
      assertEquals(options.enabled, "false");
   }

}
