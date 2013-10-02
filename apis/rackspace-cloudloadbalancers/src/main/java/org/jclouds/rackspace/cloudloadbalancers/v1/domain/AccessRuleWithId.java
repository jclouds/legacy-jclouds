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

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * The same as {@link AccessRule} but this will have an id as assigned by the Cloud Load Balancers service.
 * 
 * @author Everett Toews
 */
public class AccessRuleWithId extends AccessRule {

   private final int id;

   public AccessRuleWithId(int id, String address, Type type) {
      super(address, type);
      this.id = id;
   }

   public int getId() {
      return this.id;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      AccessRuleWithId that = AccessRuleWithId.class.cast(obj);
      
      return Objects.equal(this.id, that.id);
   }
   
   protected ToStringHelper string() {
      return Objects.toStringHelper(this).omitNullValues()
            .add("id", id).add("address", getAddress()).add("type", getType());
   }
   
   @Override
   public String toString() {
      return string().toString();
   }
}
