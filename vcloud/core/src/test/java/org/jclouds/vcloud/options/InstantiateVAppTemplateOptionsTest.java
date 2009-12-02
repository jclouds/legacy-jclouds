package org.jclouds.vcloud.options;

import static org.jclouds.vcloud.options.InstantiateVAppTemplateOptions.Builder.cpuCount;
import static org.jclouds.vcloud.options.InstantiateVAppTemplateOptions.Builder.inNetwork;
import static org.jclouds.vcloud.options.InstantiateVAppTemplateOptions.Builder.megabytes;
import static org.testng.Assert.assertEquals;

import java.net.URI;

import org.jclouds.http.functions.config.ParserModule;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code InstantiateVAppTemplateOptions}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "vcloud.InstantiateVAppTemplateOptionsTest")
public class InstantiateVAppTemplateOptionsTest {

   Injector injector = Guice.createInjector(new ParserModule());

   @Test
   public void testInNetwork() {
      InstantiateVAppTemplateOptions options = new InstantiateVAppTemplateOptions();
      options.inNetwork(URI.create("http://localhost"));
      assertEquals(options.getNetwork(), "http://localhost");
   }

   @Test
   public void testInNetworkStatic() {
      InstantiateVAppTemplateOptions options = inNetwork(URI.create("http://localhost"));
      assertEquals(options.getNetwork(), "http://localhost");
   }

   @Test
   public void testCpuCount() {
      InstantiateVAppTemplateOptions options = new InstantiateVAppTemplateOptions();
      options.cpuCount(3);
      assertEquals(options.getCpuCount(), "3");
   }

   @Test
   public void testCpuCountStatic() {
      InstantiateVAppTemplateOptions options = cpuCount(3);
      assertEquals(options.getCpuCount(), "3");
   }

   @Test
   public void testMegabytes() {
      InstantiateVAppTemplateOptions options = new InstantiateVAppTemplateOptions();
      options.megabytes(512);
      assertEquals(options.getMegabytes(), "512");
   }

   @Test
   public void testMegabytesStatic() {
      InstantiateVAppTemplateOptions options = megabytes(512);
      assertEquals(options.getMegabytes(), "512");
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testMegabytesStaticWrong() {
      megabytes(511);
   }
}
