package com.example.mvvmjetpackcourse.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DogDao {
    // returns a list of long -> referred to the uuids generated
    // suspend -> operation done on background thread
    @Insert
    suspend fun insertAll(vararg dogs: DogBreed): List<Long>

    @Query(value = "SELECT * FROM dogbreed")
    suspend fun getAllDogs(): List<DogBreed>

    @Query(value = "SELECT * FROM dogbreed WHERE uuid = :dogId")
    suspend fun getDog(dogId: Int): DogBreed

    @Query(value = "DELETE FROM dogbreed")
    suspend fun deleteAllDogs()


}