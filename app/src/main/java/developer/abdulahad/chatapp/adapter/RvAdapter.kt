package developer.abdulahad.chatapp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import developer.abdulahad.chatapp.Models.User
import developer.abdulahad.chatapp.R
import developer.abdulahad.chatapp.databinding.ItemRvBinding
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

class RvAdapter(
    var context: Context,
    var list: List<User>,
    var auth: FirebaseAuth,
    var clickInterface: ClickInterface
) : RecyclerView.Adapter<RvAdapter.Vh>() {
    inner class Vh(var itemRv: ItemRvBinding) : RecyclerView.ViewHolder(itemRv.root) {
        @SuppressLint("SimpleDateFormat")
        fun onBind(user: User, position: Int) {
            itemRv.name.text = user.name
                Glide.with(itemRv.root.context).load(user.imageUri).into(itemRv.image)
            if (auth.uid == user.uid){
                itemRv.cardRv.setCardBackgroundColor(Color.parseColor("#0091EA"))
            }else{
                itemRv.cardRv.setCardBackgroundColor(Color.WHITE)
            }
            itemRv.time.text = user.time
            if (user.online == "online") {
                itemRv.status.visibility = View.VISIBLE
                itemRv.time.visibility = View.GONE
            } else {
                itemRv.status.visibility = View.GONE
                itemRv.time.visibility = View.VISIBLE
            }
            itemRv.cardRv.setOnClickListener {
                clickInterface.clickItem(user, position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(ItemRvBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(list[position], position)
    }

    override fun getItemCount(): Int = list.size
    interface ClickInterface {
        fun clickItem(user: User, position: Int)
    }
}