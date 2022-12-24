package jafar.stories.data.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import jafar.stories.data.model.ListStory

@Dao
interface StoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(story: List<ListStory>)

    @Query("SELECT * FROM story order by createdAt DESC")
    fun getAllStory(): PagingSource<Int, ListStory>

    @Query("DELETE FROM story")
    suspend fun deleteAll()
}