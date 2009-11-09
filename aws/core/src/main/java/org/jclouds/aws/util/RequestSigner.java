package org.jclouds.aws.util;

import org.jclouds.http.HttpRequest;

/**
 * 
 * @author Adrian Cole
 */
public interface RequestSigner {

   String createStringToSign(HttpRequest input);

   String signString(String toSign);

}