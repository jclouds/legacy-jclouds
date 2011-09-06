/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.compute;

import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.reportMatcher;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.testng.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.easymock.IArgumentMatcher;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.domain.Credentials;
import org.jclouds.io.Payload;
import org.jclouds.net.IPSocket;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.predicates.SocketOpen;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.RestContext;
import org.jclouds.scriptbuilder.statements.login.AdminAccess;
import org.jclouds.scriptbuilder.statements.login.AdminAccess.Configuration;
import org.jclouds.ssh.SshClient;
import org.jclouds.util.Strings2;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;
import com.google.inject.Module;

/**
 * 
 * 
 * @author Adrian Cole
 */
@Test(groups = "live")
public class StubComputeServiceIntegrationTest extends BaseComputeServiceLiveTest {

   private static final ExecResponse EXEC_GOOD = new ExecResponse("", "", 0);
   private static final ExecResponse EXEC_BAD = new ExecResponse("", "", 1);

   public StubComputeServiceIntegrationTest() {
      provider = "stub";
   }

   @Override
   public void testCorrectAuthException() throws Exception {
   }

   protected void buildSocketTester() {
      SocketOpen socketOpen = createMock(SocketOpen.class);

      expect(socketOpen.apply(new IPSocket("144.175.1.1", 22))).andReturn(true).times(5);
      // restart of jboss
      expect(socketOpen.apply(new IPSocket("144.175.1.1", 8080))).andReturn(true).times(2);


      replay(socketOpen);

      preciseSocketTester = socketTester = new RetryablePredicate<IPSocket>(socketOpen, 1, 1, TimeUnit.MILLISECONDS);
   }

   @Override
   protected void checkHttpGet(NodeMetadata node) {

   }

