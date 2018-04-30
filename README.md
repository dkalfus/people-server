Project: PersonServer

Author: David Kalfus

The PersonServer supplies a RESTful interface to Person records.  It is written
in java using Spring Data Rest and Spring Boot. By default, it runs
in the following service URL:  

//http://localhost:5000/personService/v1/people

The context and port can be changed via the standard properties for that purpose in
application.properties.


To build the server: after the source code is checked out from source control,
run mvn clean install from the top project directory   To run,
execute mvn spring-boot:run

The build creates a standard spring-boot executable jar.  This may be run under maven by 
executing mvn spring-boot:run.  It may also be run directly via the command line command:
java -jar 

Notes on Functionality
----------------------

Please see the requirements document for detailed specifications of the endpoints and their
respective functionality 

-- All data is accepted and returned in standard json.  No pagination is currently
used; that can be added in future if necessary.  Therefore, use the full dataset GET
endpoint  ( personService/v1/people) judiciously as a volume of hundreds of thousands
of entries may overwhelm the server and/or the client.

-- Deletes always return 204 (No Content), even if the Person was not found.  This
 is somewhat nonstandard as compared to 404 (Not Found) in such a situation.
 
-- The application is not designed with a high-capacity data set in mind.  Specifically,
doing a GET of all Persons (via endpoint personService/v1/people )  may 
overload the server's memory if, say, there are millions of entries.  At some point
it may be desirable to paginate the response.  The current spec calls for a simple json list.

Technical notes
---------------
- As a spring boot app, properties may be set via application.properties and also 
overridden on the command line.  

- The controller delegates requests of complexity or involving business logic (including 
 field validations) to the PersonService.  However, it handles simple requests to
 by calling the Repository directly. 
 
- The Spring framework handles sending most server-side error messages (4xx and 5xx) to the
client.  The exception to this are application-level validations which are sent as 422 
(Unproccessable Entity) messages.  The app generates these by registering a Spring 
Exception Handler in the Base Controller which intercepts IllegalArgumentExceptions
thrown by any controller method and uses them to create a 422 response with the Exception
message returned in the body wrapped in an ErrorResponse json object that includes a timestamp.
You can create such Exceptions at the appropriate points in the server code and let them
bubble up to be caught by the hander.


