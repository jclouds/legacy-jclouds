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
package org.jclouds.cim;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import org.jclouds.cim.ResourceAllocationSettingData.ResourceType;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;

/**
 * 
 * @author Adrian Cole
 */
public class CIMPredicates {

   /**
    * Return resource allocations of the specific type.
    * 
    * @param type
    *           type to match the items
    * @return predicate
    */
   public static Predicate<ResourceAllocationSettingData> resourceTypeIn(final ResourceType... types) {
      checkNotNull(types, "resourceTypes");
      final Set<ResourceType> resourceTypes = ImmutableSet.copyOf(types);
      return new Predicate<ResourceAllocationSettingData>() {
         @Override
         public boolean apply(ResourceAllocationSettingData in) {
            return resourceTypes.contains(in.getResourceType());
         }

         @Override
         public String toString() {
            return "resourceTypeIn(" + resourceTypes + ")";
         }
      };
   }
}
