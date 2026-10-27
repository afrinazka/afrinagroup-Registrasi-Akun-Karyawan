package com.example.myapplication

import android.os.Bundle
import android.util.Patterns
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// =========================================================================
// [DIUBAH] PALET WARNA: Sage Green + Soft Pink (tema profesional/kantor)
// =========================================================================
private val SageDark = Color(0xFF5E7D69)      // sage tua  -> warna utama (tombol, judul kecil)
private val SageLight = Color(0xFFDCE8DF)     // sage muda -> garis pemisah / aksen
private val SoftPink = Color(0xFFE8A9B8)      // pink lembut -> warna sekunder
private val PinkBackground = Color(0xFFFBF1F3) // latar layar pink sangat muda
private val TextDark = Color(0xFF2F3E36)      // teks judul
private val TextMuted = Color(0xFF6F7D75)     // teks keterangan

// [BARU] Aturan tambahan: domain email kantor yang diizinkan
private const val DOMAIN_KANTOR = "@kantorku.co.id"

// [BARU] Aturan tambahan: format ID Karyawan, contoh EMP-1234
private val POLA_ID_KARYAWAN = Regex("^EMP-\\d{4}$")

/**
 * Activity utama aplikasi yang menjadi titik masuk (entry point).
 * [DIUBAH] Tema warna diganti ke sage + soft pink.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = lightColorScheme(
                    primary = SageDark,
                    onPrimary = Color.White,
                    secondary = SoftPink,
                    background = PinkBackground,
                    surface = Color.White
                )
            ) {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    RegistrationScreen()
                }
            }
        }
    }
}

/**
 * Screen-level state owner untuk formulir registrasi akun karyawan.
 * Memperagakan State, Event, Anatomi Field (OutlinedTextField), dan Form Validation di Jetpack Compose.
 */
