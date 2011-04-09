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
package org.jclouds.deltacloud.xml;

import java.net.URI;
import java.util.Map;

import org.jclouds.deltacloud.domain.Realm;
import org.jclouds.deltacloud.domain.Realm.State;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.util.SaxUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @author Adrian Cole
 */
public class RealmHandler extends ParseSax.HandlerWithResult<Realm> {
   private StringBuilder currentText = new StringBuilder();

   private URI href;
   private String id;
   private String name;
   private String limit;
   private State state;

   private Realm realm;

   public Realm getResult() {
      return realm;
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attrs) throws SAXException {
      Map<String, String> attributes = SaxUtils.cleanseAttributes(attrs);
      if (attributes.containsKey("href")) {
         this.href = URI.create(attributes.get("href"));
         this.id = attributes.get("id");
      }
   }

   @Override
   public void endElement(String uri, String localName, String qName) throws SAXException {
      if (qName.equalsIgnoreCase("limit")) {
         this.limit = currentText.toString().trim();
         if ("".equals(limit))
            limit = null;
      } else if (qName.equalsIgnoreCase("name")) {
         this.name = currentText.toString().trim();
      } else if (qName.equalsIgnoreCase("state")) {
         this.state = State.fromValue(currentText.toString().trim());
      } else if (qName.equalsIgnoreCase("realm")) {
         this.realm = new Realm(href, id, name, limit, state);
         this.href = null;
         this.id = null;
         this.name = null;
         this.limit = null;
         this.state = null;
      }
      currentText = new StringBuilder();
   }

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }
}
