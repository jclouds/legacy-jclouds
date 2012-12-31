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

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.io.BaseEncoding.base64;
import static org.jclouds.util.SaxUtils.currentOrNull;
import static org.jclouds.util.SaxUtils.equalsOrSuffix;

import org.jclouds.azure.management.domain.HostedServiceProperties;
import org.jclouds.http.functions.ParseSax;
import org.xml.sax.SAXException;

/**
 * @see <a href="http://msdn.microsoft.com/en-us/library/gg441293" >api</a>
 * @author Adrian Cole
 */
public class HostedServicePropertiesHandler extends
         ParseSax.HandlerForGeneratedRequestWithResult<HostedServiceProperties> {

   protected StringBuilder currentText = new StringBuilder();
   private HostedServiceProperties.Builder<?> builder = HostedServiceProperties.builder();

   /**
    * {@inheritDoc}
    */
   @Override
   public HostedServiceProperties getResult() {
      try {
         return builder.build();
      } finally {
         builder = HostedServiceProperties.builder();
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void endElement(String uri, String name, String qName) throws SAXException {
      if (equalsOrSuffix(qName, "Description")) {
         builder.description(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "Location")) {
         builder.location(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "AffinityGroup")) {
         builder.affinityGroup(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "Label")) {
         builder.label(new String(base64().decode(currentOrNull(currentText)), UTF_8));
      }
      currentText = new StringBuilder();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }

}
