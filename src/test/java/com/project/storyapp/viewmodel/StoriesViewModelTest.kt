package com.project.storyapp.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.recyclerview.widget.ListUpdateCallback
import com.project.storyapp.MainDispatcherRule
import com.project.storyapp.adapter.StoryPagingAdapter
import com.project.storyapp.models.StoryItem
import com.project.storyapp.repository.StoriesRepository
import com.project.storyapp.DataDummy
import kotlinx.coroutines.Dispatchers
import com.project.storyapp.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import androidx.lifecycle.MutableLiveData

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class StoriesViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var storiesRepository: StoriesRepository

    @Test
    fun `when Get Stories Should Not Null and Return Data`() = runTest {
        val dummyStories = DataDummy.generateDummyStories()
        val data: PagingData<StoryItem> = PagingData.from(dummyStories)
        val expectedStories = MutableLiveData<PagingData<StoryItem>>()
        expectedStories.value = data
        Mockito.`when`(storiesRepository.getStoriesPaging("Bearer token")).thenReturn(expectedStories)

        val storiesViewModel = StoriesViewModel(storiesRepository)
        val actualStories: PagingData<StoryItem> = storiesViewModel.getStories("Bearer token").getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryPagingAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(actualStories)

        assertNotNull(differ.snapshot())
        assertEquals(dummyStories.size, differ.snapshot().size)
        assertEquals(dummyStories[0], differ.snapshot()[0])
        println("Data yang diterima : ${differ.snapshot()}")
    }

    @Test
    fun `when Get Stories Empty Should Return No Data`() = runTest {
        val data: PagingData<StoryItem> = PagingData.from(emptyList())
        val expectedStories = MutableLiveData<PagingData<StoryItem>>()
        expectedStories.value = data
        Mockito.`when`(storiesRepository.getStoriesPaging("Bearer token")).thenReturn(expectedStories)

        val storiesViewModel = StoriesViewModel(storiesRepository)
        val actualStories: PagingData<StoryItem> = storiesViewModel.getStories("Bearer token").getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryPagingAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(actualStories)

        assertEquals(0, differ.snapshot().size)
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}
