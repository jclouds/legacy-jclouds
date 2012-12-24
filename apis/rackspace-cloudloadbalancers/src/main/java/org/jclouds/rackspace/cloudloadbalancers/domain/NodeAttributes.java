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
package org.jclouds.rackspace.cloudloadbalancers.domain;

import org.jclouds.rackspace.cloudloadbalancers.domain.internal.BaseNode;
import org.jclouds.rackspace.cloudloadbalancers.domain.internal.BaseNode.Condition;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * 
 * @author Dan Lo Bianco
 * @see <a href=
 *      "http://docs.rackspace.com/loadbalancers/api/v1.0/clb-devguide/content/Modify_Nodes-d1e2503.html"
 *      />
 */
public class NodeAttributes {
   protected String condition;
   protected Integer weight;

   public NodeAttributes condition(String condition) {
      this.condition = condition;
      return this;
   }

   public NodeAttributes weight(int weight) {
      this.weight = weight;
      return this;
   }

   public static <T extends BaseNode<T>> NodeAttributes fromNode(T n) {
      return Builder.condition(n.getCondition()).weight(n.getWeight());
   }

   public static class Builder {
      public static NodeAttributes condition(Condition condition) {
         return new NodeAttributes().condition(condition.name());
      }

      public static NodeAttributes weight(int weight) {
         return new NodeAttributes().weight(weight);
      }
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper(this).omitNullValues()
            .add("condition", condition).add("weight", weight);
   }
   
   @Override
   public String toString() {
      return string().toString();
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(condition, weight);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;

      NodeAttributes that = NodeAttributes.class.cast(obj);
      return Objects.equal(this.condition, that.condition)
            && Objects.equal(this.weight, that.weight);
   }
}
