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
#
#   Author: Adrian Cole
####
use strict;
use HTML::TreeBuilder 2.97;
use LWP::UserAgent;
use Data::Dumper;

my $refUrl = "http://docs.amazonwebservices.com/AWSEC2/latest/APIReference";

#my $refUrl           = "/tmp/scrape";
my $appUrl           = "${refUrl}/OperationList-query.html";
my $global_package   = "org.jclouds.aws.ec2";
my $commands_package = $global_package . ".commands";
my $response_package = $commands_package . ".response";
my $options_package  = $commands_package . ".options";
my $xml_package      = $global_package . ".xml";

sub parse_file {
    my $file = $_[0] || die "What File?";
    my $tree = HTML::TreeBuilder->new();
    $tree->parse_file($file);
    $tree->eof;
    return $tree;
}

sub parse {

    # return parse_file(shift);
    return parse_url(shift);
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

    #$tree->dump;

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
            my $simpleName = $class->attr('title');
            my $command    = {
                simpleName => $simpleName,
                className  => $commands_package . $simpleName,
                see      => ["${refUrl}/ApiReference-query-${simpleName}.html"],
                response => {
                    simpleName => $simpleName . "Response",
                    className  => $response_package . $simpleName . "Response",
                    see        => [
"${refUrl}/ApiReference-ItemType-${simpleName}Response.html"
                    ]
                },
                options => {
                    simpleName => $simpleName . "Options",
                    className  => $options_package . $simpleName . "Options",
                    see =>
                      ["${refUrl}/ApiReference-ItemType-${simpleName}.html"]
                },
                handler => {
                    simpleName => $simpleName . "Handler",
                    className  => $xml_package . $simpleName . "Handler",
                    see        => [
"${refUrl}/ApiReference-ItemType-${simpleName}Response.html"
                    ]
                }
            };
            $command = build_command($command);
            push @commands, $command;
        }
        push @out,
          {
            name     => $packageName,
            commands => [@commands],
          };
    }
    $tree->eof;
    $tree->delete;

    return @out;
}

sub build_app {
    my $url      = shift;
    my @packages = build_commands($url);
    return {
        see      => [$url],
        packages => [@packages]
    };
}

sub build_command {
    my $command = $_[0];
    print "parsing $$command{see}[0]...\n";
    my $tree = parse( $$command{see}[0] );

    #$tree->dump;
    my ${requestExampleDiv} =
      $tree->look_down( '_tag', 'h3', 'id',
        "ApiReference-query-$$command{simpleName}-Example-Request-1" )
      ->look_up( '_tag', 'div', 'class', 'section' );
    $$command{options}->{example} = ${requestExampleDiv}->as_HTML();

    my ${reqParamTBody} = $tree->look_down(
        '_tag', 'h2',
        'id',   "ApiReference-query-$$command{simpleName}-Request"
      )->look_up( '_tag', 'div', 'class', 'section' )
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
                $param{type} = $_;
            }
            else {
                $param{desc} = $_;
            }
        }
        if ( ${row}[2]->as_text() =~ /No/ ) {
            push @optionalParameters, {%param};
        }
        else {
            push @requiredParameters, {%param};
        }
    }
    $$command{options}->{parameters} = [@optionalParameters];
    $$command{parameters} = [@requiredParameters];

    my ${responseExampleDiv} =
      $tree->look_down( '_tag', 'h3', 'id',
        "ApiReference-query-$$command{simpleName}-Example-Response-1" )
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
    my $tree    = parse( $$command{response}->{see}[0] );

    #$tree->dump;
    $tree->eof;
    my @{fields};

    my @{fieldRows} =
      ${tree}->look_down( '_tag', 'tbody' )->look_down( '_tag', 'tr' );

    foreach ( @{fieldRows} ) {
        my @row = $_->look_down( '_tag', 'td' );
        my %field;
        $field{name} = ${row}[0]->as_text();
        my @{data} = ${row}[1]->look_down( '_tag', 'p' );
        foreach ( @{data} ) {
            $_ = $_->as_text();
            if (s/Type: //) {
                $field{type} = $_;
            }
            else {
                $field{desc} = $_;
            }
        }
        push @fields, {%field};
    }

    $$command{response}->{fields} = [@fields];
    $tree->delete;
    return $command;
}

sub print_command {
    my $classRef = shift;
    print "     $$classRef{simpleName}\n";
    print Dumper($_) . "\n" foreach ( @{ $classRef->{parameters} } );
}

sub print_app {
    my $app = build_app($appUrl);
    print Dumper($app);

    my @packages = @{ $$app{packages} };

    foreach my $packageRef (@packages) {
        my @commands = @{ $$packageRef{commands} };
        print_command foreach (@commands);
    }
}

# start app!
print_app();

