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

import com.google.common.collect.Multimap;
import static org.jclouds.aws.s3.commands.options.PutBucketOptions.Builder.createIn;
import static org.jclouds.aws.s3.commands.options.PutBucketOptions.Builder.withBucketAcl;
import org.jclouds.aws.s3.domain.S3Bucket.Metadata.LocationConstraint;
import org.jclouds.aws.s3.domain.acl.CannedAccessPolicy;
import org.jclouds.aws.s3.reference.S3Headers;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import org.testng.annotations.Test;

import java.io.UnsupportedEncodingException;

/**
 * Tests possible uses of PutBucketOptions and PutBucketOptions.Builder.*
 *
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "s3.PutBucketOptionsTest")
public class PutBucketOptionsTest {

    @Test
    public void testLocationConstraint() {
        PutBucketOptions options = new PutBucketOptions();
        options.createIn(LocationConstraint.EU);
        assertEquals(options.getLocationConstraint(), LocationConstraint.EU);
    }

    @Test
    public void testPayload() {
        PutBucketOptions options = new PutBucketOptions();
        options.createIn(LocationConstraint.EU);
        assertEquals(
                options.buildPayload(),
                "<CreateBucketConfiguration><LocationConstraint>EU</LocationConstraint></CreateBucketConfiguration>");
    }

    @Test
    public void testNullLocationConstraint() {
        PutBucketOptions options = new PutBucketOptions();
        assertNull(options.getLocationConstraint());
    }

    @Test
    public void testLocationConstraintStatic() {
        PutBucketOptions options = createIn(LocationConstraint.EU);
        assertEquals(options.getLocationConstraint(), LocationConstraint.EU);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testNPE() {
        createIn(null);
    }

    @Test
    public void testAclDefault() {
        PutBucketOptions options = new PutBucketOptions();
        assertEquals(options.getAcl(), CannedAccessPolicy.PRIVATE);
    }

    @Test
    public void testAclStatic() {
        PutBucketOptions options = withBucketAcl(CannedAccessPolicy.AUTHENTICATED_READ);
        assertEquals(options.getAcl(), CannedAccessPolicy.AUTHENTICATED_READ);
    }

    @Test
    void testBuildRequestHeaders() throws UnsupportedEncodingException {

        Multimap<String, String> headers = withBucketAcl(
                CannedAccessPolicy.AUTHENTICATED_READ).buildRequestHeaders();
        assertEquals(headers.get(S3Headers.CANNED_ACL).iterator().next(),
                CannedAccessPolicy.AUTHENTICATED_READ.toString());
    }
}
