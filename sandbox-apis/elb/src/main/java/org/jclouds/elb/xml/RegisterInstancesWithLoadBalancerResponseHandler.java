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

package org.jclouds.elb.xml;

import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.logging.Logger;

import com.google.common.collect.Sets;

public class RegisterInstancesWithLoadBalancerResponseHandler extends ParseSax.HandlerWithResult<Set<String>> {
   @Inject
   public RegisterInstancesWithLoadBalancerResponseHandler() {
   }

   @Resource
   protected Logger logger = Logger.NULL;

   private Set<String> instanceIds = Sets.newLinkedHashSet();
   private StringBuilder currentText = new StringBuilder();

   public void endElement(String uri, String localName, String qName) {
      if (qName.equals("InstanceId"))
         instanceIds.add(currentText.toString().trim());

      currentText = new StringBuilder();
   }

   @Override
   public Set<String> getResult() {
      return instanceIds;
   }

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }
}
