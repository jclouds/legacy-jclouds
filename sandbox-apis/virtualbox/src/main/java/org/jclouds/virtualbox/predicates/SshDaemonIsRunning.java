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

import static com.google.common.base.Throwables.propagate;
import static org.jclouds.compute.options.RunScriptOptions.Builder.wrapInInitScript;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import javax.inject.Named;

import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.ssh.SshException;
import org.virtualbox_4_1.IMachine;

import com.google.common.base.Predicate;

/**
 * 
 * @author Andrea Turli
 */
public class SshDaemonIsRunning implements Predicate<IMachine> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private final ComputeServiceContext context;
   private final String nodeId;

   public SshDaemonIsRunning(ComputeServiceContext context, String nodeId) {
      this.context = context;
      this.nodeId = nodeId;
   }

   @Override
   public boolean apply(@Nullable IMachine machine) {
      boolean sshDeamonIsRunning = false;
      try {
         if (context.getComputeService()
               .runScriptOnNode(nodeId, "id", wrapInInitScript(false))
               .getExitCode() == 0) {
            logger.debug("Got response from ssh daemon.");
            sshDeamonIsRunning = true;
         }
      } catch (SshException e) {
         logger.debug("No response from ssh daemon...");
         propagate(e);
      }
      return sshDeamonIsRunning;
   }

}
