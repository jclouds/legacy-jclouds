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

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.aws.s3.DateService;
import org.jclouds.aws.s3.S3Utils;
import org.jclouds.aws.s3.domain.S3Bucket;
import org.jclouds.aws.s3.domain.S3Object;
import org.jclouds.aws.s3.domain.S3Owner;
import org.jclouds.http.commands.callables.xml.ParseSax;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.inject.Inject;

/**
 * // TODO: Adrian: Document this!
 * 
 * @author Adrian Cole
 */
public class ListBucketHandler extends ParseSax.HandlerWithResult<S3Bucket> {

    public S3Bucket getResult() {
	return s3Bucket;
    }

    public void setBucketName(String bucketName) {
	this.s3Bucket = new S3Bucket(bucketName);
    }

    private S3Bucket s3Bucket;
    private S3Object.MetaData currentObjectMetaData;
    private S3Owner currentOwner;
    private StringBuilder currentText = new StringBuilder();
    @Inject
    private DateService dateParser;

    @Override
    public void startDocument() throws SAXException {
	checkNotNull(s3Bucket, "s3Bucket");
	s3Bucket.getContents().clear();
	super.startDocument();
    }

    public void startElement(String uri, String name, String qName,
	    Attributes attrs) {
	if (qName.equals("Contents")) {
	} else if (qName.equals("Owner")) {
	    currentOwner = new S3Owner();
	}
    }

    public void endElement(String uri, String name, String qName) {

	if (qName.equals("ID")) { // owner stuff
	    currentOwner.setId(currentText.toString());
	} else if (qName.equals("DisplayName")) {
	    currentOwner.setDisplayName(currentText.toString());
	} else if (qName.equals("Key")) { // content stuff
	    currentObjectMetaData = new S3Object.MetaData(currentText
		    .toString());
	} else if (qName.equals("LastModified")) {
	    currentObjectMetaData.setLastModified(dateParser
		    .dateTimeFromXMLFormat(currentText.toString()));
	} else if (qName.equals("ETag")) {
	    currentObjectMetaData.setMd5(S3Utils.fromHexString(currentText
		    .toString().replaceAll("\"", "")));
	} else if (qName.equals("Size")) {
	    currentObjectMetaData.setSize(Long
		    .parseLong(currentText.toString()));
	} else if (qName.equals("Owner")) {
	    currentObjectMetaData.setOwner(currentOwner);
	} else if (qName.equals("StorageClass")) {
	    currentObjectMetaData.setStorageClass(currentText.toString());
	} else if (qName.equals("Contents")) {
	    s3Bucket.getContents().add(currentObjectMetaData);
	} else if (qName.equals("Name")) {// bucket stuff last, as least likely
	    // } else if (qName.equals("Prefix")) {
	    // // no-op
	    // } else if (qName.equals("Marker")) {
	    // // no-op
	    // } else if (qName.equals("MaxKeys")) {
	    // // no-op
	} else if (qName.equals("IsTruncated")) {
	    boolean isTruncated = Boolean.parseBoolean(currentText.toString());
	    s3Bucket.setComplete(!isTruncated);
	}
	currentText = new StringBuilder();
    }

    public void characters(char ch[], int start, int length) {
	currentText.append(ch, start, length);
    }
}
