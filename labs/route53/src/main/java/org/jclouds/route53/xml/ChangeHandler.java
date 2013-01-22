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

import javax.inject.Inject;

import org.jclouds.date.DateService;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.route53.domain.Change;
import org.jclouds.route53.domain.Change.Status;
import org.jclouds.util.SaxUtils;

/**
 * @see <a href=
 *      "http://docs.aws.amazon.com/Route53/latest/APIReference/API_GetChange.html"
 *      />
 * 
 * @author Adrian Cole
 */
public class ChangeHandler extends ParseSax.HandlerForGeneratedRequestWithResult<Change> {
   private final DateService dateService;

   @Inject
   protected ChangeHandler(DateService dateService) {
      this.dateService = dateService;
   }

   private StringBuilder currentText = new StringBuilder();
   private Change.Builder builder = Change.builder();

   @Override
   public Change getResult() {
      try {
         return builder.build();
      } finally {
         builder = Change.builder();
      }
   }

   @Override
   public void endElement(String uri, String name, String qName) {
      if (qName.equals("Id")) {
         builder.id(SaxUtils.currentOrNull(currentText));
      } else if (qName.equals("Status")) {
         builder.status(Status.fromValue(SaxUtils.currentOrNull(currentText)));
      } else if (qName.equals("SubmittedAt")) {
         builder.submittedAt(dateService.iso8601DateParse(SaxUtils.currentOrNull(currentText)));
      }
      currentText = new StringBuilder();
   }

   @Override
   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }

}
