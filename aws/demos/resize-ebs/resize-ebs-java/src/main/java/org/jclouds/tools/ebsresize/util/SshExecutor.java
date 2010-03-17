/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.tools.ebsresize.util;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.predicates.RunScriptRunning;
import org.jclouds.compute.util.ComputeUtils;
import org.jclouds.domain.Credentials;
import org.jclouds.logging.Logger;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.ssh.SshClient;
import org.jclouds.ssh.jsch.JschSshClient;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * Allows remote command execution on instance.
 *
 * This is based on {@link org.jclouds.ssh.SshClient} and
 * {@link org.jclouds.compute.util.ComputeUtils}, with
 * some convenience methods.
 *
 * @see org.jclouds.ssh.SshClient
 * @see org.jclouds.compute.util.ComputeUtils 
 *
 * @author Oleksiy Yarmula
 */
public class SshExecutor {

    private final Predicate<SshClient> runScriptRunning =
                new RetryablePredicate<SshClient>(Predicates.not(new RunScriptRunning()),
                        600, 3, TimeUnit.SECONDS);

    private final NodeMetadata nodeMetadata;
    private final SshClient sshClient;
    private final ComputeUtils utils;

    public SshExecutor(NodeMetadata nodeMetadata,
                       Credentials instanceCredentials,
                       String keyPair,
                       InetSocketAddress socket) {
        this.nodeMetadata = nodeMetadata;

        this.sshClient =
                new JschSshClient(socket, 60000,
                                instanceCredentials.account, keyPair.getBytes());

        this.utils = new ComputeUtils(null, runScriptRunning, null);

    }

    public void connect() {
        sshClient.connect();        
    }

    public void execute(String command) {

        ComputeUtils.RunScriptOnNode script = utils.runScriptOnNode(nodeMetadata,
                "basicscript.sh", command.getBytes());

        script.setConnection(sshClient, Logger.CONSOLE);

        try {
            script.call();
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

}
