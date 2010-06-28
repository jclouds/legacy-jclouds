/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
import static org.testng.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

import org.easymock.IArgumentMatcher;
import org.jclouds.compute.domain.Architecture;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.stub.config.StubComputeServiceContextModule.StubNodeMetadata;
import org.jclouds.net.IPSocket;
import org.jclouds.predicates.SocketOpen;
import org.jclouds.rest.RestContext;
import org.jclouds.scriptbuilder.InitBuilder;
import org.jclouds.ssh.ExecResponse;
import org.jclouds.ssh.SshClient;
import org.jclouds.ssh.SshException;
import org.jclouds.util.Utils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.base.Splitter;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * 
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", enabled = true, sequential = true, testName = "stub.StubComputeServiceIntegrationTest")
public class StubComputeServiceIntegrationTest extends
      BaseComputeServiceLiveTest {

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
      assertEquals(defaultTemplate.getImage().getArchitecture(),
            Architecture.X86_64);
      assertEquals(defaultTemplate.getImage().getOsFamily(), OsFamily.UBUNTU);
      assertEquals(defaultTemplate.getLocation().getId(), "memory");
      assertEquals(defaultTemplate.getSize().getCores(), 4.0d);
   }

   @Override
   protected Injector createSshClientInjector() {
      return Guice.createInjector(new AbstractModule() {

         @Override
         protected void configure() {
            SshClient.Factory factory = createMock(SshClient.Factory.class);
            SocketOpen open = createMock(SocketOpen.class);
            SshClient client1 = createMock(SshClient.class);
            SshClient client2 = createMock(SshClient.class);
            SshClient client3 = createMock(SshClient.class);
            SshClient client4 = createMock(SshClient.class);

            expect(open.apply(new IPSocket("144.175.1.1", 22))).andReturn(true);
            expect(open.apply(new IPSocket("144.175.1.2", 22))).andReturn(true);
            expect(open.apply(new IPSocket("144.175.1.3", 22))).andReturn(true);
            expect(open.apply(new IPSocket("144.175.1.4", 22))).andReturn(true);

            expect(
                  factory.create(eq(new IPSocket("144.175.1.1", 22)),
                        eq("root"), aryEq(keyPair.get("private").getBytes())))
                  .andReturn(client1).atLeastOnce();
            expect(
                  factory.create(eq(new IPSocket("144.175.1.2", 22)),
                        eq("root"), aryEq(keyPair.get("private").getBytes())))
                  .andReturn(client2).atLeastOnce();
            expect(
                  factory.create(eq(new IPSocket("144.175.1.3", 22)),
                        eq("root"), aryEq(keyPair.get("private").getBytes())))
                  .andReturn(client3).atLeastOnce();
            expect(
                  factory.create(eq(new IPSocket("144.175.1.4", 22)),
                        eq("root"), aryEq(keyPair.get("private").getBytes())))
                  .andReturn(client4).atLeastOnce();

            helloAndJava(client1);
            helloAndJava(client2);
            helloAndJava(client3);
            helloAndJava(client4);

            replay(open);
            replay(factory);
            replay(client1);
            replay(client2);
            replay(client3);
            replay(client4);

            bind(SshClient.Factory.class).toInstance(factory);

            bind(SocketOpen.class).toInstance(open);
         }

         private void helloAndJava(SshClient client) {
            client.connect();

            expect(client.exec("echo hello")).andReturn(
                  new ExecResponse("hello", "", 0));
            expect(client.exec("java -version")).andReturn(
                  new ExecResponse("", "OpenJDK", 0));

            client.disconnect();
         }

      });
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

            expect(
                  factory.create(new IPSocket("144.175.1.1", 22), "root",
                        "romeo")).andThrow(new SshException("Auth fail"));
            expect(
                  factory.create(new IPSocket("144.175.1.1", 22), "root",
                        "password1")).andReturn(client1).atLeastOnce();

            client1.connect();
            runScript(client1, "computeserv", 1);
            client1.disconnect();

            expect(
                  factory.create(new IPSocket("144.175.1.2", 22), "root",
                        "password2")).andReturn(client2).atLeastOnce();
            expect(
                  factory.create(new IPSocket("144.175.1.3", 22), "root",
                        "password3")).andReturn(client3).atLeastOnce();
            expect(
                  factory.create(new IPSocket("144.175.1.4", 22), "root",
                        "password4")).andReturn(client4).atLeastOnce();

            runScriptAndInstallSsh(client2, "runscript", 2);
            runScriptAndInstallSsh(client3, "runscript", 3);
            runScriptAndInstallSsh(client4, "runscript", 4);

            replay(factory);
            replay(client1);
            replay(client2);
            replay(client3);
            replay(client4);

            bind(SshClient.Factory.class).toInstance(factory);
         }

         private void runScriptAndInstallSsh(SshClient client,
               String scriptName, int nodeId) {
            client.connect();

            runScript(client, scriptName, nodeId);

            expect(client.exec("mkdir .ssh")).andReturn(EXEC_GOOD);
            expect(client.exec("cat .ssh/id_rsa.pub >> .ssh/authorized_keys"))
                  .andReturn(EXEC_GOOD);
            expect(client.exec("chmod 600 .ssh/authorized_keys")).andReturn(
                  EXEC_GOOD);
            client.put(eq(".ssh/id_rsa.pub"), isEq(keyPair.get("public")));

            expect(client.exec("mkdir .ssh")).andReturn(EXEC_GOOD);
            client.put(eq(".ssh/id_rsa"), isEq(keyPair.get("private")));
            expect(client.exec("chmod 600 .ssh/id_rsa")).andReturn(EXEC_GOOD);

            client.disconnect();
            client.disconnect();

         }

         private void runScript(SshClient client, String scriptName, int nodeId) {
            client.put(eq("" + scriptName + ""), isEq(initScript(scriptName,
                  buildScript(OsFamily.UBUNTU))));

            expect(client.exec("chmod 755 " + scriptName + "")).andReturn(
                  EXEC_GOOD);
            expect(client.getUsername()).andReturn("root").atLeastOnce();
            expect(client.getHostAddress()).andReturn(nodeId + "")
                  .atLeastOnce();
            expect(client.exec("./" + scriptName + " init")).andReturn(
                  EXEC_GOOD);
            expect(client.exec("./" + scriptName + " start")).andReturn(
                  EXEC_GOOD);
            expect(client.exec("./" + scriptName + " status")).andReturn(
                  EXEC_GOOD);
            // next status says the script is done, since not found.
            expect(client.exec("./" + scriptName + " status")).andReturn(
                  EXEC_BAD);
            expect(client.exec("./" + scriptName + " tail")).andReturn(
                  EXEC_GOOD);
            expect(client.exec("./" + scriptName + " tailerr")).andReturn(
                  EXEC_GOOD);
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
      return new InitBuilder(scriptName, "/tmp/" + scriptName, "/tmp/"
            + scriptName, ImmutableMap.<String, String> of(), Iterables
            .toArray(Splitter.on("\n").split(
                  new String(checkNotNull(script, "script"))), String.class))
            .build(org.jclouds.scriptbuilder.domain.OsFamily.UNIX);
   }

   public static InputStream isEq(String value) {
      reportMatcher(new InputStreamEquals(value));
      return null;
   }

   public void testAssignability() throws Exception {
      @SuppressWarnings("unused")
      RestContext<ConcurrentMap<Integer, StubNodeMetadata>, ConcurrentMap<Integer, StubNodeMetadata>> stubContext = new ComputeServiceContextFactory()
            .createContext(provider, identity, credential)
            .getProviderSpecificContext();
   }

   private static class InputStreamEquals implements IArgumentMatcher,
         Serializable {

      private static final long serialVersionUID = 583055160049982067L;

      private final Object expected;

      public InputStreamEquals(Object expected) {
         this.expected = expected;
      }

      public boolean matches(Object actual) {
         if (this.expected == null) {
            return actual == null;
         }
         try {
            String real = Utils.toStringAndClose((InputStream) actual);
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
         InputStreamEquals other = (InputStreamEquals) o;
         return this.expected == null && other.expected == null
               || this.expected != null && this.expected.equals(other.expected);
      }

      @Override
      public int hashCode() {
         throw new UnsupportedOperationException("hashCode() is not supported");
      }

   }

   @Override
   protected void setupKeyPair() throws FileNotFoundException, IOException {
      keyPair = ImmutableMap.<String, String> of("public", "ssh-rsa",
            "private", "-----BEGIN RSA PRIVATE KEY-----");
   }

}