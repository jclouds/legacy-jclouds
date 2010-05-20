package org.jclouds.aws.ec2.xml;

import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.logging.Logger;

import com.google.common.collect.Sets;

public class RegisterInstancesWithLoadBalancerResponseHandler extends
        ParseSax.HandlerWithResult<Set<String>>
{
    @Inject
    public RegisterInstancesWithLoadBalancerResponseHandler()
    {
    }

    @Resource
    protected Logger      logger      = Logger.NULL;

    private Set<String>   instanceIds = Sets.newLinkedHashSet();
    private StringBuilder currentText = new StringBuilder();
   
    
    
    public void endElement(String uri, String localName, String qName)
    {
        if(qName.equals("InstanceId"))
            instanceIds.add(currentText.toString().trim());
    }
    @Override
    public Set<String> getResult()
    {
        return instanceIds;
    }

    public void characters(char ch[], int start, int length)
    {
        currentText.append(ch, start, length);
    }
}
