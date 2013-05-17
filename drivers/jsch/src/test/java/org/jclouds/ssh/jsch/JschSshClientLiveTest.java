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
package org.jclouds.ssh.jsch;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.jclouds.compute.domain.ExecChannel;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.domain.LoginCredentials;
import org.jclouds.io.Payload;
import org.jclouds.io.Payloads;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.ssh.SshClient;
import org.jclouds.ssh.jsch.config.JschSshClientModule;
import org.jclouds.util.Strings2;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.base.Suppliers;
import com.google.common.collect.Lists;
import com.google.common.io.Closeables;
import com.google.common.io.Files;
import com.google.common.net.HostAndPort;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests the ability of a {@link JschSshClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "JschSshClientLiveTest" )
public class JschSshClientLiveTest {
   protected static final String sshHost = System.getProperty("test.ssh.host", "localhost");
   protected static final String sshPort = System.getProperty("test.ssh.port", "22");
   protected static final String sshUser = System.getProperty("test.ssh.username");
   protected static final String sshPass = System.getProperty("test.ssh.password");
   protected static final String sshKeyFile = System.getProperty("test.ssh.keyfile");
   private File temp;

   @BeforeGroups(groups = { "live" })
   public SshClient setupClient() throws NumberFormatException, FileNotFoundException, IOException {
      int port = Integer.parseInt(sshPort);
      if (sshUser == null
            || ((sshPass == null || sshPass.trim().equals("")) && (sshKeyFile == null || sshKeyFile.trim().equals("")))
            || sshUser.trim().equals("")) {
         System.err.println("ssh credentials not present.  Tests will be lame");
         return new SshClient() {

            public void connect() {
            }

            public void disconnect() {
            }

            public Payload get(String path) {
               if (path.equals("/etc/passwd")) {
                  return Payloads.newStringPayload("root");
               } else if (path.equals(temp.getAbsolutePath())) {
                  return Payloads.newStringPayload("rabbit");
               }
               throw new RuntimeException("path " + path + " not stubbed");
            }

            public ExecResponse exec(String command) {
               if (command.equals("hostname")) {
                  return new ExecResponse(sshHost, "", 0);
               }
               throw new RuntimeException("command " + command + " not stubbed");
            }

            @Override
            public void put(String path, Payload contents) {

            }

            @Override
            public String getHostAddress() {
               return null;
            }

            @Override
            public String getUsername() {
               return null;
            }

            @Override
            public void put(String path, String contents) {

            }

            @Override
            public ExecChannel execChannel(String command) {
               if (command.equals("hostname")) {
                  return new ExecChannel(new ByteArrayOutputStream(), new ByteArrayInputStream(sshHost.getBytes()),
                           new ByteArrayInputStream(new byte[] {}), Suppliers.ofInstance(0), new Closeable() {

                              @Override
                              public void close() {

                              }

                           });
               }
               throw new RuntimeException("command " + command + " not stubbed");
            }

         };
      } else {
         Injector i = Guice.createInjector(new JschSshClientModule(), new SLF4JLoggingModule());
         SshClient.Factory factory = i.getInstance(SshClient.Factory.class);
         SshClient connection;
         if (Strings.emptyToNull(sshKeyFile) != null) {
            connection = factory.create(HostAndPort.fromParts(sshHost, port), LoginCredentials.builder().user(sshUser)
                  .privateKey(Files.toString(new File(sshKeyFile), Charsets.UTF_8)).build());
         } else {
            connection = factory.create(HostAndPort.fromParts(sshHost, port),
                  LoginCredentials.builder().user(sshUser).password(sshPass).build());
         }
         connection.connect();
         return connection;
      }
   }

   @Test
   public void testPutAndGet() throws IOException {
      temp = File.createTempFile("foo", "bar");
      temp.deleteOnExit();
      SshClient client = setupClient();
      client.put(temp.getAbsolutePath(), Payloads.newStringPayload("rabbit"));
      Payload input = setupClient().get(temp.getAbsolutePath());
      String contents = Strings2.toString(input);
      assertEquals(contents, "rabbit");
   }

