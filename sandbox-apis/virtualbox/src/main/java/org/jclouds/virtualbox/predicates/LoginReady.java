/*
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

package org.jclouds.virtualbox.predicates;

import static org.jclouds.compute.options.RunScriptOptions.Builder.wrapInInitScript;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import javax.inject.Named;

import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.ssh.SshException;

import com.google.common.base.Predicate;

/**
 * This test works only for a vm with Virtualbox Guest Additions installed
 * 
 * @author Andrea Turli
 */
public class LoginReady implements Predicate<String> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final ComputeServiceContext context;

   private String vmName;
   private String usrName;
   private String password;

   public LoginReady(ComputeServiceContext context, String vmName,
         String usrName, String password) {
      super();
      this.context = context;
      this.vmName = vmName;
      this.usrName = usrName;
      this.password = password;
   }

   @Override
   public boolean apply(@Nullable String nodeId) {
      boolean loginReady = false;
      try {
         String command = "vboxmanage guestcontrol " + vmName + " exec --image '/usr/bin/id' --username " + usrName + " --password " + password + " --timeout 3000 --wait-stdout";
         if (context.getComputeService()
               .runScriptOnNode(nodeId, command, wrapInInitScript(false).runAsRoot(false))
               .getExitCode() == 0) {
            logger.debug("Got response from VBoxManage guestcontrol %s exec --image '/usr/bin/id' --username %s --password %s --timeout 3000 --wait-stdout running on %s", vmName, usrName, password );
            loginReady = true;
         }
      } catch (SshException e) {
         logger.debug("No response from VBoxManage guestcontrol %s exec --image '/usr/bin/id' --username %s --password %s --timeout 3000 --wait-stdout running on %s", vmName, usrName, password );
         return loginReady;
      } 
      return loginReady;
   }

   @Override
   public String toString() {
      return "loginReady()";
   }

}
