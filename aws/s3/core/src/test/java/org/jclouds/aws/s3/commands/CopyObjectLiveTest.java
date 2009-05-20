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
package org.jclouds.aws.s3.commands;

import org.jclouds.aws.s3.S3IntegrationTest;
import static org.jclouds.aws.s3.commands.options.CopyObjectOptions.Builder.overrideAcl;
import org.jclouds.aws.s3.domain.acl.CannedAccessPolicy;
import org.jclouds.aws.s3.util.S3Utils;
import org.testng.annotations.Test;

import java.net.URL;
import java.util.concurrent.TimeUnit;

/**
 * Tests integrated functionality of all copyObject commands.
 * <p/>
 * Each test uses a different bucket name, so it should be perfectly fine to run
 * in parallel.
 *
 * @author Adrian Cole
 */
@Test(testName = "s3.CopyObjectLiveTest")
public class CopyObjectLiveTest extends S3IntegrationTest {
    String sourceKey = "apples";
    String destinationKey = "pears";


    @Test(groups = "live")
    void testCannedAccessPolicyPublic() throws Exception {
        String destinationBucket = bucketName + "dest";

        addObjectToBucket(bucketName, sourceKey);
        validateContent(bucketName, sourceKey);

        createBucketAndEnsureEmpty(destinationBucket);
        client.copyObject(bucketName, sourceKey, destinationBucket,
                destinationKey, overrideAcl(CannedAccessPolicy.PUBLIC_READ)).get(10, TimeUnit.SECONDS);

        validateContent(destinationBucket, destinationKey);

        URL url = new URL(String.format("http://%1$s.s3.amazonaws.com/%2$s",
                destinationBucket, destinationKey));
        S3Utils.toStringAndClose(url.openStream());

    }

}