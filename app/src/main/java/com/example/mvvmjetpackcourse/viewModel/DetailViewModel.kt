package com.example.mvvmjetpackcourse.viewModel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.example.mvvmjetpackcourse.model.DogBreed
import com.example.mvvmjetpackcourse.model.DogDatabase
import kotlinx.coroutines.launch

class DetailViewModel(application: Application) : BaseViewModel(application) {
    val dogBreed = MutableLiveData<DogBreed>()
    fun fetch(uuid: Int) {
        launch {
            val dog = DogDatabase(getApplication()).dogDao().getDog(uuid)

            dogBreed.value = dog
        }
    }
}