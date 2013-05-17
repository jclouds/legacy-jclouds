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
package org.jclouds.rackspace.cloudloadbalancers.v1.domain;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.internal.BaseNode;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.internal.BaseNode.Condition;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.internal.BaseNode.Type;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Used to update Nodes.
 * 
 * @author Everett Toews
 */
public class UpdateNode {
   private final Condition condition;
   private final Type type;
   private final Integer weight;
   
   protected UpdateNode(@Nullable Condition condition, @Nullable Type type, @Nullable Integer weight) {
      this.condition = condition;
      this.type = type;
      this.weight = weight;
   }

   public Condition getCondition() {
      return condition;
   }

   public Type getType() {
      return type;
   }

   public Integer getWeight() {
      return weight;
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

      UpdateNode that = UpdateNode.class.cast(obj);
      return Objects.equal(this.condition, that.condition)
            && Objects.equal(this.type, that.type)
            && Objects.equal(this.weight, that.weight);
   }

   public static class Builder {
      private Condition condition;
      private Type type;
      private Integer weight;

      /**
       * @see BaseNode.Builder#condition(Condition)
       */
      public Builder condition(Condition condition) {
         this.condition = condition;
         return this;
      }

      /**
       * @see BaseNode.Builder#type(Type)
       */
      public Builder type(Type type) {
         this.type = type;
         return this;
      }

      /**
       * @see BaseNode.Builder#weight(Integer)
       */
      public Builder weight(Integer weight) {
         this.weight = weight;
         return this;
      }
      
      public UpdateNode build() {
         return new UpdateNode(condition, type, weight);
      }

      public Builder from(UpdateNode in) {
         return this.condition(in.getCondition()).type(in.getType()).weight(in.getWeight());
      }
   }

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().from(this);
   }
}
