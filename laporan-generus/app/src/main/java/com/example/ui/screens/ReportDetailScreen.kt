package com.example.ui.screens

import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CategoryData
import com.example.data.model.MonthlyReport
import com.example.data.model.SubClassData
import com.example.ui.components.ScrollToButtons
import com.example.ui.components.AttendanceTargetChart
import com.example.ui.components.SantriRatioChart
import com.example.ui.components.SarpasCompletenessChart
import java.text.DateFormatSymbols

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportDetailScreen(
    report: MonthlyReport,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val monthName = DateFormatSymbols().months[report.month - 1]
    var selectedTabIndex by remember { mutableStateOf(0) }
    val categories = report.categories

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Laporan $monthName ${report.year}",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("detail_back_btn")) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            val shareText = generateShareText(report)
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, shareText)
                                type = "text/plain"
                            }
                            val shareIntent = Intent.createChooser(sendIntent, "Bagikan Laporan")
                            context.startActivity(shareIntent)
                        },
                        modifier = Modifier.testTag("detail_top_share_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = "Bagikan Laporan")
                    }
                    IconButton(onClick = onEdit, modifier = Modifier.testTag("detail_edit_btn")) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Laporan")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // General indicators panel
            GlobalStatusPanel(report)

            // Prominent Share to WhatsApp Card (Elderly Friendly)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Kirim Laporan ke Grup WhatsApp",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Kirim rangkuman laporan yang rapi ke grup WA. Orang tua bisa langsung membaca tanpa perlu mengunduh/edit laporan.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.9f)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = {
                            val shareText = generateShareText(report)
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, shareText)
                                type = "text/plain"
                            }
                            val shareIntent = Intent.createChooser(sendIntent, "Bagikan Laporan")
                            context.startActivity(shareIntent)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary,
                            contentColor = MaterialTheme.colorScheme.onSecondary
                        ),
                        modifier = Modifier.testTag("detail_banner_share_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Bagikan",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Kirim", fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Category selector tabs
            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                edgePadding = 16.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                categories.forEachIndexed { index, category ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = category.categoryName,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            // Display components depending on selected category
            if (categories.isNotEmpty() && selectedTabIndex in categories.indices) {
                val currentCategory = categories[selectedTabIndex]
                CategoryDetailContent(category = currentCategory)
            }
        }
    }
}

@Composable
fun GlobalStatusPanel(report: MonthlyReport) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = if (report.musyawarah5Unsur) Color(0xFF0D9488) else Color(0xFFD97706),
                        shape = RoundedCornerShape(10.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (report.musyawarah5Unsur) Icons.Default.CheckCircle else Icons.Default.Warning,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Musyawarah 5 Unsur Desa/Kelompok",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = if (report.musyawarah5Unsur) "TERLAKSANA DENGAN BAIK" else "TIDAK TERLAKSANA",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (report.musyawarah5Unsur) Color(0xFF0D9488) else Color(0xFFD97706)
                )
            }
        }
    }
}

