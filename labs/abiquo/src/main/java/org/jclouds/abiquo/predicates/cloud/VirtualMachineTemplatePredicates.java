/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.abiquo.predicates.cloud;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Arrays;

import org.jclouds.abiquo.domain.cloud.VirtualMachineTemplate;

import com.abiquo.model.enumerator.DiskFormatType;
import com.abiquo.model.enumerator.HypervisorType;
import com.google.common.base.Predicate;

/**
 * Container for {@link VirtualMachineTemplate} filters.
 * 
 * @author Francesc Montserrat
 */
public class VirtualMachineTemplatePredicates {
   public static Predicate<VirtualMachineTemplate> id(final Integer... ids) {
      checkNotNull(ids, "ids must be defined");

      return new Predicate<VirtualMachineTemplate>() {
         @Override
         public boolean apply(final VirtualMachineTemplate template) {
            return Arrays.asList(ids).contains(template.getId());
         }
      };
   }

   public static Predicate<VirtualMachineTemplate> name(final String... names) {
      checkNotNull(names, "names must be defined");

      return new Predicate<VirtualMachineTemplate>() {
         @Override
         public boolean apply(final VirtualMachineTemplate template) {
            return Arrays.asList(names).contains(template.getName());
         }
      };
   }

   public static Predicate<VirtualMachineTemplate> diskFormat(final DiskFormatType... formats) {
      checkNotNull(formats, "formats must be defined");

      return new Predicate<VirtualMachineTemplate>() {
         @Override
         public boolean apply(final VirtualMachineTemplate template) {
            return Arrays.asList(formats).contains(template.getDiskFormatType());
         }
      };
   }

   public static Predicate<VirtualMachineTemplate> compatible(final HypervisorType type) {
      checkNotNull(type, "type must be defined");

      return new Predicate<VirtualMachineTemplate>() {
         @Override
         public boolean apply(final VirtualMachineTemplate template) {
            return type.isCompatible(template.getDiskFormatType());
         }
      };
   }

   public static Predicate<VirtualMachineTemplate> isShared() {
      return new Predicate<VirtualMachineTemplate>() {
         @Override
         public boolean apply(final VirtualMachineTemplate input) {
            return input.unwrap().isShared();
         }
      };
   }

   public static Predicate<VirtualMachineTemplate> isInstance() {
      return new Predicate<VirtualMachineTemplate>() {
         @Override
         public boolean apply(final VirtualMachineTemplate input) {
            return input.unwrap().searchLink("master") != null;
         }
      };
   }
}
