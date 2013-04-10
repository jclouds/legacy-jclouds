package org.jclouds.ec2.internal;

import org.jclouds.apis.BaseApiLiveTest;
import org.jclouds.ec2.EC2Api;

/**
 * 
 * @author Adrian Cole
 */
public class BaseEC2ApiLiveTest extends BaseApiLiveTest<EC2Api> {
   public BaseEC2ApiLiveTest() {
      provider = "ec2";
   }
}
