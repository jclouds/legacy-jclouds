package org.jclouds.aws.ec2.binders;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.aws.ec2.domain.UserIdGroupPair;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.Binder;
import org.jclouds.rest.internal.GeneratedHttpRequest;

/**
 * Binds the String [] to query parameters named with GroupName.index
 * 
 * @author Adrian Cole
 * @since 4.0
 */
public class BindUserIdGroupPairToSourceSecurityGroupFormParams implements Binder {

   @SuppressWarnings("unchecked")
   public void bindToRequest(HttpRequest request, Object input) {
      checkArgument(checkNotNull(request, "request") instanceof GeneratedHttpRequest,
               "this binder is only valid for GeneratedHttpRequests!");
      checkArgument(checkNotNull(input, "input") instanceof UserIdGroupPair,
               "this binder is only valid for UserIdGroupPair!");
      UserIdGroupPair pair = (UserIdGroupPair) input;
      GeneratedHttpRequest generatedRequest = (GeneratedHttpRequest) request;
      generatedRequest.addFormParam("SourceSecurityGroupOwnerId", pair.getUserId());
      generatedRequest.addFormParam("SourceSecurityGroupName", pair.getGroupName());
   }
}