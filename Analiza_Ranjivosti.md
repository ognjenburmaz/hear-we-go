# Analiza ranjivosti sistema

## 1. Uvod

Ovaj dokument predstavlja izveštaj o analizi bezbednosti mikroservisne aplikacije za streaming muzike implementirane
korišćenjem **Spring Boot** framework-a.

Sistem je realizovan kao mikroservisna arhitektura koja se sastoji od:

* **API Gateway**
* **Users servis**
* **Content servis**
* **Ratings servis**
* **Subscriptions servis**
* **Notifications servis**
* **Recommendation servis**
* **Analytics servis**

### Komunikacija između servisa:

* **Sinhrono:** REST pozivi (pomoću OpenFeign ili RestTemplate)
* **Asinhrono:** Event-driven komunikacija putem **Kafka** message brokera.

### Tehnološki stack:

Sistem koristi MongoDB (dokument-orijentisana), ScyllaDB (wide-column), Neo4j (graf baza), Redis (keširanje), HDFS (
audio fajlovi), Docker/Kubernetes (orkestracija) i HTTPS za bezbednu komunikaciju.

**Cilj ovog izveštaja je:**

1. Identifikacija potencijalnih ranjivosti.
2. Analiza mogućnosti njihove eksploatacije.
3. Predlog mera zaštite i mitigacije.

---

## 2. Korišćeni alati za identifikaciju ranjivosti

### 2.1 Postman

Iako je primarno alat za razvoj i testiranje API-ja, u ovom projektu je korišćen kao platforma za **manuelnu dinamičku
analizu bezbednosti (DAST)**.

* **Otkrivanje XSS ranjivosti:** Slanje malicioznih JavaScript payload-a kroz POST i PUT zahteve kako bi se proverilo da
  li servis vrši pravilnu sanitizaciju i enkodovanje ulaza.
* **Testiranje SQL/NoSQL injection napada:** Konstruisanje specifičnih JSON objekata sa operatorima (npr. `$ne`, `$gt`)
  radi pokušaja zaobilaženja logike upita i neovlašćenog pristupa podacima u MongoDB bazi.
* **Testiranje autentifikacije i autorizacije:** Manipulacija JWT tokenima (slanje nevalidnih potpisa ili tuđih tokena)
  radi provere robusnosti API Gateway-a i RBAC (Role-Based Access Control) sistema.

### 2.2 cURL

Moćan command-line alat korišćen za preciznu kontrolu HTTP saobraćaja i brzu automatizaciju testova bez potrebe za
grafičkim interfejsom.

* **Presretanje i modifikaciju HTTP zahteva:** Ručno kreiranje i slanje zahteva sa modifikovanim HTTP glagolima (npr.
  slanje DELETE zahteva tamo gde je dozvoljen samo GET) radi provere propustljivosti ruta.
* **Testiranje validacije ulaznih podataka:** Slanje sirovih payload-a koji namerno krše `@Size`, `@Pattern` ili
  `@NotBlank` ograničenja direktno na backend servise, zaobilazeći klijentske validacije.
* **Simulaciju brute-force napada:** Korišćenje Bash petlji u kombinaciji sa cURL komandama za automatizovano slanje
  velikog broja uzastopnih zahteva ka login endpoint-u radi testiranja efikasnosti rate limitinga.

### 2.3 Telnet

Korišćen kao mrežni uslužni program za dijagnostiku dostupnosti servisa i analizu mrežne sigurnosti na niskom nivou.

* **Skeniranje otvorenih portova:** Provera da li su kritični portovi baza podataka (npr. 27017 za Mongo, 9042 za
  ScyllaDB) greškom izloženi javnosti ili su pravilno izolovani unutar Docker mreže.
* **Proveru TLS konfiguracije:** Manuelno testiranje inicijalnog rukovanja (handshake) na portovima koji zahtevaju
  SSL/TLS enkripciju.

### 2.4 Shell i Python Skripte

Ovi alati su korišćeni kao primarni mehanizam za automatizaciju testiranja bezbednosti i simulaciju realnih napada na
sistem.

* **Simulacija opterećenja (DoS):** Multithreaded Python skripte su korišćene za generisanje velikog broja konkurentnih
  zahteva ka API Gateway-u.
* **Brute-force automatizacija:** Bash (Shell) skripte su korišćene za iteriranje kroz liste lozinki i automatizovano
  slanje `cURL` zahteva radi testiranja rate limitinga.

### 2.5 Analiza konfiguracije (Spring Boot)

Proveravana je:

* Izloženost Actuator endpoint-a.
* Konfiguracija CORS-a.
* Bezbednost JWT implementacije.
* Debug režim (isključen u produkciji).

---

## 3 Demonstracija pokušaja napada

U okviru odbrane projekta, realizovana je praktična demonstracija napada korišćenjem prethodno opisanih skripti kako bi
se dokazala efikasnost implementiranih odbrambenih mehanizama.

### 1. Cross-Site Scripting (XSS)

* **Realizacija:** Skripta šalje maliciozni `<script>` tag kroz `POST` zahtev za promenu biografije korisnika.
* **Cilj:** Provera da li će sistem (Spring Boot) i baza podataka (MongoDB) prihvatiti i kasnije renderovati nevalidan
  sadržaj.

