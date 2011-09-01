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
package org.jclouds.azurequeue.xml;

import java.util.Date;
import java.util.Set;

import javax.inject.Inject;

import org.jclouds.azurequeue.domain.QueueMessage;
import org.jclouds.date.DateService;
import org.jclouds.http.functions.ParseSax;

import com.google.common.collect.Sets;

/**
 * Parses the following XML document:
 * <p/>
 * QueueMessagesList
 * 
 * @see <a href="http://msdn.microsoft.com/en-us/library/dd179474%28v=MSDN.10%29.aspx" />
 * @author Adrian Cole
 */
public class QueueMessagesListHandler extends ParseSax.HandlerWithResult<Set<QueueMessage>> {

   private Set<QueueMessage> messages = Sets.newLinkedHashSet();

   private String messageId;
   private Date insertionTime;
   private Date expirationTime;
   private int dequeueCount;
   private String popReceipt;
   private Date timeNextVisible;
   private String messageText;

   private StringBuilder currentText = new StringBuilder();

   private final DateService dateService;

   @Inject
   public QueueMessagesListHandler(DateService dateService) {
      this.dateService = dateService;
   }

   public Set<QueueMessage> getResult() {
      return messages;
   }

   public void endElement(String uri, String name, String qName) {
      if (qName.equals("MessageId")) {
         this.messageId = currentText.toString().trim();
      } else if (qName.equals("InsertionTime")) {
         this.insertionTime = parseDate();
      } else if (qName.equals("ExpirationTime")) {
         this.expirationTime = parseDate();
      } else if (qName.equals("DequeueCount")) {
         this.dequeueCount = Integer.parseInt(currentText.toString().trim());
      } else if (qName.equals("PopReceipt")) {
         this.popReceipt = currentText.toString().trim();
      } else if (qName.equals("TimeNextVisible")) {
         this.timeNextVisible = parseDate();
      } else if (qName.equals("MessageText")) {
         // TODO: figure out why we need to do trim. excess leading whitespace seems to be from
         // outside the element
         this.messageText = currentText.toString().trim();
      } else if (qName.equals("QueueMessage")) {
         messages.add(new QueueMessage(messageId, insertionTime, expirationTime, dequeueCount,
                  popReceipt, timeNextVisible, messageText));
         messageId = null;
         insertionTime = null;
         expirationTime = null;
         dequeueCount = -1;
         popReceipt = null;
         timeNextVisible = null;
         messageText = null;
      }
      currentText = new StringBuilder();
   }

   private Date parseDate() {
      return dateService.rfc822DateParse(currentText.toString().trim());
   }

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }

}
