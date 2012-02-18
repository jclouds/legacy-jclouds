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

import org.jclouds.scriptbuilder.domain.OsFamily;
import org.jclouds.scriptbuilder.domain.StatementList;

public class InstallGuestAdditions extends StatementList {

   public InstallGuestAdditions(String vboxVersion) {
      this(vboxVersion, "/mnt", "VBoxGuestAdditions_" + vboxVersion + ".iso");
   }

   public InstallGuestAdditions(String vboxVersion, String mountPoint, String vboxGuestAdditionsIso) {
      this(URI.create("http://download.virtualbox.org/virtualbox/" + vboxVersion + "/" + vboxGuestAdditionsIso),
            mountPoint, vboxGuestAdditionsIso);
   }

   public InstallGuestAdditions(URI download, String mountPoint, String vboxGuestAdditionsIso) {
      super(call("setupPublicCurl"), //
            saveHttpResponseTo(download, "{tmp}{fs}", vboxGuestAdditionsIso),//
            exec(String.format("mount -o loop {tmp}{fs}%s %s", vboxGuestAdditionsIso, mountPoint)),
            call("installModuleAssistantIfNeeded"), //
            exec(String.format("%s%s", mountPoint, "/VBoxLinuxAdditions.run")), //
            exec(String.format("umount %s", mountPoint)));
   }

   @Override
   public String render(OsFamily family) {
      if (checkNotNull(family, "family") == OsFamily.WINDOWS)
         throw new UnsupportedOperationException("windows not yet implemented");
      return super.render(family);
   }
}
