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
package org.jclouds.savvis.vpdc.xml;

import static org.jclouds.savvis.vpdc.util.Utils.cleanseAttributes;
import static org.jclouds.savvis.vpdc.util.Utils.currentOrNull;
import static org.jclouds.savvis.vpdc.util.Utils.newResource;
import static org.jclouds.util.SaxUtils.equalsOrSuffix;

import java.util.Map;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.savvis.vpdc.domain.Resource;
import org.jclouds.savvis.vpdc.domain.VDC;
import org.jclouds.savvis.vpdc.domain.VDC.Status;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.common.collect.ImmutableMap;

/**
 * @author Adrian Cole
 */
public class VDCHandler extends ParseSax.HandlerWithResult<VDC> {

   protected StringBuilder currentText = new StringBuilder();

   protected VDC.Builder builder = VDC.builder();

   public VDC getResult() {
      try {
         return builder.build();
      } finally {
         builder = VDC.builder();
      }
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attrs) throws SAXException {
      Map<String, String> attributes = cleanseAttributes(attrs);
      if (equalsOrSuffix(qName, "Vdc")) {
         // savvis doesn't add href in the header for some reason
         if (!attributes.containsKey("href") && getRequest() != null)
            attributes = ImmutableMap.<String, String> builder().putAll(attributes)
                  .put("href", getRequest().getEndpoint().toASCIIString()).build();
         Resource vDC = newResource(attributes);
         builder.name(vDC.getName()).type(vDC.getType()).id(vDC.getId()).href(vDC.getHref());
      } else if (equalsOrSuffix(qName, "Network")) {
         builder.availableNetwork(newResource(attributes));
      } else if (equalsOrSuffix(qName, "ResourceEntity")) {
         builder.resourceEntity(newResource(attributes));
      }
   }

   public void endElement(String uri, String name, String qName) {
      if (equalsOrSuffix(qName, "Description")) {
         builder.description(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "OfferingTag")) {
         builder.status(Status.fromValue(currentOrNull(currentText)));
      }
      currentText = new StringBuilder();
   }

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }
}
