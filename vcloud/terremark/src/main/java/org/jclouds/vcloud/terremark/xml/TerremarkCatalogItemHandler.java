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

package org.jclouds.vcloud.terremark.xml;

import static org.jclouds.vcloud.util.Utils.newNamedResource;

import org.jclouds.vcloud.domain.NamedResource;
import org.jclouds.vcloud.terremark.domain.TerremarkCatalogItem;
import org.jclouds.vcloud.terremark.domain.internal.TerremarkCatalogItemImpl;
import org.jclouds.vcloud.xml.CatalogItemHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @author Adrian Cole
 */
public class TerremarkCatalogItemHandler extends CatalogItemHandler {

   private NamedResource customizationOptions;
   private NamedResource computeOptions;

   public TerremarkCatalogItem getResult() {
      return new TerremarkCatalogItemImpl(catalogItem.getId(), catalogItem.getName(), catalogItem.getLocation(),
            description, computeOptions, customizationOptions, entity, properties);
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
      super.startElement(uri, localName, qName, attributes);
      if (qName.equals("Link")) {
         int nameIndex = attributes.getIndex("name");
         if (nameIndex != -1) {
            if (attributes.getValue(nameIndex).equals("Customization Options")) {
               customizationOptions = newNamedResource(attributes);
            } else if (attributes.getValue(nameIndex).equals("Compute Options")) {
               computeOptions = newNamedResource(attributes);
            }
         }

      }
   }
}
