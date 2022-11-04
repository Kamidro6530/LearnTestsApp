package com.example.learnunittestsapp.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.learnunittestsapp.getOrAwaitValue
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.*
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@SmallTest
class ShoppingItemDaoTest {
    private lateinit var database: ShoppingItemDatabase
    lateinit var dao: ShoppingItemDao

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            ShoppingItemDatabase::class.java
        ).allowMainThreadQueries().build()

        dao = database.shoppingDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
    @Test
    fun insertShoppingItem() = runTest{
        //given
        val shoppingItem = ShoppingItem("Banana",4,1.0F,"none",1)

        //when
        dao.insertShoppingItem(shoppingItem)
        val allShoppingItems = dao.observeAllShoppingItems().getOrAwaitValue()


        //then
        assertThat(allShoppingItems).contains(shoppingItem)

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun deleteShoppingItem() = runTest{
        //given
        val shoppingItem = ShoppingItem("Banana",4,1.0F,"none",1)
        dao.insertShoppingItem(shoppingItem)

        //when
        dao.deleteShoppingItem(shoppingItem)
        val allShoppingItems = dao.observeAllShoppingItems().getOrAwaitValue()

        //then
        assertThat(allShoppingItems).doesNotContain(shoppingItem)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun observeAllShoppingItems() = runTest {
        //given
        dao.insertShoppingItem(ShoppingItem("Banana",4,1.0F,"none",1))
        dao.insertShoppingItem(ShoppingItem("Orange",1,3.0F,"none",2))
        dao.insertShoppingItem(ShoppingItem("Strawberry",12,0.4F,"none",3))

        //when
        val allShoppingItems = dao.observeAllShoppingItems().getOrAwaitValue()

        //then
        assertThat(allShoppingItems).hasSize(3)
    }


    @Test
    fun observeTotalPrice() = runTest {
        //given
        dao.insertShoppingItem(ShoppingItem("Banana",4,1.0F,"none",1))
        dao.insertShoppingItem(ShoppingItem("Orange",1,3.0F,"none",2))
        dao.insertShoppingItem(ShoppingItem("Strawberry",12,0.4F,"none",3))

        //when
        val totalPriceAllShoppingItems = dao.observeTotalPrice().getOrAwaitValue()

        //then
        assertThat(totalPriceAllShoppingItems).isEqualTo(11.8F)
    }
}