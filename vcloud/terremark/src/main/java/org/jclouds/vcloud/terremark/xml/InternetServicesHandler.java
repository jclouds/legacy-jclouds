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
package org.jclouds.vcloud.terremark.xml;

import java.util.SortedSet;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.jclouds.http.functions.ParseSax.HandlerWithResult;
import org.jclouds.logging.Logger;
import org.jclouds.vcloud.terremark.domain.InternetService;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.common.collect.Sets;

/**
 * @author Adrian Cole
 */
public class InternetServicesHandler extends HandlerWithResult<SortedSet<InternetService>> {

   @Resource
   protected Logger logger = Logger.NULL;
   private final InternetServiceHandler handler;
   SortedSet<InternetService> result = Sets.newTreeSet();

   @Inject
   public InternetServicesHandler(InternetServiceHandler handler) {
      this.handler = handler;
   }

   @Override
   public SortedSet<InternetService> getResult() {
      return result;
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {
      handler.startElement(uri, localName, qName, attributes);
   }

   public void endElement(String uri, String name, String qName) {
      handler.endElement(uri, name, qName);
      if (qName.equals("InternetService")) {
         result.add(handler.getResult());
      }
   }

   public void characters(char ch[], int start, int length) {
      handler.characters(ch, start, length);
   }

}