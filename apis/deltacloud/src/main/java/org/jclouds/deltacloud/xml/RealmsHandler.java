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

import java.util.Set;

import javax.inject.Inject;

import org.jclouds.deltacloud.domain.Realm;
import org.jclouds.http.functions.ParseSax;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.common.collect.Sets;

/**
 * @author Adrian Cole
 */
public class RealmsHandler extends ParseSax.HandlerWithResult<Set<? extends Realm>> {
   private StringBuilder currentText = new StringBuilder();

   private Set<Realm> realms = Sets.newLinkedHashSet();
   private final RealmHandler realmHandler;

   @Inject
   public RealmsHandler(RealmHandler realmHandler) {
      this.realmHandler = realmHandler;
   }

   public Set<? extends Realm> getResult() {
      return realms;
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
      realmHandler.startElement(uri, localName, qName, attributes);
   }

   @Override
   public void endElement(String uri, String localName, String qName) throws SAXException {
      realmHandler.endElement(uri, localName, qName);
      if (qName.equals("realm") && currentText.toString().trim().equals("")) {
         this.realms.add(realmHandler.getResult());
      }
      currentText = new StringBuilder();
   }

   public void characters(char ch[], int start, int length) {
      realmHandler.characters(ch, start, length);
      currentText.append(ch, start, length);
   }
}
