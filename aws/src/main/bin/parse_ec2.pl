#!/usr/bin/perl
####
#
#    Copyright (C) 2009 Adrian Cole <adrian@jclouds.org>
#
#    ====================================================================
#    Licensed to the Apache Software Foundation (ASF) under one
#    or more contributor license agreements.  See the NOTICE file
#    distributed with this work for additional information
#    regarding copyright ownership.  The ASF licenses this file
#    to you under the Apache License, Version 2.0 (the
#    "License"); you may not use this file except in compliance
#    with the License.  You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
#    Unless required by applicable law or agreed to in writing,
#    software distributed under the License is distributed on an
#    "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
#    KIND, either express or implied.  See the License for the
#    specific language governing permissions and limitations
#    under the License.
####
#   Parse EC2
#
#   Builds an object tree used to create a REST service for the current EC2 api.
#
#   Prerequisites:
#     * download and install http://search.cpan.org/~petek/HTML-Tree/
#        1. download and extract the archive
#        2. cd to that location
#        3. perl MakeFile.PL
#        4. sudo make install
#
#   Usage:
#     * execute the script with no arguments.  If you've downloaded the content locally, adjust refUrl and parse
#
#   Tips:  use $tree->dump to view the current html tree and print Dumper($object) to see a reference
#          this code is formatted with PerlTidy
#   Author: Adrian Cole
####
use strict;
use HTML::TreeBuilder 2.97;
use LWP::UserAgent;
use Data::Dumper;

#my $refUrl = "http://docs.amazonwebservices.com/AWSEC2/latest/APIReference";

my $refUrl           = "/tmp/scrape";
my $appUrl           = "${refUrl}/OperationList-query.html";
my $global_package   = "org.jclouds.aws.ec2";
my $commands_package = $global_package . ".commands";
my $response_package = $commands_package . ".response";
my $options_package  = $commands_package . ".options";
my $domain_package   = $global_package . ".domain";
my $xml_package      = $global_package . ".xml";

my $domain = {};

sub parse_file {
    my $file = $_[0] || die "What File?";
    my $tree = HTML::TreeBuilder->new();
    $tree->parse_file($file);
    $tree->eof;
    return $tree;
}

sub parse {

    return parse_file(shift);

    #return parse_url(shift);
}

sub parse_java_type {
    $_ = shift;
    s/xsd:string/String/;
    s/xsd:boolean/boolean/;
    s/Integer/int/;
    s/xsd:Int/int/;
    s/xsd:dateTime/DateTime/;
    if (/Type/) {
        my $awsType  = $_;
        my $javaType = get_java_name($awsType);
        if ( !/Response/ ) {
            $domain->{$awsType} = {
                awsType   => $awsType,
                javaType  => $javaType,
                package   => $domain_package,
                className => $domain_package . "." . $javaType,
                see => ["${refUrl}/ApiReference-ItemType-${awsType}.html"],
                fields =>
                  build_fields("${refUrl}/ApiReference-ItemType-$awsType.html")
            };
        }
        $_ = $javaType;
    }

    return $_;
}

sub get_java_name {
    $_ = shift;
    if (/sSetType/) {
        s/sSetType//;
        return "Set<$_>";
    }
    if (/sSetItemType/) {
        s/sSetItemType//;
    }
    if (/sItemType/) {
        s/sItemType//;
    }
    if (/sSet/) {
        s/sSet//;
    }
    if (/Set/) {
        s/Set//;
    }
    if (/Type/) {
        s/Type//;
    }
    return $_;
}

sub parse_url {
    my $url = $_[0] || die "What URL?";

    my $response =
      LWP::UserAgent->new->request( HTTP::Request->new( GET => $url ) );
    unless ( $response->is_success ) {
        warn "Couldn't get $url: ", $response->status_line, "\n";
        return;
    }

    my $tree = HTML::TreeBuilder->new();
    $tree->parse( $response->content );
    $tree->eof;
    return $tree;
}

