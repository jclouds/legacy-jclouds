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
