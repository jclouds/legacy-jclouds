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
package org.jclouds.azure.management.xml;

import static org.jclouds.util.SaxUtils.currentOrNull;
import static org.jclouds.util.SaxUtils.equalsOrSuffix;

import java.net.URI;

import javax.inject.Inject;

import org.jclouds.azure.management.domain.HostedService;
import org.jclouds.azure.management.domain.HostedService.Builder;
import org.jclouds.http.functions.ParseSax;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @see <a href="http://msdn.microsoft.com/en-us/library/gg441293" >api</a>
 * @author Adrian Cole
 */
public class HostedServiceHandler extends ParseSax.HandlerForGeneratedRequestWithResult<HostedService> {

   protected final HostedServicePropertiesHandler hostedServicePropertiesHandler;

   @Inject
   protected HostedServiceHandler(HostedServicePropertiesHandler hostedServicePropertiesHandler) {
      this.hostedServicePropertiesHandler = hostedServicePropertiesHandler;
   }

   protected StringBuilder currentText = new StringBuilder();
   protected HostedService.Builder<?> builder = builder();

   protected Builder<?> builder() {
      return HostedService.builder();
   }

   protected boolean inHostedServiceProperties;

   /**
    * {@inheritDoc}
    */
   @Override
   public HostedService getResult() {
      try {
         return builder.build();
      } finally {
         builder = builder();
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void startElement(String url, String name, String qName, Attributes attributes) throws SAXException {
      if (equalsOrSuffix(qName, "HostedServiceProperties")) {
         inHostedServiceProperties = true;
      }
      if (inHostedServiceProperties) {
         hostedServicePropertiesHandler.startElement(url, name, qName, attributes);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void endElement(String uri, String name, String qName) throws SAXException {

      if (equalsOrSuffix(qName, "HostedServiceProperties")) {
         builder.properties(hostedServicePropertiesHandler.getResult());
         inHostedServiceProperties = false;
      } else if (inHostedServiceProperties) {
         hostedServicePropertiesHandler.endElement(uri, name, qName);
      } else if (equalsOrSuffix(qName, "Url")) {
         builder.url(URI.create(currentOrNull(currentText)));
      } else if (equalsOrSuffix(qName, "ServiceName")) {
         builder.name(currentOrNull(currentText));
      }
      currentText = new StringBuilder();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void characters(char ch[], int start, int length) {
      if (inHostedServiceProperties) {
         hostedServicePropertiesHandler.characters(ch, start, length);
      } else {
         currentText.append(ch, start, length);
      }
   }

}
