/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.ssh.jsch;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import org.apache.commons.io.IOUtils;
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

   @BeforeGroups(groups = { "live" })
   public SshClient setupClient() throws NumberFormatException, UnknownHostException {
      int port = (sshPort != null) ? Integer.parseInt(sshPort) : 22;
      InetAddress host = (sshHost != null) ? InetAddress.getByName(sshHost) : InetAddress
               .getLocalHost();
      if (sshUser == null || sshPass == null || sshUser.trim().equals("")
               || sshPass.trim().equals("")) {
         System.err.println("ssh credentials not present.  Tests will be lame");
         return new SshClient() {

            public void connect() {
            }

            public void disconnect() {
            }

            public InputStream get(String path) {
               if (path.equals("/etc/passwd")) {
                  return IOUtils.toInputStream("root");
               }
               throw new RuntimeException("path " + path + " not stubbed");
            }

            public ExecResponse exec(String command) {
               if (command.equals("hostname")) {
                  try {
                     return new ExecResponse(InetAddress.getLocalHost().getHostName(), "");
                  } catch (UnknownHostException e) {
                     throw new RuntimeException(e);
                  }
               }
               throw new RuntimeException("command " + command + " not stubbed");
            }

         };
      } else {
         Injector i = Guice.createInjector(new JschSshClientModule());
         SshClient.Factory factory = i.getInstance(SshClient.Factory.class);
         SshClient connection = factory.create(new InetSocketAddress(host, port), sshUser, sshPass);
         connection.connect();
         return connection;
      }
   }

   public void testGetEtcPassword() throws IOException {
      InputStream input = setupClient().get("/etc/passwd");
      String contents = Utils.toStringAndClose(input);
      assert contents.indexOf("root") >= 0 : "no root in " + contents;
   }

   public void testExecHostname() throws IOException {
      ExecResponse response = setupClient().exec("hostname");
      assertEquals(response.getError(), "");
      assertEquals(response.getOutput().trim(), InetAddress.getLocalHost().getHostName());
   }

}