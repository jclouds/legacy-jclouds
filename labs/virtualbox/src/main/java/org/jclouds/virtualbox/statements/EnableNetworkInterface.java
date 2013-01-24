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
import static org.jclouds.scriptbuilder.domain.Statements.exec;

import java.util.List;

import com.google.common.collect.Lists;

import org.jclouds.scriptbuilder.domain.OsFamily;
import org.jclouds.scriptbuilder.domain.Statement;
import org.jclouds.scriptbuilder.domain.StatementList;
import org.jclouds.virtualbox.domain.NetworkInterfaceCard;

/**
 * Up the network interface chosen
 * 
 * @author Andrea Turli
 * 
 */
public class EnableNetworkInterface implements Statement {

   private final StatementList statements;

   public EnableNetworkInterface(NetworkInterfaceCard networkInterfaceCard) {
      int slot = (int) networkInterfaceCard.getSlot();
      String iface = null;
      switch (slot) {
         case 0:
            iface = "eth0";
            break;
         case 1:
            iface = "eth1";
            break;
         case 2:
            iface = "eth2";
            break;
         case 3:
            iface = "eth3";
            break;
         default:
            throw new IllegalArgumentException("slot must be 0,1,2,3 (was: " + slot + ")");
      }
      this.statements = new StatementList(getStatements(iface));
   }

   private List<Statement> getStatements(String iface) {
      List<Statement> statements = Lists.newArrayList();
      statements.add(exec(String.format("echo auto %s >> /etc/network/interfaces", iface)));
      statements.add(exec(String.format("echo iface %s inet dhcp >> /etc/network/interfaces", iface)));
      statements.add(exec("/etc/init.d/networking restart"));
      return statements;
   }

   @Override
   public Iterable<String> functionDependencies(OsFamily family) {
      return statements.functionDependencies(family);
   }

   @Override
   public String render(OsFamily family) {
      if (checkNotNull(family, "family") == OsFamily.WINDOWS)
         throw new UnsupportedOperationException("windows not yet implemented");
      return statements.render(family);
   }
}
