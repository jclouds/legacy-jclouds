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
 * Tests possible uses of InitBuilder
 * 
 * @author Adrian Cole
 */
public class InitBuilderTest {

   InitBuilder testScriptBuilder = new InitBuilder().switchOn("1",
            ImmutableMap.of("start", "echo started", "stop", "echo stopped")).export("javaHome",
            "/apps/jdk1.6");

   @Test
   public void testBuildSimpleWindows() throws MalformedURLException, IOException {
      assertEquals(testScriptBuilder.build(OsFamily.WINDOWS), CharStreams.toString(Resources
               .newReaderSupplier(Resources.getResource("test_script."
                        + ShellToken.SH.to(OsFamily.WINDOWS)), Charsets.UTF_8)));
   }

   @Test
   public void testBuildSimpleUNIX() throws MalformedURLException, IOException {
      assertEquals(testScriptBuilder.build(OsFamily.UNIX), CharStreams.toString(Resources
               .newReaderSupplier(Resources.getResource("test_script."
                        + ShellToken.SH.to(OsFamily.UNIX)), Charsets.UTF_8)));
   }

   @Test
   public void testSwitchOn() {
      InitBuilder builder = new InitBuilder();
      builder.switchOn("1", ImmutableMap.of("start", "echo started", "stop", "echo stopped"));
      assertEquals(builder.switchExec, ImmutableMap.of("1", ImmutableMap.of("start",
               "echo started", "stop", "echo stopped")));
   }

   @Test
   public void testNoSwitchOn() {
      InitBuilder builder = new InitBuilder();
      assertEquals(builder.switchExec.size(), 0);
   }

   @Test
   public void testExport() {
      InitBuilder builder = new InitBuilder();
      builder.export("javaHome", "/apps/jdk1.6");
      assertEquals(builder.variables, ImmutableMap.of("javaHome", "/apps/jdk1.6"));

   }

   @Test
   public void testNoExport() {
      InitBuilder builder = new InitBuilder();
      assertEquals(builder.variables.size(), 0);
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testExportNPE() {
      new InitBuilder().export(null, null);
   }

}
