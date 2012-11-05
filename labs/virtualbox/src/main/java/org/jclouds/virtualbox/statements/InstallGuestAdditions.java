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

package org.jclouds.virtualbox.statements;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.scriptbuilder.domain.Statements.call;
import static org.jclouds.scriptbuilder.domain.Statements.exec;
import static org.jclouds.scriptbuilder.domain.Statements.saveHttpResponseTo;

import java.net.URI;
import java.util.List;

import javax.annotation.Resource;
import javax.inject.Named;

import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.scriptbuilder.domain.OsFamily;
import org.jclouds.scriptbuilder.domain.Statement;
import org.jclouds.scriptbuilder.domain.StatementList;
import org.jclouds.virtualbox.domain.IsoImage;
import org.jclouds.virtualbox.domain.StorageController;
import org.jclouds.virtualbox.domain.VmSpec;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * Mounts the DVD with guest additions that was downloaded and attached as removable storage. If no
 * guest additions is attached to the vmspec then it is downloaded.
 * 
 * @author David Alves
 * 
 */
public class InstallGuestAdditions implements Statement {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;
   private final StatementList statements;

   public InstallGuestAdditions(VmSpec vmSpecification, String vboxVersion) {
      this.statements = new StatementList(getStatements(vmSpecification, vboxVersion));
   }

   private List<Statement> getStatements(VmSpec vmSpecification, String vboxVersion) {
      List<Statement> statements = Lists.newArrayList();
      statements.add(call("installModuleAssistantIfNeeded"));
      String mountPoint = "/mnt";
      if (Iterables.tryFind(vmSpecification.getControllers(), new Predicate<StorageController>() {
         @Override
         public boolean apply(StorageController input) {
            if (!input.getIsoImages().isEmpty()) {
               for (IsoImage iso : input.getIsoImages()) {
                  if (iso.getSourcePath().contains("VBoxGuestAdditions_")) {
                     return true;
                  }
               }
            }
            return false;
         }
      }).isPresent()) {
         statements.add(exec("mount -t iso9660 /dev/sr1 " + mountPoint));
      } else {
         String vboxGuestAdditionsIso = "VBoxGuestAdditions_" + vboxVersion + ".iso";
         URI download = URI.create("http://download.virtualbox.org/virtualbox/" + vboxVersion + "/"
                  + vboxGuestAdditionsIso);
         statements.add(call("setupPublicCurl"));
         statements.add(saveHttpResponseTo(download, "{tmp}{fs}", vboxGuestAdditionsIso));//
         statements.add(exec(String.format("mount -o loop {tmp}{fs}%s %s", vboxGuestAdditionsIso, mountPoint)));
      }
      statements.add(exec(String.format("%s%s", mountPoint, "/VBoxLinuxAdditions.run --nox11"))); //
      return statements;
   }

   @Override
   public String render(OsFamily family) {
      if (checkNotNull(family, "family") == OsFamily.WINDOWS)
         throw new UnsupportedOperationException("windows not yet implemented");
      return statements.render(family);
   }

   @Override
   public Iterable<String> functionDependencies(OsFamily family) {
      return statements.functionDependencies(family);
   }
}
