/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.aws.route53;

import org.jclouds.aws.route53.AWSRoute53ProviderMetadata;
import org.jclouds.route53.Route53ApiMetadata;
import org.jclouds.providers.internal.BaseProviderMetadataTest;
import org.testng.annotations.Test;

/**
 * The AWSRoute53ProviderTest teroute53 the org.jclouds.providers.AWSRoute53Provider class.
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "AWSRoute53ProviderTest")
public class AWSRoute53ProviderTest extends BaseProviderMetadataTest {

   public AWSRoute53ProviderTest() {
      super(new AWSRoute53ProviderMetadata(), new Route53ApiMetadata());
   }
}
