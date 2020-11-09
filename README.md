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

Dette prosjektet setter opp en lokal webserver der brukeren kan utføre handlinger mot serveren. Serveren demonstrerer hvordan server og klient-siden snakker med hverandre via forskjellige typer requests, herunder Post og Get forespørsler som følger RFC7230 HTTP standard. (https://tools.ietf.org/html/rfc7231). Prosjektet benytter PostgreSQL som databaseløsning. Funksjonaliteten for dette er bygget inn i programmet og gjennom JDBC(Java Database Connectivity) i egne DAO(Data Access Object). Programmet kan gjennom bruk av kontrollere lagre, hente og filtrere data basert på forespørsler fra klientet. 

Prosjektet leverer et enkelt system for håndtering av prosjektoppgaver og man kan legge til ansatte, oppgaver og oppgavestatus. Det er mulig å tilegne ansatte til oppgaver og man kan endre oppgavestatus underveis. Prosjektet er laget med minimalt fokus på brukervennlighet da dette ikke var prioritert i oppgaveteksten.

Prosjektet er utviklet alene da arbeidsinnsatsen innledningsvis i gruppen på 2 var veldig ujevn. Etter litt diskusjon og veiledning kom vi frem til at den beste løsningen ville være å dele oss opp og levere som enkeltpersoner. Grunnet ensidig deltagelse over en relativt lang periode innledningsvis ble vi enige om at det ville være en fordel for begge parter å dele oss opp i 2, der den andre personen lagde sin egen oppgave. Vi ble enige om at dette ville resultere i bedre læring fremfor å prøve å hoppe inn i prosjektet jeg allerede hadde laget nokså mye av allerede. Vi har siden hatt god kommunikasjon og har i den siste tiden før fristen sittet sammen i kohort gruppen mens vi jobbet. Vi har behold noen av aspektene ved samarbeidet og jeg har tilbytt mine erfaringer og tips fra utviklingsprosessen.


<!-- komme-i-gang -->
## Komme i gang

For å få prosjektet til å kjøre følg punktene under.

### Programmets forutsetninger

1. Bygg programmet gjennom Maven Package. Dette gjøres ved å skrive "mvn package" i terminalen eller å velge "Package" under Maven -> Lifecycle. (Dersom programmet har vært bygget tidligere kan det være lurt å gjøre en "Clean" før "Package".

2. JAR filen er nå pakket og kan eksekveres i en egen mappe. I mappen må det være en properties fil ved navn "pgr203.properties".
    - Denne har til oppgave å servere et sett med verdier til programmet slik at lokal PostgreSQL konfigurasjon fungerer med programmet.

3. pgr203.properties fil må ha følgende verdier fra brukerens lokale PostgreSQL database:
    - dataSource.url='url'
    - dataSource.username='brukernavn'
    - dataSource.password='passord'

4. Programmet kan nå eksekveres og leser automatisk verdiene fra pgr203.properties så fremt det ligger i samme filmappe.
    - Skriv kommandoen: 'java -jar '*filnavn-på-JAR*'' i terminalen for å eksekvere den pakkede JAR filen og starte serveren.
 

### Funksjonalitet i programmet


<!-- evaluering -->
## Refleksjoner rundt prosjektet
