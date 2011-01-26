/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.vdc/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.vcloud.xml.ovf;

//import static org.jclouds.vcloud.util.Utils.cleanseAttributes;

import javax.inject.Inject;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.vcloud.domain.ovf.OvfEnvelope;
import org.jclouds.vcloud.domain.ovf.VirtualSystem;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @author Adrian Cole
 */
public class OvfEnvelopeHandler extends ParseSax.HandlerWithResult<OvfEnvelope> {

   protected final VirtualSystemHandler virtualSystemHandler;

   @Inject
   public OvfEnvelopeHandler(VirtualSystemHandler virtualSystemHandler) {
      this.virtualSystemHandler = virtualSystemHandler;
   }

   protected StringBuilder currentText = new StringBuilder();

   private VirtualSystem virtualSystem;

   private boolean inVirtualSystem;

   public OvfEnvelope getResult() {
      return new OvfEnvelope(virtualSystem);
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attrs) throws SAXException {
//      Map<String, String> attributes = cleanseAttributes(attrs);
      if (qName.endsWith("VirtualSystem")) {
         inVirtualSystem = true;
      }
      if (inVirtualSystem) {
         virtualSystemHandler.startElement(uri, localName, qName, attrs);
      }
   }

   public void endElement(String uri, String name, String qName) {
      if (qName.endsWith("VirtualSystem")) {
         inVirtualSystem = false;
         virtualSystem = virtualSystemHandler.getResult();
      }
      if (inVirtualSystem) {
         virtualSystemHandler.endElement(uri, name, qName);
      }
      currentText = new StringBuilder();
   }

   public void characters(char ch[], int start, int length) {
      if (inVirtualSystem)
         virtualSystemHandler.characters(ch, start, length);
      else
         currentText.append(ch, start, length);
   }

   protected String currentOrNull() {
      String returnVal = currentText.toString().trim();
      return returnVal.equals("") ? null : returnVal;
   }
}
