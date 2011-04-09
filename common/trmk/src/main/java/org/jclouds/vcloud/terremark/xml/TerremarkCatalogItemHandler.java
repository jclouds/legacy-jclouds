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
package org.jclouds.vcloud.terremark.xml;

import static org.jclouds.vcloud.util.Utils.cleanseAttributes;
import static org.jclouds.vcloud.util.Utils.newReferenceType;

import java.util.Map;

import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.terremark.domain.TerremarkCatalogItem;
import org.jclouds.vcloud.terremark.domain.internal.TerremarkCatalogItemImpl;
import org.jclouds.vcloud.xml.CatalogItemHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @author Adrian Cole
 */
public class TerremarkCatalogItemHandler extends CatalogItemHandler {

   private ReferenceType customizationOptions;
   private ReferenceType computeOptions;

   public TerremarkCatalogItem getResult() {
      return new TerremarkCatalogItemImpl(catalogItem.getName(), catalogItem.getHref(), description, computeOptions,
               customizationOptions, entity, properties);
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attrs) throws SAXException {
      Map<String, String> attributes = cleanseAttributes(attrs);
      super.startElement(uri, localName, qName, attrs);
      if (qName.equals("Link")) {
         if (attributes.containsKey("name")) {
            if (attributes.get("name").equals("Customization Options")) {
               customizationOptions = newReferenceType(attributes);
            } else if (attributes.get("name").equals("Compute Options")) {
               computeOptions = newReferenceType(attributes);
            }
         }

      }
   }
}
