# Catatan Submission — Capstone Akhir (Revisi)

> Salin isi di bawah ini ke kolom **Catatan / Notes** saat mengirim ulang submission.

---

## Perbaikan atas catatan reviewer sebelumnya

### 1. Obfuscation belum diterapkan di seluruh module (debug & release) — SUDAH DIPERBAIKI

| Module | Tipe module | `isMinifyEnabled` debug | `isMinifyEnabled` release | Rules |
| --- | --- | :---: | :---: | --- |
| `:app` | `com.android.application` | **true** | **true** | `app/proguard-rules.pro` |
| `:core` | `com.android.library` | **true** | **true** | `core/proguard-rules.pro` + `core/consumer-rules.pro` |
| `:favorite` | `com.android.dynamic-feature` | (lihat penjelasan) | (lihat penjelasan) | `favorite/proguard-rules.pro` |

**Mengapa `:favorite` tidak menyetel `isMinifyEnabled = true`?**
Android Gradle Plugin melarangnya dan menggagalkan build dengan pesan resmi:

```
Dynamic feature modules cannot set minifyEnabled to true.
minifyEnabled is set to true in build type 'debug'.
To enable minification for a dynamic feature module, set minifyEnabled to true in the base module.
```

Sesuai instruksi AGP tersebut, minifikasi `:favorite` dijalankan oleh R8 milik base module `:app` yang sudah `true` pada debug **dan** release. File `favorite/proguard-rules.pro` tetap dibaca dan digabungkan ke proses R8 base module. Penjelasan ini juga ditulis sebagai komentar di `favorite/build.gradle.kts`.

**Catatan mengenai buildType `debug`:**
`isMinifyEnabled = true` sudah aktif di `debug` sehingga R8 benar-benar berjalan. Namun AGP secara sengaja tidak me-*rename* simbol pada varian `debuggable` agar stack trace tetap terbaca. Karena itu bukti obfuscation paling jelas ada pada build **release**:

```
com.example.capstone.core.utils.Resource                  -> g1.d:
com.example.capstone.core.domain.usecase.MovieInteractor  -> f1.a:
com.example.capstone.core.di.CoreModuleKt                 -> e1.d:
com.example.capstone.favorite.FavoriteViewModel           -> j1.l:
com.example.capstone.databinding.ActivityDetailBinding    -> h1.a:
```

Cara verifikasi:

```bash
./gradlew assembleRelease
grep "^com\.example" app/build/outputs/mapping/release/mapping.txt | grep -v " -> com\.example"
```

File `mapping.txt` untuk debug maupun release juga diunggah otomatis sebagai artifact di GitHub Actions (job artifact: `r8-mapping-files`).

---

### 2. Masih ada issue Performance saat Inspect Code — SUDAH DIPERBAIKI (20 → 0)

| Issue | Jumlah sebelum | Sesudah | Perbaikan |
| --- | :---: | :---: | --- |
| Overdraw: painting regions more than once | 7 | **0** | `android:background="@color/bg_dark"` pada root layout dihapus (tema sudah menyetel `android:windowBackground` dengan warna sama); dipindah ke `tools:background` agar preview tetap benar |
| Missing `baselineAligned` attribute | 3 | **0** | `android:baselineAligned="false"` ditambahkan pada `LinearLayout` horizontal berisi child ber-`layout_weight` |
| Unused resources | 10 | **0** | Palet warna duplikat di `:app` dihapus (satu sumber di `:core`), layout `item_movie_card.xml` duplikat di `:app` dihapus, string tak terpakai dihapus, dan `backup_rules.xml` / `data_extraction_rules.xml` didaftarkan ke manifest dengan aturan nyata |

File yang disentuh: `activity_detail.xml`, `activity_main.xml`, `activity_splash.xml`, `fragment_home.xml`, `fragment_search.xml`, `favorite/activity_favorite.xml`, `favorite/fragment_favorite.xml`, `app/AndroidManifest.xml`, `app/res/xml/*.xml`, `values/colors.xml`, `values/strings.xml`.

**Pencegahan regresi:** issue-issue tersebut dinaikkan menjadi `error` pada blok `lint { }` di `app/build.gradle.kts` dengan `checkDependencies = true`, sehingga bila muncul lagi akan langsung menggagalkan build CI.

Sekalian dibereskan juga di luar kategori Performance (hasil `./gradlew lint`: **0 error**):

