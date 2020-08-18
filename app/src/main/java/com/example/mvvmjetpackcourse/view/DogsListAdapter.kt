package com.example.mvvmjetpackcourse.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.example.mvvmjetpackcourse.R
import com.example.mvvmjetpackcourse.databinding.ItemDogBinding
import com.example.mvvmjetpackcourse.model.DogBreed
import kotlinx.android.synthetic.main.item_dog.view.*

class DogsListAdapter(private val dogsList: ArrayList<DogBreed>) : RecyclerView.Adapter<DogsListAdapter.DogViewHolder>(), DogClickListener {
    class DogViewHolder(var view: ItemDogBinding) : RecyclerView.ViewHolder(view.root)

    fun updateDogsList(newDogsList: List<DogBreed>) {
        dogsList.clear()
        dogsList.addAll(newDogsList)
        notifyDataSetChanged()

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DogViewHolder {
        val inflater = LayoutInflater.from(parent.context)
//        val view = inflater.inflate(R.layout.item_dog, parent, false)
        val view = DataBindingUtil.inflate<ItemDogBinding>(inflater, R.layout.item_dog, parent, false)
        return DogViewHolder(view)

    }

    override fun getItemCount(): Int {
        return dogsList.size
    }

    override fun onBindViewHolder(holder: DogViewHolder, position: Int) {
        holder.view.dog = dogsList[position]
        holder.view.listener = this
//        holder.view.name.text = dogsList[position].dogBreed
//        holder.view.lifespan.text = dogsList[position].lifeSpan
//        holder.view.setOnClickListener {
//            Navigation.findNavController(it).navigate(ListFragmentDirections.actionListFragmentToDetailFragment())
//        }
//        getProgressDrawable(context = holder.view.imageView.context)?.let {
//            holder.view.imageView.loadImage(dogsList[position].imageUrl, it)
//        }
    }

    override fun onDogClicked(v: View) {
        val action = ListFragmentDirections.actionListFragmentToDetailFragment()
        val uuid = v.dogId.text.toString().toInt()
        action.dogUuid = uuid
        Navigation.findNavController(v).navigate(action)
    }

}