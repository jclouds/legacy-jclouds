/**
 *
 * Copyright (C) 2009 Adrian Cole <adriancole@jclouds.org>
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
package org.jclouds.aws.s3.commands.callables.xml;

import org.jclouds.aws.s3.DateService;
import com.google.inject.Inject;
import org.jclouds.aws.s3.domain.S3Bucket;
import org.jclouds.aws.s3.domain.S3Owner;
import org.jclouds.http.commands.callables.xml.ParseSax;
import org.xml.sax.Attributes;

import java.util.ArrayList;
import java.util.List;

/**
 * // TODO: Adrian: Document this!
 *
 * @author Adrian Cole
 */
public class ListAllMyBucketsHandler extends ParseSax.HandlerWithResult<List<S3Bucket>> {

    private List<S3Bucket> buckets = new ArrayList<S3Bucket>();
    private S3Bucket currentS3Bucket;
    private S3Owner currentOwner;
    private StringBuilder currentText = new StringBuilder();

    private final DateService dateParser;

    @Inject
    public ListAllMyBucketsHandler(DateService dateParser) {
        this.dateParser = dateParser;
    }

    public List<S3Bucket> getResult() {
        return buckets;
    }

    public void startElement(String uri, String name, String qName, Attributes attrs) {
        if (qName.equals("Bucket")) {
            currentS3Bucket = new S3Bucket();
            currentS3Bucket.setHasData(false);
            currentS3Bucket.setComplete(false);
        } else if (qName.equals("Owner")) {
            currentOwner = new S3Owner();
        }
    }

    public void endElement(String uri, String name, String qName) {
        if (qName.equals("ID")) { //owner stuff
            currentOwner.setId(currentText.toString());
        } else if (qName.equals("DisplayName")) {
            currentOwner.setDisplayName(currentText.toString());
        } else if (qName.equals("Bucket")) {
            currentS3Bucket.setCanonicalUser(currentOwner);
            buckets.add(currentS3Bucket);
        } else if (qName.equals("Name")) {
            currentS3Bucket.setName(currentText.toString());
        } else if (qName.equals("CreationDate")) {
            currentS3Bucket.setCreationDate(dateParser.dateTimeFromXMLFormat(currentText.toString()));
        }
        currentText = new StringBuilder();
    }

    public void characters(char ch[], int start, int length) {
        currentText.append(ch, start, length);
    }
}
