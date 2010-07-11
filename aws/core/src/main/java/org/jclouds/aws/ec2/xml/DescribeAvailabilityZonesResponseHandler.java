/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.aws.ec2.xml;

import java.util.Set;

import javax.annotation.Resource;

import org.jclouds.aws.ec2.domain.AvailabilityZoneInfo;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.logging.Logger;
import org.xml.sax.Attributes;

import com.google.common.collect.Sets;

/**
 * 
 * @author Adrian Cole
 */
public class DescribeAvailabilityZonesResponseHandler extends
         ParseSax.HandlerWithResult<Set<AvailabilityZoneInfo>> {
   private StringBuilder currentText = new StringBuilder();

   private Set<AvailabilityZoneInfo> availablilityZones = Sets.newLinkedHashSet();
   private String zone;
   @Resource
   protected Logger logger = Logger.NULL;
   private String region;
   private String zoneState;
   private boolean inMessageSet;
   private Set<String> messages = Sets.newHashSet();

   public Set<AvailabilityZoneInfo> getResult() {
      return availablilityZones;
   }

   public void startElement(String uri, String name, String qName, Attributes attrs) {
      if (qName.equals("messageSet")) {
         inMessageSet = true;
      }
   }

   public void endElement(String uri, String name, String qName) {
      if (qName.equals("zoneName")) {
         zone = currentText.toString().trim();
      } else if (qName.equals("regionName")) {
         try {
            region = currentText.toString().trim();
         } catch (IllegalArgumentException e) {
            logger.warn(e, "unsupported region: %s", currentText.toString().trim());
            region = "UNKNOWN";
         }
      } else if (qName.equals("zoneState")) {
         zoneState = currentText.toString().trim();
      } else if (qName.equals("message")) {
         messages.add(currentText.toString().trim());
      } else if (qName.equals("messageSet")) {
         inMessageSet = false;
      } else if (qName.equals("item") && !inMessageSet) {
         availablilityZones.add(new AvailabilityZoneInfo(zone, zoneState, region, messages));
         this.zone = null;
         this.region = null;
         this.zoneState = null;
         this.messages = Sets.newHashSet();
      }
      currentText = new StringBuilder();
   }

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }
}