@Composable
fun RegistrationScreen() {
    // =========================================================================
    // 1. STATE MANAGEMENT
    // State = data yang menentukan apa yang tampil di UI.
    // Saat state berubah, Compose melakukan Rekomposisi (menggambar ulang bagian yang terdampak).
    // =========================================================================

    // - rememberSaveable: state bertahan walau layar diputar (perubahan konfigurasi).
    var nama by rememberSaveable { mutableStateOf("") }
    var idKaryawan by rememberSaveable { mutableStateOf("") }   // [BARU] state field ID Karyawan
    var email by rememberSaveable { mutableStateOf("") }

    // - remember: password tidak disimpan di saved state demi keamanan.
    var password by remember { mutableStateOf("") }
    var konfirmasiPassword by remember { mutableStateOf("") }
    var setuju by rememberSaveable { mutableStateOf(false) }
    var passwordTerlihat by rememberSaveable { mutableStateOf(false) }

    // State status pengiriman dan hasil validasi
    var sudahSubmit by remember { mutableStateOf(false) }
    var berhasil by remember { mutableStateOf(false) }

    // =========================================================================
    // 2. FORM VALIDATION (VALIDASI FORMULIR)
    // Dihitung ulang otomatis setiap state input berubah (deklaratif/reaktif).
    // Error baru tampil setelah tombol submit ditekan (sudahSubmit = true).
    // =========================================================================

    // Nama tidak boleh kosong
    val errorNama = sudahSubmit && nama.trim().isEmpty()

    // [BARU] ID Karyawan harus berformat EMP- diikuti 4 angka
    val idValid = POLA_ID_KARYAWAN.matches(idKaryawan.trim())
    val errorId = sudahSubmit && !idValid

    // [DIUBAH] Email harus valid DAN memakai domain kantor
    val emailValid = Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches() &&
            email.trim().endsWith(DOMAIN_KANTOR, ignoreCase = true)
    val errorEmail = sudahSubmit && !emailValid

    // [DIUBAH] Password minimal 8 karakter DAN mengandung huruf + angka
    val passwordValid = password.length >= 8 &&
            password.any { it.isLetter() } &&
            password.any { it.isDigit() }
    val errorPassword = sudahSubmit && !passwordValid

    val errorKonfirmasi = sudahSubmit && konfirmasiPassword != password
    val errorPersetujuan = sudahSubmit && !setuju

    // Formulir valid jika SEMUA kriteria terpenuhi
    val formValid = nama.trim().isNotEmpty() &&
            idValid &&
            emailValid &&
            passwordValid &&
            konfirmasiPassword == password &&
            setuju

    // Layout utama layar dengan scroll vertikal
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 22.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // [DIUBAH] Header bertema karyawan
        Text("AFRINAGROUP", fontSize = 16.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        Text("Registrasi Akun Karyawan", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = TextDark)
        Text("Lengkapi data kepegawaian Anda untuk mengaktifkan akun kantor.", color = TextMuted, fontSize = 14.sp)

        // Kartu kontainer formulir
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // =========================================================================
                // 3. ANATOMI FIELD (OutlinedTextField) & EVENT HANDLING
                // - value: nilai teks dari State.
                // - onValueChange (EVENT): dipanggil saat pengguna mengetik.
                // - label, leadingIcon, trailingIcon, visualTransformation,
                //   isError, supportingText, keyboardOptions.
                // =========================================================================

                // Field 1: Nama Lengkap
                OutlinedTextField(
                    value = nama, // [Anatomi: Value]
                    onValueChange = { // [EVENT] setiap teks berubah
                        nama = it
                        berhasil = false
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Nama lengkap karyawan") }, // [DIUBAH] label
                    leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) },
                    singleLine = true,
                    isError = errorNama,
                    supportingText = { if (errorNama) Text("Nama wajib diisi") }
                )

                // [BARU] Field 2: ID Karyawan (field tambahan + aturan format)
                OutlinedTextField(
                    value = idKaryawan, // [Anatomi: Value]
                    onValueChange = { // [EVENT]
                        idKaryawan = it.uppercase() // otomatis huruf besar
                        berhasil = false
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("ID Karyawan") },
                    leadingIcon = { Icon(Icons.Filled.AccountBox, contentDescription = null) },
                    singleLine = true,
                    isError = errorId,
                    supportingText = {
                        // Petunjuk selalu tampil, berganti jadi pesan error jika tidak valid
                        Text(if (errorId) "Format harus EMP- diikuti 4 angka" else "Contoh: EMP-1234")
                    }
                )

                // Field 3: Email Kantor
                OutlinedTextField(
                    value = email, // [Anatomi: Value]
                    onValueChange = { // [EVENT]
                        email = it
                        berhasil = false
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Email kantor") }, // [DIUBAH] label
                    leadingIcon = { Icon(Icons.Filled.Email, contentDescription = null) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), // [Anatomi: Keyboard Options]
                    isError = errorEmail,
                    supportingText = { // [DIUBAH] pesan error menyebut domain kantor
                        if (errorEmail) Text("Gunakan email kantor berakhiran $DOMAIN_KANTOR")
                    }
                )

                // Field 4: Kata Sandi
                OutlinedTextField(
                    value = password, // [Anatomi: Value]
                    onValueChange = { // [EVENT]
                        password = it
                        berhasil = false
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Kata sandi") },
                    leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
                    trailingIcon = { // [Anatomi: Trailing Icon] toggle visibilitas
                        IconButton(onClick = { // [EVENT] ubah passwordTerlihat
                            passwordTerlihat = !passwordTerlihat
                        }) {
                            Icon(
                                imageVector = if (passwordTerlihat) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                contentDescription = if (passwordTerlihat) "Sembunyikan kata sandi" else "Tampilkan kata sandi"
                            )
                        }
                    },
                    visualTransformation = if (passwordTerlihat) VisualTransformation.None else PasswordVisualTransformation(), // [Anatomi: Visual Transformation]
                    singleLine = true,
                    isError = errorPassword,
                    supportingText = { // [DIUBAH] pesan sesuai aturan baru
                        if (errorPassword) Text("Minimal 8 karakter, kombinasi huruf dan angka")
                    }
                )

                // Field 5: Konfirmasi Kata Sandi
                OutlinedTextField(
                    value = konfirmasiPassword, // [Anatomi: Value]
                    onValueChange = { // [EVENT]
                        konfirmasiPassword = it
                        berhasil = false
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Konfirmasi kata sandi") },
                    leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null) },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    isError = errorKonfirmasi,
                    supportingText = { if (errorKonfirmasi) Text("Konfirmasi harus sama dengan kata sandi") }
                )

                // Checkbox persetujuan (Event & Validasi)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = setuju, // [State]
                        onCheckedChange = { // [EVENT]
                            setuju = it
                            berhasil = false
                        },
                        colors = CheckboxDefaults.colors(checkedColor = SageDark) // [DIUBAH] warna
                    )
                    // [DIUBAH] teks persetujuan bertema perusahaan
                    Text("Saya menyetujui kebijakan perusahaan", fontSize = 13.sp, color = TextDark)
                }
                if (errorPersetujuan) {
                    Text("Persetujuan wajib dicentang", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                }

                // =========================================================================
                // 4. EVENT SUBMIT FORMULIR
                // =========================================================================
                Button(
                    onClick = { // [EVENT] tombol diklik
                        sudahSubmit = true     // aktifkan tampilan error
                        berhasil = formValid   // simpan hasil validasi
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = SageDark) // [DIUBAH] warna tombol
                ) {
                    Text("Aktifkan Akun", modifier = Modifier.padding(vertical = 3.dp)) // [DIUBAH] teks tombol
                }

                // Pesan sukses jika validasi berhasil
                if (berhasil) {
                    HorizontalDivider(color = SageLight)
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = SageDark)
                        Column {
                            Text("Validasi berhasil", color = SageDark, fontWeight = FontWeight.Bold)
                            // [DIUBAH] menampilkan nama + ID karyawan
                            Text(
                                "Selamat bergabung, ${nama.trim()} (${idKaryawan.trim()})! Data siap diproses oleh HR.",
                                color = TextMuted,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }
        Text(
            "Petunjuk: isi semua kolom, ID format EMP-1234, email berakhiran $DOMAIN_KANTOR, kata sandi minimal 8 karakter (huruf + angka), lalu centang persetujuan.",
            fontSize = 12.sp,
            color = TextMuted
        )
    }
}

/**
 * Preview [RegistrationScreen] di Android Studio.
 */
@Preview(showBackground = true)
@Composable
fun RegistrationScreenPreview() {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = SageDark,
            onPrimary = Color.White,
            secondary = SoftPink,
            background = PinkBackground,
            surface = Color.White
        )
    ) {
        RegistrationScreen()
    }
}