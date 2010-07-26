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
package org.jclouds.slicehost.xml;

import java.util.Set;

import javax.annotation.Nullable;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.slicehost.domain.Slice;
import org.jclouds.slicehost.domain.Slice.Status;
import org.xml.sax.SAXException;

import com.google.inject.internal.Sets;

/**
 * @author Adrian Cole
 */
public class SliceHandler extends ParseSax.HandlerWithResult<Slice> {
   private StringBuilder currentText = new StringBuilder();

   private int id;
   private String name;
   private int flavorId;
   @Nullable
   private Integer imageId;
   @Nullable
   private Integer backupId;
   private Status status;
   @Nullable
   private Integer progress;
   private float bandwidthIn;
   private float bandwidthOut;
   private Set<String> addresses = Sets.newLinkedHashSet();
   @Nullable
   private String rootPassword;

   private Slice slice;

   public Slice getResult() {
      return slice;
   }

   @Override
   public void endElement(String uri, String localName, String qName) throws SAXException {
      if (qName.equalsIgnoreCase("id")) {
         id = Integer.parseInt(currentText.toString().trim());
      } else if (qName.equalsIgnoreCase("name")) {
         this.name = currentText.toString().trim();
      } else if (qName.equalsIgnoreCase("flavor-id")) {
         flavorId = Integer.parseInt(currentText.toString().trim());
      } else if (qName.equalsIgnoreCase("image-id")) {
         imageId = Integer.parseInt(currentText.toString().trim());
      } else if (qName.equalsIgnoreCase("backup-id")) {
         backupId = Integer.parseInt(currentText.toString().trim());
      } else if (qName.equalsIgnoreCase("status")) {
         this.status = Slice.Status.fromValue(currentText.toString().trim());
      } else if (qName.equalsIgnoreCase("progress")) {
         progress = Integer.parseInt(currentText.toString().trim());
      } else if (qName.equalsIgnoreCase("bw-in")) {
         bandwidthIn = Float.parseFloat(currentText.toString().trim());
      } else if (qName.equalsIgnoreCase("bw-out")) {
         bandwidthOut = Float.parseFloat(currentText.toString().trim());
      } else if (qName.equalsIgnoreCase("address")) {
         this.addresses.add(currentText.toString().trim());
      } else if (qName.equalsIgnoreCase("root-password")) {
         this.rootPassword = currentText.toString().trim();
      } else if (qName.equalsIgnoreCase("slice")) {
         this.slice = new Slice(id, name, flavorId, imageId, backupId, status, progress, bandwidthIn, bandwidthOut,
               addresses, rootPassword);
         this.id = -1;
         this.name = null;
         this.flavorId = -1;
         this.imageId = null;
         this.backupId = null;
         this.status = null;
         this.progress = null;
         this.bandwidthIn = 0;
         this.bandwidthOut = 0;
         this.addresses = Sets.newLinkedHashSet();
         this.rootPassword = null;
      }
      currentText = new StringBuilder();
   }

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }
}
