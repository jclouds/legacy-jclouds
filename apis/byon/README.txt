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

  * id             - opaque unique id
  * name           - optional; user specified name
  * description    - optional; long description of this node 
                               * note this is not yet in jclouds NodeMetadata
  * hostname       - name or ip address to contact the node on
  * os_arch        - ex. x86 
  * os_family      - must conform to org.jclouds.compute.domain.OsFamily in lower-hyphen format
                     ex. rhel, ubuntu, centos, debian, amzn-linux
  * os_description - long description of the os ex. Ubuntu with lamp stack 
  * os_version     - normalized to numbers when possible. ex. for centos: 5.3, ubuntu: 10.10 
  * group          - primary group of the machine. ex. hadoop 
  * tags           - optional; list of arbitrary tags. 
                               * note this list is not yet in jclouds NodeMetadata 
  * username       - primary login user. ex. ubuntu, vcloud, toor, root 
  * sudo_password  - optional; when a script is run with the "runAsRoot" option true, yet the
                               username is not root, a sudo command is invoked. If sudo_password
                               is set, the contents will be passed to sudo -S.  
                               Ex. echo 'foobar'| sudo -S init 5
 
  one of:
  
    * credential     - RSA private key or password 
    * credential_url - location of plain-text RSA private key or password.
                       ex. file:///home/me/.ssh/id_rsa
                           classpath:///id_rsa

Note that username and credentials are optional if a CredentialStoreModule is configured in 
jclouds.

=== Example File ===

nodes:
    - id: i-sdfkjh7
      name: cluster-1
      description: accounting analytics cluster
      hostname: cluster-1.mydomain.com
      os_arch: x86
      os_family: rhel
      os_description: redhat with CDH
      os_version: 5.3
      group: hadoop
      tags:
          - vanilla
      username: myUser
      credential: |
                  -----BEGIN RSA PRIVATE KEY-----
                  MIIEowIBAAKCAQEAuzaE6azgUxwESX1rCGdJ5xpdrc1XC311bOGZBCE8NA+CpFh2
                  u01Vfv68NC4u6LFgdXSY1vQt6hiA5TNqQk0TyVfFAunbXgTekF6XqDPQUf1nq9aZ
                  lMvo4vlaLDKBkhG5HJE/pIa0iB+RMZLS0GhxsIWerEDmYdHKM25o
                  -----END RSA PRIVATE KEY-----
      sudo_password: go panthers!
