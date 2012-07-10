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
package org.jclouds.elb.xml;

import static org.jclouds.util.SaxUtils.currentOrNull;
import static org.jclouds.util.SaxUtils.equalsOrSuffix;

import javax.inject.Inject;

import org.jclouds.elb.domain.PolicyType;
import org.jclouds.http.functions.ParseSax;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @see <a
 *      href="http://docs.amazonwebservices.com/ElasticLoadBalancing/latest/APIReference/API_PolicyTypeDescription.html"
 *      >doc</a>
 * 
 * @author Adrian Cole
 */
public class PolicyTypeHandler extends ParseSax.HandlerForGeneratedRequestWithResult<PolicyType> {
   protected final AttributeMetadataHandler attributeTypeHandler;

   @Inject
   protected PolicyTypeHandler(AttributeMetadataHandler attributeTypeHandler) {
      this.attributeTypeHandler = attributeTypeHandler;
   }

   private StringBuilder currentText = new StringBuilder();
   private PolicyType.Builder builder = PolicyType.builder();

   private boolean inAttributeTypes;

   /**
    * {@inheritDoc}
    */
   @Override
   public PolicyType getResult() {
      try {
         return builder.build();
      } finally {
         builder = PolicyType.builder();
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void startElement(String url, String name, String qName, Attributes attributes) throws SAXException {
      if (equalsOrSuffix(qName, "PolicyAttributeTypeDescriptions")) {
         inAttributeTypes = true;
      }
      if (inAttributeTypes) {
         attributeTypeHandler.startElement(url, name, qName, attributes);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void endElement(String uri, String name, String qName) throws SAXException {
      if (equalsOrSuffix(qName, "member")) {
         endMember(uri, name, qName);
      } else if (equalsOrSuffix(qName, "PolicyAttributeTypeDescriptions")) {
         inAttributeTypes = false;
      } else if (equalsOrSuffix(qName, "PolicyTypeName")) {
         builder.name(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "Description")) {
         builder.description(currentOrNull(currentText));
      } else if (inAttributeTypes) {
         attributeTypeHandler.endElement(uri, name, qName);
      }
      currentText = new StringBuilder();
   }

   protected void endMember(String uri, String name, String qName) throws SAXException {
      if (inAttributeTypes) {
         builder.attributeMetadata(attributeTypeHandler.getResult());
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void characters(char ch[], int start, int length) {
      if (inAttributeTypes) {
         attributeTypeHandler.characters(ch, start, length);
      } else {
         currentText.append(ch, start, length);
      }
   }

}
