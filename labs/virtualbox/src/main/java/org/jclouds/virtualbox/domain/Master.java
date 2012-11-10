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

package org.jclouds.virtualbox.domain;

import org.virtualbox_4_2.IMachine;

public class Master {

   private final IMachine machine;
   private final MasterSpec spec;

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private IMachine machine;
      private MasterSpec spec;

      public Builder machine(IMachine machine) {
         this.machine = machine;
         return this;
      }

      public Builder spec(MasterSpec spec) {
         this.spec = spec;
         return this;
      }

      public Master build() {
         return new Master(machine, spec);
      }

   }

   private Master(IMachine machine, MasterSpec spec) {
      super();
      this.machine = machine;
      this.spec = spec;
   }

   public IMachine getMachine() {
      return machine;
   }

   public MasterSpec getSpec() {
      return spec;
   }

}
