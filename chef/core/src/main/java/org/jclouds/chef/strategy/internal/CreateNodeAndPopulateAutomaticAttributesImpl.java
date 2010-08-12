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

import java.util.Map;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.chef.ChefClient;
import org.jclouds.chef.domain.Node;
import org.jclouds.chef.reference.ChefConstants;
import org.jclouds.chef.strategy.CreateNodeAndPopulateAutomaticAttributes;
import org.jclouds.domain.JsonBall;
import org.jclouds.logging.Logger;
import org.jclouds.ohai.Automatic;

import com.google.common.base.Supplier;

/**
 * 
 * Updates node with new automatic attributes.
 * 
 * @author Adrian Cole
 */
@Singleton
public class CreateNodeAndPopulateAutomaticAttributesImpl implements CreateNodeAndPopulateAutomaticAttributes {

   @Resource
   @Named(ChefConstants.CHEF_LOGGER)
   protected Logger logger = Logger.NULL;

   private final ChefClient chef;
   private final Supplier<Map<String, JsonBall>> automaticSupplier;

   @Inject
   public CreateNodeAndPopulateAutomaticAttributesImpl(ChefClient chef,
         @Automatic Supplier<Map<String, JsonBall>> automaticSupplier) {
      this.chef = checkNotNull(chef, "chef");
      this.automaticSupplier = checkNotNull(automaticSupplier, "automaticSupplier");
   }

   @Override
   public Node execute(Node node) {
      logger.trace("creating node %s", node.getName());
      node.getAutomatic().putAll(automaticSupplier.get());
      chef.createNode(node);
      logger.debug("created node %s", node.getName());
      return node;
   }

   @Override
   public Node execute(String nodeName, Iterable<String> runList) {
      return execute(new Node(nodeName, runList));
   }
}