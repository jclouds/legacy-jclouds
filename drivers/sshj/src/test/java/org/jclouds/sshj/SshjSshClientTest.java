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
package org.jclouds.sshj;

import static com.google.inject.name.Names.bindProperties;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.Properties;
import java.util.logging.Level;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.common.SSHException;
import net.schmizz.sshj.connection.ConnectionException;
import net.schmizz.sshj.sftp.SFTPException;
import net.schmizz.sshj.transport.TransportException;
import net.schmizz.sshj.userauth.UserAuthException;

import org.jclouds.domain.LoginCredentials;
import org.jclouds.logging.BufferLogger;
import org.jclouds.logging.BufferLogger.Record;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.ssh.SshClient;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.net.HostAndPort;
import com.google.inject.AbstractModule;
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
   public void setupSsh() {
      ssh = createClient();
   }

   protected SshjSshClient createClient() {
      return createClient(new Properties());
   }

   protected SshjSshClient createClient(final Properties props) {
      Injector i = Guice.createInjector(module(), new AbstractModule() {

         @Override
         protected void configure() {
            bindProperties(binder(), props);
         }

      }, new SLF4JLoggingModule());
      SshClient.Factory factory = i.getInstance(SshClient.Factory.class);
      SshjSshClient ssh = SshjSshClient.class.cast(factory.create(HostAndPort.fromParts("localhost", 22), LoginCredentials
            .builder().user("username").password("password").build()));
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
      assert ssh.shouldRetry(new ConnectionException("Read timed out", new SSHException("Read timed out",
            new SocketTimeoutException("Read timed out"))));
      assert ssh.shouldRetry(new SFTPException("Failure!"));
      assert ssh.shouldRetry(new SocketTimeoutException("connect timed out"));
      assert ssh.shouldRetry(new TransportException("socket closed"));
      assert ssh.shouldRetry(new ConnectionException("problem"));
      assert ssh.shouldRetry(new ConnectException("Connection refused"));
      assert !ssh.shouldRetry(new IOException("channel %s is not open", new NullPointerException()));
   }

   public void testOnlyRetryAuthWhenSet() {
      SshjSshClient ssh1 = createClient();
      assert !ssh1.shouldRetry(new AuthorizationException("problem", null));
      assert !ssh1.shouldRetry(new UserAuthException("problem", null));
      ssh1.retryAuth = true;
      assert ssh1.shouldRetry(new AuthorizationException("problem", null));
      assert ssh1.shouldRetry(new UserAuthException("problem", null));
   }

   public void testOnlyRetryAuthWhenSetViaProperties() {
      Properties props = new Properties();
      props.setProperty("jclouds.ssh.retry-auth", "true");
      SshjSshClient ssh1 = createClient(props);
      assert ssh1.shouldRetry(new AuthorizationException("problem", null));
      assert ssh1.shouldRetry(new UserAuthException("problem", null));
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

   public void testRetryOnToStringNpe() {
      Exception nex = new NullPointerException();
      Properties props = new Properties();
      // ensure we test toString on the exception independently
      props.setProperty("jclouds.ssh.retryable-messages", nex.toString());
      SshjSshClient ssh1 = createClient(props);
      assert ssh1.shouldRetry(new RuntimeException(nex));
   }

   private static class ExceptionWithStrangeToString extends RuntimeException {
      private static final String MESSAGE = "foo-bar-exception-tostring";

      public String toString() {
         return MESSAGE;
      }
   }

   public void testRetryOnToStringCustom() {
      Exception nex = new ExceptionWithStrangeToString();
      Properties props = new Properties();
      props.setProperty("jclouds.ssh.retryable-messages", "foo-bar");
      SshjSshClient ssh1 = createClient(props);
      assert ssh1.shouldRetry(new RuntimeException(nex));
   }

   public void testDontThrowIOExceptionOnClear() throws Exception {
      SshjSshClient ssh1 = createClient();
      SSHClient ssh = createMock(SSHClient.class);
      expect(ssh.isConnected()).andReturn(true);
      ssh.disconnect();
      expectLastCall().andThrow(new ConnectionException("disconnected"));
      replay(ssh);
      ssh1.sshClientConnection.ssh = ssh;
      ssh1.sshClientConnection.clear();
      verify(ssh);
   }

   public void testRetryNotOnToStringCustomMismatch() {
      Exception nex = new ExceptionWithStrangeToString();
      Properties props = new Properties();
      props.setProperty("jclouds.ssh.retryable-messages", "foo-baR");
      SshjSshClient ssh1 = createClient(props);
      assert !ssh1.shouldRetry(new RuntimeException(nex));
   }

   public void testRetriesLoggedAtInfoWithCount() throws Exception {
      SSHClientConnection mockConnection = createMock(SSHClientConnection.class);
      net.schmizz.sshj.SSHClient mockClient = createMock(net.schmizz.sshj.SSHClient.class);

      mockConnection.clear(); expectLastCall();
      mockConnection.create(); expectLastCall().andThrow(new ConnectionException("test1"));
      mockConnection.clear(); expectLastCall();
      //currently does two clears, one on failure (above) and one on next iteration (below)
      mockConnection.clear(); expectLastCall();
      mockConnection.create(); expectLastCall().andReturn(mockClient);
      replay(mockConnection);
      replay(mockClient);
      
      ssh.sshClientConnection = mockConnection;
      BufferLogger logcheck = new BufferLogger(ssh.getClass().getCanonicalName()); 
      ssh.logger = logcheck;
      logcheck.setLevel(Level.INFO);
      
      ssh.connect();
      
      Assert.assertEquals(ssh.sshClientConnection, mockConnection);
      verify(mockConnection);
      verify(mockClient);
      Record r = logcheck.assertLogContains("attempt 1 of 5");
      logcheck.assertLogDoesntContain("attempt 2 of 5");
      Assert.assertEquals(Level.INFO, r.getLevel());
   }

}
