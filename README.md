RESTIE
====

RESTie is a minimal kernel for REST(like) request routing.
It is written in Scala, but the API is Java-friendly.

You describe what your URIs should
look like via `RestPathPattern`s and then the kernel routes requests that comply with those
patterns to appropriate handlers. A `RestPathPattern` is always a named entity.

Currently, the default routing implementation is done via reflection, so that for
a pattern in the form:

	/rest/{customer}/{bookOrder}
	
which is given the unique name `BOOK_ORDER`, a `GET` request with a URI path:

	/rest/SuperCustomer/order-32123
	
is routed to a method with the following signature:

	def GET_BOOK_ORDER(rctx: RestRoutingContext,
	                     customer: String, bookOrder: String): Unit
	
The `rctx` parameter must always be of a subclass of `RestRoutingContext`, whose role
is to hold contextual information about the request. Under a servlet container environment,
a typical case is the `SimpleServletRoutingContext` which provides the servlet request and
 response objects. For more complex scenarios, where additional contextual information is
 needed, one can use the `RichServletRoutingContext` class.
 
 All classes currently reside under package `com.ckkloverdos.restie`.
 
 The test scenarios under `src/test` give examples of use.
 
 Enjoy
 -- Christos KK Loverdos

