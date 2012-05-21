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
package org.jclouds.util;

import com.google.common.collect.ImmutableSet;

/**
 * General utilities used in jclouds code for {@link Iterable Iterables}.
 * 
 * @author danikov
 */
public class Iterables2 {

   /**
    * Copies the contents of a wildcarded {@link Iterable} into a concrete {@link Iterable} of the left bound
    * 
    * @param unboundedValues wildcarded source {@link Iterable}
    * @return concrete-typed copy of the source
    */
   public static <T> Iterable<T> concreteCopy(Iterable<? extends T> unboundedValues) {
      // Please do not attempt to sort, as this is wasteful
      return ImmutableSet.copyOf(unboundedValues);
   }
   
}
