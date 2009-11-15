package org.jclouds.vcloud.terremark.options;

import static org.jclouds.vcloud.terremark.options.InstantiateVAppTemplateOptions.Builder.cpuCount;
import static org.jclouds.vcloud.terremark.options.InstantiateVAppTemplateOptions.Builder.inGroup;
import static org.jclouds.vcloud.terremark.options.InstantiateVAppTemplateOptions.Builder.inNetwork;
import static org.jclouds.vcloud.terremark.options.InstantiateVAppTemplateOptions.Builder.inRow;
import static org.jclouds.vcloud.terremark.options.InstantiateVAppTemplateOptions.Builder.megabytes;
import static org.jclouds.vcloud.terremark.options.InstantiateVAppTemplateOptions.Builder.withPassword;
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
   public void testInGroupDefault() {
      InstantiateVAppTemplateOptions options = new InstantiateVAppTemplateOptions();
      assertEquals(options.group, "default");
   }

   @Test
   public void testInGroup() {
      InstantiateVAppTemplateOptions options = new InstantiateVAppTemplateOptions();
      options.inGroup("group1");
      assertEquals(options.group, "group1");
   }

   @Test
   public void testInGroupStatic() {
      InstantiateVAppTemplateOptions options = inGroup("group1");
      assertEquals(options.group, "group1");
   }

   @Test
   public void testInRowDefault() {
      InstantiateVAppTemplateOptions options = new InstantiateVAppTemplateOptions();
      assertEquals(options.row, "default");
   }

   @Test
   public void testInRow() {
      InstantiateVAppTemplateOptions options = new InstantiateVAppTemplateOptions();
      options.inRow("row1");
      assertEquals(options.row, "row1");
   }

   @Test
   public void testInRowStatic() {
      InstantiateVAppTemplateOptions options = inRow("row1");
      assertEquals(options.row, "row1");
   }

   @Test
   public void testWithPasswordDefault() {
      InstantiateVAppTemplateOptions options = new InstantiateVAppTemplateOptions();
      assertEquals(options.password, "password");
   }

   @Test
   public void testWithPassword() {
      InstantiateVAppTemplateOptions options = new InstantiateVAppTemplateOptions();
      options.withPassword("password1");
      assertEquals(options.password, "password1");
   }

   @Test
   public void testWithPasswordStatic() {
      InstantiateVAppTemplateOptions options = withPassword("password1");
      assertEquals(options.password, "password1");
   }

   @Test
   public void testInNetwork() {
      InstantiateVAppTemplateOptions options = new InstantiateVAppTemplateOptions();
      options.inNetwork(URI.create("http://localhost"));
      assertEquals(options.network, "http://localhost");
   }

   @Test
   public void testInNetworkStatic() {
      InstantiateVAppTemplateOptions options = inNetwork(URI.create("http://localhost"));
      assertEquals(options.network, "http://localhost");
   }

   @Test
   public void testCpuCount() {
      InstantiateVAppTemplateOptions options = new InstantiateVAppTemplateOptions();
      options.cpuCount(3);
      assertEquals(options.cpuCount, "3");
   }

   @Test
   public void testCpuCountStatic() {
      InstantiateVAppTemplateOptions options = cpuCount(3);
      assertEquals(options.cpuCount, "3");
   }

   @Test
   public void testMegabytes() {
      InstantiateVAppTemplateOptions options = new InstantiateVAppTemplateOptions();
      options.megabytes(512);
      assertEquals(options.megabytes, "512");
   }

   @Test
   public void testMegabytesStatic() {
      InstantiateVAppTemplateOptions options = megabytes(512);
      assertEquals(options.megabytes, "512");
   }

   @Test(expectedExceptions = IllegalArgumentException.class)
   public void testMegabytesStaticWrong() {
      megabytes(511);
   }
}
