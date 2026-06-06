package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SubClassData(
    val name: String,
    val santriPria: Int = 0,
    val santriWanita: Int = 0,
    val persentaseKehadiran: Float = 0f, // 0 to 100
    val persentasePencapaian: Float = 0f, // 0 to 100
    val kendalaKbm: String = "",
    // Sarana prasarana
    val hasBukuJurnal: Boolean = false,
    val hasBukuPrestasi: Boolean = false,
    val hasBukuPanduanKbm: Boolean = false,
    val hasBukuAbsensi: Boolean = false,
    val hasAlatTulis: Boolean = false,
    val hasLemari: Boolean = false,
    val hasKursiAlasBaca: Boolean = false,
    val hasAlatLainnya: Boolean = false,
    // Other features
    val materiUpdate: String = "",
    val materiUpdateList: List<String> = emptyList(),
    val ekstrakurikuler: String = "",
    val pelaksanaanGizi: String = ""
)

@JsonClass(generateAdapter = true)
data class CategoryData(
    val categoryName: String, // "Caberawit", "Pra Remaja", "Remaja", "Muda Mudi"
    val subClasses: List<SubClassData>
)

@Entity(tableName = "monthly_reports")
data class MonthlyReport(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val month: Int, // 1 to 12
    val year: Int,
    val musyawarah5Unsur: Boolean = false,
    val categories: List<CategoryData> = emptyList(),
    val timestamp: Long = System.currentTimeMillis()
)
