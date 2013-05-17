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
package org.jclouds.route53.xml;

import static org.jclouds.util.SaxUtils.currentOrNull;

import java.util.Date;

import javax.inject.Inject;

import org.jclouds.date.DateService;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.route53.domain.Change;
import org.jclouds.route53.domain.Change.Status;
import org.xml.sax.Attributes;

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

   private String id;
   private Status status;
   private Date submittedAt;

   @Override
   public Change getResult() {
      try {
         return Change.create(id, status, submittedAt);
      } finally {
         id = null;
         status = null;
         submittedAt = null;
      }
   }

   @Override
   public void startElement(String url, String name, String qName, Attributes attributes) {
   }

   @Override
   public void endElement(String uri, String name, String qName) {
      if (qName.equals("Id")) {
         id = currentOrNull(currentText).replace("/change/", "");
      } else if (qName.equals("Status")) {
         status = Status.fromValue(currentOrNull(currentText));
      } else if (qName.equals("SubmittedAt")) {
         submittedAt = dateService.iso8601DateParse(currentOrNull(currentText));
      }
      currentText = new StringBuilder();
   }

   @Override
   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }

}
