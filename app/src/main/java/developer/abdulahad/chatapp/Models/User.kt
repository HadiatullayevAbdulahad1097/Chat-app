package developer.abdulahad.chatapp.Models

import java.io.Serializable

class User : Serializable {
    var uid : String? = null
    var name : String? = null
    var lastName : String? = null
    var number : String? = null
    var image : Int = 0
    var online : String? = null

    constructor()
    constructor(
        uid: String?,
        name: String?,
        lastName: String?,
        number: String?,
        image: Int,
        online: String?
    ) {
        this.uid = uid
        this.name = name
        this.lastName = lastName
        this.number = number
        this.image = image
        this.online = online
    }

    constructor(name: String?, lastName: String?, number: String?, image: Int, online: String?) {
        this.name = name
        this.lastName = lastName
        this.number = number
        this.image = image
        this.online = online
    }

    constructor(online: String?) {
        this.online = online
    }
}