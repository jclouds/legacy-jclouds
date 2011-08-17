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
package org.jclouds.deltacloud.xml;

import java.net.URI;
import java.util.Map;

import org.jclouds.deltacloud.domain.Image;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.util.SaxUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @author Adrian Cole
 */
public class ImageHandler extends ParseSax.HandlerWithResult<Image> {
   private StringBuilder currentText = new StringBuilder();

   private URI href;
   private String id;
   private String ownerId;
   private String name;
   private String description;
   private String architecture;

   private Image image;

   public Image getResult() {
      return image;
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attrs) throws SAXException {
      Map<String, String> attributes = SaxUtils.cleanseAttributes(attrs);
      if (qName.equals("image")) {
         String href = attributes.get("href");
         if (href != null) {
            this.href = URI.create(href);
         }
         this.id = attributes.get("id");
      }
   }

   @Override
   public void endElement(String uri, String localName, String qName) throws SAXException {
      if (qName.equalsIgnoreCase("owner_id")) {
         this.ownerId = currentText.toString().trim();
      } else if (qName.equalsIgnoreCase("name")) {
         this.name = currentText.toString().trim();
      } else if (qName.equalsIgnoreCase("description")) {
         this.description = currentText.toString().trim();
      } else if (qName.equalsIgnoreCase("architecture")) {
         this.architecture = currentText.toString().trim();
      } else if (qName.equalsIgnoreCase("image")) {
         this.image = new Image(href, id, ownerId, name, description, architecture);
         this.href = null;
         this.id = null;
         this.ownerId = null;
         this.name = null;
         this.description = null;
         this.architecture = null;
      }
      currentText = new StringBuilder();
   }

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }
}
