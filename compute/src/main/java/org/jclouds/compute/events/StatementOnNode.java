/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.compute.events;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.scriptbuilder.domain.Statement;

import com.google.common.annotations.Beta;
import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**

 * @author Adrian Cole
 */
@Beta
public class StatementOnNode {
   protected final Statement statement;
   protected final NodeMetadata node;

   public StatementOnNode(Statement statement, NodeMetadata node) {
      this.statement = checkNotNull(statement, "statement");
      this.node = checkNotNull(node, "node");
   }

   public Statement getStatement() {
      return statement;
   }

   public NodeMetadata getNode() {
      return node;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(statement, node);
   }

   @Override
   public boolean equals(Object o) {
      if (o == null)
         return false;
      if (!o.getClass().equals(getClass()))
         return false;
      StatementOnNode that = StatementOnNode.class.cast(o);
      return Objects.equal(this.statement, that.statement) && Objects.equal(this.node, that.node);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this).add("statement", statement).add("node", node.getId());
   }

}
