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
package org.jclouds.cloudstack.ec2.xml;

import com.google.common.base.Supplier;
import com.google.inject.Provider;
import org.jclouds.date.DateCodecFactory;
import org.jclouds.ec2.domain.RootDeviceType;
import org.jclouds.ec2.domain.RunningInstance;
import org.jclouds.ec2.xml.RunInstancesResponseHandler;
import org.jclouds.location.Region;
import org.xml.sax.Attributes;

import javax.inject.Inject;

import static org.jclouds.util.SaxUtils.equalsOrSuffix;

/**
 * Element introduced in newer API are causing random errors
 *
 * @author Anshul Gangwar
 */
public class CloudStackEC2RunInstancesResponseHandler extends RunInstancesResponseHandler {
   //groupSet for vpc and tagSet element introduced are cause of errors
   //These elements sub-element contain item element which is the cause of errors
   //Due to this some unexpected code gets executed and results in error
   protected boolean inTagSet = false;
   protected boolean inVpcGroupSet = false;

   @Inject
   CloudStackEC2RunInstancesResponseHandler(DateCodecFactory dateCodecFactory, @Region Supplier<String> defaultRegion,
                                            Provider<RunningInstance.Builder> builderProvider) {
      super(dateCodecFactory, defaultRegion, builderProvider);
   }

   @Override
   public void startElement(String uri, String name, String qName, Attributes attrs) {
      if (equalsOrSuffix(qName, "groupSet")) {
         if (!inInstancesSet) {
            inGroupSet = true;
         } else {
            inVpcGroupSet = true;
         }
      } else if (equalsOrSuffix(qName, "tagSet")) {
         inTagSet = true;
      } else if (!inTagSet) {
         super.startElement(uri, name, qName, attrs);
      }
   }

   @Override
   public void endElement(String uri, String name, String qName) {
      if (equalsOrSuffix(qName, "groupSet")) {
         if (!inInstancesSet) {
            inGroupSet = false;
         } else {
            inVpcGroupSet = false;
         }
      } else if (equalsOrSuffix(qName, "tagSet")) {
         inTagSet = false;
      } else if (equalsOrSuffix(qName, "rootDeviceType")) {
         //CloudStack sends this element as empty string so putting some value otherwise NPE
         builder.rootDeviceType(RootDeviceType.fromValue("ebs"));
      } else if (equalsOrSuffix(qName, "groupId") && !inGroupSet) {
         //to remove null pointer exception because of GroupSet element for vpc
      } else if (!inTagSet) {
         super.endElement(uri, name, qName);
      }
   }

   @Override
   protected boolean endOfInstanceItem() {
      return super.endOfInstanceItem() && !inTagSet && !inVpcGroupSet;
   }
}