sub build_commands {
    my $tree = parse( $_[0] );
    my @out;
    foreach my $link (
        ( $tree->look_down( '_tag', 'div', 'class', 'itemizedlist' ) ) )
    {
        my $package = $link->look_down( '_tag', 'b' );
        $_ = $package->as_text;
        s/ //g;
        my $packageName = lc();
        my @commands;

        foreach my $class ( $link->look_down( '_tag', 'a' ) ) {
            my $awsType = $class->attr('title');
            my $command = {
                awsType   => $awsType,
                package   => $commands_package . ".$packageName",
                className => $commands_package . ".$packageName." . $awsType,
                see       => ["${refUrl}/ApiReference-query-${awsType}.html"],
                response  => {
                    javaType  => $awsType . "Response",
                    awsType   => $awsType . "Response",
                    package   => $response_package . ".$packageName",
                    className => $response_package
                      . ".$packageName."
                      . $awsType
                      . "Response",
                    see => [
"${refUrl}/ApiReference-ItemType-${awsType}Response.html"
                    ]
                },
                options => {
                    awsType   => $awsType . "Options",
                    package   => $options_package . ".$packageName",
                    className => $options_package
                      . ".$packageName."
                      . $awsType
                      . "Options",
                    see => ["${refUrl}/ApiReference-ItemType-${awsType}.html"]
                },
                handler => {
                    awsType   => $awsType . "Handler",
                    package   => $xml_package . ".$packageName",
                    className => $xml_package
                      . ".$packageName."
                      . $awsType
                      . "Handler",
                    see => [
"${refUrl}/ApiReference-ItemType-${awsType}Response.html"
                    ]
                }
            };
            $command = build_command($command);

            # do not build options when there are none!
            if ( $#{ $$command{options}->{parameters} } == -1 ) {
                delete $$command{options};
            }

            # clear parameters for commands who don't require them
            if ( $#{ $$command{parameters} } == -1 ) {
                delete $$command{parameters};
            }
            push @commands, $command;
        }
        push @out,
          {
            name     => $packageName,
            commands => \@commands,
          };
    }
    $tree->eof;
    $tree->delete;

    return \@out;
}

sub build_app {
    my $url      = shift;
    my $packages = build_commands($url);
    return {
        see      => [$url],
        packages => $packages
    };
}

sub build_command {
    my $command = $_[0];

    #print "parsing $$command{see}[0]...\n";
    my $tree = parse( $$command{see}[0] );

    #$tree->dump;
    my ${requestExampleDiv} =
      $tree->look_down( '_tag', 'h3', 'id',
        "ApiReference-query-$$command{awsType}-Example-Request-1" )
      ->look_up( '_tag', 'div', 'class', 'section' );
    $$command{options}->{example} = ${requestExampleDiv}->as_HTML();

    my ${reqParamTBody} =
      $tree->look_down( '_tag', 'h2', 'id',
        "ApiReference-query-$$command{awsType}-Request" )
      ->look_up( '_tag', 'div', 'class', 'section' )
      ->look_down( '_tag', 'tbody' );
    my @{parameterRows};
    if ( defined($reqParamTBody) ) {
        @{parameterRows} = ${reqParamTBody}->look_down( '_tag', 'tr' );
    }

    my @optionalParameters;
    my @requiredParameters;

    foreach my $parameterRow ( @{parameterRows} ) {
        my @{row} = $parameterRow->look_down( '_tag', 'td' );
        my %param;
        $param{name} = ${row}[0]->as_text();
        my @{data} = ${row}[1]->look_down( '_tag', 'p' );
        foreach ( @{data} ) {
            $_ = $_->as_text();
            if (s/Default: //) {
                $param{param} = $_;
            }
            elsif (s/Type: //) {
                $param{type}     = $_;
                $param{javaType} = parse_java_type($_);
            }
            else {
                $param{desc} = $_;
            }
        }
        if ( ${row}[2]->as_text() =~ /No/ ) {
            push @optionalParameters, \%param;
        }
        else {
            push @requiredParameters, \%param;
        }
    }
    $$command{options}->{parameters} = \@optionalParameters;
    $$command{parameters} = \@requiredParameters;

    my ${responseExampleDiv} =
      $tree->look_down( '_tag', 'h3', 'id',
        "ApiReference-query-$$command{awsType}-Example-Response-1" )
      ->look_up( '_tag', 'div', 'class', 'section' );
    $$command{handler}->{example} = ${responseExampleDiv}->as_HTML();

    $command = build_response($command);

    my @{seeAlsoA} =
      $tree->look_down( '_tag', 'div', 'class', 'itemizedlist' )
      ->look_down( '_tag', 'a' );
    foreach ( @{seeAlsoA} ) {
        push @{ $command->{see} }, $_->as_text();
    }
    $tree->eof;
    $tree->delete;

    # print_command($command);
    return $command;
}

