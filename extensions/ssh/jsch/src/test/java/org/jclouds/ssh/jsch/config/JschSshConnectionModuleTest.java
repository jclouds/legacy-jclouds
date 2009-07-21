/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
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
package org.jclouds.ssh.jsch.config;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.jclouds.ssh.SshConnection;
import org.jclouds.ssh.jsch.JschSshConnection;
import org.jclouds.ssh.jsch.config.JschSshConnectionModule;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests the ability to configure a {@link JschSshConnection}
 * 
 * @author Adrian Cole
 */
@Test
public class JschSshConnectionModuleTest {

   public void testConfigureBindsClient() throws UnknownHostException {

      Injector i = Guice.createInjector(new JschSshConnectionModule());
      SshConnection.Factory factory = i.getInstance(SshConnection.Factory.class);
      SshConnection connection = factory.create(InetAddress.getLocalHost(), 22, "username",
               "password");
      assert connection instanceof JschSshConnection;
   }
}