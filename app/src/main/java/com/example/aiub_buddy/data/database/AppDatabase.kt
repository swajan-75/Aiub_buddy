package com.example.aiub_buddy.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.aiub_buddy.data.dao.RoutineDao
import com.example.aiub_buddy.data.dao.SubjectDao
import com.example.aiub_buddy.data.entity.RoutineEntity
import com.example.aiub_buddy.data.entity.SubjectEntity

@Database(entities = [RoutineEntity::class, SubjectEntity::class], version = 2)
abstract class AppDatabase : RoomDatabase(){
    abstract fun routineDao(): RoutineDao
    abstract  fun subjectDao() : SubjectDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        fun getDatabase(context: Context): AppDatabase{
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "aiub_buddy_db"
                ).fallbackToDestructiveMigration().allowMainThreadQueries().build()
                INSTANCE = instance
                instance
            }
        }


    }

}