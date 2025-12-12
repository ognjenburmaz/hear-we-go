O.B.
Dodao pocetnu strukturu projekta. Projekat se pokrece tako sto se u IDE otvori root folder u kojem su front back i ostalo
onda levo dole u servisima ako vec nema izabere se + pa spring boot i onda se stikliraju servisi koji se dizu i tu dole u toj konzoli se pali. 
Za sad mogu zajedno da se upale samo Discovery i User, registracija kao radi probaj postman. Mora ```docker-compose up -d mongo``` da se pokrene pre svega.
Sem toga s dokerom nisam jos nista probao. Common lib je zajednicka "biblioteka" gde mozete da stavljate klase koje se koriste u vise servisa.
Mora u IDE da se enable annotation processing zbog lomboka. Kad se podigne app ako radi na ```http://localhost:8761/``` ce biti eureka gui, to je register za
povezivanje servisa medjusobno. Pom koji se nalazi u backend diktira ostalima, zato moze tako da se otvori projekat u root folderu. Ako IDE ne vidi
maven projekat pri prvom otvaranju, desni klik na taj pom, add to maven, zatim reload.

O.B.
docker fix i user implements userdetails

O.B.
gateway radi, docker compose treba sve da digne, `@TODO` security da se poboljsa? i response za reg i log da bude smislen, front sta god i da se i on dokerizuje