- Internationalization — 39 `HardcodedText` + 2 `SetTextI18n` → 0 (semua teks dipindah ke `strings.xml` tiap module)
- Accessibility — 14 `ContentDescription` → 0 (`@null` untuk ikon dekoratif, deskripsi nyata untuk elemen bermakna)
- Usability — `Autofill` dan `SmallSp` → 0

---

## Continuous Integration

**Link CI:** https://github.com/farrasariffadhila/CineVerse-Capstone/actions

GitHub Actions (`.github/workflows/android.yml`) menjalankan pada setiap push/PR:

1. `./gradlew lint` — semua module, `checkDependencies = true`, issue performance/i18n/aksesibilitas sebagai **error**
2. `./gradlew testDebugUnitTest` — 9 unit test (`:core` + `:app`), semua pass
3. `./gradlew assembleDebug` + `bundleDebug` — build ter-obfuscate
4. `bundletool build-apks --mode=universal` — universal APK termasuk dynamic feature `:favorite`
5. `./gradlew assembleRelease bundleRelease` — R8 + resource shrinking

Artifact yang diunggah: APK & AAB, laporan lint, laporan unit test, dan file `mapping.txt` R8.

---

## Security — teknik & lokasi class

| Teknik | Implementasi | Lokasi |
| --- | --- | --- |
| **Obfuscation** | ProGuard/R8, `isMinifyEnabled = true` di debug & release | `app/build.gradle.kts`, `core/build.gradle.kts`, `app/proguard-rules.pro`, `core/proguard-rules.pro`, `core/consumer-rules.pro`, `favorite/proguard-rules.pro` |
| **Database Encryption** | SQLCipher 4.5.4 + `SupportFactory` pada `Room.databaseBuilder(...).openHelperFactory(factory)` | `core/src/main/java/com/example/capstone/core/di/CoreModule.kt` (`databaseModule`) |
| **Certificate Pinning** | OkHttp `CertificatePinner`, 4 SHA-256 pin untuk `api.themoviedb.org` | `core/src/main/java/com/example/capstone/core/di/CoreModule.kt` (`networkModule`) |
| **Backup hardening** *(tambahan)* | Database terenkripsi dikecualikan dari Auto Backup & device-to-device transfer | `app/src/main/res/xml/backup_rules.xml`, `app/src/main/res/xml/data_extraction_rules.xml`, didaftarkan di `AndroidManifest.xml` |

Bukti database benar-benar terenkripsi (16 byte pertama file DB — bukan `SQLite format 3`):

```
$ adb shell "run-as com.example.capstone head -c 16 databases/Movie.db" | xxd
00000000: 2f34 cc0e 7b27 4e1b 51ac 7f47 d88c 8bcd  /4..{'N.Q..G....
```

---

## Performance

- **LeakCanary** `2.14` (`debugImplementation`) — 0 distinct leaks. Semua adapter & binding didetach di `onDestroyView()`.
- **Android Lint** — 0 issue kategori Performance (sebelumnya 20).
- **Live Search** — `debounce(300ms)` + `flatMapLatest` untuk menekan jumlah request network.
- **NetworkBoundResource** + Room sebagai single source of truth (data tersedia offline).

---

## Saran yang diterapkan

1. **Fitur tambahan di luar 3 fitur utama** — Live Search dengan debounce, Featured Spotlight banner, kategori filter (Popular / Now Playing / Top Rated), Share movie, shimmer loading, serta empty & error state dengan tombol Try Again.
2. **Unit test** — 9 unit test untuk `DataMapper`, `MovieInteractor`, dan `DomainToPresentationMapper` menggunakan JUnit 4 + `kotlinx-coroutines-test`.
3. **CI dengan analisis tambahan** — selain unit test & build APK, pipeline juga menjalankan Android Lint dengan `checkDependencies = true` dan aturan error kustom, lalu mengunggah laporan lint, laporan test, dan file mapping R8.
4. **Security di luar yang diajarkan** — backup hardening (`backup_rules.xml` + `data_extraction_rules.xml`) sehingga database terenkripsi tidak ikut ter-backup ke cloud maupun ter-transfer antar perangkat, serta penghapusan log verbose/debug dari APK release lewat `-assumenosideeffects` di `app/proguard-rules.pro`.
5. **Clean code & tanpa issue Inspect Code** — `./gradlew lint` menghasilkan **0 error**; sisa warning hanya notifikasi "versi dependency yang lebih baru tersedia".
6. **Tampilan sesuai standar** — dark theme konsisten, komponen dipakai sesuai fungsinya (`ImageButton` untuk aksi navigasi, `MaterialButton` untuk aksi retry), spacing/padding konsisten, ditambah shimmer loading serta empty & error state.
