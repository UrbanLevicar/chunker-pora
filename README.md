# Chucker
## 1. Kaj je Chucker?

Chucker je odprtokodna Android knjižnica, ki deluje kot **OkHttp interceptor** in omogoča:
* pregled vseh HTTP in HTTPS zahtevkov,
* vpogled v request in response body,
* pregled headerjev, statusnih kod in časa odziva,
* pregled prometa neposredno znotraj aplikacije.

Knjižnica je namenjena izključno **debug okolju** in se v produkciji ne uporablja.

---

## 2. Zakaj je Chucker uporaben?

Pri razvoju mobilnih aplikacij pogosto prihaja do napak v komunikaciji s strežnikom:

* napačni endpointi,
* napačni ali manjkajoči headerji (npr. Authorization),
* napačni podatki v request body-ju,
* nepričakovani odgovori strežnika.

Chucker omogoča hiter in pregleden vpogled v te podatke brez uporabe zunanjih orodij.

---

## 3. Kako Chucker deluje?
* Integrira se v aplikacijo kot **OkHttp interceptor**.
* Prestrezene zahteve shrani lokalno.
* Uporabniku ponudi grafični vmesnik za pregled omrežnih klicev.
* Deluje samodejno ob vsakem HTTP klicu.

---

## 4. Licenca
* Chucker je izdan pod Apache License 2.0
* Prosta uporaba: dovoljena uporaba za osebne, izobraževalne in komercialne projekte
* Odprta koda: izvorno kodo je dovoljeno pregledovati, spreminjati in prilagajati

## 5. Časovna in prostorska zahtevnost
### Časovna zahtevnost
Chucker deluje kot OkHttp interceptor, zato se vsaka HTTP/HTTPS zahteva obdela med pošiljanjem in prejemom podatkov. Dodaten čas obdelave je majhen in je v praksi zanemarljiv v primerjavi z omrežno zakasnitvijo same zahteve.
### Prostorska zahtevnost
Chucker shranjuje prestrežene HTTP zahteve in odgovore lokalno na napravi. Torej je prostorska zahtevnost odvisna od števila in velikosti restreženih klicev (praviloma okoli 10-20MB)

## 6. Ocenitev števila uporabnikov
<img width="608" height="43" alt="image" src="https://github.com/user-attachments/assets/5482ba62-431f-432c-a848-8a967b6bd136" />

Natančno število uporabnikov ni javno dostopno, zato sem to ocenil s pomočjo podatkov iz GitHuba, kar je vidno na zgornji sliki. Tako sem število uporabnikov ocenil na 10 do 25 tisoč uporabnikov.

## 7. Vzdrževanje
Knjižnjica je aktivno vzdrževana, saj je bil zadnji commit nekaj dni nazaj, zadnja izdaja pa novembra 2025. So pa tudi ne dolgo nazaj prešli na načrtovalski vzorec graditelja (builder pattern). Imajo pa tudi aktivno skupnost, ki sodeluje pri projektu.

## 8. Primerjava z drugimi knjižnicami
| Kriterij                      | Chucker    | OkHttp Logging | Stetho  | Flipper |
| ----------------------------- | ---------- | -------------- | ------- | ------- |
| Enostavna integracija         | Da         | Da             | Ne      | Ne      |
| Preglednost HTTP klicev       | Zelo dobra | Slaba          | Srednja | Dobra   |
| Uporaba brez računalnika      | Da         | Da             | Ne      | Ne      |
| Primerno za demo/predstavitev | Da         | Ne             | Delno   | Delno   |
| Primerno za študente          | Da         | Delno          | Delno   | Ne      |
| Obremenitev aplikacije        | Nizka      | Zelo nizka     | Srednja | Visoka  |

## 9. Primer implementacije
V **build.gradle.kts** je najprej treba dodati odvisnosti:
```gradle
dependencies {
    debugImplementation("com.github.chuckerteam.chucker:library:4.2.0")
    releaseImplementation("com.github.chuckerteam.chucker:library-no-op:4.2.0")
}
```
Tu lahko vidimo da ima **no-op** verzijo, ki se uporabi v release buildu in ne dela ničesar tako, da ne vpljiva na učinkovitost delovanja aplikacije.



