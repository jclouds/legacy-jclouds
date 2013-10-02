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

import static com.google.common.io.BaseEncoding.base16;
import static org.jclouds.util.SaxUtils.currentOrNull;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.sqs.domain.Message;

import com.google.common.hash.HashCodes;

/**
 * @see <a href=
 *      "http://docs.amazonwebservices.com/AWSSimpleQueueService/2011-10-01/APIReference/Query_QueryReceiveMessage.html"
 *      />
 * 
 * @author Adrian Cole
 */
public class MessageHandler extends ParseSax.HandlerForGeneratedRequestWithResult<Message> {

   private StringBuilder currentText = new StringBuilder();
   private Message.Builder builder = Message.builder();
   private String name;

   @Override
   public Message getResult() {
      try {
         return builder.build();
      } catch (NullPointerException e) {
         return null;
      } finally {
         builder = Message.builder();
      }
   }

   @Override
   public void endElement(String uri, String name, String qName) {
      if (qName.equals("MessageId")) {
         builder.id(currentOrNull(currentText));
      } else if (qName.equals("ReceiptHandle")) {
         builder.receiptHandle(currentOrNull(currentText));
      } else if (qName.equals("MD5OfBody")) {
         builder.md5(HashCodes.fromBytes(base16().lowerCase().decode(currentOrNull(currentText))));
      } else if (qName.equals("Body")) {
         builder.body(currentOrNull(currentText));
      } else if (qName.equals("Name")) {
         this.name = currentOrNull(currentText);
      } else if (qName.equals("Value")) {
         builder.addAttribute(this.name, currentOrNull(currentText));
         this.name = null;
      }
      currentText = new StringBuilder();
   }

   @Override
   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }

}
