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

import javax.servlet.ServletException;

import org.mortbay.jetty.runner.Runner;

/**
 * @see Runner
 * @author Andrew Phillips
 */
class Runner2 extends Runner {
    public static Runner2 createRunner(String[] args) throws ServletException {
        Runner2 runner = new Runner2();
        try {
            runner.configure(args);
        } catch (Exception exception) {
            throw new ServletException("Unable to configure runner", exception);
        }
        return runner;
    }

    private final Thread serverThread;
    
    private Runner2() {
        serverThread = new Thread(new Runnable() {
            public void run() {
                try {
                    Runner2.this.run();
                } catch (Exception exception) {
                    System.err.println("exception starting server: " + exception);
                }
            }
        });
    }
    
    void start() throws ServletException {
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                stop();
            }
        }));
        serverThread.start();
    }
    
    void stop() {
        try {
            _server.stop();
        } catch (Exception exception) {
            System.err.println("exception stopping server: " + exception);
        }
        serverThread.interrupt();
    }

}
