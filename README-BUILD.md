# Streak Bot — build APK

Project Android Native menggunakan Kotlin, Android Gradle Plugin 8.5.2, Gradle 8.7, JDK 17, dan Android SDK 34.

## Build melalui Android Studio

1. Ekstrak folder `StreakBot`.
2. Buka folder tersebut melalui Android Studio.
3. Pastikan JDK 17 dan Android SDK Platform 34 terpasang.
4. Tunggu Gradle Sync selesai.
5. Pilih **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
6. APK debug berada di `app/build/outputs/apk/debug/app-debug.apk`.

## Build otomatis melalui GitHub Actions

1. Upload isi folder project ini ke repository GitHub.
2. Buka tab **Actions**.
3. Pilih workflow **Build Android APK** lalu tekan **Run workflow** (atau push ke branch `main`/`master`).
4. Setelah selesai, unduh artifact **StreakBot-debug-apk**.

## Catatan penggunaan

Aplikasi memakai Accessibility Service untuk mengoperasikan antarmuka TikTok. Aktifkan layanan hanya jika memahami izin yang diminta. Uji secara manual terlebih dahulu; pastikan penerima menyetujui pesan dan jangan gunakan untuk mengirim pesan massal atau spam. Perubahan antarmuka TikTok dapat membuat otomasi tidak bekerja.
