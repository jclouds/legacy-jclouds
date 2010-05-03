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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.jclouds.aws.ec2.EC2;
import org.jclouds.aws.ec2.domain.PublicIpInstanceIdPair;
import org.jclouds.aws.ec2.util.EC2Utils;
import org.jclouds.http.functions.ParseSax.HandlerWithResult;
import org.jclouds.logging.Logger;

import com.google.common.collect.Sets;

/**
 * 
 * @author Adrian Cole
 */
public class DescribeAddressesResponseHandler extends
         HandlerWithResult<Set<PublicIpInstanceIdPair>> {

   @Resource
   protected Logger logger = Logger.NULL;
   private Set<PublicIpInstanceIdPair> pairs = Sets.newLinkedHashSet();
   private InetAddress ipAddress;
   private StringBuilder currentText = new StringBuilder();
   @Inject
   @EC2
   String defaultRegion;
   private String instanceId;

   protected String currentOrNull() {
      String returnVal = currentText.toString().trim();
      return returnVal.equals("") ? null : returnVal;
   }

   public void endElement(String uri, String name, String qName) {
      if (qName.equals("publicIp")) {
         ipAddress = parseInetAddress(currentOrNull());
      } else if (qName.equals("instanceId")) {
         instanceId = currentOrNull();
      } else if (qName.equals("item")) {
         String region = EC2Utils.findRegionInArgsOrNull(request);
         if (region == null)
            region = defaultRegion;
         pairs.add(new PublicIpInstanceIdPair(region, ipAddress, instanceId));
         ipAddress = null;
         instanceId = null;
      }
      currentText = new StringBuilder();
   }

   private InetAddress parseInetAddress(String string) {
      String[] byteStrings = string.split("\\.");
      byte[] bytes = new byte[4];
      for (int i = 0; i < 4; i++) {
         bytes[i] = (byte) Integer.parseInt(byteStrings[i]);
      }
      try {
         return InetAddress.getByAddress(bytes);
      } catch (UnknownHostException e) {
         logger.warn(e, "error parsing ipAddress", currentText);
         throw new RuntimeException(e);
      }
   }

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }

   @Override
   public Set<PublicIpInstanceIdPair> getResult() {
      return pairs;
   }

}