@Composable
fun CategoryDetailContent(category: CategoryData) {
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- CHARTS SECTION ---
            Text(
                text = "Visualisasi Data ${category.categoryName}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            // 1. Attendance & target progress chart
            AttendanceTargetChart(
                subClasses = category.subClasses,
                modifier = Modifier.fillMaxWidth()
            )

            // 2. Male vs Female students stacked ratio chart
            SantriRatioChart(
                subClasses = category.subClasses,
                modifier = Modifier.fillMaxWidth()
            )

            // 3. Sarpas completeness progress counters
            SarpasCompletenessChart(
                subClasses = category.subClasses,
                modifier = Modifier.fillMaxWidth()
            )

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // --- SUBCLASS DETAILED LIST ---
            Text(
                text = "Rincian Data Setiap Kelas",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            category.subClasses.forEach { subClass ->
                SubClassDetailCard(subClass)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        ScrollToButtons(
            scrollState = scrollState,
            modifier = Modifier.align(Alignment.BottomEnd)
        )
    }
}

@Composable
fun SubClassDetailCard(subClass: SubClassData) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header Name
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = subClass.name.take(2).uppercase(),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = subClass.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Student Count Summary
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Pria Count Card
                Surface(
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = subClass.santriPria.toString(),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF007A87)
                        )
                        Text(
                            text = "Pria (Wan)",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Wanita Count Card
                Surface(
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = subClass.santriWanita.toString(),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFD66853)
                        )
                        Text(
                            text = "Wanita (Wati)",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Real percentages info
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                // Kehadiran Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Persentase Kehadiran:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${subClass.persentaseKehadiran}%",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // Pencapaian target Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Pencapaian Target:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${subClass.persentasePencapaian}%",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
            }

            // Sarpas checklists panel
            Column {
                Text(
                    text = "Ketersediaan Sarana Prasarana:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                val sarpasItems = listOf(
                    "Buku Jurnal" to subClass.hasBukuJurnal,
                    "Buku Prestasi" to subClass.hasBukuPrestasi,
                    "Buku Panduan" to subClass.hasBukuPanduanKbm,
                    "Buku Absensi" to subClass.hasBukuAbsensi,
                    "Alat Tulis" to subClass.hasAlatTulis,
                    "Lemari" to subClass.hasLemari,
                    "Kursi/Alas Baca" to subClass.hasKursiAlasBaca,
                    "Alat Lainnya" to subClass.hasAlatLainnya
                )

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    mainAxisSpacing = 8.dp,
                    crossAxisSpacing = 6.dp
                ) {
                    sarpasItems.forEach { (label, checked) ->
                        Surface(
                            color = if (checked) Color(0xFFE2F6F0) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (checked) Icons.Default.CheckCircle else Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = if (checked) Color(0xFF0D9488) else MaterialTheme.colorScheme.outline,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = label,
                                    fontSize = 11.sp,
                                    color = if (checked) Color(0xFF115E59) else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = if (checked) FontWeight.Medium else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }

            // Textual inputs Display
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Update Materi (multiple)
                val formattedMateri = when {
                    subClass.materiUpdateList.isNotEmpty() -> {
                        subClass.materiUpdateList.filter { it.isNotBlank() }.mapIndexed { idx, item ->
                            "• $item"
                        }.joinToString("\n")
                    }
                    subClass.materiUpdate.isNotBlank() -> {
                        "• ${subClass.materiUpdate}"
                    }
                    else -> ""
                }

                TextDetailItem(
                    title = "Materi yang Diajarkan:",
                    value = formattedMateri.ifEmpty { "Belum diisi/tidak ada materi baru" },
                    backgroundColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f),
                    indicatorColor = MaterialTheme.colorScheme.primary
                )

                // Ekstrakurikuler
                TextDetailItem(
                    title = "Tambahan Ekstrakurikuler:",
                    value = subClass.ekstrakurikuler.ifEmpty { "Tidak ada kegiatan ekstrakurikuler" },
                    backgroundColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.15f),
                    indicatorColor = MaterialTheme.colorScheme.secondary
                )

                // Kegiatan Gizi Caberawit / Kelas
                TextDetailItem(
                    title = "Pelaksanaan PMT/Gizi:",
                    value = subClass.pelaksanaanGizi.ifEmpty { "Tidak ada pelaksanaan pemberian gizi/makanan tambahan" },
                    backgroundColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.15f),
                    indicatorColor = MaterialTheme.colorScheme.tertiary
                )

                // Kendala Selama KBM
                TextDetailItem(
                    title = "Kendala Selama KBM:",
                    value = subClass.kendalaKbm.ifEmpty { "Alhamdulillah, Kegiatan Belajar Mengajar berlangsung lancar tanpa kendala" },
                    backgroundColor = if (subClass.kendalaKbm.isEmpty()) Color(0xFFF0FDF4) else Color(0xFFFEF2F2),
                    indicatorColor = if (subClass.kendalaKbm.isEmpty()) Color(0xFF16A34A) else Color(0xFFDC2626)
                )
            }
        }
    }
}

