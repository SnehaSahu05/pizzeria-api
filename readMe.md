# Pizzeria REST API

### SpringBoot / Maven / OpenAPI / JSON / Hibernate / H2-DB 

This is a backend REST application for a Swagger UI hosted @ http://localhost:8080/pizzeria/ui 

When the application is running, its OpenAPI documentation can be read from http://localhost:8080/pizzeria/doc.json

The API allows to perform the below listed tasks -
* Register a new Person or Customer
* Get a list of all Pizza Orders
* Get a list of all Pizza Orders specific to a given Person
* Delete a Pizza Order by it ID

Project features (at present) -
* Zero or more Pizza Orders can be linked with a single Person
* Deleting an Order does not cascade delete operation to the linked Person.
* Only a limited Pizza configuration is allowed:
  * Flavour: {'Hawaii', 'Regina', 'Quattro-Formaggi'}
  * Size: {'Large', 'Medium' }
  * Crust: {'Thin'}
* Ordering a Pizza requires an additional header named 'bearerToken' 
  for authentication, which is not a real security implementation, 
  but only a dummy implementation.
* Test setup for both PizzeriaService and RestController Classes
* Working tests - for one method from each class
  - readAllOrdersSortedByTime from PizzeriaService Class
  - authenticateApi from RestController Class

TODO:
* Test cases for all methods in Service and RestController
* Updating a created Order
* Include new field called OrderType in Order - TakeAway/Delivery
* Outh2 + Spring security
