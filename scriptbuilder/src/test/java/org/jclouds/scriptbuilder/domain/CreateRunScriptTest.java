/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.scriptbuilder.domain;

import static org.jclouds.scriptbuilder.domain.Statements.call;
import static org.jclouds.scriptbuilder.domain.Statements.appendFile;
import static org.jclouds.scriptbuilder.domain.Statements.createRunScript;
import static org.testng.Assert.assertEquals;

import java.io.IOException;

import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.io.CharStreams;
import com.google.common.io.Resources;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class CreateRunScriptTest {
   Statement statement = createRunScript(
            "yahooprod",
            ImmutableList.<String> of("javaHome"),
            "{tmp}{fs}{uid}{fs}scripttest",
            ImmutableList
                     .<Statement> of(
                              call("echo hello"),
                              appendFile("{tmp}{fs}{uid}{fs}scripttest{fs}temp.txt", ImmutableList
                                       .<String> of("hello world")),
                              call("echo {varl}JAVA_HOME{varr}{fs}bin{fs}java -DinstanceName={varl}INSTANCE_NAME{varr} myServer.Main")));

   public void testUNIX() throws IOException {
      assertEquals(statement.render(OsFamily.UNIX), CharStreams.toString(Resources.newReaderSupplier(Resources
               .getResource("test_runrun." + ShellToken.SH.to(OsFamily.UNIX)), Charsets.UTF_8)));
   }

   public void testWINDOWS() throws IOException {
      assertEquals(statement.render(OsFamily.WINDOWS), CharStreams.toString(Resources.newReaderSupplier(Resources
               .getResource("test_runrun." + ShellToken.SH.to(OsFamily.WINDOWS)), Charsets.UTF_8)));
   }

   public void testRedirectGuard() {
      assertEquals(CreateRunScript.addSpaceToEnsureWeDontAccidentallyRedirectFd("foo>>"), "foo>>");
      assertEquals(CreateRunScript.addSpaceToEnsureWeDontAccidentallyRedirectFd("foo0>>"), "foo0 >>");
      assertEquals(CreateRunScript.addSpaceToEnsureWeDontAccidentallyRedirectFd("foo1>>"), "foo1 >>");
      assertEquals(CreateRunScript.addSpaceToEnsureWeDontAccidentallyRedirectFd("foo2>>"), "foo2 >>");
   }

}
