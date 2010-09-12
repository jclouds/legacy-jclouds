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

package org.jclouds.compute;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.easymock.EasyMock.aryEq;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.reportMatcher;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.jclouds.compute.util.ComputeServiceUtils.getCores;
import static org.testng.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.easymock.IArgumentMatcher;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.stub.config.StubComputeServiceContextModule.StubNodeMetadata;
import org.jclouds.io.Payload;
import org.jclouds.net.IPSocket;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.predicates.SocketOpen;
import org.jclouds.rest.RestContext;
import org.jclouds.scriptbuilder.InitBuilder;
import org.jclouds.scriptbuilder.domain.Statement;
import org.jclouds.scriptbuilder.domain.Statements;
import org.jclouds.ssh.ExecResponse;
import org.jclouds.ssh.SshClient;
import org.jclouds.ssh.SshException;
import org.jclouds.util.Utils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Splitter;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.inject.AbstractModule;
import com.google.inject.Module;

/**
 * 
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", enabled = true, sequential = true, testName = "stub.StubComputeServiceIntegrationTest")
public class StubComputeServiceIntegrationTest extends BaseComputeServiceLiveTest {

   private static final ExecResponse EXEC_GOOD = new ExecResponse("", "", 0);
   private static final ExecResponse EXEC_BAD = new ExecResponse("", "", 1);

   @BeforeClass
   @Override
   public void setServiceDefaults() {
      provider = "stub";
   }

   @Override
   public void testCorrectAuthException() throws Exception {
   }

   @Test
   public void testTemplateBuilder() {
      Template defaultTemplate = client.templateBuilder().build();
      assertEquals(defaultTemplate.getImage().getOperatingSystem().getArch(), "X86_64");
      assertEquals(defaultTemplate.getImage().getOperatingSystem().getFamily(), OsFamily.UBUNTU);
      assertEquals(defaultTemplate.getLocation().getId(), provider + "zone");
      assertEquals(getCores(defaultTemplate.getHardware()), 1.0d);
   }

   protected void buildSocketTester() {
      SocketOpen socketOpen = createMock(SocketOpen.class);

      expect(socketOpen.apply(new IPSocket("144.175.1.1", 22))).andReturn(true);
      expect(socketOpen.apply(new IPSocket("144.175.1.2", 22))).andReturn(true);
      expect(socketOpen.apply(new IPSocket("144.175.1.3", 22))).andReturn(true);
      expect(socketOpen.apply(new IPSocket("144.175.1.4", 22))).andReturn(true);

      replay(socketOpen);

      socketTester = new RetryablePredicate<IPSocket>(socketOpen, 60, 1, TimeUnit.SECONDS);
   }

   @Override
   protected Module getSshModule() {
      return new AbstractModule() {

         @Override
         protected void configure() {
            SshClient.Factory factory = createMock(SshClient.Factory.class);
            SshClient client1 = createMock(SshClient.class);
            SshClient client2 = createMock(SshClient.class);
            SshClient client3 = createMock(SshClient.class);
            SshClient client4 = createMock(SshClient.class);

            expect(factory.create(new IPSocket("144.175.1.1", 22), "root", "romeo")).andThrow(
                     new SshException("Auth fail"));
            expect(factory.create(new IPSocket("144.175.1.1", 22), "root", "password1")).andReturn(client1)
                     .atLeastOnce();

            client1.connect();
            runScript(client1, "computeserv", 1);
            client1.disconnect();

            expect(factory.create(new IPSocket("144.175.1.2", 22), "root", "password2")).andReturn(client2)
                     .atLeastOnce();
            expect(factory.create(new IPSocket("144.175.1.3", 22), "root", "password3")).andReturn(client3)
                     .atLeastOnce();
            expect(factory.create(new IPSocket("144.175.1.4", 22), "root", "password4")).andReturn(client4)
                     .atLeastOnce();

            runScriptAndInstallSsh(client2, "runscript", 2);
            runScriptAndInstallSsh(client3, "runscript", 3);
            runScriptAndInstallSsh(client4, "runscript", 4);

            expect(
                     factory.create(eq(new IPSocket("144.175.1.1", 22)), eq("root"), aryEq(keyPair.get("private")
                              .getBytes()))).andReturn(client1).atLeastOnce();
            expect(
                     factory.create(eq(new IPSocket("144.175.1.2", 22)), eq("root"), aryEq(keyPair.get("private")
                              .getBytes()))).andReturn(client2).atLeastOnce();
            expect(
                     factory.create(eq(new IPSocket("144.175.1.3", 22)), eq("root"), aryEq(keyPair.get("private")
                              .getBytes()))).andReturn(client3).atLeastOnce();
            expect(
                     factory.create(eq(new IPSocket("144.175.1.4", 22)), eq("root"), aryEq(keyPair.get("private")
                              .getBytes()))).andReturn(client4).atLeastOnce();

            helloAndJava(client1);
            helloAndJava(client2);
            helloAndJava(client3);
            helloAndJava(client4);

            replay(factory);
            replay(client1);
            replay(client2);
            replay(client3);
            replay(client4);

            bind(SshClient.Factory.class).toInstance(factory);
         }

         private void runScriptAndInstallSsh(SshClient client, String scriptName, int nodeId) {
            client.connect();

            runScript(client, scriptName, nodeId);

            expect(client.exec("mkdir .ssh")).andReturn(EXEC_GOOD);
            expect(client.exec("cat .ssh/id_rsa.pub >> .ssh/authorized_keys")).andReturn(EXEC_GOOD);
            expect(client.exec("chmod 600 .ssh/authorized_keys")).andReturn(EXEC_GOOD);
            client.put(eq(".ssh/id_rsa.pub"), payloadEq(keyPair.get("public")));

            expect(client.exec("mkdir .ssh")).andReturn(EXEC_GOOD);
            client.put(eq(".ssh/id_rsa"), payloadEq(keyPair.get("private")));
            expect(client.exec("chmod 600 .ssh/id_rsa")).andReturn(EXEC_GOOD);

            client.disconnect();

         }

         private void runScript(SshClient client, String scriptName, int nodeId) {
            client.put(eq("" + scriptName + ""), payloadEq(initScript(scriptName,
                     BaseComputeServiceLiveTest.APT_RUN_SCRIPT)));
            expect(client.exec("chmod 755 " + scriptName + "")).andReturn(EXEC_GOOD);
            expect(client.getUsername()).andReturn("root").atLeastOnce();
            expect(client.getHostAddress()).andReturn(nodeId + "").atLeastOnce();
            expect(client.exec("./" + scriptName + " init")).andReturn(EXEC_GOOD);
            expect(client.exec("./" + scriptName + " start")).andReturn(EXEC_GOOD);
            expect(client.exec("./" + scriptName + " status")).andReturn(EXEC_GOOD);
            // next status says the script is done, since not found.
            expect(client.exec("./" + scriptName + " status")).andReturn(EXEC_BAD);
            expect(client.exec("./" + scriptName + " tail")).andReturn(EXEC_GOOD);
            expect(client.exec("./" + scriptName + " tailerr")).andReturn(EXEC_GOOD);
         }

         private void helloAndJava(SshClient client) {
            client.connect();

            expect(client.exec("echo hello")).andReturn(new ExecResponse("hello", "", 0));
            expect(client.exec("java -version")).andReturn(new ExecResponse("", "OpenJDK", 0));

            client.disconnect();
         }

      };
   }

   @Override
   protected void setupCredentials() {
      identity = "stub";
      credential = "stub";
   }

   protected void assertNodeZero(Set<? extends NodeMetadata> metadataSet) {
      // TODO: this fails so we override it.
   }

   public static String initScript(String scriptName, String script) {
      return new InitBuilder(scriptName, "/tmp/" + scriptName, "/tmp/" + scriptName,
               ImmutableMap.<String, String> of(), ImmutableList.<Statement> of(Statements.interpret(Iterables.toArray(
                        Splitter.on("\n").split(new String(checkNotNull(script, "script"))), String.class))))
               .build(org.jclouds.scriptbuilder.domain.OsFamily.UNIX);
   }

   public static Payload payloadEq(String value) {
      reportMatcher(new PayloadEquals(value));
      return null;
   }

   public void testAssignability() throws Exception {
      @SuppressWarnings("unused")
      RestContext<ConcurrentMap<Integer, StubNodeMetadata>, ConcurrentMap<Integer, StubNodeMetadata>> stubContext = new ComputeServiceContextFactory()
               .createContext(provider, identity, credential).getProviderSpecificContext();
   }

   private static class PayloadEquals implements IArgumentMatcher, Serializable {

      private static final long serialVersionUID = 583055160049982067L;

      private final Object expected;

      public PayloadEquals(Object expected) {
         this.expected = expected;
      }

      public boolean matches(Object actual) {
         if (this.expected == null) {
            return actual == null;
         }
         try {
            String real = Utils.toStringAndClose(((Payload) actual).getInput());
            if (!expected.equals(real)) {
               System.err.println(real);
               return false;
            }
            return true;
         } catch (IOException e) {
            Throwables.propagate(e);
            return false;
         }
      }

      public void appendTo(StringBuffer buffer) {
         appendQuoting(buffer);
         buffer.append(expected);
         appendQuoting(buffer);
      }

      private void appendQuoting(StringBuffer buffer) {
         if (expected instanceof String) {
            buffer.append("\"");
         } else if (expected instanceof Character) {
            buffer.append("'");
         }
      }

      @Override
      public boolean equals(Object o) {
         if (o == null || !this.getClass().equals(o.getClass()))
            return false;
         PayloadEquals other = (PayloadEquals) o;
         return this.expected == null && other.expected == null || this.expected != null
                  && this.expected.equals(other.expected);
      }

      @Override
      public int hashCode() {
         throw new UnsupportedOperationException("hashCode() is not supported");
      }

   }

   @Override
   protected void setupKeyPairForTest() throws FileNotFoundException, IOException {
      keyPair = ImmutableMap.<String, String> of("public", "ssh-rsa", "private", "-----BEGIN RSA PRIVATE KEY-----");
   }

   // TODO: I have absolutely no idea why I have to redeclare all this cruft. If
   // I don't, then we
   // get all sorts of not allowed to depend on errors.
   @Override
   public void testImagesCache() throws Exception {
      super.testImagesCache();
   }

   @Test(enabled = true, dependsOnMethods = { "testImagesCache" })
   public void testAScriptExecutionAfterBootWithBasicTemplate() throws Exception {
      super.testAScriptExecutionAfterBootWithBasicTemplate();
   }

   @Test(enabled = true, dependsOnMethods = "testTemplateMatch")
   public void testCreateTwoNodesWithRunScript() throws Exception {
      super.testCreateTwoNodesWithRunScript();
   }

   @Test(enabled = true, dependsOnMethods = "testCreateTwoNodesWithRunScript")
   public void testCreateAnotherNodeWithANewContextToEnsureSharedMemIsntRequired() throws Exception {
      super.testCreateAnotherNodeWithANewContextToEnsureSharedMemIsntRequired();
   }

   @Test(enabled = true, dependsOnMethods = "testCreateAnotherNodeWithANewContextToEnsureSharedMemIsntRequired")
   public void testGet() throws Exception {
      super.testGet();
   }

   @Test(enabled = true, dependsOnMethods = "testGet")
   public void testOptionToNotBlock() throws Exception {
      super.testOptionToNotBlock();
   }

   @Test(enabled = true, dependsOnMethods = "testGet")
   public void testReboot() throws Exception {
      super.testReboot();
   }

   @Test(enabled = true, dependsOnMethods = { "testImagesCache" })
   public void testTemplateMatch() throws Exception {
      super.testTemplateMatch();
   }

   @Override
   protected void cleanup() throws InterruptedException, ExecutionException, TimeoutException {
      super.cleanup();
   }

}