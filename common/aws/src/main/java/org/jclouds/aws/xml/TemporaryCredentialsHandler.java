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
package org.jclouds.aws.xml;

import javax.inject.Inject;

import org.jclouds.aws.domain.TemporaryCredentials;
import org.jclouds.date.DateService;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.util.SaxUtils;

/**
 * @see <a href=
 *      "http://docs.aws.amazon.com/STS/latest/APIReference/API_Credentials.html"
 *      />
 * 
 * @author Adrian Cole
 */
public class TemporaryCredentialsHandler extends ParseSax.HandlerForGeneratedRequestWithResult<TemporaryCredentials> {
   private final DateService dateService;

   @Inject
   protected TemporaryCredentialsHandler(DateService dateService) {
      this.dateService = dateService;
   }

   private StringBuilder currentText = new StringBuilder();
   private TemporaryCredentials.Builder builder = TemporaryCredentials.builder();

   /**
    * {@inheritDoc}
    */
   @Override
   public TemporaryCredentials getResult() {
      try {
         return builder.build();
      } finally {
         builder = TemporaryCredentials.builder();
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void endElement(String uri, String name, String qName) {
      if (qName.equals("AccessKeyId")) {
         builder.accessKeyId(SaxUtils.currentOrNull(currentText));
      } else if (qName.equals("SecretAccessKey")) {
         builder.secretAccessKey(SaxUtils.currentOrNull(currentText));
      } else if (qName.equals("SessionToken")) {
         builder.sessionToken(SaxUtils.currentOrNull(currentText));
      } else if (qName.equals("Expiration")) {
         builder.expiration(dateService.iso8601DateParse(SaxUtils.currentOrNull(currentText)));
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
