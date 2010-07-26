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
package org.jclouds.ohai;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.chef.ChefClient;
import org.jclouds.chef.domain.Node;
import org.jclouds.domain.JsonBall;
import org.jclouds.logging.Logger;

import com.google.common.base.Supplier;

/**
 * 
 * Updates node with new automatic attributes.
 * 
 * @author Adrian Cole
 */
@Singleton
public class UpdateNode {

   @Resource
   protected Logger logger = Logger.NULL;

   private final ChefClient chef;
   private final Supplier<Map<String, JsonBall>> automaticSupplier;

   @Inject
   public UpdateNode(ChefClient chef, @Named("automatic") Supplier<Map<String, JsonBall>> automaticSupplier) {
      this.chef = checkNotNull(chef, "chef");
      this.automaticSupplier = checkNotNull(automaticSupplier, "automaticSupplier");
   }

   public void updateNode(String nodeName) {
      logger.info("updating node %s", nodeName);
      Node node = chef.getNode(nodeName);
      node.getAutomatic().putAll(automaticSupplier.get());
      chef.updateNode(node);
      logger.debug("done updating node %s", nodeName);
   }
}