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

import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;

import org.jclouds.net.IPSocket;
import org.jclouds.ssh.SshClient;
import org.jclouds.ssh.jsch.config.JschSshClientModule;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.jcraft.jsch.JSchException;

/**
 * 
 * @author Adrian Cole
 */
@Test
public class JschSshClientTest {

   protected JschSshClient ssh;

   @BeforeTest
   public void setupSsh() throws UnknownHostException {
      ssh = createClient();
   }

   protected JschSshClient createClient() throws UnknownHostException {
      Injector i = Guice.createInjector(module());
      SshClient.Factory factory = i.getInstance(SshClient.Factory.class);
      JschSshClient ssh = JschSshClient.class.cast(factory.create(new IPSocket("localhost", 22),
               "username", "password"));
      return ssh;
   }

   protected Module module() {
      return new JschSshClientModule();
   }

   public void testExceptionClassesRetry() {

      assert ssh.shouldRetry(new JSchException("io error", new IOException("socket closed")));
      assert ssh.shouldRetry(new JSchException("connect error", new ConnectException("problem")));

   }

   public void testExceptionMessagesRetry() {
      assert ssh.shouldRetry(new JSchException(
               "Session.connect: java.io.IOException: End of IO Stream Read"));
      assert ssh.shouldRetry(new JSchException("Session.connect: invalid data"));
      assert ssh.shouldRetry(new JSchException(
               "Session.connect: java.net.SocketException: Connection reset"));
   }
}