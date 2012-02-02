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
package org.jclouds.ec2.xml;

import java.util.Date;

import javax.inject.Inject;

import org.jclouds.aws.util.AWSUtils;
import org.jclouds.date.DateService;
import org.jclouds.ec2.domain.Snapshot;
import org.jclouds.ec2.domain.Snapshot.Status;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.location.Region;

import com.google.common.base.Supplier;

/**
 * 
 * @author Adrian Cole
 */
public class SnapshotHandler extends ParseSax.HandlerForGeneratedRequestWithResult<Snapshot> {
   private StringBuilder currentText = new StringBuilder();

   protected final DateService dateService;
   protected final Supplier<String> defaultRegion;

   private String id;
   private String volumeId;
   private int volumeSize;
   private Status status;
   private Date startTime;
   private int progress;
   private String ownerId;
   private String description;
   private String ownerAlias;

   @Inject
   public SnapshotHandler(DateService dateService, @Region Supplier<String> defaultRegion) {
      this.dateService = dateService;
      this.defaultRegion = defaultRegion;
   }

   public Snapshot getResult() {
      String region = AWSUtils.findRegionInArgsOrNull(getRequest());
      if (region == null)
         region = defaultRegion.get();
      Snapshot snapshot = new Snapshot(region, id, volumeId, volumeSize, status, startTime,
               progress, ownerId, description, ownerAlias);
      this.id = null;
      this.volumeId = null;
      this.volumeSize = 0;
      this.status = null;
      this.startTime = null;
      this.progress = 0;
      this.ownerId = null;
      this.description = null;
      this.ownerAlias = null;
      return snapshot;
   }

   public void endElement(String uri, String name, String qName) {
      if (qName.equals("snapshotId")) {
         id = currentText.toString().trim();
      } else if (qName.equals("volumeId")) {
         volumeId = currentText.toString().trim();
      } else if (qName.equals("volumeSize")) {
         volumeSize = Integer.parseInt(currentText.toString().trim());
      } else if (qName.equals("status")) {
         status = Snapshot.Status.fromValue(currentText.toString().trim());
      } else if (qName.equals("startTime")) {
         startTime = dateService.iso8601DateParse(currentText.toString().trim());
      } else if (qName.equals("progress")) {
         String progressString = currentText.toString().trim();
         if (!progressString.equals("")) {
            progressString = progressString.substring(0, progressString.length() - 1);
            progress = Integer.parseInt(progressString);
         }
      } else if (qName.equals("ownerId")) {
         ownerId = currentText.toString().trim();
      } else if (qName.equals("description")) {
         description = currentText.toString().trim();
      } else if (qName.equals("ownerAlias")) {
         ownerAlias = currentText.toString().trim();
      }
      currentText = new StringBuilder();
   }

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }
}
