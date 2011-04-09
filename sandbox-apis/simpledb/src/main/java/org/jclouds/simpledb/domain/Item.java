/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.simpledb.domain;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

/**
 * Defines the mapping of Items
 * 
 * @author Luís A. Bastião Silva <bastiao@ua.pt>
 */
public class Item {

   private final Multimap<String, AttributePair> attributes = LinkedHashMultimap
            .create();

   public Item() {
   }

   /**
    * Creates a map of Attribute Pair
    * 
    */
   public Item(Multimap<String, AttributePair> attributes)
   {
   
	   this.attributes.putAll(attributes);
	   
   }
   
   public Item addAttributePair(@Nullable String itemName,
            AttributePair attrPair) 
   {
      this.attributes.put(itemName, attrPair);
      return this;
   }

   public Multimap<String, AttributePair> getAttributes()
   {
      return ImmutableMultimap.<String, AttributePair> builder().putAll(
    		  attributes).build();
   }
}
