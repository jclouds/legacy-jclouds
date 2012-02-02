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
package org.jclouds.aws.ec2.xml;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.jclouds.aws.ec2.domain.LaunchSpecification;
import org.jclouds.aws.ec2.domain.LaunchSpecification.Builder;
import org.jclouds.date.DateService;
import org.jclouds.ec2.domain.BlockDeviceMapping;
import org.jclouds.http.functions.ParseSax.HandlerForGeneratedRequestWithResult;
import org.jclouds.logging.Logger;
import org.xml.sax.Attributes;

/**
 * 
 * @author Adrian Cole
 */
public class LaunchSpecificationHandler extends HandlerForGeneratedRequestWithResult<LaunchSpecification> {

   @Resource
   protected Logger logger = Logger.NULL;

   protected final DateService dateService;
   protected final Builder builder;
   protected final BlockDeviceMapping.Builder blockDeviceMappingBuilder;

   @Inject
   public LaunchSpecificationHandler(DateService dateService, LaunchSpecification.Builder builder,
            BlockDeviceMapping.Builder blockDeviceMappingBuilder) {
      this.dateService = dateService;
      this.builder = builder;
      this.blockDeviceMappingBuilder = blockDeviceMappingBuilder;
   }

   protected String currentOrNull() {
      String returnVal = currentText.toString().trim();
      return returnVal.equals("") ? null : returnVal;
   }

   protected StringBuilder currentText = new StringBuilder();

   private boolean inBlockDeviceMapping;

   private String groupId;

   public void startElement(String uri, String name, String qName, Attributes attrs) {
      if (qName.equals("blockDeviceMapping")) {
         inBlockDeviceMapping = true;
      }
   }

   public void endElement(String uri, String name, String qName) {
      if (qName.equals("blockDeviceMapping")) {
         inBlockDeviceMapping = false;
      } else if (qName.equals("item") && inBlockDeviceMapping) {
         try {
            builder.blockDeviceMapping(blockDeviceMappingBuilder.build());
         } finally {
            blockDeviceMappingBuilder.clear();
         }
      } else if (qName.equals("deviceName")) {
         blockDeviceMappingBuilder.deviceName(currentOrNull());
      } else if (qName.equals("virtualName")) {
         blockDeviceMappingBuilder.virtualName(currentOrNull());
      } else if (qName.equals("snapshotId")) {
         blockDeviceMappingBuilder.snapshotId(currentOrNull());
      } else if (qName.equals("volumeSize")) {
         String volumeSize = currentOrNull();
         if (volumeSize != null)
            blockDeviceMappingBuilder.sizeInGib(Integer.parseInt(volumeSize));
      } else if (qName.equals("noDevice")) {
         String noDevice = currentOrNull();
         if (noDevice != null)
            blockDeviceMappingBuilder.noDevice(Boolean.parseBoolean(noDevice));
      } else if (qName.equals("deleteOnTermination")) {
         String deleteOnTermination = currentOrNull();
         if (deleteOnTermination != null)
            blockDeviceMappingBuilder.deleteOnTermination(Boolean.parseBoolean(deleteOnTermination));
      } else if (qName.equals("groupId")) {
         groupId = currentOrNull();
      } else if (qName.equals("groupName")) {
         builder.securityGroupIdToName(groupId, currentOrNull());
         groupId = null;
      } else if (qName.equals("imageId")) {
         builder.imageId(currentOrNull());
      } else if (qName.equals("instanceType")) {
         builder.instanceType(currentOrNull());
      } else if (qName.equals("kernelId")) {
         builder.kernelId(currentOrNull());
      } else if (qName.equals("keyName")) {
         builder.keyName(currentOrNull());
      } else if (qName.equals("availabilityZone")) {
         builder.availabilityZone(currentOrNull());
      } else if (qName.equals("ramdiskId")) {
         builder.ramdiskId(currentOrNull());
      } else if (qName.equals("enabled")) {
         String monitoringEnabled = currentOrNull();
         if (monitoringEnabled != null)
            builder.monitoringEnabled(new Boolean(monitoringEnabled));
      }
      currentText = new StringBuilder();
   }

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }

   @Override
   public LaunchSpecification getResult() {
      try {
         return builder.build();
      } finally {
         builder.clear();
      }
   }

}
