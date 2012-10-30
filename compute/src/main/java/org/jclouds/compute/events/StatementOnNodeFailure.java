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
package org.jclouds.compute.events;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.scriptbuilder.domain.Statement;

import com.google.common.annotations.Beta;
import com.google.common.base.Objects.ToStringHelper;

/**
 * A statement that failed execution on a node.
 * 
 * @author Adrian Cole
 */
@Beta
public class StatementOnNodeFailure extends StatementOnNode {

   private final Throwable cause;

   public StatementOnNodeFailure(Statement statement, NodeMetadata node, Throwable cause) {
      super(statement, node);
      this.cause = checkNotNull(cause, "cause");
   }

   public Throwable getCause() {
      return cause;
   }
   
   @Override
   protected ToStringHelper string() {
      return super.string().add("cause", cause.getMessage());
   }
}
