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

import javax.inject.Inject;

import org.jclouds.azure.management.domain.Operation;
import org.jclouds.azure.management.domain.Operation.Builder;
import org.jclouds.azure.management.domain.Operation.Status;
import org.jclouds.http.functions.ParseSax;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @see <a href="http://msdn.microsoft.com/en-us/library/ee460783" >api</a>
 * @author Adrian Cole
 */
public class OperationHandler extends ParseSax.HandlerForGeneratedRequestWithResult<Operation> {

   protected final ErrorHandler errorHandler;

   @Inject
   protected OperationHandler(ErrorHandler errorHandler) {
      this.errorHandler = errorHandler;
   }

   protected StringBuilder currentText = new StringBuilder();
   protected Operation.Builder builder = builder();

   protected Builder builder() {
      return Operation.builder();
   }

   protected boolean inError;

   /**
    * {@inheritDoc}
    */
   @Override
   public Operation getResult() {
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
      if (equalsOrSuffix(qName, "Error")) {
         inError = true;
      }
      if (inError) {
         errorHandler.startElement(url, name, qName, attributes);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void endElement(String uri, String name, String qName) throws SAXException {
      if (equalsOrSuffix(qName, "Error")) {
         builder.error(errorHandler.getResult());
         inError = false;
      } else if (inError) {
         errorHandler.endElement(uri, name, qName);
      } else if (equalsOrSuffix(qName, "ID")) {
         builder.id(currentOrNull(currentText));
      } else if (qName.equals("Status")) {
         String rawStatus = currentOrNull(currentText);
         builder.rawStatus(rawStatus);
         builder.status(Status.fromValue(rawStatus));
      } else if (equalsOrSuffix(qName, "HttpStatusCode")) {
         builder.httpStatusCode(Integer.parseInt(currentOrNull(currentText)));
      }
      currentText = new StringBuilder();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void characters(char ch[], int start, int length) {
      if (inError) {
         errorHandler.characters(ch, start, length);
      } else {
         currentText.append(ch, start, length);
      }
   }

}
