# Pizzeria REST API

### SpringBoot / OpenAPI / Maven / Hibernate / H2-DB 

This is a backend application for a Swagger UI hosted @ http://localhost:8080/pizzeria/ui 

When the application is running, its OpenAPI documentation can be read from http://localhost:8080/pizzeria/doc.json

The API allows to perform the below listed tasks -
* Register a new Person or Customer
* Get a list of all Pizza Orders
* Get a list of all Pizza Orders specific to a given Person
* Delete a Pizza Order by it ID

Other conditions are -
* Zero or more Pizza Orders can be linked with a single Person
* Deleting an Order does not cascade effect to the associated Person.
* Only a limited Pizza configuration is allowed:
  * Flavour: {'Hawaii', 'Regina', 'Quattro-Formaggi'}
  * Size: {'Large', 'Medium' }
  * Crust: {'Thin'}
* Ordering a Pizza requires an additional header named 'bearerToken' 
  for authentication, which is not a real security implementation, 
  but only a dummy implementation.

