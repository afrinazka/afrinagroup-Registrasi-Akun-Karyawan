# afrinagroup-Registrasi-Akun-Karyawan


Aplikasi Android (Jetpack Compose) untuk latihan **State, Event, dan Form Validation**.
Tema: portal HR karyawan dengan palet warna **sage green + soft pink**.

## Fitur

- Formulir registrasi akun karyawan dengan 5 field + checkbox persetujuan.
- **Field tambahan:** ID Karyawan (format `EMP-1234`).
- Validasi per field dengan pesan error di bawah kolom (`supportingText`).
- Validasi email wajib memakai domain kantor `@kantorku.co.id`.
- Kata sandi minimal 8 karakter dan harus kombinasi huruf + angka.
- Tombol tampilkan/sembunyikan kata sandi.
- Pesan sukses menampilkan nama dan ID karyawan.
- Input nama, ID, email, dan centang persetujuan tetap tersimpan saat layar diputar.

## Cara Menjalankan

1. Clone repository, lalu buka folder project di **Android Studio**.
2. Tunggu Gradle sync selesai.
3. Pilih emulator atau perangkat Android (API 24+).
4. Klik **Run ▶** pada modul `app`.
5. Untuk lihat tanpa menjalankan: buka `MainActivity.kt`, aktifkan **Split/Design**, lihat `RegistrationScreenPreview`.

> Catatan: ikon `Badge` (field ID Karyawan) ada di `material-icons-extended`.
> Jika merah, tambahkan di `app/build.gradle.kts`:
> `implementation("androidx.compose.material:material-icons-extended")`

## Perbedaan dari Contoh Awal

| Aspek | Contoh awal | Versi saya |
|---|---|---|
| Tema | Mahasiswa / KampusKu | Karyawan / KaryaKita HR |
| Palet warna | Ungu & teal | Sage green & soft pink |
| Label & teks | Nama lengkap, Daftar sekarang, dst. | Nama lengkap karyawan, Email kantor, Aktifkan Akun, dst. |
| Field | 4 field | **+ ID Karyawan** |
| Aturan email | Format email valid | Format valid **dan** domain `@kantorku.co.id` |
| Aturan password | Minimal 8 karakter | Minimal 8 karakter **dan** huruf + angka |

## Penjelasan Singkat State dan Validasi

### State (`RegistrationScreen`, bagian "1. STATE MANAGEMENT")

State adalah data yang menentukan tampilan UI. Setiap perubahan state memicu **rekomposisi**, sehingga UI otomatis diperbarui.

- `nama`, `idKaryawan`, `email`, `setuju`, `passwordTerlihat` memakai `rememberSaveable` sehingga tidak hilang saat rotasi layar.
- `password` dan `konfirmasiPassword` memakai `remember` saja (tidak disimpan di saved state demi keamanan).
- `sudahSubmit` menandai tombol sudah ditekan, jadi error baru muncul setelah submit.
- `berhasil` menyimpan hasil validasi akhir untuk menampilkan pesan sukses.

### Event

Event adalah aksi pengguna yang mengubah state:

- `onValueChange` pada setiap `OutlinedTextField`: mengisi state dengan teks baru dan mereset `berhasil = false`.
- `onCheckedChange` pada `Checkbox`: memperbarui `setuju`.
- `onClick` pada ikon mata: membalik `passwordTerlihat`.
- `onClick` pada tombol **Aktifkan Akun**: set `sudahSubmit = true` dan `berhasil = formValid`.

Alurnya: **pengguna beraksi → event → state berubah → UI direkomposisi**.

### Validasi (bagian "2. FORM VALIDATION")

Validasi dihitung ulang setiap state berubah (deklaratif):

| Field | Aturan |
|---|---|
| Nama | Tidak boleh kosong |
| ID Karyawan | Harus cocok regex `^EMP-\d{4}$` |
| Email kantor | Format email valid dan berakhiran `@kantorku.co.id` |
| Kata sandi | Minimal 8 karakter, mengandung huruf dan angka |
| Konfirmasi | Sama dengan kata sandi |
| Persetujuan | Harus dicentang |

Setiap aturan menghasilkan boolean `errorXxx` yang dipakai oleh `isError` dan `supportingText`. Variabel `formValid` bernilai `true` hanya jika semua aturan terpenuhi.
