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
package org.jclouds.sshj.config;

import org.jclouds.domain.LoginCredentials;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.ssh.SshClient;
import org.jclouds.sshj.SshjSshClient;
import org.testng.annotations.Test;

import com.google.common.net.HostAndPort;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests the ability to configure a {@link SshjSshClient}
 * 
 * @author Adrian Cole
 */
@Test
public class SshjSshClientModuleTest {

   public void testConfigureBindsClient() {

      Injector i = Guice.createInjector(new SshjSshClientModule(), new SLF4JLoggingModule());
      SshClient.Factory factory = i.getInstance(SshClient.Factory.class);
      SshClient connection = factory.create(HostAndPort.fromParts("localhost", 22), LoginCredentials.builder().user("username")
            .password("password").build());
      assert connection instanceof SshjSshClient;
   }
}
