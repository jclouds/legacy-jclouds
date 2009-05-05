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
package org.jclouds.samples.googleappengine.functest;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import org.testng.annotations.AfterTest;

import com.google.appengine.tools.KickStart;

/**
 * Basic functionality to start a local google app engine instance.
 * 
 * @author Adrian Cole
 * 
 */
public abstract class BaseGoogleAppEngineTest {

    Thread server;
    URL url;

    protected void writePropertiesAndStartServer(final String address,
	    final String port, final String warfile, Properties props)
	    throws IOException, FileNotFoundException, InterruptedException {
	url = new URL(String.format("http://%1s:%2s", address, port));

	props.store(new FileOutputStream(String.format(
		"%1s/WEB-INF/jclouds.properties", warfile)), "test");
	this.server = new Thread(new Runnable() {
	    public void run() {
		KickStart
			.main(new String[] {
				"com.google.appengine.tools.development.DevAppServerMain",
				"--disable_update_check", "-a", address, "-p",
				port, warfile });

	    }

	});
	server.start();
	Thread.sleep(7 * 1000);
    }

    @SuppressWarnings("deprecation")
    @AfterTest
    public void stopDevAppServer() throws Exception {
	server.stop();
    }

}