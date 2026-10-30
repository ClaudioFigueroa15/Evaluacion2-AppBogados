package comcl.duoc.appbogados.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Sesion::class], version = 2)
abstract class AppDatabase : RoomDatabase() {

    // Proporciona la instacia DAO para ejecutar consultas sobre la tabla "sesión"
    abstract fun sesionDao(): SesionDao

    companion object {

        //Garantiza que los cambios de la instacia sean visibles a otros hilos de ejecución
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "appbogados.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}