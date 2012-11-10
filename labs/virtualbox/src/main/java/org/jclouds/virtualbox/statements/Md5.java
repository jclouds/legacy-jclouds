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

public class Md5 implements Statement {

   private final String filePath;

   public Md5(String filePath) {
      this.filePath = checkNotNull(filePath, "filePath");
   }

   @Override
   public Iterable<String> functionDependencies(OsFamily family) {
      return ImmutableList.of();
   }

   @Override
   public String render(OsFamily family) {
      if (checkNotNull(family, "family") == OsFamily.WINDOWS)
         throw new UnsupportedOperationException("windows not yet implemented");
      return String.format("command -v md5sum >/dev/null 2>&1 && md5sum %s | awk '{print $1}' "
            + "|| command -v md5 >/dev/null 2>&1 && md5 %s | awk '{ print $4 }'", filePath, filePath);
   }

}
