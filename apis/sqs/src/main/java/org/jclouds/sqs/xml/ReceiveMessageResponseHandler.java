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

import static org.jclouds.util.SaxUtils.equalsOrSuffix;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.sqs.domain.Message;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.inject.Inject;

/**
 * @see <a
 *      href="http://docs.amazonwebservices.com/AWSSimpleQueueService/2011-10-01/APIReference/Query_QueryReceiveMessage.html"
 *      >docs</a>
 * 
 * @author Adrian Cole
 */
public class ReceiveMessageResponseHandler extends ParseSax.HandlerForGeneratedRequestWithResult<FluentIterable<Message>> {

   private final MessageHandler messageHandler;

   private Builder<Message> messages = ImmutableList.<Message> builder();

   private boolean inMessages;

   @Inject
   public ReceiveMessageResponseHandler(MessageHandler messageHandler) {
      this.messageHandler = messageHandler;
   }

   @Override
   public FluentIterable<Message> getResult() {
      return FluentIterable.from(messages.build());
   }

   @Override
   public void startElement(String url, String name, String qName, Attributes attributes) throws SAXException {
      if (equalsOrSuffix(qName, "ReceiveMessageResult")) {
         inMessages = true;
      }
      if (inMessages) {
         messageHandler.startElement(url, name, qName, attributes);
      }
   }

   @Override
   public void endElement(String uri, String name, String qName) {
      if (equalsOrSuffix(qName, "ReceiveMessageResult")) {
         inMessages = false;
      } else if (equalsOrSuffix(qName, "Message")) {
         messages.add(messageHandler.getResult());
      } else if (inMessages) {
         messageHandler.endElement(uri, name, qName);
      }
   }

   @Override
   public void characters(char ch[], int start, int length) {
      if (inMessages) {
         messageHandler.characters(ch, start, length);
      }
   }

}
