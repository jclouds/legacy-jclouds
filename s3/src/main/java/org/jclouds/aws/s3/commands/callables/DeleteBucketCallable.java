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
package org.jclouds.aws.s3.commands.callables;

import java.io.IOException;

import org.jclouds.aws.s3.S3Utils;
import org.jclouds.http.HttpException;

/**
 * // TODO: Adrian: Document this!
 * 
 * @author Adrian Cole
 */
public class DeleteBucketCallable extends DeleteCallable {

    public Boolean call() throws HttpException {
	if (getResponse().getStatusCode() == 404) {
	    return true;
	} else if (getResponse().getStatusCode() == 409) {
	    try {
		if (getResponse().getContent() == null) {
		    throw new HttpException(
			    "cannot determine error as there is no content");
		}
		String reason = S3Utils.toStringAndClose(getResponse()
			.getContent());
		if (reason.indexOf("BucketNotEmpty") >= 0)
		    return false;
		else
		    throw new HttpException("Error deleting bucket.\n" + reason);
	    } catch (IOException e) {
		throw new HttpException("Error deleting bucket", e);
	    }
	} else {
	    return super.call();
	}
    }
}