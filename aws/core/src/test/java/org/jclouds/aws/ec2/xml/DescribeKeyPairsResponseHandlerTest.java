/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.aws.ec2.xml;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.util.SortedSet;

import org.jclouds.aws.ec2.domain.KeyPair;
import org.jclouds.http.functions.BaseHandlerTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSortedSet;

/**
 * Tests behavior of {@code DescribeKeyPairsHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ec2.DescribeKeyPairsHandlerTest")
public class DescribeKeyPairsResponseHandlerTest extends BaseHandlerTest {
   public void testApplyInputStream() {

      InputStream is = getClass().getResourceAsStream("/ec2/describe_keypairs.xml");

      SortedSet<KeyPair> expected = ImmutableSortedSet.of(new KeyPair("gsg-keypair",
               "1f:51:ae:28:bf:89:e9:d8:1f:25:5d:37:2d:7d:b8:ca:9f:f5:f1:6f", null));

      SortedSet<KeyPair> result = factory.create(
               injector.getInstance(DescribeKeyPairsResponseHandler.class)).parse(is);

      assertEquals(result, expected);
   }
}
