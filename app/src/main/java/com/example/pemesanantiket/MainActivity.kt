package com.example.pemesanantiket

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.util.Locale

private val BiruUtama = Color(0xFF1E88FF)
private val HijauTotal = Color(0xFF1B7F3B)
private val MerahReset = Color(0xFFE53E3E)
private val AbuBackground = Color(0xFFF5F7FA)
private val TeksGelap = Color(0xFF1F2937)
private val TeksAbu = Color(0xFF6B7280)

enum class StatusPesanan {
    NAMA_KOSONG,   // Status : Nama Masih Kosong
    SIAP,          // Nama sudah diisi, siap dipesan
    MEMPROSES,     // Status : Memproses pesanan
    DIPESAN        // Status : Tiket telah dipesan
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                TicketScreen()
            }
        }
    }
}

fun formatRupiah(nilai: Int): String =
    String.format(Locale.forLanguageTag("id-ID"), "Rp%,d", nilai)

@Composable
fun TicketScreen() {
    var hargaTiket by remember { mutableStateOf(25000) }      // 1. Harga Tiket
    var jumlahTiket by remember { mutableStateOf(1) }         // 2. Jumlah Tiket
    var namaPembeli by remember { mutableStateOf("") }        // 3. Nama Pembeli Tiket
    var status by remember { mutableStateOf(StatusPesanan.NAMA_KOSONG) }

    val totalBayar = hargaTiket * jumlahTiket
    val sedangProses = status == StatusPesanan.MEMPROSES

    LaunchedEffect(namaPembeli) {
        if (status != StatusPesanan.MEMPROSES) {
            status = if (namaPembeli.isBlank()) StatusPesanan.NAMA_KOSONG
            else StatusPesanan.SIAP
        }
    }

    LaunchedEffect(status) {
        if (status == StatusPesanan.MEMPROSES) {
            delay(5000)
            status = StatusPesanan.DIPESAN
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AbuBackground)
            .verticalScroll(rememberScrollState())
    ) {
        HeaderTiket()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            KartuHarga(harga = hargaTiket)

            KartuNama(
                nama = namaPembeli,
                enabled = !sedangProses,
                onNamaChange = { namaPembeli = it }
            )

            KartuJumlah(
                jumlah = jumlahTiket,
                enabled = !sedangProses,
                onKurang = { if (jumlahTiket > 1) jumlahTiket-- },
                onTambah = { jumlahTiket++ }
            )

            KartuTotal(total = totalBayar)

            KartuStatus(status = status, nama = namaPembeli)

            Button(
                onClick = {
                    status = if (namaPembeli.isBlank()) StatusPesanan.NAMA_KOSONG
                    else StatusPesanan.MEMPROSES
                },
                enabled = !sedangProses,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BiruUtama,
                    disabledContainerColor = Color(0xFFB0B8C4)
                )
            ) {
                Text(
                    text = "Pesan Tiket",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Button(
                onClick = {
                    jumlahTiket = 1
                    namaPembeli = ""
                    status = StatusPesanan.NAMA_KOSONG
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MerahReset)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Reset",
                    tint = Color.White
                )
                Text(
                    text = "  RESET",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun HeaderTiket() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(BiruUtama)
            .statusBarsPadding()
            .padding(vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "\uD83C\uDFAB", fontSize = 44.sp)
        Text(
            text = "Pemesanan Tiket",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Pesan tiket dengan mudah!",
            color = Color.White.copy(alpha = 0.85f),
            fontSize = 14.sp
        )
    }
}

@Composable
fun KartuHarga(harga: Int) {
    KartuPutih {
        Text("Harga Tiket", fontWeight = FontWeight.SemiBold, color = TeksGelap)
        Text(
            text = formatRupiah(harga),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = BiruUtama
        )
        Text("per tiket", fontSize = 13.sp, color = TeksAbu)
    }
}

@Composable
fun KartuNama(nama: String, enabled: Boolean, onNamaChange: (String) -> Unit) {
    KartuPutih {
        Text("Nama Pembeli", fontWeight = FontWeight.SemiBold, color = TeksGelap)
        OutlinedTextField(
            value = nama,
            onValueChange = onNamaChange,
            enabled = enabled,
            singleLine = true,
            placeholder = { Text("Masukkan nama Anda") },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )
    }
}

@Composable
fun KartuJumlah(
    jumlah: Int,
    enabled: Boolean,
    onKurang: () -> Unit,
    onTambah: () -> Unit
) {
    KartuPutih {
        Text("Jumlah Tiket", fontWeight = FontWeight.SemiBold, color = TeksGelap)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TombolBulat(
                label = "\u2212",
                enabled = enabled && jumlah > 1,
                onClick = onKurang
            )

            Box(
                modifier = Modifier
                    .width(96.dp)
                    .height(56.dp)
                    .background(Color(0xFFEFF1F5), RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$jumlah",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = TeksGelap
                )
            }

            TombolBulat(
                label = "+",
                enabled = enabled,
                onClick = onTambah
            )
        }
    }
}

@Composable
fun KartuTotal(total: Int) {
    KartuPutih {
        Text("Total", fontWeight = FontWeight.SemiBold, color = TeksGelap)
        Text(
            text = formatRupiah(total),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = HijauTotal
        )
    }
}

@Composable
fun KartuStatus(status: StatusPesanan, nama: String) {
    val teks: String
    val warnaTeks: Color
    val warnaLatar: Color

    when (status) {
        StatusPesanan.NAMA_KOSONG -> {
            teks = "Status : Nama Masih Kosong"
            warnaTeks = Color(0xFFC62828)
            warnaLatar = Color(0xFFFFEBEE)
        }
        StatusPesanan.SIAP -> {
            teks = "Status : Silakan pesan tiket"
            warnaTeks = TeksAbu
            warnaLatar = Color(0xFFEFF1F5)
        }
        StatusPesanan.MEMPROSES -> {
            teks = "Status : Memproses pesanan........."
            warnaTeks = Color(0xFF1565C0)
            warnaLatar = Color(0xFFE3F2FD)
        }
        StatusPesanan.DIPESAN -> {
            teks = "Status : Tiket telah dipesan atas nama $nama"
            warnaTeks = HijauTotal
            warnaLatar = Color(0xFFE8F5E9)
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(warnaLatar, RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (status == StatusPesanan.MEMPROSES) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp,
                color = warnaTeks
            )
            Box(modifier = Modifier.width(10.dp))
        }
        Text(
            text = teks,
            color = warnaTeks,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp
        )
    }
}

@Composable
fun KartuPutih(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            content()
        }
    }
}

@Composable
fun TombolBulat(label: String, enabled: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = CircleShape,
        modifier = Modifier.size(56.dp),
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = BiruUtama,
            disabledContainerColor = BiruUtama.copy(alpha = 0.4f)
        )
    ) {
        Text(text = label, fontSize = 28.sp, color = Color.White, fontWeight = FontWeight.Bold)
    }
}