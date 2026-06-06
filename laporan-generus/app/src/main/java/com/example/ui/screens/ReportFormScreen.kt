package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MonthlyReport
import com.example.data.model.SubClassData
import com.example.ui.components.ScrollToButtons
import java.text.DateFormatSymbols

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportFormScreen(
    report: MonthlyReport,
    isEditing: Boolean,
    onBack: () -> Unit,
    onSave: () -> Unit,
    onUpdateMusyawarah: (Boolean) -> Unit,
    onUpdateSubClass: (categoryName: String, subclassName: String, (SubClassData) -> SubClassData) -> Unit,
    modifier: Modifier = Modifier
) {
    val monthName = DateFormatSymbols().months[report.month -  1]
    var selectedCategoryIndex by remember { mutableStateOf(0) }
    val categories = report.categories
    val scrollState = rememberScrollState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditing) "Edit Laporan $monthName ${report.year}" else "Input Baru Laporan $monthName ${report.year}",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("form_back_btn")) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Batal")
                    }
                },
                actions = {
                    Button(
                        onClick = onSave,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .testTag("form_save_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Done, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Simpan", fontWeight = FontWeight.Bold)
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
            // General indicators in draft Form (Musyawarah 5 unsur checklist)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = report.musyawarah5Unsur,
                        onCheckedChange = { onUpdateMusyawarah(it) },
                        modifier = Modifier.testTag("musyawarah_checkbox")
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Pelaksanaan Musyawarah 5 Unsur",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Beri ceklis jika musyawarah rutin 5 unsur (KPP, Guru, Pengurus, Walimurid, MM) terlaksana bulan ini.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Tabs to select which Main Category we are inputting: Caberawit, Pra-Remaja, Remaja, Muda Mudi
            TabRow(selectedTabIndex = selectedCategoryIndex) {
                categories.forEachIndexed { idx, category ->
                    Tab(
                        selected = selectedCategoryIndex == idx,
                        onClick = { selectedCategoryIndex = idx },
                        text = {
                            Text(
                                text = category.categoryName,
                                fontSize = 12.sp,
                                fontWeight = if (selectedCategoryIndex == idx) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            // The main form contents
            if (categories.isNotEmpty() && selectedCategoryIndex in categories.indices) {
                val currentCategory = categories[selectedCategoryIndex]

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Form Input Kelas ${currentCategory.categoryName}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        currentCategory.subClasses.forEach { subClass ->
                            SubClassFormItem(
                                categoryName = currentCategory.categoryName,
                                subClass = subClass,
                                onUpdate = { updater -> onUpdateSubClass(currentCategory.categoryName, subClass.name, updater) }
                            )
                        }

                        Spacer(modifier = Modifier.height(48.dp))
                    }

                    ScrollToButtons(
                        scrollState = scrollState,
                        modifier = Modifier.align(Alignment.BottomEnd)
                    )
                }
            }
        }
    }
}

@Composable
fun SubClassFormItem(
    categoryName: String,
    subClass: SubClassData,
    onUpdate: ((SubClassData) -> SubClassData) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("form_subclass_card_${subClass.name.replace(" ", "_")}"),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isExpanded) 4.dp else 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isExpanded) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header click area to expand/collapse
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(6.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = subClass.name.take(1).uppercase(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = subClass.name,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                TextButton(
                    onClick = { isExpanded = !isExpanded },
                    modifier = Modifier.testTag("expand_subclass_${subClass.name.replace(" ", "_")}")
                ) {
                    Text(if (isExpanded) "Tutup" else "Isi Form")
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null
                    )
                }
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Divider()

                    // 1. JUMLAH SANTRI
                    Text(
                        text = "1. Jumlah Santri",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Pria Santri
                        OutlinedTextField(
                            value = if (subClass.santriPria == 0) "" else subClass.santriPria.toString(),
                            onValueChange = { input ->
                                val processed = input.filter { it.isDigit() }.toIntOrNull() ?: 0
                                onUpdate { it.copy(santriPria = processed) }
                            },
                            label = { Text("Pria (Wan)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("pria_input_${subClass.name.replace(" ", "_")}"),
                            placeholder = { Text("0") }
                        )

                        // Wanita Santri
                        OutlinedTextField(
                            value = if (subClass.santriWanita == 0) "" else subClass.santriWanita.toString(),
                            onValueChange = { input ->
                                val processed = input.filter { it.isDigit() }.toIntOrNull() ?: 0
                                onUpdate { it.copy(santriWanita = processed) }
                            },
                            label = { Text("Wanita (Wati)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("wanita_input_${subClass.name.replace(" ", "_")}"),
                            placeholder = { Text("0") }
                        )
                    }

                    // 2. TIMING / PERSENTASE (Kehadiran & Target)
                    Text(
                        text = "2. Persentase KBM & Pencapaian Target",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    // Kehadiran
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Persentase Kehadiran Santri",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${subClass.persentaseKehadiran.toInt()}%",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Slider(
                            value = subClass.persentaseKehadiran,
                            onValueChange = { onUpdate { s -> s.copy(persentaseKehadiran = it) } },
                            valueRange = 0f..100f,
                            steps = 19,
                            modifier = Modifier.testTag("attendance_slider_${subClass.name.replace(" ", "_")}")
                        )
                    }

                    // Pencapaian Target
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Persentase Pencapaian Target KBM",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${subClass.persentasePencapaian.toInt()}%",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                        }
                        Slider(
                            value = subClass.persentasePencapaian,
                            onValueChange = { onUpdate { s -> s.copy(persentasePencapaian = it) } },
                            valueRange = 0f..100f,
                            steps = 19,
                            modifier = Modifier.testTag("achievement_slider_${subClass.name.replace(" ", "_")}")
                        )
                    }

                    // 3. SARANA PRASARANA
                    Text(
                        text = "3. Ketersediaan Sarana Prasarana (Sarpras)",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    // Responsive checkpoints layout grid
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        SarprasCheckboxItem(
                            label = "Buku Jurnal",
                            checked = subClass.hasBukuJurnal,
                            onCheckedChange = { onUpdate { s -> s.copy(hasBukuJurnal = it) } }
                        )
                        SarprasCheckboxItem(
                            label = "Buku Prestasi",
                            checked = subClass.hasBukuPrestasi,
                            onCheckedChange = { onUpdate { s -> s.copy(hasBukuPrestasi = it) } }
                        )
                        SarprasCheckboxItem(
                            label = "Buku Panduan KBM",
                            checked = subClass.hasBukuPanduanKbm,
                            onCheckedChange = { onUpdate { s -> s.copy(hasBukuPanduanKbm = it) } }
                        )
                        SarprasCheckboxItem(
                            label = "Buku Absensi",
                            checked = subClass.hasBukuAbsensi,
                            onCheckedChange = { onUpdate { s -> s.copy(hasBukuAbsensi = it) } }
                        )
                        SarprasCheckboxItem(
                            label = "Perlengkapan Alat Tulis",
                            checked = subClass.hasAlatTulis,
                            onCheckedChange = { onUpdate { s -> s.copy(hasAlatTulis = it) } }
                        )
                        SarprasCheckboxItem(
                            label = "Lemari",
                            checked = subClass.hasLemari,
                            onCheckedChange = { onUpdate { s -> s.copy(hasLemari = it) } }
                        )
                        SarprasCheckboxItem(
                            label = "Kursi Alas Baca",
                            checked = subClass.hasKursiAlasBaca,
                            onCheckedChange = { onUpdate { s -> s.copy(hasKursiAlasBaca = it) } }
                        )
                        SarprasCheckboxItem(
                            label = "Alat Lainnya",
                            checked = subClass.hasAlatLainnya,
                            onCheckedChange = { onUpdate { s -> s.copy(hasAlatLainnya = it) } }
                        )
                    }

                    // 4. MATERI, EKSTRA, GIZI, KENDALA
                    Text(
                        text = "4. Detail Kegiatan Belajar Mengajar",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    // Update Materi (Multiple)
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Update Laporan Materi Pembelajaran (Bisa Lebih Dari Satu)",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        val currentMateriList = when {
                            subClass.materiUpdateList.isNotEmpty() -> subClass.materiUpdateList
                            subClass.materiUpdate.isNotEmpty() -> listOf(subClass.materiUpdate)
                            else -> listOf("")
                        }

                        currentMateriList.forEachIndexed { index, materi ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = materi,
                                    onValueChange = { newVal ->
                                        val updated = currentMateriList.toMutableList()
                                        updated[index] = newVal
                                        onUpdate { s ->
                                            s.copy(
                                                materiUpdate = updated.firstOrNull() ?: "",
                                                materiUpdateList = updated
                                            )
                                        }
                                    },
                                    label = { Text("Materi Ke-${index + 1}") },
                                    modifier = Modifier.weight(1f),
                                    placeholder = { Text("Contoh: Hafalan Juz Amma, Bab Thoharoh") }
                                )

                                // Show delete button if there's more than 1 item, or if the single item is not empty
                                if (currentMateriList.size > 1 || materi.isNotEmpty()) {
                                    IconButton(
                                        onClick = {
                                            val updated = currentMateriList.toMutableList()
                                            if (updated.size > 1) {
                                                updated.removeAt(index)
                                            } else {
                                                updated[0] = ""
                                            }
                                            onUpdate { s ->
                                                s.copy(
                                                    materiUpdate = updated.firstOrNull() ?: "",
                                                    materiUpdateList = updated
                                                )
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Hapus Materi",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }
                        }

                        // Button to add more material
                        Button(
                            onClick = {
                                val updated = currentMateriList.toMutableList() + ""
                                onUpdate { s ->
                                    s.copy(materiUpdateList = updated)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                            ),
                            modifier = Modifier.align(Alignment.Start)
                        ) {
                            Text("+ Tambah Laporan Materi", fontWeight = FontWeight.Bold)
                        }
                    }

                    // Ekstrakurikuler
                    OutlinedTextField(
                        value = subClass.ekstrakurikuler,
                        onValueChange = { onUpdate { s -> s.copy(ekstrakurikuler = it) } },
                        label = { Text("Informasi Kegiatan Ekstrakurikuler") },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Contoh: Futsal generasi muda, Latihan memanah") }
                    )

                    // Pelaksanaan Gizi
                    OutlinedTextField(
                        value = subClass.pelaksanaanGizi,
                        onValueChange = { onUpdate { s -> s.copy(pelaksanaanGizi = it) } },
                        label = { Text("Pelaksanaan Program Gizi / PMT") },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Contoh: Pemberian susu & telur rebus di minggu ke-2") }
                    )

                    // Kendala selama KBM
                    OutlinedTextField(
                        value = subClass.kendalaKbm,
                        onValueChange = { onUpdate { s -> s.copy(kendalaKbm = it) } },
                        label = { Text("Kendala Selama KBM Berlangsung") },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Contoh: Beberapa anak sering datang terlambat karena hujan") }
                    )
                }
            }
        }
    }
}

@Composable
fun SarprasCheckboxItem(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Surface(
        onClick = { onCheckedChange(!checked) },
        shape = RoundedCornerShape(8.dp),
        color = if (checked) MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                fontSize = 13.sp,
                color = if (checked) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
