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
package org.jclouds.softlayer.predicates;

import com.google.common.base.Predicate;
import org.jclouds.softlayer.domain.ProductPackage;
import org.jclouds.softlayer.features.AccountClient;

import java.util.NoSuchElementException;

public class ProductPackagePredicates {

   /**
    * Attempts to obtain the packageId for the supplied packageName.
    * @param accountClient @see AccountClient
    * @param packageName The name field of the @see ProductPackage to find
    * @return The id of the package or null if no match found
    */
   public static Long getProductPackageId(AccountClient accountClient,String packageName) {
      for (ProductPackage productPackage : accountClient.getActivePackages()) {
         if (named(packageName).apply(productPackage)) return productPackage.getId();
      }
      throw new NoSuchElementException("ProductPackage:"+packageName+" not found");
   }

   /**
    * Tests if the product package name equals the packageName
    * @param packageName
    * @return true if the name is equal, otherwise false.
    */
   public static Predicate named(final String packageName) {
      return new Predicate<ProductPackage>() {
         public boolean apply(ProductPackage productPackage) {
            return productPackage.getName().equals(packageName);
         }
      };
   }

}
