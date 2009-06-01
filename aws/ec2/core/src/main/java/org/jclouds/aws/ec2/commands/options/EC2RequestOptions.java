package org.jclouds.aws.ec2.commands.options;

import org.jclouds.aws.ec2.reference.EC2Constants;
import org.jclouds.http.options.HttpRequestOptions;
import org.joda.time.DateTime;

import com.google.inject.name.Named;

/**
 * 
 * defines the interface needed to properly sign EC2 QUERY requests.
 * 
 * @author Adrian Cole
 */
public interface EC2RequestOptions extends HttpRequestOptions {

   /**
    * @see org.jclouds.aws.ec2.reference.CommonEC2Parameters#ACTION
    */
   String getAction();

   /**
    * @see org.jclouds.aws.ec2.reference.CommonEC2Parameters#AWS_ACCESS_KEY_ID
    * @see org.jclouds.aws.ec2.reference.CommonEC2Parameters#SIGNATURE
    */
   EC2RequestOptions signWith(@Named(EC2Constants.PROPERTY_AWS_ACCESSKEYID) String accessKey,
            @Named(EC2Constants.PROPERTY_AWS_SECRETACCESSKEY) String secretKey);

   /**
    * @see org.jclouds.aws.ec2.reference.CommonEC2Parameters#EXPIRES
    */
   EC2RequestOptions expireAt(DateTime time);

   /**
    * @see org.jclouds.aws.ec2.reference.CommonEC2Parameters#TIMESTAMP
    */
   EC2RequestOptions timeStamp();

   /**
    * @see org.jclouds.http.HttpHeaders#HOST
    */
   EC2RequestOptions usingHost(String hostname);

}
