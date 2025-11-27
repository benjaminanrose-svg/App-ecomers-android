package com.example.teacherstore

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.teacherstore.api.ApiProductResponse
import com.example.teacherstore.api.ApiService
import com.example.teacherstore.api.ProductApiRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProductApiRepositoryTest {

    private val api = mockk<ApiService>()
    private val repo = ProductApiRepository(api)

    @Test
    fun getProducts_devuelve_lista_desde_API() = runTest {

        // Arrange
        val fakeList = listOf(
            ApiProductResponse(
                id = 1,
                title = "Producto X",
                price = 25.0,
                description = "desc",
                category = "electronics",
                image = "url"
            )
        )

        coEvery { api.getProducts() } returns fakeList

        // Act
        val result = repo.fetchProducts()

        // Assert
        assertEquals(1, result.size)
        assertEquals("Producto X", result[0].title)
        assertEquals(25.0, result[0].price, 0.01)
    }
}
