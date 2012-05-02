/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
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
 */
package org.jclouds.demo.tweetstore.integration;

import static com.google.common.io.Closeables.closeQuietly;
import static java.lang.String.format;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;

/**
 * Basic functionality to start a local WAR-supporting Jetty instance.
 *
 * @author Andrew Phillips
 */
public class JettyServer {
    protected Runner2 server;

    public void writePropertiesAndStartServer(final String port, final String warfile,
            Properties props) throws IOException, InterruptedException, ServletException {
        String filename = String.format(
                "%1$s/WEB-INF/jclouds.properties", warfile);
        System.err.println("file: " + filename);
        storeProperties(filename, props);
        assert new File(filename).exists();
        // Jetty uses SLF4J by default
        System.setProperty("org.eclipse.jetty.util.log.class", "org.eclipse.jetty.util.log.JavaUtilLog");
        System.setProperty("java.util.logging.config.file", 
                           format("%s/WEB-INF/logging.properties", warfile));
        server = Runner2.createRunner(new String[] { "--port", port, warfile });
        server.start();
        TimeUnit.SECONDS.sleep(30);
    }

    private static void storeProperties(String filename, Properties props) throws IOException {
        FileOutputStream targetFile = new FileOutputStream(filename);
        try {
            props.store(targetFile, "test");
        } finally {
            closeQuietly(targetFile);
        }
    }

    public void stop() {
        server.stop();
    }
}