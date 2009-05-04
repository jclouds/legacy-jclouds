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
package org.jclouds.http.commands;

import org.jclouds.http.commands.callables.ReturnStringIf200;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * // TODO: Adrian: Document this!
 * 
 * @author Adrian Cole
 */
@Test
public class GetStringTest {
    private static final String GOOD_PATH = "/index.html";

    private GetString get = null;
    private ReturnStringIf200 callable = null;

    @BeforeMethod
    void setUp() {
	callable = new ReturnStringIf200();
	get = new GetString(callable, GOOD_PATH);

    }

    @AfterMethod
    void tearDown() {
	get = null;
	callable = null;
    }

    @Test
    public void testConstructor() {
	assert get.getResponseFuture() != null;
	assert get.getRequest().getUri().equals(GOOD_PATH);
	assert get.getRequest().getMethod().equals("GET");
    }
}