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

package org.jclouds.compute;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.get;
import static org.jclouds.util.Utils.checkNotEmpty;
import static org.testng.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.predicates.OperatingSystemPredicates;
import org.jclouds.rest.HttpClient;
import org.jclouds.scriptbuilder.domain.Statement;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Files;

/**
 * utilities helpful in testing compute providers
 * 
 * @author Adrian Cole
 */
public class ComputeTestUtils {
   public static Statement buildScript(OperatingSystem os) {
      if (OperatingSystemPredicates.supportsApt().apply(os))
         return RunScriptData.APT_RUN_SCRIPT;
      else if (OperatingSystemPredicates.supportsYum().apply(os))
         return RunScriptData.YUM_RUN_SCRIPT;
      else if (OperatingSystemPredicates.supportsZypper().apply(os))
         return RunScriptData.ZYPPER_RUN_SCRIPT;
      else
         throw new IllegalArgumentException("don't know how to handle" + os.toString());
   }

   public static Map<String, String> setupKeyPair() throws FileNotFoundException, IOException {
      String secretKeyFile;
      try {
         secretKeyFile = checkNotNull(System.getProperty("test.ssh.keyfile"), "test.ssh.keyfile");
      } catch (NullPointerException e) {
         secretKeyFile = System.getProperty("user.home") + "/.ssh/id_rsa";
      }
      checkSecretKeyFile(secretKeyFile);
      String secret = Files.toString(new File(secretKeyFile), Charsets.UTF_8);
      assert secret.startsWith("-----BEGIN RSA PRIVATE KEY-----") : "invalid key:\n" + secret;
      return ImmutableMap.<String, String> of("private", secret, "public",
            Files.toString(new File(secretKeyFile + ".pub"), Charsets.UTF_8));
   }

   public static void checkSecretKeyFile(String secretKeyFile) throws FileNotFoundException {
      checkNotEmpty(secretKeyFile, "System property: [test.ssh.keyfile] set to an empty string");
      if (!new File(secretKeyFile).exists()) {
         throw new FileNotFoundException("secretKeyFile not found at: " + secretKeyFile);
      }
   }

   public static void checkHttpGet(HttpClient client, NodeMetadata node, int port) {
      for (int i = 0; i < 5; i++)
         try {
            assert client.get(URI.create(String.format("http://%s:%d", get(node.getPublicAddresses(), 0), port))) != null;
            break;
         } catch (UndeclaredThrowableException e) {
            assertEquals(e.getCause().getClass(), TimeoutException.class);
            try {
               Thread.sleep(1000);
            } catch (InterruptedException e1) {
            }
         }
   }

}
