/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.ibmdev.functions;

import java.util.Set;

import org.jclouds.ibmdev.domain.Address;
import org.jclouds.ibmdev.domain.Image;
import org.jclouds.ibmdev.domain.Instance;
import org.jclouds.ibmdev.domain.Volume;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * 
 * @author Adrian Cole
 */
public class ParseUtils {

   public static final Predicate<Address> CLEAN_ADDRESS = new Predicate<Address>() {
      @Override
      public boolean apply(Address input) {
         if ("".equals(input.getIp()))
            input.setIp(null);
         return true;
      }
   };

   private static final Set<String> emptyString = ImmutableSet.of("");

   public static final Predicate<Volume> CLEAN_VOLUME = new Predicate<Volume>() {
      @Override
      public boolean apply(Volume input) {
         if (new Long(0).equals(input.getInstanceId()))
            input.setInstanceId(null);
         if (emptyString.equals(input.getProductCodes()))
            input.getProductCodes().clear();
         return true;
      }
   };

   public static final Predicate<Image> CLEAN_IMAGE = new Predicate<Image>() {
      @Override
      public boolean apply(Image input) {
         if (emptyString.equals(input.getProductCodes()))
            input.getProductCodes().clear();
         return true;
      }
   };

   public static final Predicate<Instance> CLEAN_INSTANCE = new Predicate<Instance>() {
      @Override
      public boolean apply(Instance input) {
         if (emptyString.equals(input.getProductCodes()))
            input.getProductCodes().clear();
         return true;
      }
   };

   public static <T> Set<T> clean(Iterable<T> elements, Predicate<T> cleaner) {
      return Sets.newLinkedHashSet(Iterables.filter(elements, cleaner));
   }
}