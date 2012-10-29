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

import static com.google.common.base.Predicates.in;
import static com.google.common.collect.Iterables.any;
import static com.google.common.collect.Lists.partition;
import static org.jclouds.compute.reference.ComputeServiceConstants.COMPUTE_LOGGER;
import static org.jclouds.virtualbox.settings.KeyboardScancodes.SPECIAL_KEYBOARD_BUTTON_MAP_LIST;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.inject.Named;

import org.jclouds.logging.Logger;
import org.virtualbox_4_1.ISession;

import com.google.common.base.Function;
import com.google.common.util.concurrent.Uninterruptibles;

class SendScancodes implements Function<ISession, Void> {

   @Resource
   @Named(COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;

   private static final int MAX_SIZE = 30;

   private final List<Integer> scancodes;

   public SendScancodes(List<Integer> scancodes) {
      this.scancodes = scancodes;
   }

   @Override
   public Void apply(ISession iSession) {
      for (List<Integer> maxOrLess : partition(scancodes, MAX_SIZE)) {
         long codesSent = iSession.getConsole().getKeyboard().putScancodes(maxOrLess);
         logger.debug("List of scancodes sent: ", maxOrLess);
         assert (codesSent == maxOrLess.size());
         if (any(maxOrLess, in(SPECIAL_KEYBOARD_BUTTON_MAP_LIST.values()))) {
            Uninterruptibles.sleepUninterruptibly(300, TimeUnit.MILLISECONDS);
         } else {
            Uninterruptibles.sleepUninterruptibly(50, TimeUnit.MILLISECONDS);
         }
      }
      return null;
   }

   @Override
   public String toString() {
      return "sendScancodes(" + scancodes + ")";
   }
}
