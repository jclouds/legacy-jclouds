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

import org.jclouds.aws.s3.domain.S3Bucket.MetaData.LocationConstraint;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Contains options supported in the REST API for the PUT bucket operation.
 * <p/>
 * Example usage:
 * <p/>
 * <code>
 * import static org.jclouds.aws.s3.commands.options.CreateBucketOptions.Builder.*
 * import static org.jclouds.aws.s3.domain.S3Bucket.MetaData.LocationConstraint.*;
 * import org.jclouds.aws.s3.S3Connection;
 * 
 * S3Connection connection = // get connection
 * Future<Boolean> createdInEu = connection.createBucketIfNotExists("bucketName",locationConstraint(EU));
 * <code>
 * 
 * @author Adrian Cole
 * 
 */
public class CreateBucketOptions {
    private LocationConstraint constraint;

    public CreateBucketOptions locationConstraint(LocationConstraint constraint) {
	this.constraint = checkNotNull(constraint, "constraint");
	return this;
    }

    public LocationConstraint getLocationConstraint() {
	return constraint;
    }

    public static class Builder {
	public static CreateBucketOptions locationConstraint(
		LocationConstraint constraint) {
	    CreateBucketOptions options = new CreateBucketOptions();
	    return options.locationConstraint(constraint);
	}
    }
}
