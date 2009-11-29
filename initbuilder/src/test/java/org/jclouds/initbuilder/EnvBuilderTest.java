package org.jclouds.initbuilder;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.net.MalformedURLException;

import org.jclouds.initbuilder.domain.OsFamily;
import org.jclouds.initbuilder.domain.ShellToken;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.CharStreams;
import com.google.common.io.Resources;

/**
 * Tests possible uses of EnvBuilder
 * 
 * @author Adrian Cole
 */
public class EnvBuilderTest {

   EnvBuilder testScriptBuilder = new EnvBuilder().export("javaHome",
            "/apps/jdk1.6");

   @Test
   public void testBuildSimpleWindows() throws MalformedURLException, IOException {
      assertEquals(testScriptBuilder.build(OsFamily.WINDOWS), CharStreams.toString(Resources
               .newReaderSupplier(Resources.getResource("test_env."
                        + ShellToken.SH.to(OsFamily.WINDOWS)), Charsets.UTF_8)));
   }

   @Test
   public void testBuildSimpleUNIX() throws MalformedURLException, IOException {
      assertEquals(testScriptBuilder.build(OsFamily.UNIX), CharStreams.toString(Resources
               .newReaderSupplier(Resources.getResource("test_env."
                        + ShellToken.SH.to(OsFamily.UNIX)), Charsets.UTF_8)));
   }

   @Test
   public void testExport() {
      EnvBuilder builder = new EnvBuilder();
      builder.export("javaHome", "/apps/jdk1.6");
      assertEquals(builder.variables, ImmutableMap.of("javaHome", "/apps/jdk1.6"));
   }

   @Test
   public void testNoExport() {
      EnvBuilder builder = new EnvBuilder();
      assertEquals(builder.variables.size(), 0);
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testExportNPE() {
      new EnvBuilder().export(null, null);
   }

}
