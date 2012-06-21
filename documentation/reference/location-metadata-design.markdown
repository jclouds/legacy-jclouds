---
layout: jclouds
title: Summary of motivation and design choices for the Location Metadata API
---
# Summary of motivation and design choices for the Location Metadata API

## Introduction

This project aims to enhance the current Location API in jclouds and offer:

  * additional metadata describing location details for service providers' offerings
  * a proposed format both for consuming and producing this metadata (ex. REST)

This project was initially kicked off by Cloudsoft Corporation (Alex), 
as a means to close the gaps between the current jclouds api and those needed by location-aware services.  
It includes samples and designs contributed by Cloudsoft, the jclouds community, and those we interview.

## Background and Motivation

Before this project, jclouds had a compatible Location type across cloud providers and 
across services such as BlobStore and ComputeService.  Through a common ancestor, 
objects can describe their location from a placement perspective, and this data includes an id, 
scope, description, and parent.  The scope is used to clarify the intended usage.  
For example, a Node can be launched into a Region scope, but it inevitably is a part of a smaller scope,
such as Zone (datacenter) or Host. Through parent relationships, objects can be aware of 
collocation at various levels.

Here's an example of the current location object serialized in json

{% highlight json %}
{
    "scope": "REGION",
    "id": "us-east-1",
    "description": "us-east-1",
    "parent": {
        "scope": "PROVIDER",
        "id": "ec2",
        "description": "ec2"
    }
}
{% endhighlight %}

The data in these location objects were a combination of well known facts and API accessible information.

Cross-provider, we were limited by means that there wasn't enough information to decide 
if different services were in the same network, region, jurisdiction, etc.  In essence, 
even though we could compare locations, there wasn't enough information to decide on the
 data alone whether 2 cloud services are near each other.

Inside jclouds, our goal is to make cloud computing usage as portable as possible.
Part of this means removing knowledge on keys, or expertise in clouds as a prerequisite to using them.
For example, a jclouds-based system should be able to facilitate broker services that 
help lead users to the right cloud.  Users should not have to know internal details of 
clouds like Amazon, for example their region names and zone ids.

This project aims to expose just enough details that users can create cloud portable applications who 
have physical, juridictional, or service level concerns.  

## New Use Cases

`TODO`

### Proposed Additions

