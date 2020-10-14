![Java CI with Maven](https://github.com/kristiania/pgr203innlevering2-Kohort5-1/workflows/Java%20CI%20with%20Maven/badge.svg)

Dette prosjektet setter opp en HTTP Server som brukere kan kobler til via en HTTP Klient.
Serveren demonstrerer hvordan Server og Client snakker med hverandre via REQUEST linjer som (GET, POST) på REQUEST Target som da svarer med /echo 
og status protocol (200, 404, 401, 500) og til slutt ender med status melding.

Prosjektet bygges via maven -> lifecycle -> package - SERVER Start
Viktig å velge versjonen som ender med shaded.jar da denne er bygget på riktig måte.

Brukeren skal kunne legge til flere brukere og lese ut en liste over brukere.
Via index.html kan brukeren klikke "Add new project member" som fører til newWorker.html siden som bruker HTML Form og POST action til å legge til flere medlemmer.
Vi valgte å ikke bruke for mye tid på å formatere data fra POST requesten. Forbedringer her kunne vært å lagre det som JSON objekter for så å presentere det
på en finere måte i Frontend med JavaScript.
