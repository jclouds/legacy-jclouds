package org.jclouds.vcloud.terremark.options;

import static org.jclouds.vcloud.terremark.options.TerremarkInstantiateVAppTemplateOptions.Builder.cpuCount;
import static org.jclouds.vcloud.terremark.options.TerremarkInstantiateVAppTemplateOptions.Builder.inGroup;
import static org.jclouds.vcloud.terremark.options.TerremarkInstantiateVAppTemplateOptions.Builder.inNetwork;
import static org.jclouds.vcloud.terremark.options.TerremarkInstantiateVAppTemplateOptions.Builder.inRow;
import static org.jclouds.vcloud.terremark.options.TerremarkInstantiateVAppTemplateOptions.Builder.megabytes;
import static org.jclouds.vcloud.terremark.options.TerremarkInstantiateVAppTemplateOptions.Builder.withPassword;
import static org.testng.Assert.assertEquals;

import java.net.URI;

import org.jclouds.http.functions.config.ParserModule;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code TerremarkInstantiateVAppTemplateOptions}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "vcloud.TerremarkInstantiateVAppTemplateOptionsTest")
public class TerremarkInstantiateVAppTemplateOptionsTest {

   Injector injector = Guice.createInjector(new ParserModule());

   @Test
   public void testInGroupDefault() {
      TerremarkInstantiateVAppTemplateOptions options = new TerremarkInstantiateVAppTemplateOptions();
      assertEquals(options.getGroup(), "default");
   }

   @Test
   public void testInGroup() {
      TerremarkInstantiateVAppTemplateOptions options = new TerremarkInstantiateVAppTemplateOptions();
      options.inGroup("group1");
      assertEquals(options.getGroup(), "group1");
   }

   @Test
   public void testInGroupStatic() {
      TerremarkInstantiateVAppTemplateOptions options = inGroup("group1");
      assertEquals(options.getGroup(), "group1");
   }

   @Test
   public void testInRowDefault() {
      TerremarkInstantiateVAppTemplateOptions options = new TerremarkInstantiateVAppTemplateOptions();
      assertEquals(options.getRow(), "default");
   }

   @Test
   public void testInRow() {
      TerremarkInstantiateVAppTemplateOptions options = new TerremarkInstantiateVAppTemplateOptions();
      options.inRow("row1");
      assertEquals(options.getRow(), "row1");
   }

   @Test
   public void testInRowStatic() {
      TerremarkInstantiateVAppTemplateOptions options = inRow("row1");
      assertEquals(options.getRow(), "row1");
   }

   @Test
   public void testWithPasswordDefault() {
      TerremarkInstantiateVAppTemplateOptions options = new TerremarkInstantiateVAppTemplateOptions();
      assertEquals(options.getPassword(), "getPassword()");
   }

   @Test
   public void testWithPassword() {
      TerremarkInstantiateVAppTemplateOptions options = new TerremarkInstantiateVAppTemplateOptions();
      options.withPassword("password1");
      assertEquals(options.getPassword(), "password1");
   }

   @Test
   public void testWithPasswordStatic() {
      TerremarkInstantiateVAppTemplateOptions options = withPassword("password1");
      assertEquals(options.getPassword(), "password1");
   }

   @Test
   public void testInNetwork() {
      TerremarkInstantiateVAppTemplateOptions options = new TerremarkInstantiateVAppTemplateOptions();
      options.inNetwork(URI.create("http://localhost"));
      assertEquals(options.getNetwork(), "http://localhost");
   }

   @Test
   public void testInNetworkStatic() {
      TerremarkInstantiateVAppTemplateOptions options = inNetwork(URI.create("http://localhost"));
      assertEquals(options.getNetwork(), "http://localhost");
   }

   @Test
   public void testCpuCount() {
      TerremarkInstantiateVAppTemplateOptions options = new TerremarkInstantiateVAppTemplateOptions();
      cpuCount(3);
      assertEquals(options.getCpuCount(), "3");
   }

   @Test
   public void testCpuCountStatic() {
      TerremarkInstantiateVAppTemplateOptions options = cpuCount(3);
      assertEquals(options.getCpuCount(), "3");
   }

   @Test
   public void testMegabytes() {
      TerremarkInstantiateVAppTemplateOptions options = new TerremarkInstantiateVAppTemplateOptions();
      options.megabytes(512);
      assertEquals(options.getMegabytes(), "512");
   }

   @Test
   public void testMegabytesStatic() {
      TerremarkInstantiateVAppTemplateOptions options = megabytes(512);
      assertEquals(options.getMegabytes(), "512");
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testMegabytesStaticWrong() {
      megabytes(511);
   }
}