Proposed additions to the jclouds includes concepts such as:

  * *Near-Term:*  
    * properties such as postal address, jurisdictions, coordinates
    * means to supply/consume custom properties
    * integration with [http://www.cloudaudit.org cloud audit]
  * *Long-Term:* 
    * collocation and billing relationships
    * network topology
    * latency to major internet peers

### Success Factors 
The design is considered successful when
  
 * jclouds is easier and simpler to use for those new use cases that have location mandates
 * jclouds is not more difficult to use or understand for those who don't care about location
 * jclouds supports more usecases, yet introduces no new dependencies.

### Implementation Advice

The current Location object in jclouds will need more properties than before.  
It also will need a place to store custom properties.  

Advise on usage in contexts such as BlobStore and ComputeService need to be made.  
For example, TemplateBuilder may want to include extra methods for strongly typed properties.

#### constructing Location objects 
To facilitate creating the Location objects that will serialize to the above, 
jclouds will include a Builder class that constructs the revised object.  
Third-party libraries or tools will likely supply other means, such as a groovy DSL.

#### custom properties 
location metadata can include complex types such as lists of uris and numbers.  our metadata 

#### optional extensions
A clear extension path must be defined for those who want to supply their own templateBuilder methods,
 location lists, etc.  For example, right now both apis have a listAssignableLocations method. 
 This may need to be supplied from an overriding source, for example a REST service or a file. 


## Data Format 
jclouds integration will be via a revision to the immutable Location class.  
To facilitate overrides, or exports to other systems, a data format should be identified.  
This must include at least a JSON syntax, and may include XML schema.

An example of such syntax is below
//TODO 

## Location Suppliers 

As exists today, we'd expect initial revisions to continue combining well known facts with API data.  
This should be based only on information provided by the relevant service provider and 
verified where appropriate.  Well known facts may be supplied in a cleaner way than they are 
currently (often in constant classes)

We prefer that more data be supplied by an external party, such as the service providers themselves.  
Ideally, revisions to cloud apis, or independent "location" sources will appear,
 which jclouds would integrate with.   For this reason, it must be possible for jclouds to accept 
Location objects from an alternate supply source, which may be written by end users, trusted third parties,
 or service providers.

## Interviews 

We've conducted interviews with a number of experts in various technology areas about 
their opinions on this topic and/or the design.  More interviews will be added as they are scheduled.

### Jason Lochhead CTO at Terremark Worldwide, Inc 

[Terremark](http://www.terremark.com/services/theenterprisecloud.aspx) offers cloud hosting, 
and services worldwide, including enterprise cloud locations in US and NE.  
Jclouds currently integrates with both of terremark's compute apis (trmk-ecloud, trmk-vcloudexpress), 
and Jason (@jasonlochhead) has been doing a great job keeping us in the feedback loop of their products.

On the call during 3/12/2010 were Alex Heneveld, Adrian Cole, and Jason Lochhead.  Here's a summary:

Many Terremark customers have requirements related to security compliance and audit. 
Standards compliance metadata including the following could prove useful

  * PCI 
  * SAS70
  * FISMA (low, moderate, high)
  * Update Institute tier 1-4 (from Alex)

Moreover, datacenters' SLAs, and power/efficiency metadata are differentiators some are interested in.

From an API perspective, XML would be useful as an option for rendering, and 
the location domain itself could be considered for future incorporation into Terremark APIs.

### Richard Davies, CEO of ElasticHosts

Richard (@richard_davies)'s company runs compute cloud services in their own and 
partner datacenters as well licenses their stack under the brand [ElasticStack](http://www.elasticstack.com/).
 jclouds is integrating with ElasticStack api via issue 412. 

On the call during 3/12/2010 were Alex Heneveld, Adrian Cole, and Jason Lochhead.  

Here's a summary:

Richard has the following suggestions that would help make the API relevant:

  * expose location from legal purposes.  Ex know that one is in UK, not just in EU-WEST.  
This could be exposed via ISO-3166
  * expose means of measuring latency.  For example, expose a representative edge ip address or 
hostname for measuring tools such as http://www.ris.ripe.net/cgi-bin/lg/index.cgi
  * expose a normalized price per hour model. ex VM with 2 cores at 2GHz each, 2GB RAM, 100GB disk.
  * expose SLA normalized in results basis.  ex. what percengroupe return will one receive based on a 
standard VM being down for a number of hours.

Examples in discussion included the need to be near stock exchange from a <5ms latency perspective. 
 Near a datacenter from a physical drive-time perspective. 
 Examples from Elastic hosts practice included pricing/sla models such as 
The credit is 100x if the incident is over 15 minutes, so for 2 hours on a 50 cent VM it would be 2*0.5*100 = $10

  * http://www.elastichosts.com/cloud-hosting/terms-of-service
  * http://www.elastichosts.com/cloud-hosting/pricing

### Bradford Stephens, Founder, Drawn to Scale
Bradford Stephens (@LuciousPear) founded the cloud-scale data solution [Drawn to Scale](http://drawntoscalehq.com/),
 as well actively participates in cloud and nosql communities, especially in the Seattle area.

On the call during 8/12/2010 were Alex Heneveld, Adrian Cole, and Jason Lochhead.  
Here's a summary:

A crud json service that allows the user to manipulate custom properties regarding location would fit well into 
existing processes. For example, it would allow the user to drill down into metadata related to the 
location of resources as a part of a troubleshooting or performance management process.

Most customers have an internal set of metrics that matters to them, 
and they combine this with reports about cross cloud performance. 
Typically disk-i/o is more important due to the nature of the application domain (data in this case).  
A service that provides reference data of benchmark results (or deferral to such a service) can help 
customers discover the best "bang for the buck".


In the Cloud, don't conflate 'location' and 'jurisdiction'!

### Rich Miller, Telematica Inc.

Rich Miller is CEO of [Telematica Inc.](http://www.telematica.com/about-rhm/) and 
Managing Director of Telematica's consulting practice in product strategy and 
business development for cloud computing, web-scale platforms and networked services. 

Here are notes from a call with Rich Miller, Alex H and Adrian C  on 9/12/2010, as typed up by Rich M.

#### Preface 

  * In the pursuit of compliance with policy under which production computing must be conducted, 
cloud infrastructure vendors and IaaS consumers have recently become focused on the requirement that 
infrastructure provide information (meta-data) regarding the physical location of workloads.  
  * The information required by management systems and applications in order to meet compliance criteria includes 
various types of information about the physical location of infrastructure components (both physical and virtual), 
and the physical location of data. 
    * In great measure, the interest is based on the definitions of compliance that cover data sovereignty 
and data residency - - legal constraints or industry policy that mandate
      * physical boundaries beyond which a datum covered by policy must not reside (workload at rest)
      * physical boundaries beyond which a datum covered by policy must not transit (workload in motion) 
    * Because the objective of these mandates is the assurance of  'data safety', actions that minimize 
the unauthorized release of sensitive data, they often include  descriptions of the care, treatment or 
protection that must be applied to the management or storage of the item of interest.
        * E.g. specific types of encryption, placement of storage behind network firewalls, access controls, ...

#### Jurisdiction
 
 * Jurisdiction: Cloud Infrastructure must present to management systems (and applications with 
responsibility for compliance) a means to ascertain the relevant jurisdiction with the authority 
to define compliance / non-compliance,
 which often includes (as part of the relevant basis on which compliance is assessed) information
 about physical location -- e.g. location metadata.
  
* Multiple jurisdictions: Any given workload may be subject to policy established by more than one 
jurisdictional authority at the same time.
    * At one and the same time, a workload may be subject to jurisdiction that's hierarchical 
(e.g. governmental jurisdiction defined by nation, region, and city ), or multi-dimensional and 
orthogonal (e.g. governmental and, simultaneously, an industry consortium's agreement for exchange of 
transactional data)
    * Thus, the infrastructure must present to the responsible party all of the known and 
relevant jurisdictions which define compliant behavior.
    * At minimum, the infrastructure must be tasked with presenting (on request ?) the identities of 
those jurisdictions that have authority to define compliant behavior with respect to a workload. 
      *  The question is by what means does the responsible party can obtain a complete list of 
applicable compliance rules once the list of all relevant jurisdictions have been supplied.
  * Jurisdiction does not necessarily imply geography: Jurisdiction and the policy which it mandates 
may be tied to the ownership of a workload (e.g. the responsibility that comes with data ownership) or
 with the stewardship of a workload (e.g. the physical transport or transient storage of 
personal identifying information by an intermediary).  
      *  That is, independent of the location of the workload, the owner of a workload or the steward 
to which the owner has delegated compliance responsibility
  * Definition of JURISDICTION (From www.merriam-webster.com/dictionary )
    # the power, right, or authority to interpret and apply the law
      * the authority of a sovereign power to govern or legislate
      * the power or right to exercise authority : control
     # the limits or territory within which authority may be exercised
  * What's really required of an infrastructure is:
    * the unambiguous identification of any and all the jurisdictions that have the authority to 
define policy, and 
    * the criteria by which to ascertain whether adherence to that policy has been accomplished
  * Having once provided the unambiguous identification of the jurisdiction(s)

#### Data Sovereignty
  * There does not yet exist (that I can find) a generally-accepted definition of Data Sovereignty.
  * I use it to mean the right of a jurisdictional authority to have its data treated the way it wants.  
The jurisdictional authority is most commonly a government, though (per our earlier discussion) may not 
always be governmental.
    * It is a relatively new notion but one that will undoubtedly be treated with a great amount of 
respect by everyone in the business of managing and using information, and the Safe Harbor framework is 
a good example of that deference.  (See these remarks by US CIO Vivek Kundra for another.)

#### Stewardship
  * An important and relevant concept to introduce to this conversation is the notion of 
Stewardship and to distinguish it from Ownership.
  * Definition of STEWARDSHIP (From www.merriam-webster.com/dictionary )
    # the office, duties, and obligations of a steward
    # the conducting, supervising, or managing of something; especially : the careful and 
responsible management of something entrusted to one's care &lt;stewardship of natural resources&gt;
  * In considering responsibility to meet compliance criteria, both owner and steward 
(the party to which responsibility has been delegated) are subject to policy, and may be 
treated differently by a jurisdiction.  
    * For example, the 'owner' of information subject to compliance may be obligated to 
encrypt or protect data using specified methods before delegating the transport of that 
data to a network carrier.  The network carrier may be obligated to protect its switching 
fabric in particular ways, but may not (completely) responsible for the safety of the data
 while in transit and in the care of the network.

#### Overloading the concept of Address (Location)
  * Address information (whether a postal address or a network address) has become an overloaded concept.
  A classic error in networking has been the conflation of the concepts of 
a name / identity, address / location and the route taken to get to a specific location from
 an identifiable starting point.
    * Too often, IP addresses have been used as 'names'. 
    * We need to recall that a name is supposed to remain invariant and (to the degree possible)
 provide an unambiguous reference to an entity... no matter where the entity is located, and
 should not change as a result of moving.

### Oren Teich, Product Guy at Heroku

Heroku is home to over 100,000 ruby applications.  It currently resides in Amazon's US-EAST-1 EC2 region. 
 Oren Teich is the self-labeled "Product Guy" for Heroku.  
The following is a summary of feedback from Oren on the location discussion we've had.

They've been satisfied by the technology, the zone/region concepts, and 
do not feel limited by what's currently available in EC2.  
Oren suggests they've not heard anyone caring about coordinates, details such as 
which state a service is in, and moreover would never say you're in "this datacenter."   
Rather customers are interested in whether the service is in a country or not and latency between systems.

Oren presents this following: "What makes people run code in different places?"
  
* to give people better network latency w/in US
  * to give access to jurisdiction

are these ideas red herrings?  Heroku performed tests revealing no strong relationship between 
geographic location and network latency. Moreover jurisdictional availability is currently limited;
 Amazon's EU region only exists in Ireland, which doesn't help those in Swizerland, for example.

Heroku's system includes the concept of add-on providers.  
Most add-ons are not latency sensitive.  
For example, XenCode is a video transcribing service which makes no external calls.  
25% of add-ons are latency sensitive.  For example, MemCache as a service only works well when
 it has lan-grade (<2ms) latency between nodes.

Over the course of the interview, it became clear that for the Heroku usecase, 
most location-related decisions made today are on basis of whether or not you 
can achieve lan level latency (.5-2ms).  Due to the nature of their products (ex. not an HPC application),
 bandwidth has not been an issue.

Oren has interest in tooling that can expose compliance capabilities of services, 
and a means to model cost tradeoffs.  With something like this, a customer could tick 
requirements such as PCI compliance and see plug-ins greyed out.  Moreover interesting would 
be a tool to replicate a configuration in a specific location, perhaps visually represented as a map.  
Using a tool like this, validating or refuting concerns about topology decisions could be made easily.

Complications about multi-cloud noted by Oren included transitive impact on add-on providers. 
 For example, if Heroku were to operate in a different cloud,
 it would force latency-sensitive 
add-on providers to deploy into that same IaaS.  Also introduced is complexity
 related to terminating public IP addresses, and which pieces of architecture are in multiple clouds.

### John Considine and Seshu Pasam, CloudSwitch 

CloudSwitch offers an innovative software appliance enables enterprises to run 
their applications in the right cloud computing environment—securely, simply and without changes.  
They are current contributors to jclouds and use sophisticated features from offerings such as Terremark eCloud.

John Considine (Founder and CTO) and Seshu Pasam (Engineer) have the 
following feedback related to our location work.

According to John, if you are building a location management layer, 
focus on how to express what you are looking for.  For example, 
he suggests availability zones are the worst of both worlds, as you have to be explicit 
about where to place a workload.  This binds the provider and places the burden on the end-user.  
As a user, we have to choose to either place server1 in a specific zone, or 
let Amazon decide.
  Once that server is placed, we have to work out where to place the next server.  
So now, the user has to manage the locations.  Ideally, John would like to say this server
 shares *nothing* with that server, and let the cloud provider decide where to place it. 
 Ultimately, this could have an increased granularity to say not in same region,
 not in same rack, not on same server, not with same backbone network.  

The idea is for the user to specify the requirements and allow the cloud provider
 to orchestrate the fulfillment of the requirements..

As your placing workloads in different clouds, and they are interacting.  
you want to get to "is possible" scenarios. operation of the environment 
(ex. does it hold up in practice), down the line: (can you do real-time switching)?

Example use cases include

  * location of a blobstore container that is near a compute object.
  * not-with constraints such as this node not in the same datacenter as X resource.
  * insight into replication or duplication of content such as ISOs, which can establish a coverage pattern or 
strategy answering the question of when to copy vs re-use content.

With regards to specific data points, ISO 3166-2,
 lat/longitude are useful to CloudSwitch, others less so. 
 ISO 3166-2 subdivision US-CA covers jurisdiction concerns 
(scenarios such as state info from california must not leave california).  
Latitude+longitude is useful for "as the crow flies" distance for disaster geo separation.
 In general, US companies are very reluctant to give a street address for data center locations. 
 Outside a jurisdiction, physical address has no value, as it doesn't imply network.
 Some aspects of location are challenging to model.  For example latency, bandwidth, 
and the ephemeral nature of some  locations (ex. vCloud vDC).

How do you measure latency? For example, a choice of ping time doesn't work in Amazon:
 they de-prioritize ICMP traffic, skewing the results.
 Also provider networks also have bandwidth throttles, 
and in some rare cases (ec2 micro) instances themselves have variable bandwidth capability.   
Another issue is that the locations themselves can be ephemeral.  
For example, there are deployment limits to certain virtualization infrastructure.  
Due to fragmentation or resource exhaustion, we may need to reconstruct a "vdc" somewhere else.  
There is also consideration of capabilities between locations, ex. provider to internet backbone, between nodes?  
For example, in Amazon  240 Mb/s between instances in a zone but 130 Mb/s to backbone.  
Another example would be information about which backbone networks are available within Terremark.  
Some guys are putting in dedicated links for security.  others already have dedicated lines into a 
datacenter that runs a cloud (ex. terremark/savvis).  
Dedicated links allow you to establish a floor.  
Even if there is variability on top, you can play with the model.  
Other aspects of location relating to QoS are interesting.  
For example earthquake-proofing, uptime institute tier.  
John believes business continuity in the cloud will be a hot topic and its certification will be fascinating.

Moving forward, cloudswitch are interested in deeper involvement in jclouds, 
including exposing a cloudswitch compute provider, which would empower operators to do live topology changes.

## Open Questions

### What is this really representing?

It is tempting to include more and more metadata into this project.
It is also tempting to say that this should not be the case.
These temptations are obviously contradictory but which should dominate?

#### Alex H.

It is my belief that the ideal granularity at which this location information 
is intended is an individual site used by an individual cloud service provider.  
To some extent this may scale vertically, either up (to allow a cloud company with
 multiple sites and/or a datacenter shared by multilpe cloud companies) or 
down (to indicate a particular physical rack or virtual "vOrg"-like entity).  
These *should not* be the primary concern, although where choosing between 
otherwise equally good options these larger and smaller granularities could be a consideration.

#### Adrian Cole

jclouds should remain use-case driven from a cloud portability perspective.  
We should only add a top-level property if that property is actionable, 
means something in least 2 apis we support, isn't derivable from others, 
and is requested and/or bought-in by a sufficiently diverse set of 
current jclouds users (ex. AppsThatUseJClouds and the jclouds collaborator list).  
This prevents us from losing focus on portability, or complicating models for 
very specific use cases.  Custom properties/metadata are a bit more flexible as 
they don't complicate object models.

### Multiple Inheritance? 

#### Adrian Cole

Location objects be as simple as possible and take care not to have 
the solution worse than the problem.  If we can get by with lists, 
or a single parent delegation, lets do this, even if it only covers 95%. 
 custom metadata and/or ids should be able to expose and resolve ambiguities.  
An example test case that is validated by a sufficiently diverse set of 
jclouds users could make us decide to do something different.

### Properties as Object, String, or Sets of Strings?

Most properties are typically single strings.  
But in some cases they may be sets of strings.  How should the API present these entities?

#### Adrian Cole
json types should be supported including lists, maps, and primitives.  
These would map to Map&lt;String, Object&gt; in java.

### What Jurisdictions? 

There are many types of jurisdictions, from FISMA and SAS70 to ISO 3166 to money laundering.
It feels right to hard-bake very few at this point, but it feels like we should have conventions and signposts
for representing as many as possible.  What is relevant?

_Please list as many others as is useful in this section._
_(And of course please make any mods/adds to this doc which would be helpful.)_

#### Adrian Cole
Extend Location model to support ISO 3166-1:2006, 3166-2:2007 geography statements most agree with.

#### Mark Carlson

This (ISO-3166) is the right approach, and one that CDMI takes for the metadata
called cdmi_geographic_placement :

"JSON array of JSON strings, each containing a geopolitical identifier 
specifying a region where the object is permitted to or not permitted to be stored.

Geopolitical boundaries are a list of ISO-3166 country codes.

A "!" in front of a country code excludes that country from the previous list of geopolitical boundaries."

## Next Steps 

### Immediate
  * decide which properties are required and which are custom.
  * augment the  Location API with the additional fields necessary
  * Flesh out draft of class model supporting "near-term" concepts
  * Implement Java Builder, Groovy DSL, JSON support, tests
  * Build initial set of real-world metadata files
  

### Longer Term

  * Consider XML binding and schema
  * Enroll cloud service providers in hosting information (possibly supplying a REST API standard?)
  * Agree conventions then model refinements for recording advanced concepts (called "long-term" in 
	the introduction here)
	
	
