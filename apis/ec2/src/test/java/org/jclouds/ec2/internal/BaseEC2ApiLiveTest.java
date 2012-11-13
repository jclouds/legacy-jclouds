package org.jclouds.ec2.internal;

import org.jclouds.apis.BaseContextLiveTest;
import org.jclouds.ec2.EC2Api;
import org.jclouds.ec2.EC2AsyncApi;
import org.jclouds.rest.RestContext;

import com.google.common.reflect.TypeToken;

/**
 * 
 * @author Adrian Cole
 */
public class BaseEC2ApiLiveTest extends BaseContextLiveTest<RestContext<? extends EC2Api, ? extends EC2AsyncApi>> {

   public BaseEC2ApiLiveTest() {
      provider = "ec2";
   }
   
   @Override
   protected TypeToken<RestContext<? extends EC2Api, ? extends EC2AsyncApi>> contextType() {
      return new TypeToken<RestContext<? extends EC2Api, ? extends EC2AsyncApi>>() {
         private static final long serialVersionUID = -5070937833892503232L;
      };
   }

}
