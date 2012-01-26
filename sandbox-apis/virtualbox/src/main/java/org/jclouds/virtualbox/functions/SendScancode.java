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

import java.util.List;

import org.virtualbox_4_1.ISession;

import com.google.common.base.Function;

class SendScancode implements Function<ISession, Void> {

   private final List<Integer> scancodes;

   public SendScancode(List<Integer> scancodes) {
      this.scancodes = scancodes;
   }

   @Override
   public Void apply(ISession iSession) {
      for (Integer scancode : scancodes) {
         iSession.getConsole().getKeyboard().putScancode(scancode);
      }
      return null;
   }
}