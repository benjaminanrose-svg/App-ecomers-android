package com.example.teacherstore

import com.example.teacherstore.api.ApiProductResponse
import com.example.teacherstore.api.ProductApiRepository
import com.example.teacherstore.viewmodel.ApiViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.advanceUntilIdle
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ApiViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val fakeRepo = mockk<ProductApiRepository>()
    private val viewModel = ApiViewModel(fakeRepo, testDispatcher)

    @Test
    fun `fetchProducts actualiza lista de productos correctamente`() = runTest {
        // Arrange
        val expected = listOf(
            ApiProductResponse(
                id = 1,
                title = "Laptop Gamer",
                price = 1500.0,
                description = "Powerful gaming laptop",
                category = "electronics",
                image = ""
            )
        )

        coEvery { fakeRepo.getProducts() } returns expected

        // Act
        viewModel.fetchProducts()
        advanceUntilIdle()

        // Assert
        val result = viewModel.products.value
        assertEquals(1, result.size)
        assertEquals("Laptop Gamer", result[0].title)
        assertEquals(1500.0, result[0].price, 0.0)
        assertEquals(false, viewModel.isLoading.value)
        assertEquals(null, viewModel.error.value)
    }

    @Test
    fun `fetchProducts maneja errores correctamente`() = runTest {
        // Arrange
        coEvery { fakeRepo.getProducts() } throws Exception("API error")

        // Act
        viewModel.fetchProducts()
        advanceUntilIdle()

        // Assert
        assertTrue(viewModel.products.value.isEmpty())
        assertEquals("API error", viewModel.error.value)
        assertEquals(false, viewModel.isLoading.value)
    }
}
