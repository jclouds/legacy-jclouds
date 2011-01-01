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

package org.jclouds.aws.simpledb.xml;

import com.google.common.collect.LinkedHashMultimap;
import java.util.Map;

import org.jclouds.aws.simpledb.domain.AttributePair;
import org.jclouds.date.DateService;
import org.jclouds.http.functions.ParseSax;

import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import org.jclouds.aws.simpledb.domain.Item;
import org.xml.sax.Attributes;

/**
 *
 * @author Luís A. Bastião Silva <bastiao@ua.pt>
 */
public class ItemsHandler extends
         ParseSax.HandlerWithResult<Map<String, Item>> {
   private StringBuilder currentText = new StringBuilder();

   private Map<String, Item> items = Maps.newConcurrentMap();
   private Multimap<String, AttributePair> attributes =  LinkedHashMultimap.create();
   private String attributeName;
   private String attributeValue = "";
   private String itemName;

   private boolean inside = false;

   protected final DateService dateService;

   @Inject 
   public ItemsHandler(DateService dateService) {
	   this.dateService = dateService;
   }

   public Map<String, Item> getResult() {
      return items;
   }

   public void startElement(String uri, String localName, String qName, Attributes attributes)
   {
       if (qName.equals("Attribute")) {
               inside = true;
       }
   }

   public void endElement(String uri, String name, String qName)
   {
       if (qName.equals("Attribute"))
       {
               inside = false;

               
               System.out.println("AttributeName: " + attributeName);
               System.out.println("AttributeValue: " + attributeValue);
       }
       else if(qName.equals("Name")) {
               if (inside)
                       attributeName = currentText.toString().trim();
               else
                       itemName = currentText.toString().trim();
			   
      } else if (qName.equals("Value"))
      {
    	  attributeValue = currentText.toString().trim();

          attributes.put(attributeName,new AttributePair(attributeName,
                  attributeValue, false));
      } 
      else if (qName.equals("Item")) 
      {
          System.out.println("ItemName: " + itemName);
          
          Item item = new Item(attributes);
          items.put(itemName, item);
          attributes =  LinkedHashMultimap.create();
          this.attributeName = null;
          this.attributeValue = null;
          this.itemName = null;
          inside = false;
      }
      currentText = new StringBuilder();
   }

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }
}
