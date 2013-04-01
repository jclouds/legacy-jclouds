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

package org.jclouds.management;

import com.google.common.base.Optional;
import com.google.common.collect.Sets;
import org.jclouds.management.annotations.ManagedAttribute;
import org.jclouds.management.annotations.ManagedType;

import java.util.Set;

@ManagedType
public class TypeA extends ParentA {

   @ManagedAttribute(description = "The id of type A")
   private final Long id;
   @ManagedAttribute(description = "The name of type A")
   private final String name;
   @ManagedAttribute(description = "Some description")
   private final String description;

   @ManagedAttribute(description = "An Optional attribute")
   private final Optional<String> note = Optional.of("A note.");

   @ManagedAttribute(description = "A collection of B")
   private final Set<TypeB> typeBSet = Sets.newHashSet(new TypeB(1L), new TypeB(2L));

   @ManagedAttribute(description = "Some strings")
   private final Set<String> stringSet = Sets.newHashSet("one", "two");

   public TypeA(Long id, String name, String description) {
      this.id = id;
      this.name = name;
      this.description = description;
   }

   public Long getId() {
      return id;
   }

   public String getName() {
      return name;
   }

   public String getDescription() {
      return description;
   }

   public Optional<String> getNote() {
      return note;
   }
}
