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

import java.net.URI;
import java.util.Collections;

import org.jclouds.scriptbuilder.ScriptBuilder;
import org.jclouds.scriptbuilder.domain.OsFamily;
import org.jclouds.scriptbuilder.domain.SaveHttpResponseTo;
import org.jclouds.scriptbuilder.domain.Statement;

import com.google.common.collect.ImmutableMultimap;

public class InstallGuestAdditions implements Statement {

   private final String vboxVersion;
   private final String mountPoint;

   public InstallGuestAdditions(String vboxVersion) {
      this(vboxVersion, "/mnt");
   }

   public InstallGuestAdditions(String vboxVersion, String mountPoint) {
      this.vboxVersion = checkNotNull(vboxVersion, "vboxVersion");
      this.mountPoint = checkNotNull(mountPoint, "mountPoint");
   }

   @Override
   public Iterable<String> functionDependencies(OsFamily family) {
      return Collections.emptyList();
   }

   @Override
   public String render(OsFamily family) {
      checkNotNull(family, "family");
      if (family == OsFamily.WINDOWS)
         throw new UnsupportedOperationException("windows not yet implemented");

      String vboxGuestAdditionsIso = "VBoxGuestAdditions_" + vboxVersion + ".iso";
      ScriptBuilder scriptBuilder = new ScriptBuilder()
            .addStatement(
                  new SaveHttpResponseTo("{tmp}{fs}", vboxGuestAdditionsIso, "GET", URI
                        .create("http://download.virtualbox.org/virtualbox/" + vboxVersion + "/"
                              + vboxGuestAdditionsIso), ImmutableMultimap.<String, String> of()))
            .addStatement(exec(String.format("mount -o loop {tmp}{fs}%s %s", vboxGuestAdditionsIso, mountPoint)))
            .addStatement(call("installModuleAssistantIfNeeded"))
            .addStatement(exec(String.format("%s%s", mountPoint, "/VBoxLinuxAdditions.run")))
            .addStatement(exec(String.format("umount %s", mountPoint)));

      return scriptBuilder.render(family);
   }

}
