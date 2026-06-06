package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.CategoryData
import com.example.data.model.MonthlyReport
import com.example.data.model.SubClassData
import com.example.data.repository.ReportRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

class ReportViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ReportRepository

    val allReports: StateFlow<List<MonthlyReport>>

    private val _activeDraft = MutableStateFlow<MonthlyReport?>(null)
    val activeDraft: StateFlow<MonthlyReport?> = _activeDraft.asStateFlow()

    private val _selectedReportForViewing = MutableStateFlow<MonthlyReport?>(null)
    val selectedReportForViewing: StateFlow<MonthlyReport?> = _selectedReportForViewing.asStateFlow()

    private val _isEditing = MutableStateFlow(false)
    val isEditing: StateFlow<Boolean> = _isEditing.asStateFlow()

    init {
        val db = AppDatabase.getDatabase(application)
        repository = ReportRepository(db.reportDao())
        allReports = repository.allReports.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    fun startNewReportDraft(month: Int, year: Int) {
        viewModelScope.launch {
            // Check if report already exists for this month & year
            val existing = repository.getReportByMonthAndYear(month, year)
            if (existing != null) {
                // Load existing to edit!
                _activeDraft.value = existing
                _isEditing.value = true
            } else {
                val newReport = MonthlyReport(
                    month = month,
                    year = year,
                    musyawarah5Unsur = false,
                    categories = generateDefaultCategories()
                )
                _activeDraft.value = newReport
                _isEditing.value = false
            }
        }
    }

    fun startEditingReport(report: MonthlyReport) {
        _activeDraft.value = report.copy()
        _isEditing.value = true
    }

    fun stopEditingDraft() {
        _activeDraft.value = null
        _isEditing.value = false
    }

    fun selectReportForViewing(report: MonthlyReport?) {
        _selectedReportForViewing.value = report
    }

    fun updateDraftMusyawarah(value: Boolean) {
        _activeDraft.value = _activeDraft.value?.copy(musyawarah5Unsur = value)
    }

    fun updateDraftSubClass(categoryName: String, subClassName: String, updateBlock: (SubClassData) -> SubClassData) {
        val currentDraft = _activeDraft.value ?: return
        val updatedCategories = currentDraft.categories.map { category ->
            if (category.categoryName == categoryName) {
                val updatedClasses = category.subClasses.map { subClass ->
                    if (subClass.name == subClassName) {
                        updateBlock(subClass)
                    } else {
                        subClass
                    }
                }
                category.copy(subClasses = updatedClasses)
            } else {
                category
            }
        }
        _activeDraft.value = currentDraft.copy(categories = updatedCategories)
    }

    fun saveDraft(onComplete: () -> Unit) {
        val draft = _activeDraft.value ?: return
        viewModelScope.launch {
            repository.saveReport(draft.copy(timestamp = System.currentTimeMillis()))
            _activeDraft.value = null
            _isEditing.value = false
            onComplete()
        }
    }

    fun deleteReport(id: Long, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.deleteReportById(id)
            if (_selectedReportForViewing.value?.id == id) {
                _selectedReportForViewing.value = null
            }
            onComplete()
        }
    }

    private fun generateDefaultCategories(): List<CategoryData> {
        return listOf(
            CategoryData(
                categoryName = "Caberawit",
                subClasses = listOf(
                    SubClassData(name = "PAUD"),
                    SubClassData(name = "Kelas 1"),
                    SubClassData(name = "Kelas 2"),
                    SubClassData(name = "Kelas 3")
                )
            ),
            CategoryData(
                categoryName = "Pra Remaja",
                subClasses = listOf(
                    SubClassData(name = "Kelas 4"),
                    SubClassData(name = "Kelas 5"),
                    SubClassData(name = "Kelas 6"),
                    SubClassData(name = "Pra Remaja Umum")
                )
            ),
            CategoryData(
                categoryName = "Remaja",
                subClasses = listOf(
                    SubClassData(name = "Kelas 7"),
                    SubClassData(name = "Kelas 8"),
                    SubClassData(name = "Kelas 9"),
                    SubClassData(name = "Remaja Umum")
                )
            ),
            CategoryData(
                categoryName = "Muda Mudi",
                subClasses = listOf(
                    SubClassData(name = "Kelas Mahasiswa"),
                    SubClassData(name = "Kelas Pekerja"),
                    SubClassData(name = "Muda Mudi Umum")
                )
            )
        )
    }
}
