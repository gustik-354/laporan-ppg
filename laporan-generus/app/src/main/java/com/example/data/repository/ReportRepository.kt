package com.example.data.repository

import com.example.data.local.ReportDao
import com.example.data.model.MonthlyReport
import kotlinx.coroutines.flow.Flow

class ReportRepository(private val reportDao: ReportDao) {
    val allReports: Flow<List<MonthlyReport>> = reportDao.getAllReports()

    suspend fun getReportById(id: Long): MonthlyReport? {
        return reportDao.getReportById(id)
    }

    suspend fun getReportByMonthAndYear(month: Int, year: Int): MonthlyReport? {
        return reportDao.getReportByMonthAndYear(month, year)
    }

    suspend fun saveReport(report: MonthlyReport): Long {
        return reportDao.insertReport(report)
    }

    suspend fun deleteReportById(id: Long) {
        reportDao.deleteReportById(id)
    }
}
