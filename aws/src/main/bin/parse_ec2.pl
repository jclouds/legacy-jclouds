#!/usr/bin/perl
#
#
# Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
#
# ====================================================================
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#   http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
# ====================================================================
#
####
#   Parse EC2
#
#   Builds an object tree used to create a REST service for the current EC2 api.
#
#   Prerequisites:
#     * install HTML-Tree (http://search.cpan.org/~petek/HTML-Tree/)
#        1. download and extract the archive
#        2. cd to that location
#        3. perl MakeFile.PL
#        4. sudo make install
#     * install JSON (http://search.cpan.org/~makamaka/JSON-2.14/lib/JSON.pm)
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
use JSON;

my $refUrl = "http://docs.amazonwebservices.com/AWSEC2/latest/APIReference";

#my $refUrl = "/tmp/scrape";

my $appUrl = "${refUrl}/OperationList-query.html";

my $domain = {};

sub parse_file {
    my $file = $_[0] || die "What File?";
    my $tree = HTML::TreeBuilder->new();
    $tree->parse_file($file);
    $tree->eof;
    return $tree;
}

sub parse {
    #return parse_file(shift);

    return parse_url(shift);
}

sub get_subtypes {
    $_ = shift;
    if ( /^[A-Z]/ && $_ ne 'String' && $_ ne 'Integer' && $_ ne 'Boolean' ) {
        my $type = $_;
        $domain->{$type} = build_bean($type);
    }
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

sub build_queries {
    my $tree = parse( $_[0] );
    my @out;
    foreach my $link (
        ( $tree->look_down( '_tag', 'div', 'class', 'itemizedlist' ) ) )
    {
        my $category = $link->look_down( '_tag', 'b' )->as_text();

        my $queries;
        foreach my $class ( $link->look_down( '_tag', 'a' ) ) {
            my $type  = $class->attr('title');
            my $query = build_query($type);

            # add a domain object for the response type
            $domain->{ $type . "Response" } =
              build_bean( $type . "Response", "Response" );

            $query->{responseType} = $type . "Response";

            $queries->{$type} = $query;
        }

        push @out,
          {
            name    => $category,
            queries => $queries,
          };
    }
    $tree->eof;
    $tree->delete;
    return \@out;
}

sub build_app {
    my $url        = shift;
    my $categories = build_queries($url);
    return {
        see        => [$url],
        categories => $categories,
        domain     => $domain
    };
}

sub build_query {
    my $type  = shift;
    my $query = build_bean( $type, "Request" );
    my $tree = parse(${ $query->{see} }[0]);

    my @{seeAlsoA} =
      $tree->look_down( '_tag', 'div', 'class', 'itemizedlist' )
      ->look_down( '_tag', 'a' );

    foreach ( @{seeAlsoA} ) {
        push @{ $query->{see} }, $_->as_text();
    }

    $tree->eof;
    $tree->delete;

    return $query;
}

sub build_fields {
    my @{fieldRows} = @_;
    my @params;
    foreach my $fieldRow ( @{fieldRows} ) {
        my @{row} = $fieldRow->look_down( '_tag', 'td' );
        my %param;
        $param{name} = ${row}[0]->as_text();
        my $enumDiv =
          ${row}[1]->look_down( '_tag', 'div', "class", "itemizedlist" );
        if ( defined $enumDiv ) {
            my $enum;
            my @enumEntries = $enumDiv->look_down( '_tag', 'p' );
            foreach my $enumEntry (@enumEntries) {
                $enumEntry = $enumEntry->as_text();
                my ( $code, $state ) = split( /: /, $enumEntry );
                chomp($code);
                chomp($state);
                $enum->{$code} = $state;
            }
            $param{enum} = $enum;
            $param{desc} = ${row}[1]->look_down( '_tag', 'p' )->as_text();
        }
        else {
            my @{data} = ${row}[1]->look_down( '_tag', 'p' );
            foreach ( @{data} ) {
                $_ = $_->as_text();
                if (s/Default: //) {
                    $param{default} = $_;
                }
                elsif (s/Type: //) {
                    $param{type} = $_;
                    get_subtypes($_);
                }
                elsif (s/Ancestor: //) {
                    $param{ancestor} = $_;
                }
                elsif (s/Children: //) {
                    $param{children} = $_;
                }
                elsif (s/Constraints: //) {
                    $param{constraints} = $_;
                    if (m/.*default: ([0-9]+)/) {
                        $param{default} = $1;
                    }
                }
                elsif (s/Valid Values: //) {
                    if (/\|/) {
                        my @valid_values = split(' \| ');
                        $param{valid_values} = \@valid_values;
                    }
                    elsif (/([0-9]+) ?\-([0-9]+)/) {
                        $param{valid_values} = "$1-$2";
                    }
                }
                else {
                    $param{desc} = $_;
                }
            }
        }

        if ( defined ${row}[2] && ${row}[2]->as_text() =~ /No/ ) {
            $param{optional} = 'true';
        }
        else {
            $param{optional} = 'false';
        }
        push @params, \%param;
    }
    return \@params;
}

sub build_bean {
    my $type  = shift;
    my $class = shift;
    my $bean  = { type => $type, };

    my $see = "${refUrl}/ApiReference-ItemType-${type}.html";
    if ( defined $class ) {
        $_ = $type;
        if ( $class =~ /Response/ ) {

            # responses are related to the query.  In this case, we must take
            # off the suffix Response to get the correct metadata url.
            s/$class//;
        }
        else {
            # if we are the query object, then there is a different master url.
            $see = "${refUrl}/ApiReference-query-${type}.html";
        }
        my $query = "${refUrl}/ApiReference-query-${_}.html";
        push @{ $bean->{see} }, $query;
        my $tree = parse($query);

        if ( !defined $tree ) {
            print "could not parse tree $_[0]\n";
            return {};
        }
        my $id = "ApiReference-query-${_}-Example-${class}-1";
        my ${requestExampleDiv} =
          $tree->look_down( '_tag', 'h3', 'id', "$id" )
          ->look_up( '_tag', 'div', 'class', 'section' );
        $bean->{example_html} = ${requestExampleDiv}->as_HTML();
        $bean->{example_code} =
          ${requestExampleDiv}
          ->look_down( '_tag', 'pre', 'class', 'programlisting' )->as_text();
        $tree->eof;
        $tree->delete;

    }
    push @{ $bean->{see} }, $see unless defined $bean->{see};

    my $tree = parse($see);

    if ( !defined $tree ) {
        print "could not parse tree $_[0]\n";
        return {};
    }

    #    $tree->dump();
    $tree->eof;
    my $id = "ApiReference-ItemType-${type}-Ancestors";
    my ${ancestorH2} = $tree->look_down( '_tag', 'h2', 'id', "$id" );
    my ${ancestorDiv} =
      ${ancestorH2}->look_up( '_tag', 'div', 'class', 'section' )
      if defined ${ancestorH2};
    my ${ancestorLink} =
      ${ancestorDiv}->look_down( '_tag', 'a', 'class', 'xref' )
      if defined ${ancestorDiv};
    if ( defined ${ancestorLink} ) {
        $bean->{ancestor} = ${ancestorLink}->as_text();
    }
    else {
        $bean->{ancestor} = "None";
    }

    my @{fieldRows} = my $body = ${tree}->look_down( '_tag', 'tbody' );

    if ( !defined $body ) {
        print "could not parse body $_[0]\n";
        return [];
    }

    @{fieldRows} = $body->look_down( '_tag', 'tr' );

    my $fields = build_fields( @{fieldRows} );
    $tree->delete;
    $bean->{fields} = $fields;
    return $bean;

}

# start app!
my $app = build_app($appUrl);
my $app_json = to_json( $app, { utf8 => 1, pretty => 1 } );
print $app_json
