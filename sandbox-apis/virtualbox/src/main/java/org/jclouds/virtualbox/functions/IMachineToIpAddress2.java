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

package org.jclouds.virtualbox.functions;

import static org.jclouds.compute.options.RunScriptOptions.Builder.runAsRoot;

import javax.annotation.Nullable;

import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.options.RunScriptOptions;
import org.jclouds.scriptbuilder.domain.Call;
import org.jclouds.scriptbuilder.domain.Statement;
import org.virtualbox_4_1.IMachine;

import com.google.common.base.Function;

/**
 * Get an IP address from an IMachine using arp of the host machine.
 * 
 * @author Mattias Holmqvist, Andrea Turli
 */
public class IMachineToIpAddress2 implements Function<IMachine, String> {

   private ComputeServiceContext context;
   public IMachineToIpAddress2(ComputeServiceContext context) {
      this.context = context;
   }

   @Override
   public String apply(@Nullable IMachine machine) {
      final String hostId = "host";

      ExecResponse execResponse = runScriptOnNode(hostId, new Call("installGuestAdditions"),
            runAsRoot(false).wrapInInitScript(false));
      String ipAddress = execResponse.getOutput();
      System.out.println("IP address " + ipAddress);
      return ipAddress;
   }

   private ExecResponse runScriptOnNode(String nodeId, Statement command, RunScriptOptions options) {
      return context.getComputeService().runScriptOnNode(nodeId, command, options);
   }

}
