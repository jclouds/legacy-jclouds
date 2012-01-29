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

import javax.annotation.Resource;
import javax.inject.Named;

import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.virtualbox_4_1.ISession;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

class SendScancode implements Function<ISession, Void> {

   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private static final int MAX_SIZE = 30;
   private final List<Integer> scancodes;

   public SendScancode(List<Integer> scancodes) {
      this.scancodes = scancodes;
   }

   @Override
   public Void apply(ISession iSession) {
      for (List<Integer> maxOrLess : Lists.partition(scancodes, MAX_SIZE)) {
         long codeStores = iSession.getConsole().getKeyboard().putScancodes(maxOrLess);
         logger.debug("List of scancodes sent: ", maxOrLess);
         assert (codeStores == maxOrLess.size());
         // TODO @Adrian if maxOrLess contains SPECIAL CHAR sleep should be higher than NORMAL CHAR (300 ms - 50 ms)
//         if (Iterables.any(maxOrLess, Predicates.in(SPECIAL_KEYBOARD_BUTTON_MAP_LIST.values()))) {
//            try {
//               Thread.sleep(300);
//            } catch (InterruptedException e) {
//               logger.debug("There was a problem in sleeping this thread", e);
//            }
//         }
         // TODO without extra check the extra time needed is more or less 250 ms
         try {
            Thread.sleep(250);
         } catch (InterruptedException e) {
            logger.debug("There was a problem in sleeping this thread", e);
         }
      }
      return null;
   }
}