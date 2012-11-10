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
package org.jclouds.virtualbox.predicates;

import static com.google.common.base.Predicates.and;
import static com.google.common.collect.Iterables.any;
import static com.google.common.collect.Iterables.transform;
import static org.jclouds.virtualbox.predicates.IMediumPredicates.deviceTypeEquals;
import static org.jclouds.virtualbox.predicates.IMediumPredicates.hasParent;
import static org.jclouds.virtualbox.predicates.IMediumPredicates.machineIdsContain;
import static org.jclouds.virtualbox.util.IMediumAttachments.toMedium;
import static org.virtualbox_4_2.DeviceType.HardDisk;

import org.virtualbox_4_2.IMachine;
import org.virtualbox_4_2.IMedium;

import com.google.common.base.Predicate;

/**
 * 
 * @author Adrian Cole
 */
public class IMachinePredicates {
   /**
    * TODO: andrea please explain
    * 
    */
   static enum IsLinkedClone implements Predicate<IMachine> {
      INSTANCE;

      @SuppressWarnings("unchecked")
      @Override
      public boolean apply(IMachine machine) {
         Iterable<IMedium> mediumsOnMachine = transform(machine.getMediumAttachments(), toMedium());
         // TODO: previous impl walked the parent medium back to the child
         // before checking machine ids. Determine if that extra walk was really
         // necessary
         return any(mediumsOnMachine, and(deviceTypeEquals(HardDisk), hasParent(), machineIdsContain(machine.getId())));
      }

      @Override
      public String toString() {
         return "isLinkedClone()";
      }
   }

   public static Predicate<IMachine> isLinkedClone() {
      return IsLinkedClone.INSTANCE;
   }
}
