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
import org.jclouds.date.DateCodecFactory;
import org.jclouds.ec2.xml.CreateVolumeResponseHandler;
import org.jclouds.location.Region;
import org.jclouds.location.Zone;
import org.xml.sax.Attributes;

import javax.inject.Inject;
import java.util.Map;
import java.util.Set;

/**
 * Element introduced in newer API are causing random errors
 *
 * @author Anshul Gangwar
 */
public class CloudStackEC2CreateVolumeResponseHandler extends CreateVolumeResponseHandler {
   //tagSet element introduced is cause of errors
   //The element's sub-element contain item element which is the cause of errors
   //Due to this some unexpected code gets executed and results in error
   private boolean inTagSet = false;

   @Inject
   protected CloudStackEC2CreateVolumeResponseHandler(DateCodecFactory dateCodecFactory,
                                                      @Region Supplier<String> defaultRegion,
                                                      @Zone Supplier<Map<String,
                                                              Supplier<Set<String>>>> regionToZonesSupplier,
                                                      @Zone Supplier<Set<String>> zonesSupplier) {
      super(dateCodecFactory, defaultRegion, regionToZonesSupplier, zonesSupplier);
   }

   @Override
   public void startElement(String uri, String localName, String qName, Attributes attributes) {
      if (qName.equals("tagSet")) {
         inTagSet = true;
      } else if (!inTagSet) {
         super.startElement(uri, localName, qName, attributes);
      }
   }

   @Override
   public void endElement(String uri, String localName, String qName) {
      if (qName.equals("tagSet")) {
         inTagSet = false;
      } else if (!inTagSet) {
         super.endElement(uri, localName, qName);
      }
   }
}