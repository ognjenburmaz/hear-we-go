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