   @Test
   public void testGetEtcPassword() throws IOException {
      Payload input = setupClient().get("/etc/passwd");
      String contents = Strings2.toString(input);
      assert contents.indexOf("root") >= 0 : "no root in " + contents;
   }

   @Test
   public void testExecHostname() throws IOException {
      SshClient client = setupClient();
      try {
         ExecResponse response = client.exec("hostname");
         assertEquals(response.getExitStatus(), 0);
         assertEquals(response.getError(), "");
         assertEquals(response.getOutput().trim(), "localhost".equals(sshHost) ? InetAddress.getLocalHost().getHostName()
               : sshHost);
      } finally {
         client.disconnect();
      }
   }

   @Test
   public void testExecInvalidCommand() throws IOException {
      SshClient client = setupClient();
      try {
         ExecResponse response = client.exec("thisCommandDoesNotExist");
         assertNotEquals(response.getExitStatus(), 0);
         assertTrue(response.getOutput().contains("not found") || response.getError().contains("not found"), 
               "stdout="+response.getOutput()+"; stderr="+response.getError());
      } finally {
         client.disconnect();
      }
   }

   // Added for issue #1016.
   @Test(invocationCount=100)
   public void testExecHostnameRepeatedlyWithDifferentSessions() throws Exception {
      testExecHostname();
   }

   // Added for issue #1016.
   @Test
   public void testExecHostnameRepeatedlyWithSameSessions() throws Exception {
      final SshClient client = setupClient();
      
      try {
         for (int i = 0; i < 100; i++) {
            ExecResponse response = client.exec("hostname");
            assertEquals(response.getError(), "");
            assertEquals(response.getOutput().trim(), "localhost".equals(sshHost) ? InetAddress.getLocalHost().getHostName()
                     : sshHost);
            //System.out.println("completed (sequentially) "+i);
         }
      } finally {
         client.disconnect();
      }
   }
   
   // Added for issue #1016.
   // Note that some commands fail the first few attempt, but with default retries at 5 they do pass (for me locally).
   // The error is "JSchException: channel is not opened".
   // With the thread-pool size at 100, you get failures a lot more often.
   @Test
   public void testExecHostnameConcurrentlyWithSameSessions() throws Exception {
      final SshClient client = setupClient();
      ListeningExecutorService userExecutor = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(10));
      List<ListenableFuture<ExecResponse>> futures = Lists.newArrayList();
      try {
         for (int i = 0; i < 100; i++) {
            futures.add(userExecutor.submit(new Callable<ExecResponse>() {
               @Override
               public ExecResponse call() {
                  ExecResponse response = client.exec("hostname");
                  //System.out.println("completed (concurrently) "+count.incrementAndGet());
                  return response;
                  
               }
            }));
         }
         List<ExecResponse> responses = Futures.allAsList(futures).get(3000, TimeUnit.SECONDS);
         for (ExecResponse response : responses) {
            assertEquals(response.getError(), "");
            assertEquals(response.getOutput().trim(), "localhost".equals(sshHost) ? InetAddress.getLocalHost().getHostName()
                     : sshHost);
         }
      } finally {
         userExecutor.shutdownNow();
         client.disconnect();
      }
   }
   
   @Test
   public void testExecChannelTakesStdinAndNoEchoOfCharsInOuputAndOutlivesClient() throws IOException {
      SshClient client = setupClient();
      ExecChannel response = client.execChannel("cat <<EOF");
      client.disconnect();
      assertEquals(response.getExitStatus().get(), null);
      try {
         PrintStream printStream = new PrintStream(response.getInput());
         printStream.append("foo\n");
         printStream.append("EOF\n");
         printStream.close();
         assertEquals(Strings2.toStringAndClose(response.getError()), "");
         assertEquals(Strings2.toStringAndClose(response.getOutput()), "");
      } finally {
         Closeables.closeQuietly(response);
      }
      assertEquals(response.getExitStatus().get(), Integer.valueOf(0));
   }
}
