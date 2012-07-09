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

import org.jclouds.elb.domain.Policy;
import org.jclouds.http.functions.ParseSax;
import org.xml.sax.SAXException;

import com.google.common.primitives.Ints;

/**
 * @see <a
 *      href="http://docs.amazonwebservices.com/ElasticLoadBalancing/latest/APIReference/API_PolicyDescription.html"
 *      >xml</a>
 * 
 * @author Adrian Cole
 */
public class PolicyHandler extends ParseSax.HandlerForGeneratedRequestWithResult<Policy> {

   private StringBuilder currentText = new StringBuilder();
   private Policy.Builder builder = Policy.builder();
   private String key;

   /**
    * {@inheritDoc}
    */
   @Override
   public Policy getResult() {
      try {
         return builder.build();
      } finally {
         builder = Policy.builder();
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void endElement(String uri, String name, String qName) throws SAXException {
      if (equalsOrSuffix(qName, "PolicyName")) {
         builder.name(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "PolicyTypeName")) {
         builder.typeName(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "AttributeName")) {
         key = currentOrNull(currentText);
      } else if (equalsOrSuffix(qName, "AttributeValue")) {
         String value = currentOrNull(currentText);
         if ("true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value))
            builder.attribute(key, Boolean.valueOf(value));
         else if (Ints.tryParse(value) != null)
            builder.attribute(key, Long.valueOf(value));
         else
            builder.attribute(key, value);
         key = null;
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
