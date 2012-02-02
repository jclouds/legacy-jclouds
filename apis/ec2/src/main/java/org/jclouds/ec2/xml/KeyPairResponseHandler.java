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

import static org.jclouds.util.SaxUtils.currentOrNull;

import javax.inject.Inject;

import org.jclouds.aws.util.AWSUtils;
import org.jclouds.ec2.domain.KeyPair;
import org.jclouds.ec2.domain.KeyPair.Builder;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.location.Region;

import com.google.common.base.Supplier;

/**
 * 
 * @see <a href=
 *      "http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-CreateKeyPair.html"
 *      />
 * @author Adrian Cole
 */
public class KeyPairResponseHandler extends ParseSax.HandlerForGeneratedRequestWithResult<KeyPair> {
   private final Supplier<String> defaultRegion;
   private Builder builder;

   @Inject
   public KeyPairResponseHandler(@Region Supplier<String> defaultRegion) {
      this.defaultRegion = defaultRegion;
   }

   @Override
   public void startDocument() {
      builder = KeyPair.builder().region(defaultRegion.get());
   }

   private StringBuilder currentText = new StringBuilder();

   public KeyPair getResult() {
      String region = AWSUtils.findRegionInArgsOrNull(getRequest());
      if (region != null)
         builder.region(region);
      try {
         return builder.build();
      } finally {
         builder = KeyPair.builder().region(defaultRegion.get());
      }
   }

   public void endElement(String uri, String name, String qName) {
      if (qName.equals("keyFingerprint")) {
         builder.sha1OfPrivateKey(currentOrNull(currentText));
      } else if (qName.equals("keyMaterial")) {
         builder.keyMaterial(currentOrNull(currentText));
      } else if (qName.equals("keyName")) {
         builder.keyName(currentOrNull(currentText));
      }
      currentText = new StringBuilder();
   }

   public void characters(char ch[], int start, int length) {
      currentText.append(ch, start, length);
   }
}
