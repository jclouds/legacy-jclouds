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
package org.jclouds.virtualbox.experiment;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.encryption.bouncycastle.config.BouncyCastleCryptoModule;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.sshj.config.SshjSshClientModule;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

public class TestUtils {
   public static ComputeServiceContext computeServiceForLocalhost() throws FileNotFoundException, IOException {
      Properties contextProperties = new Properties();

      StringBuilder nodes = new StringBuilder();
      nodes.append("nodes:\n");
      nodes.append("    - id: host\n");
      nodes.append("      name: host installing virtualbox\n");
      nodes.append("      hostname: localhost\n");
      nodes.append("      os_family: ").append(OsFamily.LINUX).append("\n");
      nodes.append("      os_description: ").append(System.getProperty("os.name")).append("\n");
      nodes.append("      os_version: ").append(System.getProperty("os.version")).append("\n");
      nodes.append("      group: ").append("ssh").append("\n");
      nodes.append("      username: ").append(System.getProperty("user.name")).append("\n");
      nodes.append("      credential_url: file://").append(System.getProperty("user.home")).append("/.ssh/id_rsa")
            .append("\n");
      nodes.append("\n");
      nodes.append("    - id: guest\n");
      nodes.append("      name: new guest\n");
      nodes.append("      hostname: localhost\n");
      nodes.append("      login_port: 2222\n");
      nodes.append("      os_family: ubuntu").append("\n");
      nodes.append("      os_description: ubuntu/11.04").append("\n");
      nodes.append("      os_version: 11.04").append("\n");
      nodes.append("      group: guest").append("\n");
      nodes.append("      username: toor").append("\n");
      nodes.append("      credential: password").append("\n");

      contextProperties.setProperty("byon.nodes", nodes.toString());

      return new ComputeServiceContextFactory().createContext("byon", "foo", "bar", ImmutableSet.<Module> of(
            new SshjSshClientModule(), new SLF4JLoggingModule(), new BouncyCastleCryptoModule()), contextProperties);
   }
}
