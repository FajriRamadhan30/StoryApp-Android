# StoryApp - Paging3 ViewModel Testing

Welcome to the **StoryApp**! This project demonstrates the implementation of modern Android development practices using **Paging3**, **MVVM architecture**, and **unit testing** to ensure robust and efficient data handling for story-based applications.

## Features
- **Efficient Data Loading**: Stories are fetched and displayed seamlessly using Paging3.
- **Unit Testing**: Ensures data accuracy and app reliability with rigorous tests for ViewModel functionality.
- **LiveData Integration**: Observing data flow between ViewModel and UI.
- **RecyclerView Animations**: Smooth UI transitions with custom animations.
- **Token-Based Authentication**: Secure data fetching for authenticated users.

---

## Objective
This repository specifically focuses on **unit testing the ViewModel** that handles paginated story data. The tests ensure the following scenarios are handled effectively:

1. **When Data is Available:**
   - Verify that the returned data is **not null**.
   - Ensure the total count of stories matches the **dummy data size**.
   - Confirm the **first story** matches the expected dummy data.

2. **When No Data is Available:**
   - Verify that the total count of returned stories is **zero**.

---

## Tech Stack
### Languages & Tools
- **Kotlin**: Primary programming language.
- **Paging3**: For efficient data pagination.
- **Mockito**: Mocking framework for unit testing.
- **JUnit4**: Testing framework for Java and Kotlin.
- **AndroidX Libraries**: Lifecycle, LiveData, and ViewModel.

---

## Testing Setup

### Prerequisites
- Android Studio Flamingo or later
- Gradle 8.0 or later
- Kotlin 1.8.0 or later

### Key Classes
- **StoriesViewModel**: Contains the business logic for fetching paginated story data.
- **StoriesRepository**: Handles data fetching from API.
- **StoryPagingAdapter**: Adapter for RecyclerView that displays story items.

---

## Unit Tests
Unit tests ensure ViewModel functionality using the following scenarios:

### Test 1: When Data is Available
```kotlin
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
}
```

### Test 2: When No Data is Available
```kotlin
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
```

---

## Running the Tests

1. Open the project in **Android Studio**.
2. Navigate to the `StoriesViewModelTest` file.
3. Right-click and select **Run 'StoriesViewModelTest'**.
4. Verify the output in the Run/Debug window.

---

## Expected Test Outputs
- **Test 1 (When Data is Available):**
  - Data is **not null**.
  - Data size matches dummy data.
  - First data item matches the first dummy story.

- **Test 2 (When No Data is Available):**
  - Data size is **zero**.

---

## Contributions
Feel free to fork this repository and contribute improvements or additional features. Follow the **Git Flow** process for submitting pull requests.

---

## License
This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.
