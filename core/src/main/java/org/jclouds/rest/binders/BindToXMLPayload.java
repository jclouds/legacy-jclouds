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
package org.jclouds.rest.binders;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.http.HttpRequest;
import org.jclouds.io.MutableContentMetadata;
import org.jclouds.rest.Binder;
import org.jclouds.xml.XMLParser;

import com.google.common.base.Strings;

/**
 * Binds the request parameters to an XML formatted payload.
 * 
 * @author Ignasi Barrera
 */
@Singleton
public class BindToXMLPayload implements Binder {
   protected final XMLParser xmlParser;

   @Inject
   public BindToXMLPayload(final XMLParser xmlParser) {
      this.xmlParser = checkNotNull(xmlParser, "xmlParser");
   }

   @Override
   public <R extends HttpRequest> R bindToRequest(final R request, final Object input) {
      try {
         String xml = xmlParser.toXML(checkNotNull(input, "input"));
         request.setPayload(xml);
         MutableContentMetadata metadata = request.getPayload().getContentMetadata();
         if (contentTypeMustBeAdded(metadata)) {
            metadata.setContentType("application/xml");
         }
         return request;
      } catch (IOException ex) {
         throw new BindException(request, ex);
      }
   }

   private static boolean contentTypeMustBeAdded(final MutableContentMetadata metadata) {
      return Strings.isNullOrEmpty(metadata.getContentType())
            || metadata.getContentType().equals("application/unknown");
   }
}
