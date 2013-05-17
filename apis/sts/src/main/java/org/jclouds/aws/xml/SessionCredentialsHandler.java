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
package org.jclouds.aws.xml;

import static org.jclouds.util.SaxUtils.currentOrNull;

import javax.inject.Inject;

import org.jclouds.aws.domain.SessionCredentials;
import org.jclouds.date.DateService;
import org.jclouds.http.functions.ParseSax;

/**
 * @see <a href=
 *      "http://docs.aws.amazon.com/STS/latest/APIReference/API_Credentials.html"
 *      />
 * 
 * @author Adrian Cole
 */
public class SessionCredentialsHandler extends ParseSax.HandlerForGeneratedRequestWithResult<SessionCredentials> {
   private final DateService dateService;

   @Inject
   protected SessionCredentialsHandler(DateService dateService) {
      this.dateService = dateService;
   }

   private StringBuilder currentText = new StringBuilder();
   private SessionCredentials.Builder builder = SessionCredentials.builder();

   @Override
   public SessionCredentials getResult() {
      try {
         return builder.build();
      } finally {
         builder = SessionCredentials.builder();
      }
   }

   @Override
   public void endElement(String uri, String name, String qName) {
      if (qName.equals("AccessKeyId")) {
         builder.accessKeyId(currentOrNull(currentText));
      } else if (qName.equals("SecretAccessKey")) {
         builder.secretAccessKey(currentOrNull(currentText));
      } else if (qName.equals("SessionToken")) {
         builder.sessionToken(currentOrNull(currentText));
      } else if (qName.equals("Expiration")) {
         try {
            builder.expiration(dateService.iso8601SecondsDateParse(currentOrNull(currentText)));
         } catch (IllegalArgumentException e) {
            builder.expiration(dateService.iso8601DateParse(currentOrNull(currentText)));
         }
      }
      currentText = new StringBuilder();
   }

   @Override
   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }
}
