---
layout: jclouds
title: Columnar Data abstraction API
---
#Columnar Data abstraction API

## Introduction

jclouds already support several abstractions, for instance computing services, blobstore, etc.
However jclouds was not supporting still an abstraction to Database-as-a-Service, mainly these that
are Data Columnar access. The goal of this create an abstraction to columnar data, 
ex. simpledb, azure table, etc. 

Nowadays there is a new trend to store information on these systems, into columnar data instead
 traditional relational system. This page explains the design for the abstraction.

The code was designed in a separated branch, until now.

http://github.com/bastiao/jclouds


Main author: Luís Bastião from IEETA/DETI @ Universidade de Aveiro, 
and thanks for inputs from jclouds community.


## Scalability

There are several problems regarding the scalability, that has to be solved in this
 abstraction. For instance, Amazon Simple DB use a horizontal scalability, i.e., different locations. 
On the other side Azure Table use vertical one. For each partition key, represent 
a different node to have the information.

In this case, it will be solved through the table id, that will identify the 
Table name and the node label or location label.


## Details

The abstraction will contain the LDC (Lowest Domain Common, and for now just 
two API was being considered, but it might work with other tables.

### APIs

In this section, we will discuss the different concepts in a Columnar Data.

We started by creating two files to create sync and asynchronous API 

 * AsyncDataColumnar
 * DataColumnar

#### Table

Columnar Data abstraction taking two examples of cloud players: SimpleDB (AWS) and Azure Table.

They have a significative differences. 
On one hand, SimpleDB they use a simple identifier (item name). On another hand, 
Azure use a compose a element pair to create an identifier: {Partition Key, Row ID}

So first abstraction that I considered was an Identifier, then every instance has to implement one.


*  **Create Table**

The create table is strongly related with the scalability issue, so in that case the API will be something like:

{% highlight java %}
	   public void createTable(String table);
{% endhighlight %}


In a Columnar database, the data auto-fit into the table, and we don't need to design the 
structure of table in the create action, because it has a mutable state.

* **Delete Table**


The remove APi it is quite similar to the create, but more simple, just something like that:

{% highlight java %}
	   public void removeTable(String table);
{% endhighlight %}

* **Query**

The query is an important mechanism to get data from a table in a database. 
In this case the abstraction just support a simple select, and conditions to the product. 
This implementation should be based on JPA implementation.

* **Select data**

{% highlight java %}
// First arg is table name, and the second one is the list of fields to retrieve.
public Query createQuery(String table, List<String> fields);
public Query createNativeQuery(String sql);
{% endhighlight %}

For instance

{% highlight sql %}
SELECT A, B, C FROM Table 
{% endhighlight %}


* **Conditions**

How to apply conditions/filter the query? First step, it will be create 
a Query through the createQuery method. Thus, you should be able to filter the query.

{% highlight java %}
public void executeQuery();
public void setParameter(String name, String value);
{% endhighlight %}
Sample SQL:

{% highlight sql %}
SELECT A, B, C FROM Table WHERE name = :name 
{% endhighlight %}
Then, you can apply the set 

{% highlight java %}
Query query = ColumnarObj.createNativeQuery("SELECT A, B, C FROM Table WHERE name = :name");
query.setParameter("name", "Luis");
{% endhighlight %}

* **Insert**

The insert mechanism, will be also using the abstraction of JPA, however 
we will specialize into an Table implementation in order to receive attributes. 

{% highlight java %}
public void persist(Table table);
{% endhighlight %}

Moreover, it will have the same behavior, however merge operation will also be supported by this function.

* **Update**

{% highlight java %}
new Exception("NOT SUPPORTED YET!!!");
{% endhighlight %}

* **Remove**


{% highlight java %}
public void remove(String table, String id);
{% endhighlight %}

## Supported providers

None until now on jclouds, but the simpledb implementation already lives in sandbox.
