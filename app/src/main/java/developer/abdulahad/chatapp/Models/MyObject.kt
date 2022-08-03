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
    var number = ""
    var user = User()
    var position = 0

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

    private lateinit var sp: SharedPreferences
    fun init(c: Context) {
        sp = c.getSharedPreferences("name", Context.MODE_PRIVATE)
    }

    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = edit()
        operation(editor)
        editor.apply()
    }

    var list: ArrayList<String>
        get() = gsonStringToList(sp.getString("keyList", "[]")!!)
        set(value) = sp.edit {
            it.putString("keyList", listToGsonString(value))
        }

    private fun gsonStringToList(gsonString: String): ArrayList<String> =
        Gson().fromJson(gsonString, object : TypeToken<ArrayList<String>>() {}.type)

    private fun listToGsonString(list: ArrayList<String>): String = Gson().toJson(list)
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