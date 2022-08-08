package developer.abdulahad.chatapp.Models

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import developer.abdulahad.chatapp.MainActivity

object MyObject {
    var list = ArrayList<User>()

    fun getUid():String{
        val firebaseAuth = FirebaseAuth.getInstance()
        return firebaseAuth.uid!!
    }

    fun updateOnlineStatus(status: String){
        val databaseReference = FirebaseDatabase.getInstance().getReference("users").child(getUid())
        val map = HashMap<String,Any>()
        map["online"]=status
        databaseReference.updateChildren(map)
    }
    fun updateTime(time: String){
        val databaseReference = FirebaseDatabase.getInstance().getReference("users").child(getUid())
        val map = HashMap<String,Any>()
        map["time"]=time
        databaseReference.updateChildren(map)
    }
}

object MyData {
    private const val NAME = "my_cash_file"
    private const val MODE = Context.MODE_PRIVATE

    private lateinit var sharedPreferences: SharedPreferences

    fun init(context: Context) {
        sharedPreferences = context.getSharedPreferences(NAME, MODE)
    }

    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = edit()
        operation(editor)
        editor.apply()
    }

    var name: String?
        get() = sharedPreferences.getString("name", "")
        set(value) = sharedPreferences.edit {
            it.putString("name", value.toString())
        }
}