package com.example.data.local

import androidx.room.*
import com.example.data.model.MonthlyReport
import kotlinx.coroutines.flow.Flow

@Dao
interface ReportDao {
    @Query("SELECT * FROM monthly_reports ORDER BY year DESC, month DESC, timestamp DESC")
    fun getAllReports(): Flow<List<MonthlyReport>>

    @Query("SELECT * FROM monthly_reports WHERE id = :id LIMIT 1")
    suspend fun getReportById(id: Long): MonthlyReport?

    @Query("SELECT * FROM monthly_reports WHERE month = :month AND year = :year LIMIT 1")
    suspend fun getReportByMonthAndYear(month: Int, year: Int): MonthlyReport?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: MonthlyReport): Long

    @Delete
    suspend fun deleteReport(report: MonthlyReport)

    @Query("DELETE FROM monthly_reports WHERE id = :id")
    suspend fun deleteReportById(id: Long)
}