### 2. SQL Injection

* **Realizacija:** Skripta salje malicioznu SQL sintaksu u JSON formatu.
* **Cilj:** Pokušaj zaobilaska autentifikacije.

### 3. Brute-force Attack

* **Realizacija:** Python skripta koja automatizovano isprobava stotine lozinki iz predefinisane liste (
  `dictionary attack`) ka `/auth/login`.
* **Cilj:** Demonstracija rada **Rate Limiting** mehanizma koji nakon određenog broja pokušaja vraća
  `HTTP 429 Too Many Requests`.

### 4. Denial of Service (DoS)

* **Realizacija:** Python skripta koja koristi `threading` za simultano slanje hiljada zahteva ka Content servisu.
* **Cilj:** Testiranje **Circuit Breaker-a** (Resilience4j) koji treba da prekine komunikaciju sa preopterećenim
  servisom i sačuva stabilnost ostatka sistema.

---

## 4. Identifikovane ranjivosti

### 4.1 Cross-Site Scripting (XSS)

**Opis:** Ukoliko se korisnički unos (biografija, naziv pesme, opis) ne enkoduje pravilno, napadač može ubaciti
zlonamerni JavaScript kod.

* **Primer napada:** `<script>alert('XSS')</script>`
* **Potencijalne posledice:** Krađa JWT tokena, preuzimanje sesije, manipulacija prikazanim podacima.
* **Mera zaštite:** Server-side validacija, Output encoding (Thymeleaf escape), Content Security Policy (CSP).

### 4.2 SQL / NoSQL Injection

**Opis:** Nepravilna obrada podataka u MongoDB upitima može omogućiti NoSQL injection.

* **Primer napada:** `{"username": {"$ne": null}, "password": {"$ne": null}}`
* **Potencijalne posledice:** Neovlašćen pristup, čitanje/brisanje baze, eskalacija privilegija.
* **Mera zaštite:** Korišćenje Spring Data repozitorijuma, Strong typing (DTO), `@Valid` anotacije, zabrana dinamičkih
  upita.

### 4.3 Brute-Force napad

**Opis:** Napadač pogađa lozinke velikim brojem pokušaja.

* **Potencijalne posledice:** Preuzimanje korisničkih naloga.
* **Mera zaštite:** Rate limiting (Bucket4j/Redis), zaključavanje naloga, OTP autentifikacija, logovanje neuspešnih
  pokušaja (Loki/Grafana).

### 4.4 Denial of Service (DoS)

**Opis:** Zagušenje sistema velikim brojem zahteva prema API Gateway-u.

* **Potencijalne posledice:** Nedostupnost servisa.
* **Mera zaštite:** Rate limiting, Circuit Breaker (Resilience4j), Timeout konfiguracija, Kubernetes resource limits.

### 4.5 Neispravna konfiguracija HTTPS-a

**Rizik:** MITM (Man-in-the-Middle) napadi, presretanje kredencijala.

* **Mera zaštite:** Forsiranje TLS 1.2+, onemogućavanje slabih cipher suite-ova, HSTS zaglavlje.

### 4.6 Slabo heširanje lozinki

**Rizik:** MD5 ili SHA1 omogućavaju brze offline napade (Rainbow tables).

* **Mera zaštite:** **BCrypt** sa salt-om, adekvatan strength faktor, periodična promena lozinke.

### 4.7 Preterano logovanje osetljivih podataka

**Rizik:** Curenje JWT tokena ili lozinki kroz logove u Grafana/Loki sistem.

* **Mera zaštite:** Maskiranje podataka, isključivanje stack trace-a u produkciji, rotacija logova.

---

## 5. Mogućnost eksploatacije

Najkritičnije identifikovane ranjivosti su:

1. **Injection napadi** – direktan pristup bazi.
2. **XSS** – kompromitacija klijentske strane.
3. **DoS** – narušavanje dostupnosti streaming servisa.

---

## 6. Preporučene bezbednosne mere

### 6.1 Autentifikacija i autorizacija

* JWT sa kratkim rokom trajanja.
* Refresh token mehanizam.
* **RBAC** (Role-Based Access Control) na nivou svakog endpoint-a.

### 6.2 Validacija podataka

* Stroga `@Valid` validacija u kontrolerima.
* Regex provere za imena i biografije (Whitelist pristup).
* Boundary checking (min/max vrednosti).

### 6.3 Sigurna komunikacija

* Obavezan HTTPS između svih servisa.
* Enkripcija podataka u tranzitu.

### 6.4 Monitoring i Audit

* Centralizovano logovanje (Loki).
* **Alarmiranje** na detektovane brute-force pokušaje i TLS handshake neuspehe u Grafani.

---

## 7. Zaključak

Analizom bezbednosti identifikovane su standardne ranjivosti mikroservisnih sistema. Primenom predloženih mehanizama (
strog ulazni filter, BCrypt, Rate Limiting, TLS 1.2+ i centralizovani monitoring), sistem postiže visok nivo otpornosti
na moderne sajber napade.