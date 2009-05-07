/**
 *
 * Copyright (C) 2009 Adrian Cole <adrian@jclouds.org>
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
package org.jclouds.aws.s3.commands.options;

import static org.jclouds.aws.s3.commands.options.PutBucketOptions.Builder.locationConstraint;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import org.jclouds.aws.s3.domain.S3Bucket.MetaData.LocationConstraint;
import org.testng.annotations.Test;

/**
 * Tests possible uses of GetBucketOptions and GetBucketOptions.Builder.*
 * 
 * @author Adrian Cole
 */
public class PutBucketOptionsTest {

    @Test
    public void testLocationConstraint() {
	PutBucketOptions options = new PutBucketOptions();
	options.locationConstraint(LocationConstraint.EU);
	assertEquals(options.getLocationConstraint(), LocationConstraint.EU);
    }

    @Test
    public void testNullLocationConstraint() {
	PutBucketOptions options = new PutBucketOptions();
	assertNull(options.getLocationConstraint());
    }

    @Test
    public void testLocationConstraintStatic() {
	PutBucketOptions options = locationConstraint(LocationConstraint.EU);
	assertEquals(options.getLocationConstraint(), LocationConstraint.EU);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testNPE() {
	locationConstraint(null);
    }

}
