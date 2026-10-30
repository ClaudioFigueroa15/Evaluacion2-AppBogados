package comcl.duoc.appbogados.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SesionDao {

    // Inserta o actualiza la sesión activa en la base de datos local (Room)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardar(sesion: Sesion)

    // Consulta la sesión activa en la base de datos local
    @Query("SELECT * FROM sesion WHERE id = 1")
    suspend fun obtener(): Sesion?

    // Elimina los registros de la tabla "sesion"
    @Query("DELETE FROM sesion")
    suspend fun cerrar()
}