@Composable
fun TextDetailItem(
    title: String,
    value: String,
    backgroundColor: Color,
    indicatorColor: Color
) {
    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(35.dp)
                    .background(indicatorColor, shape = RoundedCornerShape(2.dp))
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

// Simple legacy FlowRow mockup for compose
@Composable
fun FlowRow(
    modifier: Modifier = Modifier,
    mainAxisSpacing: androidx.compose.ui.unit.Dp = 0.dp,
    crossAxisSpacing: androidx.compose.ui.unit.Dp = 0.dp,
    content: @Composable () -> Unit
) {
    androidx.compose.ui.layout.Layout(
        content = content,
        modifier = modifier
    ) { measurables, constraints ->
        val placeables = measurables.map { measurable ->
            measurable.measure(constraints.copy(minWidth = 0, minHeight = 0))
        }

        val mainSpacingPx = mainAxisSpacing.roundToPx()
        val crossSpacingPx = crossAxisSpacing.roundToPx()

        val rows = mutableListOf<List<androidx.compose.ui.layout.Placeable>>()
        var currentRow = mutableListOf<androidx.compose.ui.layout.Placeable>()
        var rowWidth = 0

        placeables.forEach { placeable ->
            if (rowWidth + placeable.width + mainSpacingPx > constraints.maxWidth && currentRow.isNotEmpty()) {
                rows.add(currentRow)
                currentRow = mutableListOf()
                rowWidth = 0
            }
            currentRow.add(placeable)
            rowWidth += placeable.width + mainSpacingPx
        }
        if (currentRow.isNotEmpty()) {
            rows.add(currentRow)
        }

        var totalHeight = 0
        rows.forEachIndexed { i, row ->
            val maxHeight = row.maxOf { it.height }
            totalHeight += maxHeight
            if (i < rows.size - 1) {
                totalHeight += crossSpacingPx
            }
        }

        layout(constraints.maxWidth, totalHeight) {
            var currentY = 0
            rows.forEach { row ->
                val maxHeight = row.maxOf { it.height }
                var currentX = 0
                row.forEach { placeable ->
                    placeable.placeRelative(currentX, currentY + (maxHeight - placeable.height) / 2)
                    currentX += placeable.width + mainSpacingPx
                }
                currentY += maxHeight + crossSpacingPx
            }
        }
    }
}

fun generateShareText(report: MonthlyReport): String {
    val monthName = DateFormatSymbols().months[report.month - 1]
    val sb = java.lang.StringBuilder()
    sb.append("📝 *LAPORAN BULANAN KBM*\n")
    sb.append("📅 *Bulan:* $monthName ${report.year}\n")
    sb.append("👥 *Musyawarah 5 Unsur:* ${if (report.musyawarah5Unsur) "TERLAKSANA DENGAN BAIK" else "BELUM TERLAKSANA"}\n\n")

    report.categories.forEach { category ->
        sb.append("━━━━━━━━━━━━━━━━━━━━━\n")
        sb.append("🟢 *Kategori: ${category.categoryName.uppercase()}*\n")
        sb.append("━━━━━━━━━━━━━━━━━━━━━\n\n")

        category.subClasses.forEach { subClass ->
            sb.append("📍 *Kelas:* ${subClass.name}\n")
            sb.append("• 👦 Santri Wan (Pria): ${subClass.santriPria} anak\n")
            sb.append("• 👧 Santri Wati (Wanita): ${subClass.santriWanita} anak\n")
            sb.append("• 📈 Kehadiran KBM: ${subClass.persentaseKehadiran}%\n")
            sb.append("• 🎯 Pencapaian Target: ${subClass.persentasePencapaian}%\n")

            // Format Multiple KBM Materi
            val materials = if (subClass.materiUpdateList.isNotEmpty()) {
                subClass.materiUpdateList.filter { it.isNotBlank() }
            } else if (subClass.materiUpdate.isNotBlank()) {
                listOf(subClass.materiUpdate)
            } else {
                emptyList()
            }

            val materiText = if (materials.isNotEmpty()) {
                materials.map { materi -> "  - $materi" }.joinToString("\n")
            } else {
                "  - Belum diisi / tidak ada materi baru"
            }
            sb.append("• 📚 *Materi Pembelajaran:*\n$materiText\n")

            // Sarana Prasarana
            val sarpasItems = mutableListOf<String>()
            if (subClass.hasBukuJurnal) sarpasItems.add("Buku Jurnal")
            if (subClass.hasBukuPrestasi) sarpasItems.add("Buku Prestasi")
            if (subClass.hasBukuPanduanKbm) sarpasItems.add("Buku Panduan")
            if (subClass.hasBukuAbsensi) sarpasItems.add("Buku Absensi")
            if (subClass.hasAlatTulis) sarpasItems.add("Alat Tulis")
            if (subClass.hasLemari) sarpasItems.add("Lemari")
            if (subClass.hasKursiAlasBaca) sarpasItems.add("Kursi/Alas Baca")
            if (subClass.hasAlatLainnya) sarpasItems.add("Alat Lainnya")

            val sarpasText = if (sarpasItems.isNotEmpty()) {
                sarpasItems.joinToString(", ")
            } else {
                "Belum lengkap/tersedia"
            }
            sb.append("• 📦 *Sarpras Tersedia:* $sarpasText\n")

            if (subClass.ekstrakurikuler.isNotBlank()) {
                sb.append("• 🏆 *Ekstrakurikuler:* ${subClass.ekstrakurikuler}\n")
            }
            if (subClass.pelaksanaanGizi.isNotBlank()) {
                sb.append("• 🥛 *PMT / Gizi Tambahan:* ${subClass.pelaksanaanGizi}\n")
            }
            if (subClass.kendalaKbm.isNotBlank()) {
                sb.append("• ⚠️ *Kendala KBM:* ${subClass.kendalaKbm}\n")
            } else {
                sb.append("• ✅ *Kendala KBM:* Lancar, aman, dan tertib\n")
            }
            sb.append("\n")
        }
    }

    sb.append("-----------------------------------------\n")
    sb.append("Laporan ini diunggah secara resmi oleh Penanggung Jawab KBM lewat sistem aplikasi digital.")
    return sb.toString()
}
