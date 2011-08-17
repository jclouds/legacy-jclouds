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

import java.util.Set;

import org.jclouds.ec2.domain.Permission;
import org.jclouds.http.functions.ParseSax;

import com.google.common.collect.Sets;

/**
 * 
 * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/index.html?ApiReference-query-DescribeImageAttribute.html"
 *      />
 * @author Adrian Cole
 */
public class PermissionHandler extends ParseSax.HandlerWithResult<Permission> {

   private StringBuilder currentText = new StringBuilder();
   private Set<String> userIds = Sets.newHashSet();
   private Set<String> groups = Sets.newHashSet();

   public Permission getResult() {
      return new Permission(userIds, groups);
   }

   public void endElement(String uri, String name, String qName) {

      if (qName.equalsIgnoreCase("group")) {
         groups.add(currentText.toString().trim());
      } else if (qName.equalsIgnoreCase("userId")) {
         userIds.add(currentText.toString().trim());
      }
      currentText = new StringBuilder();
   }

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }
}
