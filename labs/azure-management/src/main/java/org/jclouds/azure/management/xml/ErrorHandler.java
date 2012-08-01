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

import org.jclouds.azure.management.domain.Error;
import org.jclouds.azure.management.domain.Error.Code;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.util.SaxUtils;
import org.xml.sax.SAXException;

/**
 * @see <a href="http://msdn.microsoft.com/en-us/library/ee460801" >api</a>
 * 
 * @author Adrian Cole
 */
public class ErrorHandler extends ParseSax.HandlerForGeneratedRequestWithResult<Error> {

   private StringBuilder currentText = new StringBuilder();
   private Error.Builder builder = Error.builder();

   /**
    * {@inheritDoc}
    */
   @Override
   public Error getResult() {
      try {
         return builder.build();
      } finally {
         builder = Error.builder();
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void endElement(String uri, String name, String qName) throws SAXException {
      if (qName.equals("Code")) {
         String rawCode = SaxUtils.currentOrNull(currentText);
         builder.rawCode(rawCode);
         builder.code(Code.fromValue(rawCode));
      } else if (qName.equals("Message")) {
         builder.message(SaxUtils.currentOrNull(currentText));
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
