package com.fares.training.takenotes.data.remote

import com.fares.training.takenotes.data.local.Note
import com.fares.training.takenotes.data.remote.requests.*
import com.fares.training.takenotes.data.remote.responses.PictureResponse
import com.fares.training.takenotes.data.remote.responses.SimpleResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface NoteApi {

    @POST("/register")
    suspend fun registerRequest(
        @Body registerRequest: AccountRequest
    ): Response<SimpleResponse>

    @POST("/login")
    suspend fun loginRequest(
        @Body loginRequest: AccountRequest
    ): Response<SimpleResponse>

    @POST("/addNote")
    suspend fun addNote(
        @Body note: Note
    ): Response<ResponseBody>

    @POST("/deleteNote")
    suspend fun deleteNote(
        @Body deleteNoteRequest: DeleteNoteRequest
    ): Response<ResponseBody>

    @POST("/addOwnerToNote")
    suspend fun addOwnerToNote(
        @Body addOwnerRequest: AddOwnerRequest
    ): Response<SimpleResponse>

    @GET("/getNotes")
    suspend fun getNotes(): Response<List<Note>>

    @POST("/addPictureToNote")
    suspend fun addPictureToNote(
        @Body addPictureRequest: AddPictureRequest
    ): Response<SimpleResponse>

    @GET("/getNotePicture/{id}")
    suspend fun getNotePicture(
        @Path("id") noteId:String
    ): Response<PictureResponse>
}