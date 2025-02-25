package com.dicoding.picodiploma.loginwithanimation.view.main

import androidx.lifecycle.ViewModel
import com.dicoding.picodiploma.loginwithanimation.data.UserRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody

class CreateActivityViewModel(private val userRepository: UserRepository) : ViewModel() {
    fun postStory(
        file: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody?,
        lon: RequestBody?
    ) = userRepository.postStory(file, description, lat, lon)
}