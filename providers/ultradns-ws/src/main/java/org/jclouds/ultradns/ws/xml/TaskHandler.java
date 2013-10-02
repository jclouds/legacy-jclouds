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
package org.jclouds.ultradns.ws.xml;

import static org.jclouds.util.SaxUtils.currentOrNull;
import static org.jclouds.util.SaxUtils.equalsOrSuffix;

import java.net.URI;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.ultradns.ws.domain.Task;
import org.jclouds.ultradns.ws.domain.Task.StatusCode;
import org.xml.sax.Attributes;

/**
 * 
 * @author Adrian Cole
 */
public class TaskHandler extends ParseSax.HandlerForGeneratedRequestWithResult<Task> {
   private StringBuilder currentText = new StringBuilder();
   private Task.Builder builder = Task.builder();

   @Override
   public Task getResult() {
      try {
         return builder.build();
      } finally {
         builder = Task.builder();
      }
   }

   @Override
   public void startElement(String url, String name, String qName, Attributes attributes) {
   }

   @Override
   public void endElement(String uri, String name, String qName) {
      if (equalsOrSuffix(qName, "guid")) {
         builder.guid(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "code")) {
         builder.statusCode(StatusCode.valueOf(currentOrNull(currentText)));
      } else if (equalsOrSuffix(qName, "message")) {
         builder.message(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "resultUrl")) {
         builder.resultUrl(URI.create(currentOrNull(currentText)));
      }
      currentText = new StringBuilder();
   }

   @Override
   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }
}
