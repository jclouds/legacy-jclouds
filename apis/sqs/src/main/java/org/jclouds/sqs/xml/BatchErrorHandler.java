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
package org.jclouds.sqs.xml;

import static org.jclouds.util.SaxUtils.currentOrNull;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.sqs.domain.BatchError;

/**
 * @see <a href=
 *      "http://docs.amazonwebservices.com/AWSSimpleQueueService/latest/APIReference/Query_QueryDeleteMessageBatch.html"
 *      />
 * 
 * @author Adrian Cole
 */
public class BatchErrorHandler extends ParseSax.HandlerForGeneratedRequestWithResult<BatchError> {

   private StringBuilder currentText = new StringBuilder();
   private BatchError.Builder builder = BatchError.builder();

   @Override
   public BatchError getResult() {
      try {
         return builder.build();
      } catch (NullPointerException e) {
         return null;
      } finally {
         builder = BatchError.builder();
      }
   }

   @Override
   public void endElement(String uri, String name, String qName) {
      if (qName.equals("Id")) {
         builder.id(currentOrNull(currentText));
      } else if (qName.equals("SenderFault")) {
         builder.senderFault(Boolean.parseBoolean(currentOrNull(currentText)));
      } else if (qName.equals("Code")) {
         builder.code(currentOrNull(currentText));
      } else if (qName.equals("Message")) {
         builder.message(currentOrNull(currentText));
      }
      currentText = new StringBuilder();
   }

   @Override
   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }

}
