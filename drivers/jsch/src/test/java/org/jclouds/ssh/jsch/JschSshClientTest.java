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

import static com.google.inject.name.Names.bindProperties;

import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.Properties;

import org.jclouds.domain.LoginCredentials;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.ssh.SshClient;
import org.jclouds.ssh.jsch.config.JschSshClientModule;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.net.HostAndPort;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

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
      JschSshClient ssh = JschSshClient.class.cast(factory.create(HostAndPort.fromParts("localhost", 22), LoginCredentials
            .builder().user("username").password("password").build()));
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

   public void testRetryOnToStringNpe() throws UnknownHostException {
      Exception nex = new NullPointerException();
      Properties props = new Properties();
      // ensure we test toString on the exception independently
      props.setProperty("jclouds.ssh.retryable-messages", nex.toString());
      JschSshClient ssh1 = createClient(props);
      assert ssh1.shouldRetry(new RuntimeException(nex));
   }

   private static class ExceptionWithStrangeToString extends RuntimeException {
      private static final String MESSAGE = "foo-bar-exception-tostring";
      public String toString() { return MESSAGE; }
   }

   public void testRetryOnToStringCustom() throws UnknownHostException {
      Exception nex = new ExceptionWithStrangeToString();
      Properties props = new Properties();
      props.setProperty("jclouds.ssh.retryable-messages", "foo-bar");
      JschSshClient ssh1 = createClient(props);
      assert ssh1.shouldRetry(new RuntimeException(nex));
   }

   public void testRetryNotOnToStringCustomMismatch() throws UnknownHostException {
      Exception nex = new ExceptionWithStrangeToString();
      Properties props = new Properties();
      props.setProperty("jclouds.ssh.retryable-messages", "foo-baR");
      JschSshClient ssh1 = createClient(props);
      assert !ssh1.shouldRetry(new RuntimeException(nex));
   }

}
