package com.example.teacherstore.fakes

import com.example.teacherstore.viewmodel.CartViewModel
import com.example.teacherstore.repository.FakeProductRepository

class FakeCartViewModel : CartViewModel(
    productRepo = FakeProductRepository()
)