sub build_response {
    my $command = $_[0];
    $$command{response}->{fields} =
      build_fields( $$command{response}->{see}[0] );
    remove_request_ids( $$command{response} );
    return $command;
}

# request id is not a domain concern
sub remove_request_ids {
    my $response = shift;
    my @fields   = @{ ${response}->{fields} };
    for ( 0 .. $#fields ) {
        if ( $fields[$_]->{name} eq "requestId" ) {
            splice( @{ ${response}->{fields} }, $_, 1 );
        }
    }
}

sub build_fields {

    #print "parsing $_[0]\n";
    my $tree = parse( $_[0] );

    $tree->eof;
    my @{fields};

    my @{fieldRows} = my $body = ${tree}->look_down( '_tag', 'tbody' );
    return [] unless defined $body;    #TODO

    @{fieldRows} = $body->look_down( '_tag', 'tr' );

    foreach ( @{fieldRows} ) {
        my @row = $_->look_down( '_tag', 'td' );
        my %field;
        $field{name} = ${row}[0]->as_text();
        my @{data} = ${row}[1]->look_down( '_tag', 'p' );
        foreach ( @{data} ) {
            $_ = $_->as_text();
            if (s/Type: //) {
                $field{type}     = $_;
                $field{javaType} = parse_java_type($_);
            }
            else {
                $field{desc} = $_;
            }
        }
        push @fields, \%field;
    }

    $tree->delete;
    return \@fields;
}

sub gen_java_code {
    my $app = shift;

    #print Dumper($app);

    my @packages = @{ $$app{packages} };

    foreach my $packageRef (@packages) {
        print "$packageRef->{name}\n";

        #print Dumper($packageRef);
        my @commands = @{ $packageRef->{commands} };
        foreach my $command (@commands) {
            print_command($command);
        }
    }
}

sub print_domain_code {
    my $domain = shift;
    while ( my ( $awsType, $classDef ) = each %$domain ) {
        print "$classDef->{code}\n\n";
    }
}

sub print_response_code {
    my $app      = shift;
    my @packages = @{ $$app{packages} };
    foreach my $packageRef (@packages) {
        my @commands = @{ $packageRef->{commands} };
        foreach my $command (@commands) {
            print "$command->{response}->{code}\n\n"
              if defined $command->{response}->{code};
        }
    }
}

sub print_command_code {
    my $app      = shift;
    my @packages = @{ $$app{packages} };
    foreach my $packageRef (@packages) {
        my @commands = @{ $packageRef->{commands} };
        foreach my $command (@commands) {
            print "$command->{code}\n\n"
              if defined $command->{code};
        }
    }
}

sub gen_bean_code_for_response {
    my $app      = shift;
    my @packages = @{ $$app{packages} };

    foreach my $packageRef (@packages) {
        my @commands = @{ $packageRef->{commands} };
        foreach my $command (@commands) {
            my $fieldCount = scalar @{ $command->{response}->{fields} };

            # convert to native java type
            if ( $fieldCount == 1 ) {
                $command->{response}->{javaType} =
                  ${ $command->{response}->{fields} }[0]->{javaType};
            }
            else {
                gen_bean_code( $command->{response} );
            }
        }
    }
}

sub gen_bean_code_for_domain {
    my $hierarchy = shift;
    while ( my ( $awsType, $classDef ) = each %$hierarchy ) {
        gen_bean_code($classDef);
    }
    return $hierarchy;
}

sub gen_bean_code_for_command {
    my $app      = shift;
    my @packages = @{ $$app{packages} };

    foreach my $packageRef (@packages) {
        my @commands = @{ $packageRef->{commands} };
        foreach my $command (@commands) {
            gen_command($command);
        }
    }
}

