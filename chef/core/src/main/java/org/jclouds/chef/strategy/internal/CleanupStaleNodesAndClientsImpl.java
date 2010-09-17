/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.chef.strategy.internal;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.and;
import static com.google.common.base.Predicates.notNull;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;
import static org.jclouds.chef.util.ChefUtils.fromOhaiTime;

import java.util.Calendar;
import java.util.Date;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.chef.domain.Node;
import org.jclouds.chef.reference.ChefConstants;
import org.jclouds.chef.strategy.CleanupStaleNodesAndClients;
import org.jclouds.chef.strategy.DeleteAllClientsInList;
import org.jclouds.chef.strategy.DeleteAllNodesInList;
import org.jclouds.chef.strategy.ListNodes;
import org.jclouds.domain.JsonBall;
import org.jclouds.logging.Logger;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

/**
 * 
 * Cleans up nodes and clients who have been hanging around too long.
 * 
 * @author Adrian Cole
 */
@Singleton
public class CleanupStaleNodesAndClientsImpl implements CleanupStaleNodesAndClients {
   @Resource
   @Named(ChefConstants.CHEF_LOGGER)
   protected Logger logger = Logger.NULL;

   private final ListNodes nodeLister;
   private final DeleteAllNodesInList nodeDeleter;
   private final DeleteAllClientsInList clientDeleter;

   @Inject
   public CleanupStaleNodesAndClientsImpl(DeleteAllNodesInList nodeDeleter, DeleteAllClientsInList clientDeleter,
            ListNodes nodeLister) {
      this.nodeLister = checkNotNull(nodeLister, "nodeLister");
      this.nodeDeleter = checkNotNull(nodeDeleter, "nodeDeleter");
      this.clientDeleter = checkNotNull(clientDeleter, "clientDeleter");
   }

   @Override
   public void execute(final String prefix, int secondsStale) {
      final Calendar expired = Calendar.getInstance();
      expired.setTime(new Date());
      expired.add(Calendar.SECOND, -secondsStale);
      Iterable<? extends Node> staleNodes = filter(nodeLister.execute(new Predicate<String>() {

         @Override
         public boolean apply(String input) {
            return input.startsWith(prefix);
         }

      }), and(notNull(), new Predicate<Node>() {
         @Override
         public boolean apply(Node input) {
            JsonBall dateLong = input.getAutomatic().get("ohai_time");
            if (dateLong == null)
               return true;
            Calendar nodeUpdate = Calendar.getInstance();
            nodeUpdate.setTime(fromOhaiTime(dateLong));
            return expired.after(nodeUpdate);
         }

      }));
      Iterable<String> nodeNames = transform(staleNodes, new Function<Node, String>() {

         @Override
         public String apply(Node from) {
            return from.getName();
         }

      });
      nodeDeleter.execute(nodeNames);
      clientDeleter.execute(nodeNames);
   }
}