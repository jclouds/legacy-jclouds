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
import org.jclouds.aws.ec2.domain.IpPermission;
import org.jclouds.aws.ec2.domain.IpProtocol;
import org.jclouds.aws.ec2.domain.SecurityGroup;
import org.jclouds.aws.ec2.domain.UserIdGroupPair;
import org.jclouds.aws.ec2.util.EC2Utils;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.xml.sax.Attributes;

import com.google.common.collect.Sets;

/**
 * Parses: DescribeSecurityGroupsResponse xmlns="http://ec2.amazonaws.com/doc/2010-06-15/"
 * 
 * @see <a href="http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/index.html?ApiReference-query-DescribeSecurityGroups.html"
 *      />
 * @author Adrian Cole
 */
public class DescribeSecurityGroupsResponseHandler extends
         ParseSax.HandlerForGeneratedRequestWithResult<SortedSet<SecurityGroup>> {
   @Inject
   @Region
   String defaultRegion;

   private StringBuilder currentText = new StringBuilder();
   private SortedSet<SecurityGroup> securtyGroups = Sets.newTreeSet();
   private String groupName;
   private String ownerId;
   private String groupDescription;
   private SortedSet<IpPermission> ipPermissions = Sets.newTreeSet();
   private int fromPort;
   private int toPort;
   private SortedSet<UserIdGroupPair> groups = Sets.newTreeSet();
   private String userId;
   private String userIdGroupName;
   private IpProtocol ipProtocol;
   private SortedSet<String> ipRanges = Sets.newTreeSet();

   private boolean inIpPermissions;
   private boolean inIpRanges;
   private boolean inGroups;

   public SortedSet<SecurityGroup> getResult() {
      return securtyGroups;
   }

   public void startElement(String uri, String name, String qName, Attributes attrs) {
      if (qName.equals("ipPermissions")) {
         inIpPermissions = true;
      } else if (qName.equals("ipRanges")) {
         inIpRanges = true;
      } else if (qName.equals("groups")) {
         inGroups = true;
      }
   }

   public void endElement(String uri, String name, String qName) {
      if (qName.equals("groupName")) {
         if (!inGroups)
            this.groupName = currentText.toString().trim();
         else
            this.userIdGroupName = currentText.toString().trim();
      } else if (qName.equals("ownerId")) {
         this.ownerId = currentText.toString().trim();
      } else if (qName.equals("userId")) {
         this.userId = currentText.toString().trim();
      } else if (qName.equals("groupDescription")) {
         this.groupDescription = currentText.toString().trim();
      } else if (qName.equals("ipProtocol")) {
         this.ipProtocol = IpProtocol.fromValue(currentText.toString().trim());
      } else if (qName.equals("fromPort")) {
         this.fromPort = Integer.parseInt(currentText.toString().trim());
      } else if (qName.equals("toPort")) {
         this.toPort = Integer.parseInt(currentText.toString().trim());
      } else if (qName.equals("cidrIp")) {
         this.ipRanges.add(currentText.toString().trim());
      } else if (qName.equals("ipPermissions")) {
         inIpPermissions = false;
      } else if (qName.equals("ipRanges")) {
         inIpRanges = false;
      } else if (qName.equals("groups")) {
         inGroups = false;
      } else if (qName.equals("item")) {
         if (inIpPermissions && !inIpRanges && !inGroups) {
            ipPermissions.add(new IpPermission(fromPort, toPort, groups, ipProtocol, ipRanges));
            this.fromPort = -1;
            this.toPort = -1;
            this.groups = Sets.newTreeSet();
            this.ipProtocol = null;
            this.ipRanges = Sets.newTreeSet();
         } else if (inIpPermissions && !inIpRanges && inGroups) {
            this.groups.add(new UserIdGroupPair(userId, userIdGroupName));
            this.userId = null;
            this.userIdGroupName = null;
         } else if (!inIpPermissions && !inIpRanges && !inGroups) {
            String region = EC2Utils.findRegionInArgsOrNull((GeneratedHttpRequest<?>) request);
            if (region == null)
               region = defaultRegion;
            securtyGroups.add(new SecurityGroup(region, groupName, ownerId, groupDescription,
                     ipPermissions));
            this.groupName = null;
            this.ownerId = null;
            this.groupDescription = null;
            this.ipPermissions = Sets.newTreeSet();
         }
      }

      currentText = new StringBuilder();
   }

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }
}
