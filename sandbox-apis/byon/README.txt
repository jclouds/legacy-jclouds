= Bring Your Own Nodes to the jclouds ComputeService =
The bring your own node provider (byon) allows you to specify a source which jclouds will read
nodes from.  Using this, you can have jclouds control your standalone machines, or even cloud
hosts that are sitting idle.

== Constraints ==
The byon provider only supports the following functions of ComputeService:
  * listNodes
  * listNodesDetailsMatching
  * getNodeMetadata
  * runScriptOnNodesMatching

== How to use the byon provider == 
The byon provider requires you supply a list of nodes using a property.  Here are 
the valid properties you can use:
  * byon.endpoint - url to access the list, can be http://, file://, classpath://
  * byon.nodes    - inline defined yaml in string form.

Note:

The identity and credential fields of the ComputeServiceContextFactory are ignored.

=== Java example ===

Properties props = new Properties();

// if you built the yaml string by hand
props.setProperty("byon.nodes", stringLiteral);

// or you can specify an external reference
props.setProperty("byon.endpoint", "file://path/to/byon.yaml");

// or you can specify a file in your classpath
props.setProperty("byon.endpoint", "classpath:///byon.yaml");

context = new ComputeServiceContextFactory().createContext("byon", "foo", "bar", 
               ImmutableSet.<Module> of(new JschSshClientModule()), props);

== File format == 
You must define your nodes in yaml, and they must be in a collection called nodes.

Here are the properties:
  * id            - opaque unique id
  * name          - optional; user specified name
  * hostname      - name or ip address to contact the node on
  * os_arch       - ex. x86 
  * os_family     - must conform to org.jclouds.compute.domain.OsFamily in lower-hyphen format
                    ex. rhel, ubuntu, centos, debian, amzn-linux
  * os_name       - ex. redhat 
  * os_version    - normalized to numbers when possible. ex. for centos: 5.3, ubuntu: 10.10 
  * group         - primary group of the machine. ex. hadoop 
  * tags          - list of arbitrary tags. * note this list is not yet in jclouds NodeMetadata 
  * username      - primary login user to the os. ex. ubuntu, vcloud, root 
  * sudo_password - optional; base 64 encoded sudo password (ex. input to sudo -S)
  one of:
    * credential     - base 64 encoded RSA private key or password 
    * credential_url - location of plain-text RSA private key or password.
                       ex. file:///home/me/.ssh/id_rsa
                           classpath:///id_rsa

=== Example File ===

nodes:
    - id: cluster-1
      name: cluster-1
      hostname: cluster-1.mydomain.com
      os_arch: x86
      os_family: rhel
      os_name: redhat
      os_version: 5.3
      group: hadoop
      tags:
          - vanilla
      username: myUser
      credential: ZmFuY3lmb290
      sudo_password: c3Vkbw==
