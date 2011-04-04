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

package org.jclouds.deltacloud.xml;

import java.util.Set;

import javax.inject.Inject;

import org.jclouds.deltacloud.domain.Instance;
import org.jclouds.http.functions.ParseSax;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.common.collect.Sets;

/**
 * @author Adrian Cole
 */
public class InstancesHandler extends ParseSax.HandlerWithResult<Set<? extends Instance>> {
   private StringBuilder currentText = new StringBuilder();

   private Set<Instance> instances = Sets.newLinkedHashSet();
   private final InstanceHandler instanceHandler;

   @Inject
   public InstancesHandler(InstanceHandler locationHandler) {
      this.instanceHandler = locationHandler;
   }

   public Set<? extends Instance> getResult() {
      return instances;
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
      instanceHandler.startElement(uri, localName, qName, attributes);
   }

   @Override
   public void endElement(String uri, String localName, String qName) throws SAXException {
      instanceHandler.endElement(uri, localName, qName);
      if (qName.equals("instance") && currentText.toString().trim().equals("")) {
         this.instances.add(instanceHandler.getResult());
      }
      currentText = new StringBuilder();
   }

   public void characters(char ch[], int start, int length) {
      instanceHandler.characters(ch, start, length);
      currentText.append(ch, start, length);
   }
}
