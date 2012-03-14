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

package org.jclouds.virtualbox.predicates;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.options.RunScriptOptions;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.virtualbox.functions.IMachineToNodeMetadata;
import org.jclouds.virtualbox.statements.InstallGuestAdditions;
import org.jclouds.virtualbox.util.MachineUtils;
import org.virtualbox_4_1.IMachine;
import org.virtualbox_4_1.VirtualBoxManager;

import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Inject;

@Singleton
public class GuestAdditionsInstaller implements Predicate<IMachine> {

  @Resource
  @Named(ComputeServiceConstants.COMPUTE_LOGGER)
  protected Logger                          logger = Logger.NULL;

  private final IMachineToNodeMetadata      imachineToNodeMetadata;
  private final MachineUtils                machineUtils;
  private final Supplier<VirtualBoxManager> manager;

  @Inject
  public GuestAdditionsInstaller(Supplier<VirtualBoxManager> manager, MachineUtils machineUtils,
      IMachineToNodeMetadata imachineToNodeMetadata) {
    this.machineUtils = machineUtils;
    this.imachineToNodeMetadata = imachineToNodeMetadata;
    this.manager = manager;
  }

  @Override
  public boolean apply(IMachine machine) {
    String vboxVersion = Iterables.get(Splitter.on('r').split(manager.get().getVBox().getVersion()), 0);
    ListenableFuture<ExecResponse> execFuture = machineUtils.runScriptOnNode(imachineToNodeMetadata.apply(machine),
        new InstallGuestAdditions(vboxVersion), RunScriptOptions.NONE);
    ExecResponse execResponse = Futures.getUnchecked(execFuture);
    return execResponse == null ? false : execResponse.getExitStatus() == 0;
  }

}