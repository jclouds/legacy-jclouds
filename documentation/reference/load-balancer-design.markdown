---
layout: jclouds
title: Design of our Load Balancer Abstraction
---
# Design of our Load Balancer Abstraction

### Design  Goals
* Easy integration with jclouds ComputeService
* Support for members not defined in !ComputeService
* Look/Feel like other jclouds java/clojure abstractions

## Prior Design 
  * [model](https://github.com/jclouds/jclouds/blob/1.0-beta-9/loadbalancer/src/main/java/org/jclouds/loadbalancer/domain/LoadBalancerMetadata.java)
  * [api](https://github.com/jclouds/jclouds/blob/1.0-beta-9/loadbalancer/src/main/java/org/jclouds/loadbalancer/LoadBalancerService.java)
  * [live test](https://github.com/jclouds/jclouds/blob/1.0-beta-9/loadbalancer/src/test/java/org/jclouds/loadbalancer/BaseLoadBalancerServiceLiveTest.java)

### APIs to cover with our abstraction 

## ELB
Elastic Load Balancers in Amazon Web Services.

### Documentation
  * [API Documentation](http://docs.amazonwebservices.com/ElasticLoadBalancing/latest/APIReference/)

### jclouds code
  * [model](https://github.com/jclouds/jclouds/blob/master/sandbox-apis/elb/src/main/java/org/jclouds/elb/domain/LoadBalancer.java)
  * [JClouds API](https://github.com/jclouds/jclouds/blob/master/sandbox-apis/elb/src/main/java/org/jclouds/elb/ELBClient.java)
  * [live test](https://github.com/jclouds/jclouds/blob/master/sandbox-apis/elb/src/test/java/org/jclouds/elb/ELBClientLiveTest.java)

### Notes


## Terremark vCloudExpress

### Documentation
  * [API Documentation](https://community.vcloudexpress.terremark.com/en-us/product_docs/m/vcefiles/2342/download.aspx)

### jclouds code 
  * model [InternetService](https://github.com/jclouds/jclouds/blob/master/common/trmk/src/main/java/org/jclouds/vcloud/terremark/domain/InternetService.java) 
[Node](https://github.com/jclouds/jclouds/blob/master/common/trmk/src/main/java/org/jclouds/vcloud/terremark/domain/Node.java)
  * [API](https://github.com/jclouds/jclouds/blob/master/common/trmk/src/main/java/org/jclouds/vcloud/terremark/TerremarkVCloudClient.java)
  * [live test](https://github.com/jclouds/jclouds/blob/master/common/trmk/src/test/java/org/jclouds/vcloud/terremark/TerremarkClientLiveTest.java)

### Notes

## Terremark Enterprise Cloud
Like their express offering, but including additional services such as
trusted network groups, http monitors, back services, etc.

### Documentation 
  * [API Documentation](http://support.theenterprisecloud.com/kb/default.asp?id=922&Lang=1&SID=)

### jclouds code 
  * model [IntenetService](https://github.com/jclouds/jclouds/blob/master/common/trmk/src/main/java/org/jclouds/vcloud/terremark/domain/InternetService.java) [Node](https://github.com/jclouds/jclouds/blob/master/common/trmk/src/main/java/org/jclouds/vcloud/terremark/domain/Node.java)
  * [API](https://github.com/jclouds/jclouds/blob/master/common/trmk/src/main/java/org/jclouds/vcloud/terremark/TerremarkVCloudClient.java)
  * [Live test](https://github.com/jclouds/jclouds/blob/master/common/trmk/src/test/java/org/jclouds/vcloud/terremark/TerremarkClientLiveTest.java)

### Notes 
jclouds model is not up to date, supporting the eCloud features

## GoGrid
### Documentation 
  * [API Documentation](http://docs.amazonwebservices.com/ElasticLoadBalancing/latest/APIReference/)

### jclouds code
  * [model](https://github.com/jclouds/jclouds/blob/master/providers/gogrid/src/main/java/org/jclouds/gogrid/domain/LoadBalancer.java)
  * [API](https://github.com/jclouds/jclouds/blob/master/providers/gogrid/src/main/java/org/jclouds/gogrid/services/GridLoadBalancerClient.java)
  * [test](https://github.com/jclouds/jclouds/blob/master/providers/gogrid/src/test/java/org/jclouds/gogrid/GoGridLiveTestDisabled.java)

### Notes


## CloudStack
Currently supported as a standalone product and as a service providers including [Ninefold](http://ninefold.com/)

### Documentation
  * [API Documentaiton](http://download.cloud.com/releases/2.2.0/api/TOC_User.html)

### jclouds code

  * [jclouds model](https://github.com/jclouds/jclouds/blob/master/sandbox-apis/cloudstack/src/main/java/org/jclouds/cloudstack/domain/LoadBalancerRule.java)
  * [jclouds api](https://github.com/jclouds/jclouds/blob/master/sandbox-apis/cloudstack/src/main/java/org/jclouds/cloudstack/features/LoadBalancerClient.java)
  * [live test](https://github.com/jclouds/jclouds/blob/master/sandbox-apis/cloudstack/src/test/java/org/jclouds/cloudstack/features/LoadBalancerClientLiveTest.java)

### Notes

## Rackspace CloudLoadBalancers

Slight [deviation](http://wiki.openstack.org/LoadBalancing?action=AttachFile&do=view&target=Difference+between+RackSpace+Cloud+LoadBalancers+API+and+OpenStack+API+Proposal.pdf) 
from OpenStack Load Balancing api.

Differences between the existing Rackspace vs OpenStack version

  * single vip/lb
  * want to reference existing vips by address, not id
  * removed draining status/condition
  * separated out weight as independent from round robin
  * disallow updating of protocol/port
  * has usage report function, but only bytes in/out
  * removed access control as an explicit function (defer to global services)
  * only support LEAST_CONNECTIONS and ROUND_ROBIN by default

### Documentation
  * [API Documentation](http://docs.rackspacecloud.com/loadbalancers/api/v1.0/clb-devguide/content/ch04s01.html)

### jclouds code
  * [jclouds model](https://github.com/jclouds/jclouds/blob/master/providers/cloudloadbalancers-us/src/main/java/org/jclouds/cloudloadbalancers/domain/LoadBalancer.java)
  * [jclouds api](https://github.com/jclouds/jclouds/blob/master/providers/cloudloadbalancers-us/src/main/java/org/jclouds/cloudloadbalancers/features/LoadBalancerClient.java)
  * [live test](https://github.com/jclouds/jclouds/blob/master/providers/cloudloadbalancers-us/src/test/java/org/jclouds/cloudloadbalancers/features/LoadBalancerClientLiveTest.java)

### Notes

## SoftLayer

SoftLayer includes the following load balancers.  We will need to decide which offerings make sense to include in this abstraction.

  * Geo Load Balancer - a DNS-based system that uses rules and health checks
  * datacenter load balancer - hardware device to do ip and port load balancing for use with a single datacenter
  * Netscaler VPX - full access to a Netscaler devicewhich can be a layer 7 load balancer

### Documentation
  * [overview](http://knowledgelayer.softlayer.com/categories/Load+Balancing/)
  * [API Documentation](http://sldn.softlayer.com/wiki/index.php/SoftLayer_Network_LoadBalancer_Service_%28type%29)

### jclouds code
Load Balancer code hasn't yet been written
  * [jclouds provider](https://github.com/jclouds/jclouds/blob/master/sandbox-providers)

### Notes

## Discussions
  * [loadbalancer design now -> code sprint early next week](http://groups.google.com/group/jclouds-dev/browse_thread/thread/481fe09da50ba241)

