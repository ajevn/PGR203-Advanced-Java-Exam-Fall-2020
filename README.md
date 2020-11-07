![Java CI with Maven](https://github.com/kristiania/pgr203eksamen-Kohort-5/workflows/Java%20CI%20with%20Maven/badge.svg)


<!-- Innhold -->
## Innhold

* [Om Prosjektet](#om-prosjektet)
* [Getting Started](#komme-i-gang)
  * [Funksjonalitet](#funksjonalitet)
* [Evaluering](#evaluering)




<!-- om-prosjektet -->
## Om Prosjektet
* Andreas Jevnaker - kandidatnummer 10086

Dette prosjektet setter opp en HTTP Server og kobler til en database som brukere kan kobler til via en HTTP Klient. Serveren demonstrerer hvordan Server og Client snakker med hverandre via REQUEST linjer som (GET, POST) på REQUEST Target som da svarer med /echo og status protocol (200, 404, 401, 500) og til slutt ender med status melding. Skal også demonstrere hvordan dataen blir skrevet, lagret i en database og sendt tilbake til brukeren.

Prosjektet bygges via maven -> lifecycle -> package - SERVER Start Viktig å velge versjonen som ender med shaded.jar da denne er bygget på riktig måte.

Brukeren skal kunne legge til flere brukere via newWorker.html som kjøres via localHost, der de 3 parameterene (firstNamer, lastName, email) blir lagret i ProjectMemberDao og oppdatert til Postgres databasen. Postgres databasen lagrer da de dataen den blir tilsendt og skriver de ut i en ProjectMember liste som da er et array med de forskjellige medlemene (firstName, lastName, email).


<!-- komme-i-gang -->
## Komme i gang

For å få prosjektet til å kjøre følg punktene under.

### Programmets forutsetninger

1. Bygg programmet gjennom Maven Package. Dette gjøres ved å skrive "mvn package" i terminalen eller å velge "Package" under Maven -> Lifecycle. (Dersom programmet har vært bygget tidligere kan det være lurt å gjøre en "Clean" før "Package".

2. Skriv kommandoen: 'java -jar *filnavn*' i terminalen for å eksekvere den pakkede JAR filen.

### Funksjonalitet i programmet


<!-- evaluering -->
## Refleksjoner rundt prosjektet
