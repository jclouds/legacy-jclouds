package org.jclouds.elb.binders;

import static org.jclouds.aws.util.AWSUtils.indexStringArrayToFormValuesWithStringFormat;

import javax.inject.Singleton;

import org.jclouds.http.HttpRequest;
import org.jclouds.rest.Binder;

/**
 * Binds the String [] to form parameters named with LoadBalancerNames.member.index
 * 
 * @author Adrian Cole
 */
@Singleton
public class BindLoadBalancerNamesToIndexedFormParams implements Binder {
   @Override
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      return indexStringArrayToFormValuesWithStringFormat(request, "LoadBalancerNames.member.%s", input);
   }

}
