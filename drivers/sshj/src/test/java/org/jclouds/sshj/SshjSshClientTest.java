/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.sshj;

import java.io.IOException;
import java.net.UnknownHostException;

import net.schmizz.sshj.common.SSHException;
import net.schmizz.sshj.connection.ConnectionException;
import net.schmizz.sshj.transport.TransportException;
import net.schmizz.sshj.userauth.UserAuthException;

import org.jclouds.domain.Credentials;
import org.jclouds.net.IPSocket;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.ssh.SshClient;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * 
 * @author Adrian Cole
 */
@Test
public class SshjSshClientTest {

   protected SshjSshClient ssh;

   @BeforeTest
   public void setupSsh() throws UnknownHostException {
      ssh = createClient();
   }

   protected SshjSshClient createClient() throws UnknownHostException {
      Injector i = Guice.createInjector(module());
      SshClient.Factory factory = i.getInstance(SshClient.Factory.class);
      SshjSshClient ssh = SshjSshClient.class.cast(factory.create(new IPSocket("localhost", 22), new Credentials(
            "username", "password")));
      return ssh;
   }

   protected Module module() {
      return new SshjSshClientModule();
   }

   @Test(expectedExceptions = AuthorizationException.class)
   public void testPropateConvertsAuthException() {
      ssh.propagate(new UserAuthException(""), "");
   }

   public void testExceptionClassesRetry() {
      assert ssh.shouldRetry(new TransportException("socket closed"));
      assert ssh.shouldRetry(new ConnectionException("problem"));
      assert !ssh.shouldRetry(new IOException("channel %s is not open", new NullPointerException()));
   }

   public void testOnlyRetryAuthWhenSet() throws UnknownHostException {
      SshjSshClient ssh1 = createClient();
      assert !ssh1.shouldRetry(new AuthorizationException("problem", null));
      ssh1.retryAuth = true;
      assert ssh1.shouldRetry(new AuthorizationException("problem", null));
   }

   public void testExceptionMessagesRetry() {
      assert !ssh.shouldRetry(new SSHException(""));
      assert !ssh.shouldRetry(new NullPointerException((String) null));
   }

   public void testCausalChainHasMessageContaining() {
      assert ssh.causalChainHasMessageContaining(
            new SSHException("Session.connect: java.io.IOException: End of IO Stream Read")).apply(
            " End of IO Stream Read");
      assert ssh.causalChainHasMessageContaining(
            new SSHException("Session.connect: java.net.SocketException: Connection reset")).apply("java.net.Socket");
      assert !ssh.causalChainHasMessageContaining(new NullPointerException()).apply(" End of IO Stream Read");
   }
}
