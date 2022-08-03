package developer.abdulahad.chatapp.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import developer.abdulahad.chatapp.Models.MyMessage
import developer.abdulahad.chatapp.Models.User
import developer.abdulahad.chatapp.databinding.ItemIAmSendBinding
import developer.abdulahad.chatapp.databinding.ItemSendMeBinding

class MessageAdapter(val auth: FirebaseAuth,val list: ArrayList<MyMessage>) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    inner class ToVh(var itemSendMeBinding: ItemSendMeBinding) : RecyclerView.ViewHolder(itemSendMeBinding.root){
        fun onBind(myMessage: MyMessage,position: Int){
            itemSendMeBinding.tvText.text = myMessage.text
//            if (list[position].toId == auth.uid){
//                itemSendMeBinding.tvName.text = user.name
//            }
        }
    }

    inner class FromVh(var itemIAmSend:ItemIAmSendBinding) : RecyclerView.ViewHolder(itemIAmSend.root){
        fun onBind(myMessage: MyMessage,position: Int){
            itemIAmSend.tvText.text = myMessage.text
//            if (user.uid == auth.uid){
//                itemIAmSend.tvName.text = user.name
//            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 0){
            return FromVh(ItemIAmSendBinding.inflate(LayoutInflater.from(parent.context),parent,false))
        }else{
            return ToVh(ItemSendMeBinding.inflate(LayoutInflater.from(parent.context),parent,false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == 0){
            var fromHolder = holder as FromVh
            fromHolder.onBind(list[position],position)
        }else{
            var toHolder = holder as ToVh
            toHolder.onBind(list[position],position)
        }
    }

    override fun getItemCount(): Int = list.size

    override fun getItemViewType(position: Int): Int {
        return if (list[position].fromId == auth.uid){
            0
        }else{
            1
        }
    }
}