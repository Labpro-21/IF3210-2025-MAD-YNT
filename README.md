# Tugas Besar Android IF3210 2024/2025

## Deskripsi Aplikasi - Purrytify

**Purrytify** adalah aplikasi pemutar audio berbasis Android dengan berbagai fitur yang mendukung pengalaman mendengarkan musik secara personal dan interaktif. Aplikasi ini terdiri dari beberapa fitur utama:

- **Home**: Menampilkan lagu-lagu terbaru yang ditambahkan serta daftar lagu yang terakhir diputar.
- **Library**: Menyediakan daftar lagu yang telah ditambahkan pengguna. Pengguna dapat menambahkan lagu baru dan memberikan *like* untuk menandai lagu favorit.
- **Profile**: Menampilkan informasi pengguna yang telah login, termasuk jumlah lagu yang dimiliki, disukai, dan didengarkan.
- **Auth**: Fitur untuk login dan logout.
- **Pemutaran Lagu**: Memutar lagu yang telah ditambahkan pengguna.
- **Notification Control**: Mengendalikan pemutaran lagu langsung dari notifikasi.
- **Edit Profile**: Mengubah informasi pengguna, termasuk lokasi.
- **Online Song**: Menyediakan akses ke lagu secara daring.
- **Online Song by Region**: Lagu yang tersedia disesuaikan berdasarkan lokasi pengguna.
- **Download Song**: Mengunduh lagu yang tersedia secara daring.
- **Sound Capsule**: Rekap bulanan seperti:
  - Artis & lagu teratas
  - Total waktu mendengarkan
  - *Streak* mendengarkan selama sebulan
- **Audio Routing**: Memungkinkan pemilihan perangkat output audio (misalnya speaker, headset, Bluetooth, dll).


## Library & Dependency yang Digunakan

Purrytify menggunakan berbagai pustaka untuk mendukung fitur-fitur di atas:

### Core Android & Jetpack Compose
- `androidx-core-ktx`
- `androidx-core-splashscreen`
- `androidx-activity-compose`
- `androidx-compose-bom`
- `androidx-ui`
- `androidx-ui-graphics`
- `androidx-ui-tooling`
- `androidx-ui-tooling-preview`
- `androidx-material3`
- `androidx-material-icons-extended`
- `androidx-constraintlayout`
- `androidx-runtime-livedata`
- `coil-compose`

### Navigation & Lifecycle
- `androidx-navigation-fragment-ktx`
- `androidx-navigation-ui-ktx`
- `androidx-navigation-compose`
- `androidx-lifecycle-livedata-ktx`
- `androidx-lifecycle-viewmodel-ktx`
- `androidx-lifecycle-runtime-ktx`
- `androidx-lifecycle-viewmodel-compose`

### Media & Playback
- `androidx-media`
- `androidx-media3-session`
- `androidx-media3-ui`
- `androidx-media3-exoplayer`

### Database & Networking
- `androidx-room-runtime`
- `androidx-room-ktx`
- `room-compiler`
- `retrofit`
- `converter-gson`

### Asynchronous
- `kotlinx-coroutines-android`

### Location & Maps
- `play-services-location`
- `play-services-maps`
- `maps-compose`

### Permissions
- `accompanist-permissions`

### WorkManager
- `androidx-work-runtime-ktx`

## Plugins

- `com.android.application`
- `org.jetbrains.kotlin.android`
- `org.jetbrains.kotlin.plugin.compose`

## Screenshot Aplikasi
![image](https://github.com/user-attachments/assets/c76144c6-7851-488c-b8f9-e01f7bc83168)
![image](https://github.com/user-attachments/assets/ebd66fae-3556-4c04-b116-c6f4e7a91335)
![image](https://github.com/user-attachments/assets/5345ce8f-b4ee-4776-aa8b-0462887ba77f)
![image](https://github.com/user-attachments/assets/ffcfc772-9191-4ee2-b439-e7a1649e4178)
![image](https://github.com/user-attachments/assets/57f5e3e3-332e-4580-a883-14e6af8636d3)
![image](https://github.com/user-attachments/assets/75ab1ebf-d1c7-4e9a-a99a-3b98a6428e07)
![image](https://github.com/user-attachments/assets/370d7ff1-56de-4dc4-b9c2-bbf9d5c50d95)
![image](https://github.com/user-attachments/assets/d0469a05-9a31-4053-812a-56fc4b309f92)
![image](https://github.com/user-attachments/assets/aec8c746-aee0-47c1-8023-c385dda6301b)
![image](https://github.com/user-attachments/assets/0099d3e7-d011-452e-8e5a-7411129b9b02)
![image](https://github.com/user-attachments/assets/416c3528-4259-49d5-8d56-6caae599e953)


## Pembagian Kerja Anggota Kelompok

| NIM          | Nama Anggota | Tugas yang Dilakukan                       |
|--------------|--------------|--------------------------------------------|
| 13522036      | Akbar Al Fattah    | Login, Logout, Background Service, Pemutaran Lagu, Audio Routing, Deeplink, Share song |
| 13522088      | Muhamad Rafli Rasyiidin    | Header, Navbar, Profile, Home, Online Song, Download, Edit Profile, Profile Location, Sound Capsule|
| 13522100      | M. Hanief Fatkhan Nashrullah    | Library, Network sensing, Header, Navbar, Rework (Fully migrate to Jetpack Compose), Exoplayer, Notification Control, Responsive UI, Database Usage Tracker, |

## Jumlah Jam Persiapan dan Pengerjaan Anggota

| NIM          | Nama Anggota | Jam Pengerjaan |
|--------------|--------------|-----------|
| 13522036      | Akbar Al Fattah    |Login(8 Jam), Logout (2 Jam), Background Service (12 Jam), Pemutaran Lagu (12 Jam), Total: 34 Jam |
| 13522088      | Muhamad Rafli Rasyiidin    | Header (1 jam), Navbar (1 jam), Profile (12 jam), Home (10 jam), Total: 24 jam   |
| 13522100      | M. Hanief Fatkhan Nashrullah    |  RecyclerView(12 Jam), Setup Room Database(4 Jam), Setup Bottom Navigation Layout (6 Jam), Add Edit Delete Song(10 Jam), Network Sensing (2 Jam), Rework (18 Jam), Exoplayer (18 Jam), Notification Control (10 Jam), Responsive UI (5 Jam), Database Usage Tracker (5 Jam) Total: 90 Jam  |
