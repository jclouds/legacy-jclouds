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
package org.jclouds.ssh.jsch;

import static org.testng.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.domain.Credentials;
import org.jclouds.io.Payload;
import org.jclouds.io.Payloads;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.net.IPSocket;
import org.jclouds.ssh.SshClient;
import org.jclouds.ssh.SshException;
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

         };
      } else {
         Injector i = Guice.createInjector(new JschSshClientModule(), new SLF4JLoggingModule());
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
      assertEquals(response.getOutput().trim(), "localhost".equals(sshHost) ? InetAddress.getLocalHost().getHostName()
            : sshHost);
   }

   // Seems to be using oauth now instead of bouncycastle and is more strict.
   // Commands used to generate this key are:
   // openssl dsaparam -outform PEM 2048 > dsaparams
   // openssl gendsa -aes256 -out privkey.pem dsaparams
   private static final String key = "-----BEGIN DSA PRIVATE KEY-----\n" +
         "Proc-Type: 4,ENCRYPTED\n" +
         "DEK-Info: AES-256-CBC,8C3A84B8BDA9FE69AAB4A322D7795161\n" +
         "\n" +
         "B8nuwNIcw5UtV9gGX2LiOq5OZ0uDgWsRon/mmWu+8EFd6X1aautVw8pCZuNusNkS\n" +
         "GZlO1JBIgKdX6Qqx0cPsirFB7GTNbBVHOIMqYbmQKW5Ju+n+NkNIomDJDJqBWknE\n" +
         "ZIkegznvdLN11r6F4jreusnVepSNYeRwKxA5KAT0S6XsgVFKSJZIyJj8EKZl/25D\n" +
         "a7LKoYRlf5QK+Q1/zmMyZcCt0irIMcHxslpVlyATajAADB0hwBl4Xh0H3oHR3PU1\n" +
         "xhsliYTARGov6Wn7adDCG9zWDzO7cX3941ub0FPoDdPLxGkmwqEwijF1XWvYbIUC\n" +
         "EBjomG3pwjC1kfoqAYJhThi8vmQYtFyCagcZMauHDKuqwUr1o3jS51PBe3bKeg4M\n" +
         "dP/JSTiTAUGtwV/MobThQFvCWJm22jIR6Eb0IYPcncUQuZ1QO2piwSvMUZZYCFX/\n" +
         "uY7fHkPZyBkZIrxGc91jhSQlo8qsVBIThJpLYI2M2PjKxTzsgZ2mWWK/Zm7pVqqI\n" +
         "ldTJ1cpSMb2/9BsXF0CWvzaC4qN5Ymmrp152M4ZEnsRN3ycd9wFCD2cM7w5frj4Z\n" +
         "3Q3M9/qNYuPfHddJa1DIkYpZxTTzOo4BBWx2O32D3in+2YZWjBgdxej17hKVkmUR\n" +
         "C432CtEqUYAtVv//3TZ47hYsywvvVEX/3ljcCObrHKDha6i3SwXMqe1tL3BYexOn\n" +
         "LS8aGQ148oekWaSWYrXCo0gjuJgY3hJZKUHoKdhvyW/FZG3rMjk5NlU9IwjMweqz\n" +
         "Bznl7sMxHqtW4BPV9fM4uaiM8LOMkIm6euu/1a2o///TaEgFr/H1ybLdcg8Au1Iy\n" +
         "sH68Xn+pmkx1bdVCCpi44EtAEHrpX11AC+cuvu8KG0A+Tpy3WW7YXYkregEQM3kF\n" +
         "XzzyJfHuZKvM7qXsMnt/T5VCYX1LSEtXFABFMHDsPC9qs1LVLdSC5U0Ux0Ac0Sqq\n" +
         "MRG2Yc8hDOPvPOmiqD9OK9PC6fa+bbMEtlS2O5Cd0l+hoE8OD1EFP1hAGQ0ivjZT\n" +
         "zMJXBUVUtYAkrpU6NcY+ub7IyYBR3wOWSAUbolx3K4p2o8k3MGFdLHb4dGvypIv2\n" +
         "oHhZLNLYGPrAN2g0gpNmlepDS1aG6422770O/Eh1bDXDyGYJRW3INwWenN8KbuYd\n" +
         "-----END DSA PRIVATE KEY-----";

   public void testPrivateKeyWithPassphrase() throws UnknownHostException {
      Injector i = Guice.createInjector(new JschSshClientModule(),new SLF4JLoggingModule());
      SshClient.Factory factory = i.getInstance(SshClient.Factory.class);
      try {
         JschSshClient ssh = JschSshClient.class.cast(factory.create(new IPSocket("localhost", 22), new Credentials(
               "username", key)));
         ssh.connect();
         assert false; // this code should never be reached.
      } catch (SshException e) {
         // Success!
      }
   }
}