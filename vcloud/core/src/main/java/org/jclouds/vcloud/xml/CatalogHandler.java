/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.vcloud.xml;

import static org.jclouds.vcloud.util.Utils.newNamedResource;

import java.util.List;
import java.util.SortedMap;

import javax.inject.Inject;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.vcloud.domain.Catalog;
import org.jclouds.vcloud.domain.NamedResource;
import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.internal.CatalogImpl;
import org.jclouds.vcloud.util.Utils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @author Adrian Cole
 */
public class CatalogHandler extends ParseSax.HandlerWithResult<Catalog> {

   protected final TaskHandler taskHandler;

   @Inject
   public CatalogHandler(TaskHandler taskHandler) {
      this.taskHandler = taskHandler;
   }

   private StringBuilder currentText = new StringBuilder();

   private NamedResource catalog;
   private SortedMap<String, NamedResource> contents = Maps.newTreeMap();
   protected List<Task> tasks = Lists.newArrayList();
   private String description;
   private NamedResource org;

   private boolean published = true;

   public Catalog getResult() {
      return new CatalogImpl(catalog.getName(), catalog.getType(), catalog.getId(), org, description, contents, tasks,
               published);
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
      if (qName.equals("Catalog")) {
         catalog = Utils.newNamedResource(attributes);
      } else if (qName.equals("CatalogItem")) {
         Utils.putNamedResource(contents, attributes);
      } else if (qName.equals("Link") && "up".equals(Utils.attrOrNull(attributes, "rel"))) {
         org = newNamedResource(attributes);
      } else {
         taskHandler.startElement(uri, localName, qName, attributes);
      }
   }

   public void endElement(String uri, String name, String qName) {
      taskHandler.endElement(uri, name, qName);
      if (qName.equals("Task")) {
         this.tasks.add(taskHandler.getResult());
      } else if (qName.equals("Description")) {
         description = currentOrNull();
      } else if (qName.equals("IsPublished")) {
         published = Boolean.parseBoolean(currentOrNull());
      }
      currentText = new StringBuilder();
   }

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }

   protected String currentOrNull() {
      String returnVal = currentText.toString().trim();
      return returnVal.equals("") ? null : returnVal;
   }
}
