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
package org.jclouds.openstack.keystone.v2_0.functions;

import javax.inject.Singleton;

import org.jclouds.internal.ClassMethodArgsAndReturnVal;
import org.jclouds.rest.functions.ImplicitOptionalConverter;

import com.google.common.base.Optional;

/**
 * 
 * @author Adrian Cole
 * 
 */
@Singleton
public class PresentWhenAdminURLExistsForIdentityService implements ImplicitOptionalConverter {

   @Override
   public Optional<Object> apply(ClassMethodArgsAndReturnVal input) {
      //TODO: log and return absent when the admin url for identity service isn't available
      return Optional.of(input.getReturnVal());
   }

   public String toString() {
      return "presentWhenAdminURLExistsForIdentityService()";
   }

}