# inserts code into the class definitions
sub gen_command {
    my $classDef        = shift;
    my $responsePackage = $${classDef}{response}->{package};
    my $optionsImport;
    $optionsImport = "import " . $${classDef}{options}->{package} . ".*"
      if defined $${classDef}{options};

    my ${code} = <<EOF;
package $classDef->{package};
import ${domain_package}.*;
import ${responsePackage}.*;
${optionsImport}
import org.jclouds.http.commands.callables.xml.ParseSax;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import org.jclouds.http.HttpFutureCommand;

/**
 *
EOF
    foreach my $see ( @{ $classDef->{see} } ) {
        if ( $see =~ /html/ ) {
            $see = "<a href='$see' />";
        }
        ${code} = ${code} . <<EOF;
 * \@see $see 
EOF

    }

# TODO change optons to be default of Base... instead of doing this in render logic.
    my $optionsParameter =
      "\@Assisted BaseEC2RequestOptions<EC2RequestOptions> options";
    $optionsParameter =
      "\@Assisted " . $classDef->{options}->{awsType} . " options"
      if defined $classDef->{options}->{awsType};

    ${code} = ${code} . <<EOF;
 * \@author Adrian Cole 
 */
public class $classDef->{javaType} extends HttpFutureCommand<$classDef->{response}->{javaType}> {
   \@Inject
   public $classDef->{javaType}(\@Named(AWSConstants.PROPERTY_AWS_ACCESSKEYID) String awsAccessKeyId, 
                                \@Named(AWSConstants.PROPERTY_AWS_SECRETACCESSKEY) String awsSecretAccessKey, 
                                ParseSax<$classDef->{response}->{javaType}> callable, ${optionsParameter}
EOF

    my $optionString;
    my $args;

    # print fields
    foreach my $field ( @{ $classDef->{parameters} } ) {
        my ${name} = $field->{name};
        $name = lcfirst($name);
        my $uname = ucfirst($name);

        $args         .= "\@Assisted $field->{javaType} ${name}, ";
        $optionString .= ".with${uname}(${uname})";
    }

    ${code} = ${code} . <<EOF;
                   ${args}) {
	  super("GET", "/" + options.buildQueryString()${optionString}.signWith(awsAccessKeyId,awsSecretAccessKey), callable);
   }

} 
EOF
    $classDef->{code} = $code;

}

# inserts code into the class definitions
sub gen_bean_code {
    my $classDef = shift;
    my ${code} = <<EOF;
package $classDef->{package};
EOF
    if ( "$classDef->{package}" ne "${domain_package}" ) {
        ${code} = ${code} . <<EOF;
       
import ${domain_package}.*;
EOF
    }
    ${code} = ${code} . <<EOF;

/**
 *
EOF
    foreach my $see ( @{ $classDef->{see} } ) {
        if ( $see =~ /html/ ) {
            $see = "<a href='$see' />";
        }
        ${code} = ${code} . <<EOF;
 * \@see $see 
EOF

    }
    ${code} = ${code} . <<EOF;
 * \@author Adrian Cole 
 */
public class $classDef->{javaType} {

EOF

    # print fields
    foreach my $field ( @{ $classDef->{fields} } ) {
        my ${name} = $field->{name};
        $name = lcfirst($name);
        ${code} = ${code} . <<EOF;
   /**
    *
    * $field->{desc} 
    * /
   private $field->{javaType} ${name};
   
EOF

    }

    # print get/set
    foreach my $field ( @{ $classDef->{fields} } ) {
        my ${name} = $field->{name};
        $name = lcfirst($name);
        my $uname = ucfirst($name);

        ${code} = ${code} . <<EOF;
   /**
    *
    * \@return $field->{desc} 
    * /
   public $field->{javaType} get${uname}(){
      return this.${name};
   }

   /**
    *
    * \@param $name $field->{desc} 
    * /
   public void set${uname}($field->{javaType} $name){
      this.${name} = ${name};
   }
  
EOF
    }
    ${code} = ${code} . "}";
    $classDef->{code} = $code;

}

# start app!
my $app = build_app($appUrl);

gen_bean_code_for_domain($domain);
gen_bean_code_for_response($app);
gen_bean_code_for_command($app);

print_domain_code($domain);
print_response_code($app);
print_command_code($app);
