O.B.
Dodao pocetnu strukturu projekta. Projekat se pokrece tako sto se u IDE otvori root folder u kojem su front back i
ostalo
onda levo dole u servisima ako vec nema izabere se + pa spring boot i onda se stikliraju servisi koji se dizu i tu dole
u toj konzoli se pali.
Za sad mogu zajedno da se upale samo Discovery i User, registracija kao radi probaj postman. Mora
```docker-compose up -d mongo``` da se pokrene pre svega.
Sem toga s dokerom nisam jos nista probao. Common lib je zajednicka "biblioteka" gde mozete da stavljate klase koje se
koriste u vise servisa.
Mora u IDE da se enable annotation processing zbog lomboka. Kad se podigne app ako radi na ```http://localhost:8761/```
ce biti eureka gui, to je register za
povezivanje servisa medjusobno. Pom koji se nalazi u backend diktira ostalima, zato moze tako da se otvori projekat u
root folderu. Ako IDE ne vidi
maven projekat pri prvom otvaranju, desni klik na taj pom, add to maven, zatim reload.

O.B.
docker fix i user implements userdetails

O.B.
gateway radi, docker compose treba sve da digne, `@TODO` security da se poboljsa? i response za reg i log da bude
smislen, front sta god i da se i on dokerizuje

A.V.

- Konfigurisan mongodb, sada se pri paljenju docker compose mongo baza za user-service puni sa podacima iz
  ```backend/users-service/src/main/resources/db/import_users.js``` (Skripta se pokrece SAMO JEDNOM, ako zelimo da je
  pokrenemo opet potrebno obrisati ```hear-we-go_mongo_data``` volume sa dockera)
- Zavrsen Spring Security na users-service, login i registracija rade kako treba, prilikom logina salje se username i
  password, a dobija se jwt i expiration date, videti postman zahteve ovde:
  ```https://www.postman.com/workspace/My-Workspace~af45c8f7-dc83-4402-8b3b-ba9332cb2405/collection/39270923-523321ee-ce19-468a-a271-0fe1cfaf5642?action=share&source=copy-link&creator=39270923``` ,
  folder Veliki Projekat
- ~~api-gateway nam uopste ne treba kao spring app, vec samo iskoristimo postojeci NGINX image i konfigurisemo sve
  putanje
  u ```backend/nginx/nginx.conf```. Obratiti paznju da NGINX menja http zahteve tako sto izbaci deo posle "location" pa
  ce npr zahtev ```http://localhost:8080/api/users/login``` biti prosledjen na user-service kao
  ```http://users-service:8081/login```. (Ovo vrv znate, ali nije localhost i drugi je port zato sto unutar dockera ovaj
  servis slusa na 8081, a mi saljemo zahtev na 8080 na kom slusa NGNIX)~~
- Ipak vracen stari api gateway (Eureka i Spring Cloud)
- Trebalo bi se pozabaviti autorizacijom u ngnix, ali ne bi trebalo da je puno komplikovano
- `@TODO` jos samo FE aplikacija, trelloi i ako cemo odmah raditi i sertifikate tj komunikaciju preko HTTPS

O.K.
Uradjen front za auth

O.B.
Back crud za pesme, albume, izvodjace (content service), code cleanup

A.V.
Implementirano logovanje pomocu jednokratne lozinke (Zahtev 1.2 u specifikaciji). Sada pored lozinke korisnik mora da
unese i jednokratan kod koji mu se salje na mejl. Uradjen i backend i frontend deo.
Dodati ovo u .env: EMAIL_PASSWORD=dmbj gzqy dxxx veau  
Takodje svi useri trenutno imaju moje mejlove pa mozete to promeniti/dodati nove u
```backend/users-service/src/main/resources/db/import_users.js```
(Obavezno u docker desktop containers > obrisati ceo compose pa volumes > obrisati mongov volume)

A.V.
Zavrsen account recovery, sada u slucaju zaboravljene lozinke korisnik upisuje mejl od svog naloga, na koji ce se
poslati magicni link
koji ce omoguciti korisniku promenu lozinke. Uradjen i frontend i backend

A.V.
Namestio MailHog, kada se app pokrene u dockeru mail-ovi se nece slati na pravi mejl nego ce se nalaziti na
```http://localhost:8025/```, tako da ne mora svako da dodaje svoje mejlove medju user-e

O.B. https setup prebacen na jednu skriptu uputstva u README mora biti java instalirana, 
notifikacije zavrsene, rate limit impl s skriptom za simuliranje dos napada u nju treba ubaciti validan
jwt token pre pokretanja, dodao navbar admin links, subscriptions back impl nije testiran flow,
back validacija za user i content serv, repo cleanup

A.V
TODO:
~~Omogućiti korisniku da promeni lozinku. Da bi se lozinka promenila, mora biti bar 1 dan stara.
U svrhu promene lozinke implementirati imejl-bazirani reset lozinke sa poznatom imejl adresom i
kratkotrajnim linkom.~~

~~Potrebno je demonstrirati auditabilnost korisničkog naloga
onemogućavanjem prijave na sistem na određeno vreme nakon isteka roka važenja
lozinke. Maksimalni period važenja aktivne lozinke je 60 dana
(na odbrani simulirati periode za izmenu na kraće).~~

Implementirati potvrdu registracije. (?)

znaci sad kad ima validacija na back na front se ne pokazuju dobre poruke npr sifra ti je pre kratka
ali to nigde ne pise nego izdje neka arbitrarna poruka bar mislim

~~Srediti enviroment varijable~~

~~oko ovoga nisam sig npr u konzolu
browsera vrati pogresan kod konkretno dns primer kad se pravi umetnik baci eror i zaledi na toj str a
umetnik je kreiran normalno~~

O.B.

subscriptions now work, removed home link in navbar, now brand name is that link, added missing env pairs,
artist page now displays artist name, can follow artist or genre, can view subs on profile page and cancel them,
added content db script

rating service, grpc, 2.7 sve uradjeno