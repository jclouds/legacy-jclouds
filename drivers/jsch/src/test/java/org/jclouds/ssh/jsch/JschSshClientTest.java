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

import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.Properties;

import com.google.inject.AbstractModule;
import org.jclouds.domain.Credentials;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.net.IPSocket;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.ssh.SshClient;
import org.jclouds.ssh.SshException;
import org.jclouds.ssh.jsch.config.JschSshClientModule;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

import static com.google.inject.name.Names.bindProperties;

/**
 * 
 * @author Adrian Cole
 */
@Test
public class JschSshClientTest {

   protected JschSshClient ssh;

   @BeforeTest
   public void setupSsh() throws UnknownHostException {
      ssh = createClient(new Properties());
   }

   protected JschSshClient createClient() throws UnknownHostException {
      return createClient(new Properties());
   }

   protected JschSshClient createClient(final Properties props) throws UnknownHostException {
       Injector i = Guice.createInjector(module(), new AbstractModule() {
         @Override
         protected void configure() {
            bindProperties(binder(), props);
         }
      }, new SLF4JLoggingModule());
      SshClient.Factory factory = i.getInstance(SshClient.Factory.class);
      JschSshClient ssh = JschSshClient.class.cast(factory.create(new IPSocket("localhost", 22), new Credentials(
            "username", "password")));
      return ssh;
   }

   protected Module module() {
      return new JschSshClientModule();
   }

   @Test(expectedExceptions = AuthorizationException.class)
   public void testPropateConvertsAuthException() {
      ssh.propagate(new JSchException("Auth fail"), "");
   }

   public void testExceptionClassesRetry() {
      assert ssh.shouldRetry(new JSchException("io error", new IOException("socket closed")));
      assert ssh.shouldRetry(new JSchException("connect error", new ConnectException("problem")));
      assert ssh.shouldRetry(new IOException("channel %s is not open", new NullPointerException()));
      assert ssh.shouldRetry(new IOException("channel %s is not open", new NullPointerException(null)));
   }

   public void testOnlyRetryAuthWhenSet() throws UnknownHostException {
      JschSshClient ssh1 = createClient();
      assert !ssh1.shouldRetry(new AuthorizationException("problem", null));
      ssh1.retryAuth = true;
      assert ssh1.shouldRetry(new AuthorizationException("problem", null));
   }

   public void testOnlyRetryAuthWhenSetViaProperties() throws UnknownHostException {
      Properties props = new Properties();
      props.setProperty("jclouds.ssh.retry-auth", "true");
      JschSshClient ssh1 = createClient(props);
      assert ssh1.shouldRetry(new AuthorizationException("problem", null));
   }

   public void testExceptionMessagesRetry() {
      assert !ssh.shouldRetry(new NullPointerException(""));
      assert !ssh.shouldRetry(new NullPointerException((String) null));
      assert ssh.shouldRetry(new JSchException("Session.connect: java.io.IOException: End of IO Stream Read"));
      assert ssh.shouldRetry(new JSchException("Session.connect: invalid data"));
      assert ssh.shouldRetry(new JSchException("Session.connect: java.net.SocketException: Connection reset"));
   }

   public void testDoNotRetryOnGeneralSftpError() {
      // http://sourceforge.net/mailarchive/forum.php?thread_name=CAARMrHVhASeku48xoAgWEb-nEpUuYkMA03PoA5TvvFdk%3DjGKMA%40mail.gmail.com&forum_name=jsch-users
      assert !ssh.shouldRetry(new SftpException(ChannelSftp.SSH_FX_FAILURE, new NullPointerException().toString()));
   }

   public void testCausalChainHasMessageContaining() {
      assert ssh.causalChainHasMessageContaining(
            new JSchException("Session.connect: java.io.IOException: End of IO Stream Read")).apply(
            " End of IO Stream Read");
      assert ssh.causalChainHasMessageContaining(new JSchException("Session.connect: invalid data")).apply(
            " invalid data");
      assert ssh.causalChainHasMessageContaining(
            new JSchException("Session.connect: java.net.SocketException: Connection reset")).apply("java.net.Socket");
      assert !ssh.causalChainHasMessageContaining(new NullPointerException()).apply(" End of IO Stream Read");
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
      Injector i = Guice.createInjector(module(),new SLF4JLoggingModule());
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