package com.r_mit.taskt

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.Path

interface GraphApiService {
    // Fetch Excel data
    @GET("me/drive/items/{fileId}/workbook/worksheets/Sheet1/range(address='A1:F100000')")
    suspend fun getExcelData(
        @Path("fileId") fileId: String,
        @Header("Authorization") authHeader: String
    ): WorkbookRange

    // Update Excel data
    @PATCH("me/drive/items/{fileId}/workbook/worksheets/{sheetName}/range(address='{rangeAddress}')")
    suspend fun updateExcelData(
        @Path("fileId") fileId: String,
        @Path("sheetName") sheetName: String,
        @Path("rangeAddress") rangeAddress: String,
        @Header("Authorization") authHeader: String,
        @Body requestBody: UpdateRequestBody
    )
}

