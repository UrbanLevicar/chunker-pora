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
```kotlin
object RetrofitClient {
    private const val BASE_URL = "http://localhost:8080/api/"

    fun createApiService(context: Context): ApiService {

        val chuckerCollector = ChuckerCollector(
            context = context, // Kontekst aplikacije
            showNotification = false, // Prikaži obvestilo ko kaj prestrežemo
            retentionPeriod = RetentionManager.Period.ONE_HOUR // Kako dolgo hraniti prestrežene podatke
        )

        val chuckerInterceptor = ChuckerInterceptor.Builder(context)
            .collector(chuckerCollector) // Da ve kam shranjevati podatke
            .maxContentLength(250_000L) // 250 KB
            .alwaysReadResponseBody(true) // Vedno prebere telo odgovora
            .build()

        // OkHttp z Chuckerjem
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(chuckerInterceptor)  // CHUCKER
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(ApiService::class.java)
    }
}
```
```kotlin
binding.btnOpenChucker.setOnClickListener {
    startActivity(Chucker.getLaunchIntent(this))
}
```
<img width="493" height="1101" alt="image" src="https://github.com/user-attachments/assets/293d25f3-baa9-46dd-8b6e-28e61d8669f9" />
<img width="495" height="1102" alt="image" src="https://github.com/user-attachments/assets/30486b21-c58d-44d4-aaf7-0337be119aa0" />
<img width="493" height="1105" alt="image" src="https://github.com/user-attachments/assets/3aaef0a8-9336-4cd0-b572-5a4daea9b4b4" />
<img width="493" height="1103" alt="image" src="https://github.com/user-attachments/assets/73ceb336-0635-4ae2-a210-f9cd15406a9f" />





