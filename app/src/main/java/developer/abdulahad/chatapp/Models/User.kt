package developer.abdulahad.chatapp.Models

import android.net.Uri
import java.io.Serializable

data class User(
    var uid: String? = null,
    var name: String? = null,
    var email: String? = null,
    var password: String? = null,
    var imageUri: String? = null,
    var online: String? = null,
    var time: String? = null
) : Serializable