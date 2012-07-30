package org.jclouds.ec2.internal;

import org.jclouds.apis.BaseContextLiveTest;
import org.jclouds.ec2.EC2ApiMetadata;
import org.jclouds.ec2.EC2AsyncClient;
import org.jclouds.ec2.EC2Client;
import org.jclouds.rest.RestContext;

import com.google.common.reflect.TypeToken;

/**
 * 
 * @author Adrian Cole
 */
public class BaseEC2ClientLiveTest extends BaseContextLiveTest<RestContext<? extends EC2Client, ? extends EC2AsyncClient>> {

   public BaseEC2ClientLiveTest() {
      provider = "ec2";
   }
   
   @Override
   protected TypeToken<RestContext<? extends EC2Client, ? extends EC2AsyncClient>> contextType() {
      return EC2ApiMetadata.CONTEXT_TOKEN;
   }

}
