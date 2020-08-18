package com.example.mvvmjetpackcourse.viewModel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.example.mvvmjetpackcourse.model.DogBreed
import com.example.mvvmjetpackcourse.model.DogDatabase
import com.example.mvvmjetpackcourse.model.DogsApiService
import com.example.mvvmjetpackcourse.util.NotificationsHelper
import com.example.mvvmjetpackcourse.util.SharedPreferencesHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch

class ListViewModel(application: Application) : BaseViewModel(application) {
    private val dogsService = DogsApiService()
    private val disposable = CompositeDisposable()
    val dogs = MutableLiveData<List<DogBreed>>()
    private var prefHelper = SharedPreferencesHelper(getApplication())
    private var refreshTime = 5 * 60 * 1000 * 1000 * 1000L
    // specify that is a generic error with the retrieval of the data
    // true = error
    val dogsLoadError = MutableLiveData<Boolean>()

    // for spinner
    val loading = MutableLiveData<Boolean>()

    fun refresh() {
        val updateTime = prefHelper.getUpdateTIme()
        if (updateTime != null && updateTime != 0L && System.nanoTime() - updateTime < refreshTime) {
            fetchFromDatabase()
        } else {
            fetchFromRemote()
        }
    }

    fun refreshBypassCache() {
        fetchFromRemote()
    }

    private fun fetchFromRemote() {
        loading.value = true
        disposable.add(
            dogsService.getDogs().subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread()).subscribeWith(object :
                DisposableSingleObserver<List<DogBreed>>() {
                override fun onSuccess(dogsList: List<DogBreed>) {
                    storeDogsLocally(dogsList)
                    Toast.makeText(getApplication(), "Dogs retrieved from endpoint", Toast.LENGTH_SHORT).show()
                    NotificationsHelper(getApplication()).createNotification()
                }

                override fun onError(e: Throwable) {
                    dogsLoadError.value = true
                    loading.value = false
                    e.printStackTrace()
                }

            })
        )

    }

    private fun fetchFromDatabase() {
        loading.value = true
        // background thread operation
        launch {
            val dogs = DogDatabase(getApplication()).dogDao().getAllDogs()
            dogsRetrieved(dogs)
            Toast.makeText(getApplication(), "Dogs retrieved from database", Toast.LENGTH_SHORT).show()
        }
    }

    private fun dogsRetrieved(dogsList: List<DogBreed>) {
        dogs.value = dogsList
        dogsLoadError.value = false
        loading.value = false
    }

    private fun storeDogsLocally(list: List<DogBreed>) {
        launch {
            val dao = DogDatabase(getApplication()).dogDao()
            dao.deleteAllDogs()

            // expand the list into individual elements
            val result = dao.insertAll(*list.toTypedArray())
            print(list.toTypedArray())
            var i = 0
            while (i < list.size) {
                // assign uuids to the right object
                list[i].uuid = result[i].toInt()
                ++i
            }
            dogsRetrieved(list)
        }
        prefHelper.saveUpdateTIme(System.nanoTime())
    }

    private fun checkCacheDuration() {
        val cacheDurationPreference = prefHelper.getCacheDuration()
        try {
            val cachePreferenceInt = cacheDurationPreference?.toInt() ?: 5 * 60
            refreshTime = cachePreferenceInt * 1000 * 1000 * 1000L
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        }
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}
