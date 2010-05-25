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
package org.jclouds.ssh.jsch;

import static org.testng.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.jclouds.net.IPSocket;
import org.jclouds.ssh.ExecResponse;
import org.jclouds.ssh.SshClient;
import org.jclouds.ssh.jsch.config.JschSshClientModule;
import org.jclouds.util.Utils;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests the ability of a {@link JschSshClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "ssh.JschSshClientLiveTest")
public class JschSshClientLiveTest {
   protected static final String sshHost = System.getProperty("jclouds.test.ssh.host");
   protected static final String sshPort = System.getProperty("jclouds.test.ssh.port");
   protected static final String sshUser = System.getProperty("jclouds.test.ssh.username");
   protected static final String sshPass = System.getProperty("jclouds.test.ssh.password");
   protected static final String sshKeyFile = System.getProperty("jclouds.test.ssh.keyfile");
   private File temp;

   @BeforeGroups(groups = { "live" })
   public SshClient setupClient() throws NumberFormatException, FileNotFoundException, IOException {
      int port = (sshPort != null) ? Integer.parseInt(sshPort) : 22;
      if (sshUser == null
               || ((sshPass == null || sshPass.trim().equals("")) && (sshKeyFile == null || sshKeyFile
                        .trim().equals(""))) || sshUser.trim().equals("")) {
         System.err.println("ssh credentials not present.  Tests will be lame");
         return new SshClient() {

            public void connect() {
            }

            public void disconnect() {
            }

            public InputStream get(String path) {
               if (path.equals("/etc/passwd")) {
                  return Utils.toInputStream("root");
               } else if (path.equals(temp.getAbsolutePath())) {
                  return Utils.toInputStream("rabbit");
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
            public void put(String path, InputStream contents) {

            }

            @Override
            public String getHostAddress() {
               return null;
            }

            @Override
            public String getUsername() {
               return null;
            }

         };
      } else {
         Injector i = Guice.createInjector(new JschSshClientModule());
         SshClient.Factory factory = i.getInstance(SshClient.Factory.class);
         SshClient connection;
         if (sshKeyFile != null && !sshKeyFile.trim().equals("")) {
            connection = factory.create(new IPSocket(sshHost, port), sshUser, Utils
                     .toStringAndClose(new FileInputStream(sshKeyFile)).getBytes());
         } else {
            connection = factory.create(new IPSocket(sshHost, port), sshUser, sshPass);
         }
         connection.connect();
         return connection;
      }
   }

   public void testPutAndGet() throws IOException {
      temp = File.createTempFile("foo", "bar");
      temp.deleteOnExit();
      SshClient client = setupClient();
      client.put(temp.getAbsolutePath(), Utils.toInputStream("rabbit"));
      InputStream input = setupClient().get(temp.getAbsolutePath());
      String contents = Utils.toStringAndClose(input);
      assertEquals(contents, "rabbit");
   }

   public void testGetEtcPassword() throws IOException {
      InputStream input = setupClient().get("/etc/passwd");
      String contents = Utils.toStringAndClose(input);
      assert contents.indexOf("root") >= 0 : "no root in " + contents;
   }

   public void testExecHostname() throws IOException {
      ExecResponse response = setupClient().exec("hostname");
      assertEquals(response.getError(), "");
      assertEquals(response.getOutput().trim(), sshHost);
   }

}