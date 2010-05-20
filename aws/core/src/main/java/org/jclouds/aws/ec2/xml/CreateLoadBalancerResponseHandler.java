package org.jclouds.aws.ec2.xml;

import javax.annotation.Resource;

import org.jclouds.http.functions.ParseSax.HandlerWithResult;
import org.jclouds.logging.Logger;

public class CreateLoadBalancerResponseHandler extends HandlerWithResult<String> {

    @Resource
    protected Logger logger = Logger.NULL;

    private String dnsName;
    private StringBuilder currentText = new StringBuilder();

    protected String currentOrNull() {
       String returnVal = currentText.toString().trim();
       return returnVal.equals("") ? null : returnVal;
    }

    public void endElement(String uri, String name, String qName) {
       if (qName.equals("DNSName")) {
          dnsName = currentOrNull();
       }
       currentText = new StringBuilder();
    }

    public void characters(char ch[], int start, int length) {
       currentText.append(ch, start, length);
    }
    
    @Override
    public String getResult() {
       return dnsName;
    }
}