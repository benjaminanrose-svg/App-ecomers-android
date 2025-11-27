package com.example.teacherstore.fakes

import com.example.teacherstore.viewmodel.ApiViewModel
import com.example.teacherstore.repository.FakeProductApiRepository

class FakeApiViewModel : ApiViewModel(
    repository = FakeProductApiRepository()
)
