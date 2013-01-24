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

package org.jclouds.virtualbox.functions;

import com.google.common.base.Function;
import org.virtualbox_4_2.DeviceType;
import org.virtualbox_4_2.IMachine;

import java.util.Map;

/**
 * @author Andrea Turli
 */
public class ApplyBootOrderToMachine implements Function<IMachine, Void> {


   private Map<Long, DeviceType> positionAndDeviceType;

   public ApplyBootOrderToMachine(Map<Long, DeviceType> positionAndDeviceType) {
      this.positionAndDeviceType = positionAndDeviceType;
   }

   @Override
   public Void apply(IMachine machine) {
      for(long position : positionAndDeviceType.keySet()) {
         machine.setBootOrder(position, positionAndDeviceType.get(position));
      }
      machine.saveSettings();
      return null;
   }
}
