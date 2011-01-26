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

package org.jclouds.ssh.jsch;

import static org.testng.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.domain.Credentials;
import org.jclouds.io.Payload;
import org.jclouds.io.Payloads;
import org.jclouds.net.IPSocket;
import org.jclouds.ssh.SshClient;
import org.jclouds.ssh.jsch.config.JschSshClientModule;
import org.jclouds.util.Strings2;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests the ability of a {@link JschSshClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live")
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

         };
      } else {
         Injector i = Guice.createInjector(new JschSshClientModule());
         SshClient.Factory factory = i.getInstance(SshClient.Factory.class);
         SshClient connection;
         if (sshKeyFile != null && !sshKeyFile.trim().equals("")) {
            connection = factory.create(new IPSocket(sshHost, port),
                  new Credentials(sshUser, Strings2.toStringAndClose(new FileInputStream(sshKeyFile))));
         } else {
            connection = factory.create(new IPSocket(sshHost, port), new Credentials(sshUser, sshPass));
         }
         connection.connect();
         return connection;
      }
   }

   public void testPutAndGet() throws IOException {
      temp = File.createTempFile("foo", "bar");
      temp.deleteOnExit();
      SshClient client = setupClient();
      client.put(temp.getAbsolutePath(), Payloads.newStringPayload("rabbit"));
      Payload input = setupClient().get(temp.getAbsolutePath());
      String contents = Strings2.toStringAndClose(input.getInput());
      assertEquals(contents, "rabbit");
   }

   public void testGetEtcPassword() throws IOException {
      Payload input = setupClient().get("/etc/passwd");
      String contents = Strings2.toStringAndClose(input.getInput());
      assert contents.indexOf("root") >= 0 : "no root in " + contents;
   }

   public void testExecHostname() throws IOException {
      ExecResponse response = setupClient().exec("hostname");
      assertEquals(response.getError(), "");
      assertEquals(response.getOutput().trim(), sshHost);
   }

}