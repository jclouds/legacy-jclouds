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

import java.util.Set;

import org.jclouds.azure.management.domain.HostedServiceWithDetailedProperties;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.util.SaxUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.inject.Inject;

/**
 * @see <a href="http://msdn.microsoft.com/en-us/library/ee460781">doc</a>
 * 
 * @author Adrian Cole
 */
public class ListHostedServicesHandler extends
         ParseSax.HandlerForGeneratedRequestWithResult<Set<HostedServiceWithDetailedProperties>> {

   private final HostedServiceWithDetailedPropertiesHandler hostedServiceHandler;

   private Builder<HostedServiceWithDetailedProperties> hostedServices = ImmutableSet
            .<HostedServiceWithDetailedProperties> builder();

   private boolean inHostedService;

   @Inject
   public ListHostedServicesHandler(HostedServiceWithDetailedPropertiesHandler hostedServiceHandler) {
      this.hostedServiceHandler = hostedServiceHandler;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Set<HostedServiceWithDetailedProperties> getResult() {
      return hostedServices.build();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void startElement(String url, String name, String qName, Attributes attributes) throws SAXException {
      if (SaxUtils.equalsOrSuffix(qName, "HostedService")) {
         inHostedService = true;
      }
      if (inHostedService) {
         hostedServiceHandler.startElement(url, name, qName, attributes);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void endElement(String uri, String name, String qName) throws SAXException {
      if (qName.equals("HostedService")) {
         inHostedService = false;
         hostedServices.add(hostedServiceHandler.getResult());
      } else if (inHostedService) {
         hostedServiceHandler.endElement(uri, name, qName);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void characters(char ch[], int start, int length) {
      if (inHostedService) {
         hostedServiceHandler.characters(ch, start, length);
      }
   }

}
