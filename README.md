![Java CI with Maven](https://github.com/kristiania/pgr203eksamen-Kohort-5/workflows/Java%20CI%20with%20Maven/badge.svg)


<!-- Innhold -->
## Innhold

* [Om Prosjektet](#om-prosjektet)
* [Getting Started](#komme-i-gang)
  * [Funksjonalitet](#funksjonalitet)
* [Evaluering](#evaluering)




<!-- om-prosjektet -->
## Om Prosjektet
* Medlem 1 - kandidatnummer 10086



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
 
### Bruke Programmet
Ettersom jeg har fokusert lite på brukervennlighet er programmet nokså enkelt å navigere.
- Trykk på "Add new project member" -> Her kan man legge til nye brukere med fornavn, etternavn og epost.
    - Disse blir dekodet til utf-8 i programmet og håndterer derfor norske tegn og symbolder på korrekt måte før det lagres i databasen.
- Trykk på "Add new task" for å legge til oppgave med navn, beskrivelse og initiell status.
- Trykk på "List tasks" for å få en liste over alle oppgavene i databasen. Disse viser også oppdatert status og medlemmene som er tildelt oppgaven.
    - Det er på denne siden mulig å filtrere oppgaver på medlem og status. Nedtrekksmenyene er oppdatert med seneste data fra databasen.
    - Det blir også vist en informasjonsmelding dersom det ikke finnes treff på filtreringen.
- Trykk på "Update Task" for å oppdatere status på en valgfri oppgave.
- Trykk på "Assign task to member" for å tildele en oppgave til et medlem. Begge nedtrekksmenyer er oppdatert med data fra databasen.
### Programdesign
Programmet oppfyller alle kravene spesifisert som minstekrav i oppgaven. Programmet gir en detaljert logg over requests, nye tilkoblinger og feil/infomeldinger forbundet med programmet.
Jeg har også gjort noen av tilleggskravene og vil liste disse under.
- Håndtering av request target blir gjort og brukeren vil få dynamiske error meldinger rendret som HTML. Håndterer blant annet
Feil i URL - F.eks localhost:8080/index.ht (Gir statuskode 404 og knapp for redirect til hovedside).
Dersom man prøver å tildele en oppgave til et medlem som allerede er tildelt oppgaven (Gir statuskode 422 unprocessable entity og informerer brukeren om dette)

- UML diagram for hver klasse i tillegg til datamodell for hvordan flyten av data gjennomføres i programmet. (Finnes i mappe "Dokumentasjon")
*[Datamodell](Dokumentasjon/Datamodell.png).
*[ControllerUML](Dokumentasjon/Controllers_uml.png).
*[DataAccessObjectUML](Dokumentasjon/DataAccessObject_uml.png).
*[ServerUML](Dokumentasjon/Server_uml.png).

- Har til dels klart å utvikle et rammeverk rundt HttpMessage og HttpResonse som kalles av controllere og lager dynamiske Http responser. Har også en Message klasse for generering av Error eller Info responser. Hadde tiden strukket til skulle jeg gjerne refactoret mye av koden forbundet med requester og responser og heller latt de arve fra HttpMessage som subklasser.

- Det meste av routing skjer via HttpController og controllerMap og gjorde at HttpServer har renere og mindre kode i seg. Det meste av funksjonalitet tas hånd om i dedikerte kontrollere.

- Kontrollere bruker de 3 DAO klassene ProjectMemberDao, ProjectTaskDao og MemberTaskDao effektivt. Hadde jeg hatt bedre tid ville jeg abstrahert mer av koden i de respektive DAOene inn i AbstractDao. Metoder som insert() og list() hadde ikke trengt å være egne metoder i de forskjellige DAOene men ettersom jeg var alene om oppgaven rakk jeg ikke dette.

- Jeg undersøkte metoder for automatisk testdekning men ble begrenset av at CodeCov ikke fikk tilgang til Kristiania repositories. Jeg har derimot inkludert rapporter for testdekningen i mappen "Dokumentasjon".

<!-- evaluering -->
## Erfaringer
Jeg har lært svært mye av dette prosjektet. Det innledningsvis vanskelige gruppesamarbeidet gjorde at jeg måtte tilstrebe å være mer utadvendt og lære meg å se ting fra flere sider. Løsningen vi deretter kom frem til har fungert veldig bra og jeg vil si at det var til begges fordel å dele oss opp. Vi har fortsatt samarbeidet og delt ideer og erfaringer. Prosjektet var veldig spennende da produktet vi skulle utvikle var både interessant og stort. Det har hele tiden vært en rød tråd koblet opp mot oppgaven, noe som gjorde at når selve utviklingsdelen startet søkte jeg å lære mest mulig om de forskjellige delene av prosjektet. Jeg har hatt et godt samarbeid med andre grupper og det har vært morsomt å dele løsninger, ideer og problemer i fellesskap.
