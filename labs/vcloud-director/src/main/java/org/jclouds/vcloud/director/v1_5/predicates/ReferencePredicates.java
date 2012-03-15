/*
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
package org.jclouds.vcloud.director.v1_5.predicates;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;
import org.jclouds.vcloud.director.v1_5.domain.Link;
import org.jclouds.vcloud.director.v1_5.domain.Reference;

import com.google.common.base.Predicate;

/**
 * Predicates handy when working with Reference Types
 * 
 * @author Adrian Cole
 */

public class ReferencePredicates {

   /**
    * matches references of the given name
    * 
    * @param <T>
    *           type of the Reference, ex. {@link Link}
    * @param name
    *           ex. {@code context.getApi().getCurrentSession().getOrg()}
    * @return predicate that will match references of the given name
    */
   public static <T extends Reference> Predicate<T> nameEquals(final String name) {
      checkNotNull(name, "name must be defined");

      return new Predicate<T>() {
         @Override
         public boolean apply(T reference) {
            return name.equals(reference.getName());
         }

         @Override
         public String toString() {
            return "nameEquals(" + name + ")";
         }
      };
   }

   /**
    * matches references of the given type
    * 
    * @param <T>
    *           type of the Reference, ex. {@link Link}
    * @param type
    *           ex. {@link VCloudDirectorMediaType#CATALOG}
    * @return predicate that will match references of the given type
    * @see VCloudDirectorMediaType
    */
   public static <T extends Reference> Predicate<T> typeEquals(final String type) {
      checkNotNull(type, "type must be defined");

      return new Predicate<T>() {
         @Override
         public boolean apply(T reference) {
            return type.equals(reference.getType());
         }

         @Override
         public String toString() {
            return "typeEquals(" + type + ")";
         }
      };
   }
}
