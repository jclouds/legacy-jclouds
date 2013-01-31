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
package org.jclouds.route53.xml;

import static org.jclouds.util.SaxUtils.currentOrNull;
import static org.jclouds.util.SaxUtils.equalsOrSuffix;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.route53.domain.RecordSet.Type;
import org.jclouds.route53.domain.RecordSetIterable;
import org.jclouds.route53.domain.RecordSetIterable.Builder;
import org.xml.sax.Attributes;

import com.google.inject.Inject;

/**
 * @see <a href=
 *      "http://docs.aws.amazon.com/Route53/latest/APIReference/API_ListResourceRecordSets.html"
 *      />
 * 
 * @author Adrian Cole
 */
public class ListResourceRecordSetsResponseHandler extends
      ParseSax.HandlerForGeneratedRequestWithResult<RecordSetIterable> {

   private final ResourceRecordSetHandler resourceRecordSetHandler;

   private StringBuilder currentText = new StringBuilder();
   private Builder builder = RecordSetIterable.builder();

   private boolean inResourceRecordSets;

   @Inject
   public ListResourceRecordSetsResponseHandler(ResourceRecordSetHandler resourceRecordSetHandler) {
      this.resourceRecordSetHandler = resourceRecordSetHandler;
   }

   @Override
   public RecordSetIterable getResult() {
      try {
         return builder.build();
      } finally {
         builder = RecordSetIterable.builder();
      }
   }

   @Override
   public void startElement(String url, String name, String qName, Attributes attributes) {
      if (equalsOrSuffix(qName, "ResourceRecordSets")) {
         inResourceRecordSets = true;
      }
      if (inResourceRecordSets) {
         resourceRecordSetHandler.startElement(url, name, qName, attributes);
      }
   }

   @Override
   public void endElement(String uri, String name, String qName) {
      if (inResourceRecordSets) {
         if (qName.equals("ResourceRecordSets")) {
            inResourceRecordSets = false;
         } else if (qName.equals("ResourceRecordSet")) {
            builder.add(resourceRecordSetHandler.getResult());
         } else {
            resourceRecordSetHandler.endElement(uri, name, qName);
         }
      } else if (qName.equals("NextRecordName")) {
         builder.nextRecordName(currentOrNull(currentText));
      } else if (qName.equals("NextRecordType")) {
         builder.nextRecordType(Type.valueOf(currentOrNull(currentText)));
      } else if (qName.equals("NextRecordIdentifier")) {
         builder.nextRecordIdentifier(currentOrNull(currentText));
      }
      currentText = new StringBuilder();
   }

   @Override
   public void characters(char ch[], int start, int length) {
      if (inResourceRecordSets) {
         resourceRecordSetHandler.characters(ch, start, length);
      } else {
         currentText.append(ch, start, length);
      }
   }
}
