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

import java.util.NoSuchElementException;

/**
 * Returns the capacity of the ProductItem.
 * @author Jason King
 */
public class CapacityFromProductItem implements Function<ProductItem,Float> {

    /**
     * 
     * @param productItem the productItem to use
     * @return the capacity
     * @throws NoSuchElementException if the capacity is missing
     */
    @Override
    public Float apply(ProductItem productItem) {
        Float result = productItem.getCapacity();
        if(result==null) throw new NoSuchElementException();
        return result;
    }
}
