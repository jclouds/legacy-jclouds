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

import org.jclouds.scriptbuilder.domain.OsFamily;
import org.jclouds.scriptbuilder.domain.Statement;

import com.google.common.collect.ImmutableList;

/**
 * Assign to user the passwordless sudo rights
 * 
 * @author andrea turli
 * 
 */
public class PasswordlessSudo implements Statement {

   private final String user;
   
   public PasswordlessSudo(String user) {
      this.user = user;
   }

   @Override
   public Iterable<String> functionDependencies(OsFamily family) {
      return ImmutableList.of();
   }
   
   @Override
   public String render(OsFamily family) {
      if (checkNotNull(family, "family") == OsFamily.WINDOWS)
         throw new UnsupportedOperationException("windows not yet implemented");
      return String.format("touch /etc/sudoers.d/passwordless && echo \"%s ALL = NOPASSWD: ALL\" > /etc/sudoers.d/passwordless && chmod 0440 /etc/sudoers.d/passwordless", user);
   }

}
