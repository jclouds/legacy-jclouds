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
package org.jclouds.compute.callable;

import com.google.common.base.Function;
import org.jclouds.compute.callables.RunScriptOnNodeUsingSsh;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.domain.Credentials;
import org.jclouds.scriptbuilder.statements.login.UserAdd;
import org.jclouds.ssh.SshClient;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.annotation.Nullable;
import java.io.IOException;

import static org.easymock.EasyMock.*;
import static org.jclouds.compute.options.RunScriptOptions.Builder.wrapInInitScript;
import static org.jclouds.scriptbuilder.domain.Statements.exec;

/**
 * @author Adam Lowe
 */
@Test(groups={"unit"}, singleThreaded = true)
public class RunScriptOnNodeUsingSshTest  {
    private SshClient sshClient;
    private NodeMetadata node;
    private Function<NodeMetadata, SshClient> sshFactory;

    @BeforeMethod(groups={"unit"})
    public void init() {
        sshClient = createMock(SshClient.class);
        sshFactory = new Function<NodeMetadata, SshClient>() {
            @Override
            public SshClient apply(@Nullable NodeMetadata nodeMetadata) {
                return sshClient;
            }
        };
        node = createMock(NodeMetadata.class);
        expect(node.getCredentials()).andReturn(new Credentials("tester", "notalot"));
        expect(node.getAdminPassword()).andReturn(null).atLeastOnce();
        replay(node);
    }

    public void simpleTest() {
        RunScriptOnNodeUsingSsh testMe = new RunScriptOnNodeUsingSsh(sshFactory, node, exec("echo $USER\necho $USER"),
                wrapInInitScript(false).runAsRoot(false));

        testMe.init();

        sshClient.connect();
        expect(sshClient.getUsername()).andReturn("tester");
        expect(sshClient.getHostAddress()).andReturn("somewhere.example.com");
        expect(sshClient.exec("echo $USER\n" +
            "echo $USER\n")).andReturn(new ExecResponse("tester\ntester\n", null, 0));
        sshClient.disconnect();
        replay(sshClient);

        testMe.call();
    }

    public void simpleRootTest() {
        RunScriptOnNodeUsingSsh testMe = new RunScriptOnNodeUsingSsh(sshFactory, node, exec("echo $USER\necho $USER"),
                wrapInInitScript(false).runAsRoot(true));

        testMe.init();

        sshClient.connect();
        expect(sshClient.getUsername()).andReturn("tester");
        expect(sshClient.getHostAddress()).andReturn("somewhere.example.com");
        expect(sshClient.exec("sudo sh <<'RUN_SCRIPT_AS_ROOT_SSH'\n" +
                "echo $USER\n" +
                "echo $USER\n" +
                "RUN_SCRIPT_AS_ROOT_SSH\n")).andReturn(new ExecResponse("root\nroot\n", null, 0));
        sshClient.disconnect();
        replay(sshClient);

        testMe.call();
    }

    public void simpleRootTestWithSudoPassword() throws IOException {
        node = createMock(NodeMetadata.class);
        expect(node.getCredentials()).andReturn(new Credentials("tester", "notalot"));
        expect(node.getAdminPassword()).andReturn("testpassword!").atLeastOnce();
        replay(node);
        RunScriptOnNodeUsingSsh testMe = new RunScriptOnNodeUsingSsh(sshFactory, node, exec("echo $USER\necho $USER"),
                wrapInInitScript(false).runAsRoot(true));
        testMe.init();

        sshClient.connect();
        expect(sshClient.getUsername()).andReturn("tester");
        expect(sshClient.getHostAddress()).andReturn("somewhere.example.com");
        expect(sshClient.exec("sudo -S sh <<'RUN_SCRIPT_AS_ROOT_SSH'\n" +
                "testpassword!\n" +
                "echo $USER\n" +
                "echo $USER\n" +
                "RUN_SCRIPT_AS_ROOT_SSH\n")).andReturn(new ExecResponse("root\nroot\n", null, 0));
        sshClient.disconnect();
        replay(sshClient);

        testMe.call();
    }

    public void testUserAddAsRoot() {
        RunScriptOnNodeUsingSsh testMe = new RunScriptOnNodeUsingSsh(sshFactory, node,
                UserAdd.builder().login("testuser").build(),
                wrapInInitScript(false).runAsRoot(true).overrideLoginCredentialWith("test"));

        testMe.init();

        sshClient.connect();
        expect(sshClient.getUsername()).andReturn("tester");
        expect(sshClient.getHostAddress()).andReturn("somewhere.example.com");
        expect(sshClient.exec("sudo sh <<'RUN_SCRIPT_AS_ROOT_SSH'\n" +
                "mkdir -p /home/users/testuser\n" +
                "useradd -s /bin/bash -d /home/users/testuser testuser\n" +
                "chown -R testuser /home/users/testuser\n" +
                "RUN_SCRIPT_AS_ROOT_SSH\n")).andReturn(new ExecResponse("done", null, 0));
        sshClient.disconnect();
        replay(sshClient);

        testMe.call();
    }
}