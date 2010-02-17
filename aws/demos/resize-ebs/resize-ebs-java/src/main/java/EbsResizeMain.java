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

import org.jclouds.aws.domain.Region;
import org.jclouds.domain.Credentials;
import org.jclouds.tools.ebsresize.InstanceVolumeManager;
import org.jclouds.util.Utils;

import java.io.*;

/**
 * Launcher for EBS resize demo. This is the Java version of what
 * can be written in Ruby (see <jclouds>/aws/demos/resize-ebs/jruby-client)
 *
 * @author Oleksiy Yarmula
 */
public class EbsResizeMain {

    String accessKeyId = "YOUR_ACCESS_KEY_ID";
    String secretKey = "YOUR_SECRET_KEY";
    String instanceId = "AMAZON_INSTANCE_ID (i-xxxxxx)";
    Region region = Region.US_EAST_1;
    int newSize = 6;
    String pathToKeyPair = "";
    String remoteLogin = "ubuntu";
    String remotePassword = "";

    public static void main(String[] args) throws Exception {
        new EbsResizeMain().launch();
    }

    public void launch() throws Exception {

        InstanceVolumeManager manager = new InstanceVolumeManager(accessKeyId, secretKey);

        String privateKey =
                Utils.toStringAndClose(new FileInputStream(pathToKeyPair));

        manager.resizeVolume(instanceId, region, new Credentials(remoteLogin, remotePassword),
                privateKey, newSize);
    }

}
