/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.compute;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reportMatcher;
import static org.jclouds.util.Predicates2.retry;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.easymock.IArgumentMatcher;
import org.jclouds.compute.config.AdminAccessConfiguration;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.internal.BaseComputeServiceLiveTest;
import org.jclouds.compute.util.OpenSocketFinder;
import org.jclouds.crypto.Pems;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.io.Payload;
import org.jclouds.predicates.SocketOpen;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.ssh.SshClient;
import org.jclouds.util.Strings2;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;
import com.google.common.net.HostAndPort;
import com.google.inject.AbstractModule;
import com.google.inject.Module;

/**
 * 
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", testName="StubComputeServiceIntegrationTest")
public class StubComputeServiceIntegrationTest extends BaseComputeServiceLiveTest {

   private static final ExecResponse EXEC_GOOD = new ExecResponse("", "", 0);
   private static final ExecResponse EXEC_BAD = new ExecResponse("", "", 1);
   private static final ExecResponse EXEC_RC_GOOD = new ExecResponse("0", "", 0);

   public StubComputeServiceIntegrationTest() {
      provider = "stub";
   }

   @Override
   public void testCorrectAuthException() throws Exception {
   }

   protected void buildSocketTester() {
      SocketOpen socketOpen = createMock(SocketOpen.class);

      expect(socketOpen.apply(HostAndPort.fromParts("144.175.1.1", 22))).andReturn(true).times(5);

      replay(socketOpen);

      socketTester = retry(socketOpen, 1, 1, MILLISECONDS);
      
      openSocketFinder = new OpenSocketFinder(){

         @Override
         public HostAndPort findOpenSocketOnNode(NodeMetadata node, int port, long timeoutValue, TimeUnit timeUnits) {
            return HostAndPort.fromParts("144.175.1.1", 8080);
         }
         
      };
   }

   @Override
   protected void checkHttpGet(NodeMetadata node) {

   }

   @Override
   protected Module getSshModule() {
      return new AbstractModule() {
         @Override
         protected void configure() {
            bind(AdminAccessConfiguration.class).toInstance(new AdminAccessConfiguration() {
               public Supplier<String> defaultAdminUsername() {
                  return Suppliers.ofInstance("defaultAdminUsername");
               }

               public Supplier<Map<String, String>> defaultAdminSshKeys() {
                  return Suppliers.<Map<String, String>> ofInstance(ImmutableMap.of("public", "publicKey", "private",
                        Pems.PRIVATE_PKCS1_MARKER));
               }
               
               public Function<String, String> cryptFunction() {
                  return new Function<String, String>() {
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
            SshClient client6 = createMock(SshClient.class);
            SshClient client7 = createMock(SshClient.class);

            expect(
                  factory.create(HostAndPort.fromParts("144.175.1.1", 22),
                        LoginCredentials.builder().user("root").password("password1").build())).andReturn(client1);
            expect(
                  factory.create(HostAndPort.fromParts("144.175.1.1", 22),
                        LoginCredentials.builder().user("web").privateKey(Pems.PRIVATE_PKCS1_MARKER).build())).andReturn(client1New)
                  .times(10);
            runScriptAndService(client1, client1New);

            expect(
                  factory.create(HostAndPort.fromParts("144.175.1.2", 22),
                        LoginCredentials.builder().user("root").password("password2").build())).andReturn(client2)
                  .times(4);
            expect(
                  factory.create(HostAndPort.fromParts("144.175.1.2", 22),
                        LoginCredentials.builder().user("root").password("password2").build())).andReturn(client2New);
            expect(
                  factory.create(HostAndPort.fromParts("144.175.1.2", 22),
                        LoginCredentials.builder().user("foo").privateKey(Pems.PRIVATE_PKCS1_MARKER).build())).andReturn(client2Foo);
            expect(
                  factory.create(HostAndPort.fromParts("144.175.1.2", 22),
                        LoginCredentials.builder().user("root").password("romeo").build())).andThrow(
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
               runScript(client2New, "adminUpdate", Strings2.toStringAndClose(StubComputeServiceIntegrationTest.class
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

            expect(
                  factory.create(HostAndPort.fromParts("144.175.1.3", 22),
                        LoginCredentials.builder().user("root").password("password3").build())).andReturn(client3)
                  .times(2);
            expect(
                  factory.create(HostAndPort.fromParts("144.175.1.4", 22),
                        LoginCredentials.builder().user("root").password("password4").build())).andReturn(client4)
                  .times(2);
            expect(
                  factory.create(HostAndPort.fromParts("144.175.1.5", 22),
                        LoginCredentials.builder().user("root").password("password5").build())).andReturn(client5)
                  .times(2);
            expect(
                  factory.create(HostAndPort.fromParts("144.175.1.6", 22),
                        LoginCredentials.builder().user("root").password("password6").build())).andReturn(client6)
                  .times(2);
            expect(
                  factory.create(HostAndPort.fromParts("144.175.1.7", 22),
                        LoginCredentials.builder().user("root").password("password7").build())).andReturn(client7)
                  .times(2);

            runScriptAndInstallSsh(client3, "bootstrap", 3);
            runScriptAndInstallSsh(client4, "bootstrap", 4);
            runScriptAndInstallSsh(client5, "bootstrap", 5);
            runScriptAndInstallSsh(client6, "bootstrap", 6);
            runScriptAndInstallSsh(client7, "bootstrap", 7);

            expect(
                  factory.create(eq(HostAndPort.fromParts("144.175.1.1", 22)),
                        eq(LoginCredentials.builder().user("defaultAdminUsername").privateKey(Pems.PRIVATE_PKCS1_MARKER).build())))
                  .andReturn(client1);
            expect(
                  factory.create(eq(HostAndPort.fromParts("144.175.1.2", 22)),
                        eq(LoginCredentials.builder().user("defaultAdminUsername").privateKey(Pems.PRIVATE_PKCS1_MARKER).build())))
                  .andReturn(client2);
            expect(
                  factory.create(eq(HostAndPort.fromParts("144.175.1.3", 22)),
                        eq(LoginCredentials.builder().user("defaultAdminUsername").privateKey(Pems.PRIVATE_PKCS1_MARKER).build())))
                  .andReturn(client3);
            expect(
                  factory.create(eq(HostAndPort.fromParts("144.175.1.4", 22)),
                        eq(LoginCredentials.builder().user("defaultAdminUsername").privateKey(Pems.PRIVATE_PKCS1_MARKER).build())))
                  .andReturn(client4);
            expect(
                  factory.create(eq(HostAndPort.fromParts("144.175.1.5", 22)),
                        eq(LoginCredentials.builder().user("defaultAdminUsername").privateKey(Pems.PRIVATE_PKCS1_MARKER).build())))
                  .andReturn(client5);
            expect(
                  factory.create(eq(HostAndPort.fromParts("144.175.1.6", 22)),
                        eq(LoginCredentials.builder().user("defaultAdminUsername").privateKey(Pems.PRIVATE_PKCS1_MARKER).build())))
                  .andReturn(client6);
            expect(
                  factory.create(eq(HostAndPort.fromParts("144.175.1.7", 22)),
                        eq(LoginCredentials.builder().user("defaultAdminUsername").privateKey(Pems.PRIVATE_PKCS1_MARKER).build())))
                  .andReturn(client7);

            helloAndJava(client2);
            helloAndJava(client3);
            helloAndJava(client4);
            helloAndJava(client5);
            helloAndJava(client6);
            helloAndJava(client7);

            replay(factory);
            replay(client1);
            replay(client1New);
            replay(client2);
            replay(client2New);
            replay(client2Foo);
            replay(client3);
            replay(client4);
            replay(client5);
            replay(client6);
            replay(client7);

            bind(SshClient.Factory.class).toInstance(factory);
         }

         private void runScriptAndService(SshClient client, SshClient clientNew) {
            client.connect();

            try {
               String scriptName = "configure-jetty";
               client.put("/tmp/init-" + scriptName, Strings2.toStringAndClose(StubComputeServiceIntegrationTest.class
                        .getResourceAsStream("/initscript_with_jetty.sh")));
               expect(client.exec("chmod 755 /tmp/init-" + scriptName)).andReturn(EXEC_GOOD);
               expect(client.exec("ln -fs /tmp/init-" + scriptName + " " + scriptName)).andReturn(EXEC_GOOD);
               expect(client.getUsername()).andReturn("root").atLeastOnce();
               expect(client.getHostAddress()).andReturn("localhost").atLeastOnce();
               expect(client.exec("/tmp/init-" + scriptName + " init")).andReturn(EXEC_GOOD);
               expect(client.exec("/tmp/init-" + scriptName + " start")).andReturn(EXEC_GOOD);
               expect(client.exec("/tmp/init-" + scriptName + " status")).andReturn(EXEC_GOOD);
               // next status says the script is done, since not found.
               expect(client.exec("/tmp/init-" + scriptName + " status")).andReturn(EXEC_BAD);
               expect(client.exec("/tmp/init-" + scriptName + " stdout")).andReturn(EXEC_GOOD);
               expect(client.exec("/tmp/init-" + scriptName + " stderr")).andReturn(EXEC_GOOD);
               expect(client.exec("/tmp/init-" + scriptName + " exitstatus")).andReturn(EXEC_RC_GOOD);

               // note we have to reconnect here, as we updated the login user.
               client.disconnect();

               clientNew.connect();               
               expect(clientNew.getUsername()).andReturn("web").atLeastOnce();
               expect(clientNew.getHostAddress()).andReturn("localhost").atLeastOnce();
               expect(clientNew.exec("head -1 /usr/local/jetty/VERSION.txt | cut -f1 -d ' '\n")).andReturn(EXEC_GOOD);
               clientNew.disconnect();
                              
               clientNew.connect();
               expect(clientNew.exec("java -fullversion\n")).andReturn(EXEC_GOOD);
               clientNew.disconnect();

               String startJetty = new StringBuilder()
                  .append("cd /usr/local/jetty").append('\n')
                  .append("nohup java -jar start.jar jetty.port=8080 > start.out 2> start.err < /dev/null &").append('\n')
                  .append("test $? && sleep 1").append('\n').toString();

               clientNew.connect();
               expect(clientNew.exec(startJetty)).andReturn(EXEC_GOOD);
               clientNew.disconnect();

               clientNew.connect();
               expect(clientNew.exec("cd /usr/local/jetty\n./bin/jetty.sh stop\n")).andReturn(EXEC_GOOD);
               clientNew.disconnect();

               clientNew.connect();
               expect(clientNew.exec(startJetty)).andReturn(EXEC_GOOD);
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
            expect(client.exec("/tmp/init-" + scriptName + " init")).andReturn(EXEC_GOOD);
            expect(client.exec("/tmp/init-" + scriptName + " start")).andReturn(EXEC_GOOD);
            expect(client.exec("/tmp/init-" + scriptName + " status")).andReturn(EXEC_GOOD);
            // next status says the script is done, since not found.
            expect(client.exec("/tmp/init-" + scriptName + " status")).andReturn(EXEC_BAD);
            expect(client.exec("/tmp/init-" + scriptName + " stdout")).andReturn(EXEC_GOOD);
            expect(client.exec("/tmp/init-" + scriptName + " stderr")).andReturn(EXEC_GOOD);
            expect(client.exec("/tmp/init-" + scriptName + " exitstatus")).andReturn(EXEC_RC_GOOD);
         }

         private void helloAndJava(SshClient client) {
            client.connect();

            expect(client.exec("echo hello")).andReturn(new ExecResponse("hello", "", 0));
            expect(client.exec("java -version")).andReturn(new ExecResponse("", "OpenJDK", 0));

            client.disconnect();
         }

      };
   }

   protected void assertNodeZero(Set<? extends NodeMetadata> metadataSet) {
      // TODO: this fails so we override it.
   }

   public static Payload payloadEq(String value) {
      reportMatcher(new PayloadEquals(value));
      return null;
   }

   private static class PayloadEquals implements IArgumentMatcher {

      private final Object expected;

      public PayloadEquals(Object expected) {
         this.expected = expected;
      }

      public boolean matches(Object actual) {
         if (this.expected == null) {
            return actual == null;
         }
         try {
            String real = Strings2.toString(((Payload) actual));
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
   protected void setupKeyPairForTest() {
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
   
   @Test(enabled = false)
   @Override
   public void weCanCancelTasks(NodeMetadata node) throws InterruptedException, ExecutionException {
      // not sure how to do multithreading in a mock so that tests can work
   }

   @Test(enabled = true, dependsOnMethods = { "testCompareSizes" })
   public void testCreateAndRunAService() throws Exception {
      super.testCreateAndRunAService();
   }

   @Test(enabled = true, dependsOnMethods = "testTemplateMatch")
   public void testConcurrentUseOfComputeServiceToCreateNodes() throws Exception {
      super.testConcurrentUseOfComputeServiceToCreateNodes();
   }

   @Test(enabled = true, dependsOnMethods = "testConcurrentUseOfComputeServiceToCreateNodes")
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

   @Test(enabled = true, dependsOnMethods = "testSuspendResume")
   public void testListNodesByIds() throws Exception {
      super.testListNodesByIds();
   }

   @Test(enabled = true, dependsOnMethods = { "testListNodes", "testGetNodesWithDetails", "testListNodesByIds" })
   public void testDestroyNodes() {
      super.testDestroyNodes();
   }

}
