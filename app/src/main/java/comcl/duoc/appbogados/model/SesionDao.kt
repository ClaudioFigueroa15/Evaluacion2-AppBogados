package comcl.duoc.appbogados.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SesionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardar(sesion: Sesion)

    @Query("SELECT * FROM sesion WHERE id = 1")
    suspend fun obtener(): Sesion?

    @Query("DELETE FROM sesion")
    suspend fun cerrar()
}