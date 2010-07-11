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

import java.util.SortedSet;

import javax.inject.Inject;

import org.jclouds.aws.Region;
import org.jclouds.aws.ec2.domain.InstanceState;
import org.jclouds.aws.ec2.domain.InstanceStateChange;
import org.jclouds.aws.ec2.util.EC2Utils;
import org.jclouds.http.functions.ParseSax.HandlerForGeneratedRequestWithResult;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.xml.sax.Attributes;

import com.google.common.collect.Sets;

/**
 * Parses the following XML document:
 * <p/>
 * TerminateInstancesResponse xmlns="http://ec2.amazonaws.com/doc/2009-11-30/"
 * StartInstancesResponse xmlns="http://ec2.amazonaws.com/doc/2009-11-30/" StopInstancesResponse
 * xmlns="http://ec2.amazonaws.com/doc/2009-11-30/"
 * 
 * @author Adrian Cole
 * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-ItemType-TerminateInstancesResponseInfoType.html"
 *      />
 * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-ItemType-StartInstancesResponseInfoType.html"
 *      />
 * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-ItemType-StopInstancesResponseInfoType.html"
 *      />
 */
public class InstanceStateChangeHandler extends
         HandlerForGeneratedRequestWithResult<SortedSet<InstanceStateChange>> {
   private StringBuilder currentText = new StringBuilder();
   @Inject
   @Region
   String defaultRegion;

   SortedSet<InstanceStateChange> instances = Sets.newTreeSet();
   private InstanceState shutdownState;
   private InstanceState previousState;
   private String instanceId;

   private boolean inCurrentState;

   private boolean inPreviousState;

   @Override
   public SortedSet<InstanceStateChange> getResult() {
      return instances;
   }

   public void startElement(String uri, String name, String qName, Attributes attrs) {
      if (qName.equals("shutdownState") || qName.equals("currentState")) {
         inCurrentState = true;
      } else if (qName.equals("previousState")) {
         inPreviousState = true;
      }
   }

   public void endElement(String uri, String name, String qName) {

      if (qName.equals("instanceId")) {
         this.instanceId = currentOrNull();
      } else if (qName.equals("shutdownState") || qName.equals("currentState")) {
         inCurrentState = false;
      } else if (qName.equals("previousState")) {
         inPreviousState = false;
      } else if (qName.equals("name")) {
         if (inCurrentState) {
            shutdownState = InstanceState.fromValue(currentOrNull());
         } else if (inPreviousState) {
            previousState = InstanceState.fromValue(currentOrNull());
         }
      } else if (qName.equals("item")) {
         String region = EC2Utils.findRegionInArgsOrNull((GeneratedHttpRequest<?>) request);
         if (region == null)
            region = defaultRegion;
         instances.add(new InstanceStateChange(region, instanceId, shutdownState, previousState));
         this.instanceId = null;
         this.shutdownState = null;
         this.previousState = null;
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
