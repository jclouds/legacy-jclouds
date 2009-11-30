package org.jclouds.scriptbuilder;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.net.MalformedURLException;

import org.jclouds.scriptbuilder.ScriptBuilder;
import org.jclouds.scriptbuilder.domain.OsFamily;
import org.jclouds.scriptbuilder.domain.ShellToken;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.CharStreams;
import com.google.common.io.Resources;

/**
 * Tests possible uses of ScriptBuilder
 * 
 * @author Adrian Cole
 */
public class ScriptBuilderTest {

   ScriptBuilder testScriptBuilder = new ScriptBuilder().switchOn("1",
            ImmutableMap.of("start", "echo started", "stop", "echo stopped"))
            .addEnvironmentVariableScope("default", ImmutableMap.of("javaHome", "/apps/jdk1.6"));

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
      ScriptBuilder builder = new ScriptBuilder();
      builder.switchOn("1", ImmutableMap.of("start", "echo started", "stop", "echo stopped"));
      assertEquals(builder.switchExec, ImmutableMap.of("1", ImmutableMap.of("start",
               "echo started", "stop", "echo stopped")));
   }

   @Test
   public void testNoSwitchOn() {
      ScriptBuilder builder = new ScriptBuilder();
      assertEquals(builder.switchExec.size(), 0);
   }

   @Test
   public void testExport() {
      ScriptBuilder builder = new ScriptBuilder();
      builder.addEnvironmentVariableScope("default", ImmutableMap.of("javaHome", "/apps/jdk1.6"));
      assertEquals(builder.functions, ImmutableMap.of("default", "{fncl}default{fncr}   {export} JAVA_HOME={vq}/apps/jdk1.6{vq}{lf}{fnce}"));
   }

   @Test
   public void testNoExport() {
      ScriptBuilder builder = new ScriptBuilder();
      assertEquals(builder.functions.size(), 0);
   }

   @Test(expectedExceptions = NullPointerException.class)
   public void testExportNPE() {
      new ScriptBuilder().addEnvironmentVariableScope(null, null);
   }

}
