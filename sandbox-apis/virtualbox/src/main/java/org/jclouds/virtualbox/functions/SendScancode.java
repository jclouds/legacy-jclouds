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
import org.jclouds.virtualbox.settings.KeyboardScancodes;
import org.virtualbox_4_1.ISession;

import com.beust.jcommander.internal.Lists;
import com.google.common.base.Function;
import com.google.common.collect.Sets;

class SendScancode implements Function<ISession, Void> {
   
   @Resource
   @Named(ComputeServiceConstants.COMPUTE_LOGGER)
   protected Logger logger = Logger.NULL;


   private static final int MAX_KEYCODES_ACCEPTED = 30;
   private final List<Integer> scancodes;

   public SendScancode(List<Integer> scancodes) {
      this.scancodes = scancodes;
   }

   @Override
   public Void apply(ISession iSession) {
      int i = 0, j = 0, length = scancodes.size();
      if (length > MAX_KEYCODES_ACCEPTED) {
         while (i <= length) {
            j = (i + 30 > length) ? length : i + MAX_KEYCODES_ACCEPTED;
            List<Integer> sublist = Lists.newArrayList(scancodes).subList(i, j);
            long codeStores = iSession.getConsole().getKeyboard().putScancodes(sublist);
            logger.debug("List of scancodes sent: ", sublist);
            assert(codeStores==sublist.size());
            i = i + MAX_KEYCODES_ACCEPTED;
            try {
               Thread.sleep(50);
            } catch (InterruptedException e) {
               logger.debug("There was a problem in sleeping this thread", e);
            }
         }
      } else {
         iSession.getConsole().getKeyboard().putScancodes(scancodes);
         if(Sets.difference(Sets.newHashSet(scancodes), Sets.newHashSet(KeyboardScancodes.SPECIAL_KEYBOARD_BUTTON_MAP_LIST.values())) != null) {
            try {
               Thread.sleep(200);
            } catch (InterruptedException e) {
               logger.debug("There was a problem in sleeping this thread", e);
            }
         }
      }
      return null;
   }
}