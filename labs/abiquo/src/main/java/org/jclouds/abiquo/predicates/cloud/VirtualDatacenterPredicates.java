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
import static com.google.common.collect.Iterables.transform;

import java.util.Arrays;
import java.util.List;

import org.jclouds.abiquo.domain.cloud.Conversion;
import org.jclouds.abiquo.domain.cloud.VirtualDatacenter;
import org.jclouds.abiquo.domain.cloud.VirtualMachineTemplate;
import org.jclouds.abiquo.domain.infrastructure.Datacenter;
import org.jclouds.abiquo.reference.ValidationErrors;
import org.jclouds.abiquo.reference.rest.ParentLinkName;

import com.abiquo.model.enumerator.ConversionState;
import com.abiquo.model.enumerator.HypervisorType;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;

/**
 * Container for {@link VirtualDatacenter} filters.
 * 
 * @author Ignasi Barrera
 */
public class VirtualDatacenterPredicates {
   public static Predicate<VirtualDatacenter> name(final String... names) {
      checkNotNull(names, "names must be defined");

      return new Predicate<VirtualDatacenter>() {
         @Override
         public boolean apply(final VirtualDatacenter virtualDatacenter) {
            return Arrays.asList(names).contains(virtualDatacenter.getName());
         }
      };
   }

   public static Predicate<VirtualDatacenter> type(final HypervisorType... types) {
      checkNotNull(types, "types must be defined");

      return new Predicate<VirtualDatacenter>() {
         @Override
         public boolean apply(final VirtualDatacenter virtualDatacenter) {
            return Arrays.asList(types).contains(virtualDatacenter.getHypervisorType());
         }
      };
   }

   public static Predicate<VirtualDatacenter> datacenter(final Datacenter... datacenters) {
      checkNotNull(datacenters, "datacenters must be defined");

      final List<Integer> ids = Lists.newArrayList(transform(Arrays.asList(datacenters),
            new Function<Datacenter, Integer>() {
               @Override
               public Integer apply(final Datacenter input) {
                  return input.getId();
               }
            }));

      return new Predicate<VirtualDatacenter>() {
         @Override
         public boolean apply(final VirtualDatacenter virtualDatacenter) {
            // Avoid using the getDatacenter() method since it will generate an
            // unnecessary API
            // call. We can get the ID from the datacenter link.
            Integer datacenterId = checkNotNull(virtualDatacenter.unwrap().getIdFromLink(ParentLinkName.DATACENTER),
                  ValidationErrors.MISSING_REQUIRED_LINK);

            return ids.contains(datacenterId);
         }
      };
   }

   /**
    * Check if the given template type is compatible with the given virtual
    * datacenter type taking into account the conversions of the template.
    * 
    * @param template
    *           The template to check.
    * @return Predicate to check if the template or its conversions are
    *         compatibles with the given virtual datacenter.
    */
   public static Predicate<VirtualDatacenter> compatibleWithTemplateOrConversions(final VirtualMachineTemplate template) {
      return new Predicate<VirtualDatacenter>() {
         @Override
         public boolean apply(final VirtualDatacenter vdc) {
            HypervisorType type = vdc.getHypervisorType();
            boolean compatible = type.isCompatible(template.getDiskFormatType());
            if (!compatible) {
               List<Conversion> compatibleConversions = template.listConversions(type, ConversionState.FINISHED);
               compatible = compatibleConversions != null && !compatibleConversions.isEmpty();
            }
            return compatible;
         }
      };
   }
}
