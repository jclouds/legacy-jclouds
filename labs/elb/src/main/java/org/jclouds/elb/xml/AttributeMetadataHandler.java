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

import org.jclouds.elb.domain.AttributeMetadata;
import org.jclouds.elb.domain.AttributeMetadata.Cardinality;
import org.jclouds.http.functions.ParseSax;
import org.xml.sax.SAXException;

/**
 * @see <a
 *      href="http://docs.amazonwebservices.com/ElasticLoadBalancing/latest/APIReference/API_PolicyAttributeTypeDescription.html"
 *      >xml</a>
 * 
 * @author Adrian Cole
 */
public class AttributeMetadataHandler extends ParseSax.HandlerForGeneratedRequestWithResult<AttributeMetadata<?>> {

   private StringBuilder currentText = new StringBuilder();
   @SuppressWarnings("rawtypes")
   private AttributeMetadata.Builder builder = AttributeMetadata.builder();
   @SuppressWarnings("rawtypes")
   private Class currentType = String.class;

   /**
    * {@inheritDoc}
    */
   @Override
   public AttributeMetadata<?> getResult() {
      try {
         return builder.build();
      } finally {
         builder = AttributeMetadata.builder();
      }
   }

   /**
    * {@inheritDoc}
    */
   @SuppressWarnings("unchecked")
   @Override
   public void endElement(String uri, String name, String qName) throws SAXException {
      if (equalsOrSuffix(qName, "AttributeName")) {
         builder.name(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "AttributeType")) {
         String rawType = currentOrNull(currentText);
         if ("Long".equals(rawType)) {
            currentType = Long.class;
         } else if ("Boolean".equals(rawType)) {
            currentType = Boolean.class;
         } else {
            currentType = String.class;
         }
         builder.type(currentType);
         builder.rawType(rawType);
      } else if (equalsOrSuffix(qName, "Cardinality")) {
         builder.cardinality(Cardinality.valueOf(currentOrNull(currentText)));
      } else if (equalsOrSuffix(qName, "DefaultValue")) {
         Object value = currentOrNull(currentText);
         if (currentType == Long.class)
            value = Long.valueOf(currentOrNull(currentText));
         else if (currentType == Boolean.class)
            value = Boolean.valueOf(currentOrNull(currentText));
         builder.defaultValue(value);
      } else if (equalsOrSuffix(qName, "UnhealthyThreshold")) {
         builder.description(currentOrNull(currentText));
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
