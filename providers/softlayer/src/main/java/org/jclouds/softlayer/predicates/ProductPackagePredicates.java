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
package org.jclouds.softlayer.predicates;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.softlayer.domain.ProductPackage;

import com.google.common.base.Predicate;

public class ProductPackagePredicates {

   /**
    * Tests if the product package name equals the packageName
    * @param packageName
    * @return true if the name is equal, otherwise false.
    */
   public static Predicate<ProductPackage> named(final String packageName) {
      return new Predicate<ProductPackage>() {
         public boolean apply(ProductPackage productPackage) {
             checkNotNull(productPackage, "productPackage cannot be null");
             return productPackage.getName().equals(packageName);
         }
      };
   }

}
