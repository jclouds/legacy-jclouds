package org.jclouds.scriptbuilder;

import static org.jclouds.scriptbuilder.domain.Statements.findPid;
import static org.jclouds.scriptbuilder.domain.Statements.interpret;
import static org.jclouds.scriptbuilder.domain.Statements.kill;
import static org.jclouds.scriptbuilder.domain.Statements.switchOn;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.net.MalformedURLException;

import org.jclouds.scriptbuilder.domain.OsFamily;
import org.jclouds.scriptbuilder.domain.ShellToken;
import org.jclouds.scriptbuilder.domain.Switch;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.CharStreams;
import com.google.common.io.Resources;

/**
 * Tests possible uses of ScriptBuilder
 * 
 * @author Adrian Cole
 */
public class ScriptBuilderTest {

   ScriptBuilder testScriptBuilder = new ScriptBuilder().addStatement(
            switchOn("1", ImmutableMap.of("start", interpret("echo started{lf}"), "stop",
                     interpret("echo stopped{lf}")))).addEnvironmentVariableScope("default",
            ImmutableMap.of("javaHome", "/apps/jdk1.6"));

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

   ScriptBuilder findPidBuilder = new ScriptBuilder().addStatement(findPid("{args}")).addStatement(
            interpret("echo {varl}FOUND_PID{varr}{lf}"));

   @Test
   public void testFindPidWindows() throws MalformedURLException, IOException {
      assertEquals(findPidBuilder.build(OsFamily.WINDOWS), CharStreams.toString(Resources
               .newReaderSupplier(Resources.getResource("test_find_pid."
                        + ShellToken.SH.to(OsFamily.WINDOWS)), Charsets.UTF_8)));
   }

   @Test
   public void testFindPidUNIX() throws MalformedURLException, IOException {
      assertEquals(findPidBuilder.build(OsFamily.UNIX), CharStreams.toString(Resources
               .newReaderSupplier(Resources.getResource("test_find_pid."
                        + ShellToken.SH.to(OsFamily.UNIX)), Charsets.UTF_8)));
   }

   ScriptBuilder seekAndDestroyBuilder = new ScriptBuilder().addStatement(findPid("{args}"))
            .addStatement(kill());

   @Test
   public void testSeekAndDestroyWindows() throws MalformedURLException, IOException {
      assertEquals(seekAndDestroyBuilder.build(OsFamily.WINDOWS), CharStreams.toString(Resources
               .newReaderSupplier(Resources.getResource("test_seek_and_destroy."
                        + ShellToken.SH.to(OsFamily.WINDOWS)), Charsets.UTF_8)));
   }

   @Test
   public void testSeekAndDestroyUNIX() throws MalformedURLException, IOException {
      assertEquals(seekAndDestroyBuilder.build(OsFamily.UNIX), CharStreams.toString(Resources
               .newReaderSupplier(Resources.getResource("test_seek_and_destroy."
                        + ShellToken.SH.to(OsFamily.UNIX)), Charsets.UTF_8)));
   }

   @Test
   public void testSwitchOn() {
      ScriptBuilder builder = new ScriptBuilder();
      builder.addStatement(switchOn("1", ImmutableMap.of("start", interpret("echo started{lf}"),
               "stop", interpret("echo stopped{lf}"))));
      assertEquals(builder.statements, ImmutableList.of(new Switch("1", ImmutableMap.of("start",
               interpret("echo started{lf}"), "stop", interpret("echo stopped{lf}")))));
   }

   @Test
   public void testNoSwitchOn() {
      ScriptBuilder builder = new ScriptBuilder();
      assertEquals(builder.statements.size(), 0);
   }

   @Test
   public void testExport() {
      ScriptBuilder builder = new ScriptBuilder();
      builder.addEnvironmentVariableScope("default", ImmutableMap.of("javaHome", "/apps/jdk1.6"));
      assertEquals(builder.functions, ImmutableMap.of("default",
               "{fncl}default{fncr}   {export} JAVA_HOME={vq}/apps/jdk1.6{vq}{lf}{fnce}"));
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
