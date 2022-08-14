package developer.abdulahad.chatapp.adapter

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import developer.abdulahad.chatapp.Models.MyMessage
import developer.abdulahad.chatapp.Models.User
import developer.abdulahad.chatapp.R
import developer.abdulahad.chatapp.databinding.ItemIAmSendBinding
import developer.abdulahad.chatapp.databinding.ItemSendMeBinding

class MessageAdapter(val auth: FirebaseAuth, val list: ArrayList<MyMessage>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var start = 0
    inner class ToVh(var itemSendMeBinding: ItemSendMeBinding) :
        RecyclerView.ViewHolder(itemSendMeBinding.root) {
        fun onBind(myMessage: MyMessage, position: Int) {
            if (myMessage.type == 0) {
                itemSendMeBinding.textLinear.visibility = View.VISIBLE
                itemSendMeBinding.videoView.visibility = View.GONE
                itemSendMeBinding.image.visibility = View.GONE
                itemSendMeBinding.music.visibility = View.GONE
                itemSendMeBinding.tvText.text = myMessage.text
                itemSendMeBinding.tvName.text = myMessage.name
            }else if (myMessage.type == 1) {
                itemSendMeBinding.videoView.visibility = View.GONE
                itemSendMeBinding.textLinear.visibility = View.GONE
                itemSendMeBinding.image.visibility = View.VISIBLE
                itemSendMeBinding.music.visibility = View.GONE
                Glide.with(itemSendMeBinding.root.context).load(myMessage.storage).into(itemSendMeBinding.image)
            }else if (myMessage.type == 2){
                itemSendMeBinding.videoView.visibility = View.GONE
                itemSendMeBinding.textLinear.visibility = View.GONE
                itemSendMeBinding.image.visibility = View.GONE
                itemSendMeBinding.music.visibility = View.VISIBLE
                var mediaPlayer = MediaPlayer.create(itemSendMeBinding.root.context, Uri.parse(myMessage.storage))
                itemSendMeBinding.pausePlay.setOnClickListener {
                    if (start == 0) {
                        mediaPlayer.start()
                        itemSendMeBinding.pausePlay.setImageResource(R.drawable.pause)
                        start = 1
                    } else {
                        mediaPlayer.pause()
                        itemSendMeBinding.pausePlay.setImageResource(R.drawable.play)
                        start = 0
                    }
                }
            }else {
                itemSendMeBinding.videoView.visibility = View.VISIBLE
                itemSendMeBinding.textLinear.visibility = View.GONE
                itemSendMeBinding.image.visibility = View.GONE
                itemSendMeBinding.music.visibility = View.GONE
                itemSendMeBinding.videoView.setVideoPath(myMessage.storage)
                itemSendMeBinding.videoView.setOnClickListener {
                    itemSendMeBinding.videoView.start()
                }
                itemSendMeBinding.videoView.setOnLongClickListener {
                    itemSendMeBinding.videoView.pause()
                    true
                }
            }
        }
    }

    inner class FromVh(var itemIAmSend: ItemIAmSendBinding) :
        RecyclerView.ViewHolder(itemIAmSend.root) {
        fun onBind(myMessage: MyMessage) {
            if (myMessage.type == 0) {
                itemIAmSend.textLinear.visibility = View.VISIBLE
                itemIAmSend.videoView.visibility = View.GONE
                itemIAmSend.image.visibility = View.GONE
                itemIAmSend.music.visibility = View.GONE
                itemIAmSend.tvText.text = myMessage.text
                itemIAmSend.tvName.text = myMessage.name
            }else if (myMessage.type == 1) {
                itemIAmSend.videoView.visibility = View.GONE
                itemIAmSend.textLinear.visibility = View.GONE
                itemIAmSend.image.visibility = View.VISIBLE
                itemIAmSend.music.visibility = View.GONE
                Glide.with(itemIAmSend.root.context).load(myMessage.storage).into(itemIAmSend.image)
            }else if (myMessage.type == 2){
                itemIAmSend.videoView.visibility = View.GONE
                itemIAmSend.textLinear.visibility = View.GONE
                itemIAmSend.image.visibility = View.GONE
                itemIAmSend.music.visibility = View.VISIBLE
                val mediaPlayer = MediaPlayer.create(itemIAmSend.root.context, Uri.parse(myMessage.storage))
                itemIAmSend.pausePlay.setOnClickListener {
                    start = if (start == 0) {
                        mediaPlayer.start()
                        itemIAmSend.pausePlay.setImageResource(R.drawable.pause_yahshil)
                        1
                    } else {
                        itemIAmSend.pausePlay.setImageResource(R.drawable.play_yashil)
                        mediaPlayer.pause()
                        0
                    }
                }
            }else {
                itemIAmSend.videoView.visibility = View.VISIBLE
                itemIAmSend.textLinear.visibility = View.GONE
                itemIAmSend.image.visibility = View.GONE
                itemIAmSend.music.visibility = View.GONE
                itemIAmSend.videoView.setVideoPath(myMessage.storage)
                itemIAmSend.videoView.setOnClickListener {
                    itemIAmSend.videoView.start()
                }
                itemIAmSend.videoView.setOnLongClickListener {
                    itemIAmSend.videoView.pause()
                    true
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 0) {
            return FromVh(
                ItemIAmSendBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        } else {
            return ToVh(
                ItemSendMeBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == 0) {
            var fromHolder = holder as FromVh
            fromHolder.onBind(list[position])
        } else {
            var toHolder = holder as ToVh
            toHolder.onBind(list[position], position)
        }
    }

    override fun getItemCount(): Int = list.size

    override fun getItemViewType(position: Int): Int {
        return if (list[position].fromId == auth.uid) {
            0
        } else {
            1
        }
    }
}