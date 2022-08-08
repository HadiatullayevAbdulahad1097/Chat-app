package developer.abdulahad.chatapp.Models

import com.google.firebase.storage.FirebaseStorage

data class MyMessage(var text:String? = null, var fromId:String? = null,var toId:String? = null, var name:String? = null,var type:Int? = null)
