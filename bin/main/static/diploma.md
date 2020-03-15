# Andorid app
---
__MAIN RESPONSIBILITY:__ Show the location of route transports on the map
## Working on:

## To do:
- Application design (look).
- Geofencing - when you are in range of a bus station it should automatically show the routes that are passing through this station

## Ideas:



# PCB
---
__MAIN RESPONSIBILITY:__ Sends coordinates of the transport location to server.
It should also include the transport id.


# Server
---
__MAIN RESPONSIBILITY:__ Stores and provides data to clients
There should be a procedure of registering new transport to the system.
Basically, the transport sends requests with it's location including the ID every time.
If a transport with such ID doesn't have route assigned, it should be added to a queue of unassigned transports.
