/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.ec2.xml;

import static org.jclouds.util.SaxUtils.currentOrNull;
import static org.jclouds.util.SaxUtils.equalsOrSuffix;

import org.jclouds.ec2.domain.Tag;
import org.jclouds.http.functions.ParseSax;
import org.xml.sax.SAXException;

import com.google.common.base.Strings;

/**
 * @see <a
 *      href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-DescribeTags.html"
 *      >xml</a>
 * 
 * @author Adrian Cole
 */
public class TagHandler extends ParseSax.HandlerForGeneratedRequestWithResult<Tag> {

   private StringBuilder currentText = new StringBuilder();
   private Tag.Builder builder = Tag.builder();

   /**
    * {@inheritDoc}
    */
   @Override
   public Tag getResult() {
      try {
         return builder.build();
      } finally {
         builder = Tag.builder();
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void endElement(String uri, String name, String qName) throws SAXException {
      if (equalsOrSuffix(qName, "resourceId")) {
         builder.resourceId(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "resourceType")) {
         builder.resourceType(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "key")) {
         builder.key(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "value")) {
         // empty is same as not present
         builder.value(Strings.emptyToNull(currentOrNull(currentText)));
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