   @Override
   protected Module getSshModule() {
      return new AbstractModule() {

         @Override
         protected void configure() {
            bind(AdminAccess.Configuration.class).toInstance(new Configuration() {

               @Override
               public Supplier<String> defaultAdminUsername() {
                  return Suppliers.ofInstance("defaultAdminUsername");
               }

               @Override
               public Supplier<Map<String, String>> defaultAdminSshKeys() {
                  return Suppliers.<Map<String, String>> ofInstance(ImmutableMap.of("public", "publicKey", "private",
                           "privateKey"));
               }

               @Override
               public Function<String, String> cryptFunction() {
                  return new Function<String, String>() {

                     @Override
                     public String apply(String input) {
                        return String.format("crypt(%s)", input);
                     }

                  };
               }

               public Supplier<String> passwordGenerator() {
                  return Suppliers.ofInstance("randompassword");
               }
            });
            SshClient.Factory factory = createMock(SshClient.Factory.class);
            SshClient client1 = createMock(SshClient.class);
            SshClient client1New = createMock(SshClient.class);
            SshClient client2 = createMock(SshClient.class);
            SshClient client2New = createMock(SshClient.class);
            SshClient client2Foo = createMock(SshClient.class);
            SshClient client3 = createMock(SshClient.class);
            SshClient client4 = createMock(SshClient.class);
            SshClient client5 = createMock(SshClient.class);

            expect(factory.create(new IPSocket("144.175.1.1", 22), new Credentials("root", "password1"))).andReturn(
                     client1);
            expect(factory.create(new IPSocket("144.175.1.1", 22), new Credentials("web", "privateKey"))).andReturn(
                     client1New).times(6);
            runScriptAndService(client1, client1New);

            expect(factory.create(new IPSocket("144.175.1.2", 22), new Credentials("root", "password2"))).andReturn(
                     client2).times(4);
            expect(factory.create(new IPSocket("144.175.1.2", 22), new Credentials("root", "password2"))).andReturn(
                     client2New);
            expect(factory.create(new IPSocket("144.175.1.2", 22), new Credentials("foo", "privateKey"))).andReturn(
                     client2Foo);
            expect(factory.create(new IPSocket("144.175.1.2", 22), new Credentials("root", "romeo"))).andThrow(
                     new AuthorizationException("Auth fail", null));

            // run script without backgrounding (via predicate)
            client2.connect();
            expect(client2.exec("hostname\n")).andReturn(new ExecResponse("stub-r\n", "", 0));
            client2.disconnect();

            // run script without backgrounding (via id)
            client2.connect();
            expect(client2.exec("hostname\n")).andReturn(new ExecResponse("stub-r\n", "", 0));
            client2.disconnect();

            client2.connect();
            try {
               runScript(client2, "runScriptWithCreds",
                        Strings2.toStringAndClose(StubComputeServiceIntegrationTest.class
                                 .getResourceAsStream("/runscript.sh")), 2);
            } catch (IOException e) {
               Throwables.propagate(e);
            }
            client2.disconnect();

            client2New.connect();
            try {
               runScript(client2New, "adminUpdate",
                        Strings2.toStringAndClose(StubComputeServiceIntegrationTest.class
                                 .getResourceAsStream("/runscript_adminUpdate.sh")), 2);
            } catch (IOException e) {
               Throwables.propagate(e);
            }
            client2New.disconnect();
            
            // check id 
            client2Foo.connect();
            expect(client2Foo.getUsername()).andReturn("foo").atLeastOnce();
            expect(client2Foo.getHostAddress()).andReturn("foo").atLeastOnce();
            expect(client2Foo.exec("echo $USER\n")).andReturn(new ExecResponse("foo\n", "", 0));
            client2Foo.disconnect();

            
            expect(factory.create(new IPSocket("144.175.1.3", 22), new Credentials("root", "password3"))).andReturn(
                     client3).times(2);
            expect(factory.create(new IPSocket("144.175.1.4", 22), new Credentials("root", "password4"))).andReturn(
                     client4).times(2);
            expect(factory.create(new IPSocket("144.175.1.5", 22), new Credentials("root", "password5"))).andReturn(
                     client5).times(2);

            runScriptAndInstallSsh(client3, "bootstrap", 3);
            runScriptAndInstallSsh(client4, "bootstrap", 4);
            runScriptAndInstallSsh(client5, "bootstrap", 5);

            expect(
                     factory.create(eq(new IPSocket("144.175.1.1", 22)), eq(new Credentials("defaultAdminUsername",
                              "privateKey")))).andReturn(client1);
            expect(
                     factory.create(eq(new IPSocket("144.175.1.2", 22)), eq(new Credentials("defaultAdminUsername",
                              "privateKey")))).andReturn(client2);
            expect(
                     factory.create(eq(new IPSocket("144.175.1.3", 22)), eq(new Credentials("defaultAdminUsername",
                              "privateKey")))).andReturn(client3);
            expect(
                     factory.create(eq(new IPSocket("144.175.1.4", 22)), eq(new Credentials("defaultAdminUsername",
                              "privateKey")))).andReturn(client4);
            expect(
                     factory.create(eq(new IPSocket("144.175.1.5", 22)), eq(new Credentials("defaultAdminUsername",
                              "privateKey")))).andReturn(client5);

            helloAndJava(client2);
            helloAndJava(client3);
            helloAndJava(client4);
            helloAndJava(client5);

            replay(factory);
            replay(client1);
            replay(client1New);
            replay(client2);
            replay(client2New);
            replay(client2Foo);
            replay(client3);
            replay(client4);
            replay(client5);

            bind(SshClient.Factory.class).toInstance(factory);
         }

         private void runScriptAndService(SshClient client, SshClient clientNew) {
            client.connect();

            try {
               String scriptName = "configure-jboss";
               client.put("/tmp/init-" + scriptName, Strings2.toStringAndClose(StubComputeServiceIntegrationTest.class
                        .getResourceAsStream("/initscript_with_jboss.sh")));
               expect(client.exec("chmod 755 /tmp/init-" + scriptName)).andReturn(EXEC_GOOD);
               expect(client.exec("ln -fs /tmp/init-" + scriptName + " " + scriptName)).andReturn(EXEC_GOOD);
               expect(client.getUsername()).andReturn("root").atLeastOnce();
               expect(client.getHostAddress()).andReturn("localhost").atLeastOnce();
               expect(client.exec("./" + scriptName + " init")).andReturn(EXEC_GOOD);
               expect(client.exec("./" + scriptName + " start")).andReturn(EXEC_GOOD);
               expect(client.exec("./" + scriptName + " status")).andReturn(EXEC_GOOD);
               // next status says the script is done, since not found.
               expect(client.exec("./" + scriptName + " status")).andReturn(EXEC_BAD);
               expect(client.exec("./" + scriptName + " tail")).andReturn(EXEC_GOOD);
               expect(client.exec("./" + scriptName + " tailerr")).andReturn(EXEC_GOOD);
               // note we have to reconnect here, as we updated the login user.
               client.disconnect();

               clientNew.connect();
               expect(clientNew.exec("java -fullversion\n")).andReturn(EXEC_GOOD);
               clientNew.disconnect();
               
               clientNew.connect();
               scriptName = "jboss";
               clientNew.put("/tmp/init-" + scriptName, Strings2
                        .toStringAndClose(StubComputeServiceIntegrationTest.class
                                 .getResourceAsStream("/runscript_jboss.sh")));
               expect(clientNew.exec("chmod 755 /tmp/init-" + scriptName)).andReturn(EXEC_GOOD);
               expect(clientNew.exec("ln -fs /tmp/init-" + scriptName + " " + scriptName)).andReturn(EXEC_GOOD);
               expect(clientNew.getUsername()).andReturn("web").atLeastOnce();
               expect(clientNew.getHostAddress()).andReturn("localhost").atLeastOnce();
               expect(clientNew.exec("./" + scriptName + " init")).andReturn(EXEC_GOOD);
               expect(clientNew.exec("./" + scriptName + " start")).andReturn(EXEC_GOOD);
               clientNew.disconnect();
               clientNew.connect();
               expect(clientNew.exec("./" + scriptName + " tail\n")).andReturn(EXEC_GOOD);
               clientNew.disconnect();

               clientNew.connect();
               expect(clientNew.exec("./" + scriptName + " stop\n")).andReturn(EXEC_GOOD);
               clientNew.disconnect();
               
               clientNew.connect();
               expect(clientNew.exec("./" + scriptName + " start\n")).andReturn(EXEC_GOOD);
               clientNew.disconnect();
               
               clientNew.connect();
               expect(clientNew.exec("./" + scriptName + " tail\n")).andReturn(EXEC_GOOD);
               clientNew.disconnect();
            } catch (IOException e) {
               Throwables.propagate(e);
            }
            clientNew.disconnect();

         }

         private void runScriptAndInstallSsh(SshClient client, String scriptName, int nodeId) {
            client.connect();

            try {
               runScript(client, scriptName, Strings2.toStringAndClose(StubComputeServiceIntegrationTest.class
                        .getResourceAsStream("/initscript_with_java.sh")), nodeId);
            } catch (IOException e) {
               Throwables.propagate(e);
            }

            client.disconnect();

         }

         private void runScript(SshClient client, String scriptName, String script, int nodeId) {
            client.put("/tmp/init-" + scriptName, script);
            expect(client.exec("chmod 755 /tmp/init-" + scriptName)).andReturn(EXEC_GOOD);
            expect(client.exec("ln -fs /tmp/init-" + scriptName + " " + scriptName)).andReturn(EXEC_GOOD);
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
            expect(client.exec("java -version")).andReturn(new ExecResponse("", "1.6", 0));

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

   public static Payload payloadEq(String value) {
      reportMatcher(new PayloadEquals(value));
      return null;
   }

   public void testAssignability() throws Exception {
      @SuppressWarnings("unused")
      RestContext<ConcurrentMap<String, NodeMetadata>, ConcurrentMap<String, NodeMetadata>> stubContext = new ComputeServiceContextFactory()
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
            String real = Strings2.toStringAndClose(((Payload) actual).getInput());
            assertEquals(real, expected);
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

   @Override
   public void testCompareSizes() throws Exception {
      super.testCompareSizes();
   }

   @Test(enabled = true, dependsOnMethods = { "testImagesCache" })
   public void testAScriptExecutionAfterBootWithBasicTemplate() throws Exception {
      super.testAScriptExecutionAfterBootWithBasicTemplate();
   }

   @Test(enabled = true, dependsOnMethods = { "testCompareSizes" })
   public void testCreateAndRunAService() throws Exception {
      super.testCreateAndRunAService();
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
   public void testCredentialsCache() throws Exception {
      super.testCredentialsCache();
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

   @Test(enabled = true, dependsOnMethods = "testReboot")
   public void testSuspendResume() throws Exception {
      super.testSuspendResume();
   }

   @Test(enabled = true, dependsOnMethods = { "testImagesCache" })
   public void testTemplateMatch() throws Exception {
      super.testTemplateMatch();
   }

   @Test(enabled = true, dependsOnMethods = "testSuspendResume")
   public void testGetNodesWithDetails() throws Exception {
      super.testGetNodesWithDetails();
   }

   @Test(enabled = true, dependsOnMethods = "testSuspendResume")
   public void testListNodes() throws Exception {
      super.testListNodes();
   }

   @Test(enabled = true, dependsOnMethods = { "testListNodes", "testGetNodesWithDetails" })
   public void testDestroyNodes() {
      super.testDestroyNodes();
   }

   @Override
   protected void cleanup() throws InterruptedException, ExecutionException, TimeoutException {
      super.cleanup();
   }

}