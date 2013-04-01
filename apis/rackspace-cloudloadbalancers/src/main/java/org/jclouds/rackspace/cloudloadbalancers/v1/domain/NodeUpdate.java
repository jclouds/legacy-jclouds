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
package org.jclouds.rackspace.cloudloadbalancers.v1.domain;

import org.jclouds.rackspace.cloudloadbalancers.v1.domain.internal.BaseNode;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.internal.BaseNode.Condition;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.internal.BaseNode.Type;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Used to update Nodes.
 * 
 * @author Dan Lo Bianco
 */
public class NodeUpdate {

   protected Condition condition;
   protected Type type;
   protected Integer weight;

   public NodeUpdate condition(Condition condition) {
      this.condition = condition;
      return this;
   }

   public NodeUpdate type(Type type) {
      this.type = type;
      return this;
   }

   public NodeUpdate weight(Integer weight) {
      this.weight = weight;
      return this;
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this).omitNullValues()
            .add("condition", condition).add("type", type).add("weight", weight);
   }
   
   @Override
   public String toString() {
      return string().toString();
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(condition, type, weight);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;

      NodeUpdate that = NodeUpdate.class.cast(obj);
      return Objects.equal(this.condition, that.condition)
            && Objects.equal(this.type, that.type)
            && Objects.equal(this.weight, that.weight);
   }

   public static class Builder {
      /**
       * @see BaseNode.Builder#condition(Condition)
       */
      public static NodeUpdate condition(Condition condition) {
         return new NodeUpdate().condition(condition);
      }

      /**
       * @see BaseNode.Builder#type(Type)
       */
      public static NodeUpdate type(Type type) {
         return new NodeUpdate().type(type);
      }

      /**
       * @see BaseNode.Builder#weight(Integer)
       */
      public static NodeUpdate weight(Integer weight) {
         return new NodeUpdate().weight(weight);
      }
   }

   public static <T extends BaseNode<T>> NodeUpdate fromNode(T n) {
      return Builder.condition(n.getCondition()).type(n.getType()).weight(n.getWeight());
   }
}
