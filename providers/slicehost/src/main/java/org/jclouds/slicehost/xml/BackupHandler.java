/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.slicehost.xml;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import javax.annotation.Resource;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.logging.Logger;
import org.jclouds.slicehost.domain.Backup;
import org.xml.sax.SAXException;

/**
 * @author Adrian Cole
 */
public class BackupHandler extends ParseSax.HandlerWithResult<Backup> {
   private StringBuilder currentText = new StringBuilder();

   private int id;
   private String name;
   private String sliceId;
   private Date date;

   private Backup backup;
   @Resource
   protected Logger logger = Logger.NULL;

   public Backup getResult() {
      return backup;
   }

   @Override
   public void endElement(String uri, String localName, String qName) throws SAXException {
      if (qName.equalsIgnoreCase("id")) {
         id = Integer.parseInt(currentText.toString().trim());
      } else if (qName.equalsIgnoreCase("name")) {
         this.name = currentText.toString().trim();
      } else if (qName.equalsIgnoreCase("slice_id")) {
         sliceId = currentText.toString().trim();
      } else if (qName.equalsIgnoreCase("date")) {
         try {
            date = DateFormat.getInstance().parse(currentText.toString().trim());
         } catch (ParseException e) {
            logger.warn(e, "error parsing: %s", currentText.toString().trim());
         }
      } else if (qName.equalsIgnoreCase("backup")) {
         this.backup = new Backup(id, name, sliceId, date);
         this.id = -1;
         this.name = null;
         this.sliceId = null;
         this.date = null;
      }
      currentText = new StringBuilder();
   }

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }
}
