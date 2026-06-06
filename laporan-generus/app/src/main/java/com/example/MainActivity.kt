package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.ReportDetailScreen
import com.example.ui.screens.ReportFormScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.ReportViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel: ReportViewModel = viewModel()
                    val reports by viewModel.allReports.collectAsStateWithLifecycle()
                    val activeDraft by viewModel.activeDraft.collectAsStateWithLifecycle()
                    val selectedReportForViewing by viewModel.selectedReportForViewing.collectAsStateWithLifecycle()
                    val isEditing by viewModel.isEditing.collectAsStateWithLifecycle()

                    when {
                        activeDraft != null -> {
                            ReportFormScreen(
                                report = activeDraft!!,
                                isEditing = isEditing,
                                onBack = { viewModel.stopEditingDraft() },
                                onSave = {
                                    viewModel.saveDraft {
                                        // On save success, we can clear viewing if we were updating, or open details!
                                        viewModel.selectReportForViewing(null)
                                    }
                                },
                                onUpdateMusyawarah = { viewModel.updateDraftMusyawarah(it) },
                                onUpdateSubClass = { catName, subClassName, updater ->
                                    viewModel.updateDraftSubClass(catName, subClassName, updater)
                                }
                            )
                        }
                        selectedReportForViewing != null -> {
                            ReportDetailScreen(
                                report = selectedReportForViewing!!,
                                onBack = { viewModel.selectReportForViewing(null) },
                                onEdit = {
                                    viewModel.startEditingReport(selectedReportForViewing!!)
                                }
                            )
                        }
                        else -> {
                            DashboardScreen(
                                reports = reports,
                                onSelectReport = { report ->
                                    viewModel.selectReportForViewing(report)
                                },
                                onStartNewReport = { month, year ->
                                    viewModel.startNewReportDraft(month, year)
                                },
                                onDeleteReport = { id ->
                                    viewModel.deleteReport(id)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
