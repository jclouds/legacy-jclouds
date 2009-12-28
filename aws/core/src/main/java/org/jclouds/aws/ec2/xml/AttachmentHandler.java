/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.aws.ec2.xml;

import java.util.Date;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.jclouds.aws.ec2.domain.Attachment;
import org.jclouds.date.DateService;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.logging.Logger;

/**
 * 
 * @author Adrian Cole
 */
public class AttachmentHandler extends ParseSax.HandlerWithResult<Attachment> {
   private StringBuilder currentText = new StringBuilder();

   @Resource
   protected Logger logger = Logger.NULL;
   @Inject
   protected DateService dateService;

   private String volumeId;
   private String instanceId;
   private String device;
   private Attachment.Status attachmentStatus;
   private Date attachTime;

   public Attachment getResult() {
      return new Attachment(volumeId, instanceId, device, attachmentStatus, attachTime);
   }

   public void endElement(String uri, String name, String qName) {
      if (qName.equals("volumeId")) {
         volumeId = currentText.toString().trim();

      } else if (qName.equals("volumeId")) {
         volumeId = currentText.toString().trim();

      } else if (qName.equals("status")) {
         attachmentStatus = Attachment.Status.fromValue(currentText.toString().trim());
      } else if (qName.equals("instanceId")) {
         instanceId = currentText.toString().trim();
      } else if (qName.equals("device")) {
         device = currentText.toString().trim();
      } else if (qName.equals("attachTime")) {
         attachTime = dateService.iso8601DateParse(currentText.toString().trim());
      }
      currentText = new StringBuilder();
   }

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }
}
