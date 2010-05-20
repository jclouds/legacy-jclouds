package org.jclouds.aws.ec2.xml;

import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.jclouds.aws.ec2.EC2;
import org.jclouds.aws.ec2.domain.ElasticLoadBalancer;
import org.jclouds.aws.ec2.domain.ElasticLoadBalancer.AppCookieStickinessPolicy;
import org.jclouds.aws.ec2.domain.ElasticLoadBalancer.LBCookieStickinessPolicy;
import org.jclouds.aws.ec2.domain.ElasticLoadBalancer.LoadBalancerListener;
import org.jclouds.aws.ec2.util.EC2Utils;
import org.jclouds.date.DateService;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.logging.Logger;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.xml.sax.Attributes;

import com.google.common.collect.Sets;

public class DescribeLoadBalancersResponseHandler extends
        ParseSax.HandlerWithResult<Set<ElasticLoadBalancer>>
{
    @Inject
    public DescribeLoadBalancersResponseHandler(@EC2 String defaultRegion)
    {
        this.defaultRegion = defaultRegion;
        this.listenerHandler = new LoadBalancerListenerHandler();
    }

    @Inject
    protected DateService                     dateService;

    @Resource
    protected Logger                          logger                        = Logger.NULL;

    private Set<ElasticLoadBalancer>          contents                      = Sets
                                                                                    .newLinkedHashSet();
    private StringBuilder                     currentText                   = new StringBuilder();
    private final String                      defaultRegion;
    private final LoadBalancerListenerHandler listenerHandler;

    private boolean                           inListenerDescriptions        = false;
    private boolean                           inInstances                   = false;
    private boolean                           inAppCookieStickinessPolicies = false;
    private boolean                           inLBCookieStickinessPolicies  = false;
    private boolean                           inAvailabilityZones           = false;
    private boolean                           inLoadBalancerDescriptions    = false;

    private ElasticLoadBalancer               elb;
    private AppCookieStickinessPolicy         appCookieStickinessPolicy;
    private LBCookieStickinessPolicy          lBCookieStickinessPolicy;

    public void startElement(String uri, String localName, String qName,
            Attributes attributes)
    {
        
        if (qName.equals("ListenerDescriptions") || inListenerDescriptions)
        {
            inListenerDescriptions = true;
        }
        else if (qName.equals("AppCookieStickinessPolicies"))
        {
            inAppCookieStickinessPolicies = true;
        }
        else if (qName.equals("LBCookieStickinessPolicies"))
        {
            inLBCookieStickinessPolicies = true;
        }
        else if (qName.equals("LoadBalancerDescriptions"))
        {
            inLoadBalancerDescriptions = true;
        }
        else if (qName.equals("Instances"))
        {
            inInstances = true;
        }
        else if (qName.equals("AvailabilityZones"))
        {
            inAvailabilityZones = true;
        }

        if (qName.equals("member"))
        {
            if (!(inListenerDescriptions || inAppCookieStickinessPolicies || inInstances
                    || inLBCookieStickinessPolicies || inAvailabilityZones))
            {
                elb = new ElasticLoadBalancer();
            }
        }
    }

    public void endElement(String uri, String localName, String qName)
    {
        logger.info(qName);
        
        //if end tag is one of below then set inXYZ to false
        if (qName.equals("ListenerDescriptions"))
        {
            inListenerDescriptions = false;
        }
        else if (qName.equals("AppCookieStickinessPolicies"))
        {
            inAppCookieStickinessPolicies = false;
        }
        else if (qName.equals("LBCookieStickinessPolicies"))
        {
            inLBCookieStickinessPolicies = false;
        }
        else if (qName.equals("LoadBalancerDescriptions"))
        {
            inLoadBalancerDescriptions = false;
        }
        else if (qName.equals("Instances"))
        {
            inInstances = false;
        }
        else if (qName.equals("AvailabilityZones"))
        {
            inAvailabilityZones = false;
        }
        

        if (qName.equals("DNSName"))
        {
            elb.setDnsName(currentText.toString().trim());
        }
        else if (qName.equals("LoadBalancerName"))
        {
            elb.setName(currentText.toString().trim());
        }
        else if (qName.equals("InstanceId"))
        {
            elb.getInstanceIds().add(currentText.toString().trim());
        }

        else if (qName.equals("member"))
        {

            if (inAvailabilityZones)
            {
                elb.getAvailabilityZones().add(currentText.toString().trim());
            }
            else if (!(inListenerDescriptions || inAppCookieStickinessPolicies || inInstances
                    || inLBCookieStickinessPolicies || inAvailabilityZones))
            {
                try
                {
                    String region = EC2Utils.findRegionInArgsOrNull(request);
                    if (region == null)
                        region = defaultRegion;

                    elb.setRegion(region);
                    contents.add(elb);
                }
                catch (NullPointerException e)
                {
                    logger.warn(e, "malformed load balancer: %s", localName);
                }

                this.elb = null;

            }

        }

        currentText = new StringBuilder();
    }

    @Override
    public Set<ElasticLoadBalancer> getResult()
    {
        return contents;
    }

    public void characters(char ch[], int start, int length)
    {
        currentText.append(ch, start, length);
    }
    
    @Override
    public void setContext(GeneratedHttpRequest<?> request) {
       listenerHandler.setContext(request);
       super.setContext(request);
    }

    public class LoadBalancerListenerHandler extends
            ParseSax.HandlerWithResult<Set<LoadBalancerListener>>
    {
        private Set<LoadBalancerListener> listeners   = Sets.newHashSet();
        private StringBuilder             currentText = new StringBuilder();
        private LoadBalancerListener      listener;

        public void startElement(String uri, String name, String qName,
                Attributes attrs)
        {
            if (qName.equals("member"))
            {
                listener = new LoadBalancerListener();
            }
        }

        public void endElement(String uri, String name, String qName)
        {
            if (qName.equals("Protocol"))
            {
                listener.setProtocol(currentText.toString().trim());
            }
            else if (qName.equals("LoadBalancerPort"))
            {
                listener.setLoadBalancerPort(Integer.parseInt(currentText
                        .toString().trim()));
            }
            else if (qName.equals("InstancePort"))
            {
                listener.setInstancePort(Integer.parseInt(currentText
                        .toString().trim()));
            }
            else if (qName.equals("member"))
            {
                listeners.add(listener);
            }
            
            currentText = new StringBuilder();

        }

        @Override
        public Set<LoadBalancerListener> getResult()
        {
            return listeners;
        }

        public void characters(char ch[], int start, int length)
        {
            currentText.append(ch, start, length);
        }

    }
}
