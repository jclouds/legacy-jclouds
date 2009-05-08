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
package org.jclouds.aws.s3.xml;

import org.jclouds.aws.s3.DateService;
import org.jclouds.aws.s3.S3Utils;
import org.jclouds.aws.s3.domain.S3Object;
import org.jclouds.http.commands.callables.xml.ParseSax;

import com.google.inject.Inject;

/**
 * Parses the response from Amazon S3 COPY Object command.
 * 
 * @author Adrian Cole
 */
public class CopyObjectHandler extends
	ParseSax.HandlerWithResult<S3Object.Metadata> {

    private S3Object.Metadata metaData;
    private StringBuilder currentText = new StringBuilder();
    @Inject
    private DateService dateParser;

    public void setKey(String key) {
	metaData = new S3Object.Metadata(key);
    }

    public S3Object.Metadata getResult() {
	return metaData;
    }

    public void endElement(String uri, String name, String qName) {
	if (qName.equals("ETag")) {
	    metaData.setMd5(S3Utils.fromHexString(currentText.toString()
		    .replaceAll("\"", "")));
	} else if (qName.equals("LastModified")) {
	    metaData.setLastModified(dateParser
		    .dateTimeFromXMLFormat(currentText.toString()));
	}
	currentText = new StringBuilder();
    }

    public void characters(char ch[], int start, int length) {
	currentText.append(ch, start, length);
    }
}
