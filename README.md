![Java CI with Maven](https://github.com/kristiania/pgr203innevering3-Kohort-5-1/workflows/Java%20CI%20with%20Maven/badge.svg?branch=main)
# pgr203innevering3-Kohort-5-1


Dette prosjektet setter opp en HTTP Server og kobler til en database som brukere kan kobler til via en HTTP Klient. Serveren demonstrerer hvordan Server og Client snakker med hverandre via REQUEST linjer som (GET, POST) på REQUEST Target som da svarer med /echo og status protocol (200, 404, 401, 500) og til slutt ender med status melding.
Skal også demonstrere hvordan dataen blir skrevet, lagret i en database og sendt tilbake til brukeren.

Prosjektet bygges via maven -> lifecycle -> package - SERVER Start Viktig å velge versjonen som ender med shaded.jar da denne er bygget på riktig måte.

Brukeren skal kunne legge til flere brukere via newWorker.html som kjøres via localHost, der de 3 parameterene (firstNamer, lastName, email) blir lagret i ProjectMemberDao og oppdatert til Postgres databasen.
Postgres databasen lagrer da de dataen den blir tilsendt og skriver de ut i en ProjectMember liste som da er et array med de forskjellige medlemene (firstName, lastName, email).
