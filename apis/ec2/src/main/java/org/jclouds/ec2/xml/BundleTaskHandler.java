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
import org.jclouds.ec2.domain.BundleTask;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.location.Region;

import com.google.common.base.Supplier;

/**
 * 
 * @author Adrian Cole
 */
public class BundleTaskHandler extends ParseSax.HandlerForGeneratedRequestWithResult<BundleTask> {
   private StringBuilder currentText = new StringBuilder();

   @Inject
   protected DateService dateService;
   @Inject
   @Region
   Supplier<String> defaultRegion;

   private String bundleId;
   private String code;
   private String message;
   private String instanceId;
   private int progress = 0;
   private Date startTime;
   private String state;
   private String bucket;
   private String prefix;
   private Date updateTime;

   public BundleTask getResult() {
      String region = AWSUtils.findRegionInArgsOrNull(getRequest());
      if (region == null)
         region = defaultRegion.get();
      BundleTask.Error error = null;
      if (code != null)
         error = new BundleTask.Error(code, message);
      BundleTask returnVal = new BundleTask(region, bundleId, error, instanceId, progress, startTime,
            state, bucket, prefix, updateTime);
      this.bundleId = null;
      this.code = null;
      this.message = null;
      this.instanceId = null;
      this.progress = 0;
      this.startTime = null;
      this.state = null;
      this.bucket = null;
      this.prefix = null;
      this.updateTime = null;
      return returnVal;
   }

   public void endElement(String uri, String name, String qName) {
      if (qName.equals("bundleId")) {
         bundleId = currentText.toString().trim();
      } else if (qName.equals("code")) {
         code = currentText.toString().trim();
      } else if (qName.equals("message")) {
         message = currentText.toString().trim();
      } else if (qName.equals("instanceId")) {
         instanceId = currentText.toString().trim();
      } else if (qName.equals("progress")) {
         String temp = currentText.toString().trim();
         temp = temp.substring(0, temp.length() - 1);
         progress = Integer.parseInt(temp);
      } else if (qName.equals("startTime")) {
         startTime = dateService.iso8601DateParse(currentText.toString().trim());
      } else if (qName.equals("state")) {
         state = currentText.toString().trim();
      } else if (qName.equals("bucket")) {
         bucket = currentText.toString().trim();
      } else if (qName.equals("prefix")) {
         prefix = currentText.toString().trim();
      } else if (qName.equals("updateTime")) {
         updateTime = dateService.iso8601DateParse(currentText.toString().trim());
      }
      currentText = new StringBuilder();
   }

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }
}
