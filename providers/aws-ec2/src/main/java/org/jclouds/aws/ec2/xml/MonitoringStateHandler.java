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

import java.util.Map;

import org.jclouds.aws.ec2.domain.MonitoringState;
import org.jclouds.http.functions.ParseSax;

import com.google.common.collect.Maps;

/**
 * 
 * @author Adrian Cole
 */
public class MonitoringStateHandler extends
         ParseSax.HandlerWithResult<Map<String, MonitoringState>> {
   private StringBuilder currentText = new StringBuilder();

   private Map<String, MonitoringState> monitoringState = Maps.newHashMap();
   private String instanceId;
   private MonitoringState state;

   public Map<String, MonitoringState> getResult() {
      return monitoringState;
   }

   public void endElement(String uri, String name, String qName) {
      if (qName.equals("instanceId")) {
         instanceId = currentText.toString().trim();
      } else if (qName.equals("state")) {
         state = MonitoringState.fromValue(currentText.toString().trim());
      } else if (qName.equals("item")) {
         monitoringState.put(instanceId, state);
         this.instanceId = null;
         this.state = null;
      }
      currentText = new StringBuilder();
   }

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }
}
