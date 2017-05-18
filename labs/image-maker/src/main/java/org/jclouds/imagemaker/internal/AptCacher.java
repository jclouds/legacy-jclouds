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

package org.jclouds.imagemaker.internal;

import java.util.List;

import javax.inject.Inject;

import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.imagemaker.PackageProcessor;
import org.jclouds.scriptbuilder.domain.Statement;

import com.google.common.collect.ImmutableSet;

public class AptCacher implements PackageProcessor {

   private final ComputeServiceContext ctx;

   @Inject
   public AptCacher(ComputeServiceContext ctx) {
      this.ctx = ctx;
   }

   @Override
   public boolean isCompatible(NodeMetadata node) {
      return ImmutableSet.of(OsFamily.UBUNTU, OsFamily.DEBIAN).contains(node.getOperatingSystem().getFamily());
   }

   public static class AptCacheStatement implements Statement {

      private List<String> packages;

      public AptCacheStatement(List<String> packages) {
         this.packages = packages;
      }

      @Override
      public Iterable<String> functionDependencies(org.jclouds.scriptbuilder.domain.OsFamily family) {
         return ImmutableSet.of();
      }

      @Override
      public String render(org.jclouds.scriptbuilder.domain.OsFamily family) {
         StringBuilder builder = new StringBuilder().append("apt-get -d -y install");
         if (family == org.jclouds.scriptbuilder.domain.OsFamily.UNIX) {
            for (String pakage : packages) {
               builder.append(" ").append(pakage);
            }
            return builder.toString();
         }
         throw new UnsupportedOperationException();
      }
   }

   @Override
   public ExecResponse process(NodeMetadata node, List<String> packages) {
      return ctx.getComputeService().runScriptOnNode(node.getId(), new AptCacheStatement(packages));
   }

   @Override
   public Type type() {
      return Type.CACHER;
   }

   @Override
   public String name() {
      return "apt";
   }
}
