package org.jclouds.aws.ec2.binders;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.net.InetAddress;

import org.jclouds.aws.ec2.util.EC2Utils;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.Binder;
import org.jclouds.rest.internal.GeneratedHttpRequest;

/**
 * Binds the String [] to form parameters named with InstanceId.index
 * 
 * @author Adrian Cole
 * @since 4.0
 */
public class BindInetAddressesToIndexedFormParams implements Binder {

   @SuppressWarnings("unchecked")
   public void bindToRequest(HttpRequest request, Object input) {
      checkArgument(checkNotNull(request, "input") instanceof GeneratedHttpRequest,
               "this binder is only valid for GeneratedHttpRequests!");
      checkArgument(checkNotNull(input, "input") instanceof InetAddress[],
               "this binder is only valid for InetAddress[] : " + input.getClass());
      InetAddress[] addresses = (InetAddress[]) input;
      String[] addressStrings = new String[addresses.length];
      for (int i = 0; i < addresses.length; i++) {
         addressStrings[i] = addresses[i].getHostAddress();
      }
      EC2Utils.indexFormValuesWithPrefix((GeneratedHttpRequest<?>) request, "PublicIp",
               addressStrings);
   }

}