package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SubClassData

@Composable
fun AttendanceTargetChart(
    subClasses: List<SubClassData>,
    modifier: Modifier = Modifier
) {
    val textPaintColor = MaterialTheme.colorScheme.onSurface.toArgb()
    val attendanceColor = MaterialTheme.colorScheme.primary
    val targetColor = MaterialTheme.colorScheme.tertiary
    val secondaryGridColor = MaterialTheme.colorScheme.outlineVariant

    var triggerAnimation by remember { mutableStateOf(false) }
    val animScale by animateFloatAsState(
        targetValue = if (triggerAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 800),
        label = "chartScale"
    )

    LaunchedEffect(subClasses) {
        triggerAnimation = true
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {
        Text(
            text = "Grafik Kehadiran vs Target Pencapaian (%)",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Legend
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(attendanceColor, shape = RoundedCornerShape(2.dp))
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Kehadiran %", style = MaterialTheme.typography.labelMedium)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(targetColor, shape = RoundedCornerShape(2.dp))
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Pencapaian %", style = MaterialTheme.typography.labelMedium)
            }
        }

        if (subClasses.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Tidak ada data kelas", style = MaterialTheme.typography.bodyMedium)
            }
        } else {
            val density = LocalDensity.current
            val textPaint = remember(textPaintColor) {
                android.graphics.Paint().apply {
                    color = textPaintColor
                    textSize = with(density) { 10.sp.toPx() }
                    textAlign = android.graphics.Paint.Align.CENTER
                    isAntiAlias = true
                }
            }

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            ) {
                val canvasWidth = size.width
                val canvasHeight = size.height

                val leftMargin = 80f
                val bottomMargin = 60f
                val topMargin = 20f
                val rightMargin = 20f

                val chartWidth = canvasWidth - leftMargin - rightMargin
                val chartHeight = canvasHeight - bottomMargin - topMargin

                // Draw horizontal grid lines (0%, 25%, 50%, 75%, 100%)
                val steps = 4
                for (i in 0..steps) {
                    val ratio = i.toFloat() / steps.toFloat()
                    val y = chartHeight + topMargin - (ratio * chartHeight)
                    val percentVal = (ratio * 100).toInt()

                    drawLine(
                        color = secondaryGridColor,
                        start = Offset(leftMargin, y),
                        end = Offset(canvasWidth - rightMargin, y),
                        strokeWidth = 1f
                    )

                    drawIntoCanvas { canvas ->
                        canvas.nativeCanvas.drawText(
                            "$percentVal%",
                            leftMargin - 20f,
                            y + 10f,
                            textPaint.apply { textAlign = android.graphics.Paint.Align.RIGHT }
                        )
                    }
                }

                // Draw Grouped Bars
                val numGroups = subClasses.size
                val groupWidth = chartWidth / numGroups
                val barWidth = groupWidth * 0.3f
                val spacingVal = groupWidth * 0.1f

                subClasses.forEachIndexed { index, subClass ->
                    val groupCenterX = leftMargin + (index * groupWidth) + (groupWidth / 2f)

                    // Left Bar (Attendance)
                    val attendHeight = (subClass.persentaseKehadiran / 100f) * chartHeight * animScale
                    val attendTop = chartHeight + topMargin - attendHeight
                    val attendLeft = groupCenterX - barWidth - (spacingVal / 2f)

                    drawRoundRect(
                        color = attendanceColor,
                        topLeft = Offset(attendLeft, attendTop),
                        size = Size(barWidth, attendHeight),
                        cornerRadius = CornerRadius(8f, 8f)
                    )

                    // Right Bar (Target Progress)
                    val targetHeight = (subClass.persentasePencapaian / 100f) * chartHeight * animScale
                    val targetTop = chartHeight + topMargin - targetHeight
                    val targetLeft = groupCenterX + (spacingVal / 2f)

                    drawRoundRect(
                        color = targetColor,
                        topLeft = Offset(targetLeft, targetTop),
                        size = Size(barWidth, targetHeight),
                        cornerRadius = CornerRadius(8f, 8f)
                    )

                    // Draw Class name underneath
                    drawIntoCanvas { canvas ->
                        canvas.nativeCanvas.drawText(
                            subClass.name,
                            groupCenterX,
                            canvasHeight - 15f,
                            textPaint.apply { textAlign = android.graphics.Paint.Align.CENTER }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SantriRatioChart(
    subClasses: List<SubClassData>,
    modifier: Modifier = Modifier
) {
    val totalPria = subClasses.sumOf { it.santriPria }
    val totalWanita = subClasses.sumOf { it.santriWanita }
    val grandTotal = totalPria + totalWanita

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {
        Text(
            text = "Grafik Distribusi Santri (Jumlah Pria vs Wanita)",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Total Santri: $grandTotal anak", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (grandTotal == 0) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Data santri kosong", style = MaterialTheme.typography.bodyMedium)
            }
        } else {
            val ratioPria = totalPria.toFloat() / grandTotal.toFloat()
            val ratioWanita = totalWanita.toFloat() / grandTotal.toFloat()

            // Visual stacked line/bar indicator
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(28.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(14.dp)
                    )
            ) {
                if (totalPria > 0) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(ratioPria)
                            .background(
                                color = Color(0xFF007A87), // Custom Deep Teal
                                shape = if (totalWanita == 0) RoundedCornerShape(14.dp) else RoundedCornerShape(
                                    topStart = 14.dp,
                                    bottomStart = 14.dp
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "L: ${totalPria} (${(ratioPria * 100).toInt()}%)",
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                if (totalWanita > 0) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(ratioWanita)
                            .background(
                                color = Color(0xFFD66853), // Custom Soft Coral
                                shape = if (totalPria == 0) RoundedCornerShape(14.dp) else RoundedCornerShape(
                                    topEnd = 14.dp,
                                    bottomEnd = 14.dp
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "P: ${totalWanita} (${(ratioWanita * 100).toInt()}%)",
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Subcategories details as a stacked chart breakdown
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(12.dp).background(Color(0xFF007A87), shape = RoundedCornerShape(4.dp)))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Santri Pria (Wan): $totalPria anak", style = MaterialTheme.typography.labelMedium)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(12.dp).background(Color(0xFFD66853), shape = RoundedCornerShape(4.dp)))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Santri Wanita (Wati): $totalWanita anak", style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

@Composable
fun SarpasCompletenessChart(
    subClasses: List<SubClassData>,
    modifier: Modifier = Modifier
) {
    if (subClasses.isEmpty()) return

    val totalSubclasses = subClasses.size
    val numJurnal = subClasses.count { it.hasBukuJurnal }
    val numPrestasi = subClasses.count { it.hasBukuPrestasi }
    val numPanduan = subClasses.count { it.hasBukuPanduanKbm }
    val numAbsensi = subClasses.count { it.hasBukuAbsensi }
    val numTulis = subClasses.count { it.hasAlatTulis }
    val numLemari = subClasses.count { it.hasLemari }
    val numKursi = subClasses.count { it.hasKursiAlasBaca }
    val numLainnya = subClasses.count { it.hasAlatLainnya }

    val sarpasItems = listOf(
        Pair("Buku Jurnal", numJurnal),
        Pair("Buku Prestasi", numPrestasi),
        Pair("Buku Panduan", numPanduan),
        Pair("Buku Absensi", numAbsensi),
        Pair("Alat Tulis", numTulis),
        Pair("Lemari", numLemari),
        Pair("Kursi/Alas Baca", numKursi),
        Pair("Alat Lainnya", numLainnya)
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {
        Text(
            text = "Ketersediaan Sarpras (% Kelas Yang Memiliki)",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        sarpasItems.forEach { (name, count) ->
            val fillRatio = if (totalSubclasses > 0) count.toFloat() / totalSubclasses.toFloat() else 0f
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.width(110.dp)
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(10.dp)
                        .background(MaterialTheme.colorScheme.outlineVariant, shape = RoundedCornerShape(5.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(fillRatio)
                            .background(MaterialTheme.colorScheme.secondary, shape = RoundedCornerShape(5.dp))
                    )
                }

                Text(
                    text = "${(fillRatio * 100).toInt()}%",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .width(45.dp)
                        .padding(start = 8.dp),
                    textAlign = TextAlign.End
                )
            }
        }
    }
}
