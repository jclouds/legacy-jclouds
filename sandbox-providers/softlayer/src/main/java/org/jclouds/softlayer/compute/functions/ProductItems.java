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
package org.jclouds.softlayer.compute.functions;

import com.google.common.base.Function;
import org.jclouds.softlayer.domain.ProductItem;

public class ProductItems {

    /**
     * Creates a function to get the capacity from a product item.
     */
    public static Function<ProductItem,Float> capacity() {
        return new Function<ProductItem,Float>() {
            @Override
            public Float apply(ProductItem productItem) {
                return productItem.getCapacity();
            }
        };
    }

    /**
     * Creates a function to get the description from a product item.
     */
    public static Function<ProductItem,String> description() {
        return new Function<ProductItem,String>() {
            @Override
            public String apply(ProductItem productItem) {
                return productItem.getDescription();
            }
        };
    }
}
