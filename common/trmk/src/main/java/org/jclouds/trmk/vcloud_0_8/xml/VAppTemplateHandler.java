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
package org.jclouds.trmk.vcloud_0_8.xml;

import static org.jclouds.trmk.vcloud_0_8.util.Utils.newReferenceType;
import static org.jclouds.util.SaxUtils.cleanseAttributes;
import static org.jclouds.util.SaxUtils.currentOrNull;

import java.util.Map;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.trmk.vcloud_0_8.domain.ReferenceType;
import org.jclouds.trmk.vcloud_0_8.domain.Status;
import org.jclouds.trmk.vcloud_0_8.domain.VAppTemplate;
import org.jclouds.trmk.vcloud_0_8.domain.internal.VAppTemplateImpl;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @author Adrian Cole
 */
public class VAppTemplateHandler extends ParseSax.HandlerWithResult<VAppTemplate> {
   private StringBuilder currentText = new StringBuilder();

   private ReferenceType catalog;
   private String description;
   private Status status;

   public VAppTemplate getResult() {
      return new VAppTemplateImpl(catalog.getName(), catalog.getHref(), description, status);
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attrs) throws SAXException {
      Map<String, String> attributes = cleanseAttributes(attrs);
      if (qName.equals("VAppTemplate")) {
         catalog = newReferenceType(attributes);
         if (attributes.containsKey("status"))
            status = Status.fromValue(attributes.get("status"));
      }
   }

   public void endElement(String uri, String name, String qName) {
      if (qName.equals("Description")) {
         description = currentOrNull(currentText);
      }
      currentText = new StringBuilder();
   }

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }
}
