package com.example.pizza_backend.service;


import com.example.pizza_backend.api.dto.request.category.CategoryRequest;
import com.example.pizza_backend.api.dto.request.category.CategorySearchRequest;
import com.example.pizza_backend.api.dto.response.CategoryResponse;
import com.example.pizza_backend.exception.IdNotFoundException;
import com.example.pizza_backend.mapper.CategoryMapper;
import com.example.pizza_backend.persistence.entity.Category;
import com.example.pizza_backend.persistence.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryService categoryService;

    private Category category;
    private CategoryResponse categoryResponse;
    private CategoryRequest categoryRequest;
    private CategorySearchRequest searchRequest;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setCategoryId(1L);
        category.setCategoryName("Pizza");
        category.setCategoryImg("pizza.jpg");

        categoryResponse = CategoryResponse.builder()
                .categoryId(1L)
                .categoryName("Pizza")
                .build();

        categoryRequest = new CategoryRequest();
        categoryRequest.setCategoryId(1L);
        categoryRequest.setCategoryName("Pizza");

        searchRequest = new CategorySearchRequest();
        searchRequest.setCategoryId(1L);
        searchRequest.setCategoryName("Pizza");
    }

    @Nested
    @DisplayName("getAllCategories")
    class GetAllCategoriesTests {

        @Test
        @DisplayName("should return all categories when request is null")
        void shouldReturnAllCategories_WhenRequestIsNull() {
            // SET
            when(categoryRepository.findAll()).thenReturn(List.of(category));
            when(categoryMapper.toCategoryResponse(category)).thenReturn(categoryResponse);

            // TEST
            List<CategoryResponse> result = categoryService.getAllCategories((CategorySearchRequest) null);

            // VERIFY
            assertThat(result).hasSize(1);
            assertThat(result).containsExactly(categoryResponse);
            verify(categoryRepository).findAll();
            verify(categoryRepository, never()).searchCategory(any(), any());
        }

        @Test
        @DisplayName("should return filtered categories when request has criteria")
        void shouldReturnFilteredCategories_WhenRequestHasCriteria() {
            // SET
            when(categoryRepository.searchCategory(1L, "Pizza")).thenReturn(List.of(category));
            when(categoryMapper.toCategoryResponse(category)).thenReturn(categoryResponse);

            // TEST
            List<CategoryResponse> result = categoryService.getAllCategories(searchRequest);

            // VERIFY
            assertThat(result).hasSize(1);
            assertThat(result).containsExactly(categoryResponse);
            verify(categoryRepository).searchCategory(1L, "Pizza");
            verify(categoryRepository, never()).findAll();
        }

        @Test
        @DisplayName("should return empty list when no categories found")
        void shouldReturnEmptyList_WhenNoCategoriesFound() {
            // SET
            when(categoryRepository.findAll()).thenReturn(List.of());

            // TEST
            List<CategoryResponse> result = categoryService.getAllCategories((CategorySearchRequest) null);

            // VERIFY
            assertThat(result).isEmpty();
            verifyNoInteractions(categoryMapper);
        }

        @Test
        @DisplayName("should call overloaded method with null")
        void shouldCallOverloadedMethodWithNull() {
            // SET
            when(categoryRepository.findAll()).thenReturn(List.of(category));
            when(categoryMapper.toCategoryResponse(category)).thenReturn(categoryResponse);

            // TEST
            List<CategoryResponse> result = categoryService.getAllCategories();

            // VERIFY
            assertThat(result).hasSize(1);
            verify(categoryRepository).findAll();
        }
    }

    @Nested
    @DisplayName("createCategory")
    class CreateCategoryTests {

        @Mock
        private MultipartFile imageFile;

        @Test
        @DisplayName("should create category when valid request")
        void shouldCreateCategory_WhenValidRequest() throws IOException {
            // SET
            String username = "admin";
            when(imageFile.getOriginalFilename()).thenReturn("pizza.jpg");
            when(categoryMapper.toCategory(categoryRequest, username)).thenReturn(category);
            when(categoryRepository.save(category)).thenReturn(category);

            try (MockedStatic<FileStorageService> mockedFileStorage = mockStatic(FileStorageService.class)) {
                // TEST
                String result = categoryService.createCategory(categoryRequest, imageFile, username);

                // VERIFY
                assertThat(result).isEqualTo("success");
                verify(categoryRepository).save(category);
                mockedFileStorage.verify(() ->
                        FileStorageService.saveFile("Images/category-photos/", imageFile, "pizza.jpg")
                );
            }
        }
    }

    @Nested
    @DisplayName("updateCategory")
    class UpdateCategoryTests {

        @Mock
        private MultipartFile imageFile;

        @Test
        @DisplayName("should update category without image when imageFile is null")
        void shouldUpdateCategory_WhenImageFileIsNull() throws IOException {
            // SET
            String username = "admin";
            when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
            when(categoryRepository.save(category)).thenReturn(category);

            // TEST
            String result = categoryService.updateCategory(categoryRequest, null, username);

            // VERIFY
            assertThat(result).isEqualTo("success");
            verify(categoryMapper).updateCategoryFromInput(categoryRequest, category, username);
            verify(categoryRepository).save(category);
        }

        @Test
        @DisplayName("should update category with image when imageFile is provided")
        void shouldUpdateCategoryWithImage_WhenImageFileProvided() throws IOException {
            // SET
            String username = "admin";
            when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
            when(categoryRepository.save(category)).thenReturn(category);
            when(imageFile.isEmpty()).thenReturn(false);
            when(imageFile.getOriginalFilename()).thenReturn("new-pizza.jpg");

            try (MockedStatic<FileStorageService> mockedFileStorage = mockStatic(FileStorageService.class)) {
                // TEST
                String result = categoryService.updateCategory(categoryRequest, imageFile, username);

                // VERIFY
                assertThat(result).isEqualTo("success");
                verify(categoryMapper).updateCategoryFromInput(categoryRequest, category, username);
                mockedFileStorage.verify(() ->
                        FileStorageService.deleteFile("Images/category-photos/", "pizza.jpg")
                );
                mockedFileStorage.verify(() ->
                        FileStorageService.saveFile("Images/category-photos/", imageFile, "new-pizza.jpg")
                );
            }
        }

        @Test
        @DisplayName("should throw exception when categoryId is null")
        void shouldThrowException_WhenCategoryIdIsNull() {
            // SET
            categoryRequest.setCategoryId(null);

            // TEST & VERIFY
            assertThatThrownBy(() -> categoryService.updateCategory(categoryRequest, null, "admin"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("The given category Id cannot be null");

            verifyNoInteractions(categoryRepository, categoryMapper);
        }

        @Test
        @DisplayName("should throw exception when category not found")
        void shouldThrowException_WhenCategoryNotFound() {
            // SET
            when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

            // TEST & VERIFY
            assertThatThrownBy(() -> categoryService.updateCategory(categoryRequest, null, "admin"))
                    .isInstanceOf(IdNotFoundException.class)
                    .hasMessage("Category Not found");

            verify(categoryMapper, never()).updateCategoryFromInput(any(), any(), any());
        }
    }

    @Nested
    @DisplayName("deleteCategory")
    class DeleteCategoryTests {

        @Test
        @DisplayName("should delete category when valid request")
        void shouldDeleteCategory_WhenValidRequest() throws IOException {
            // SET
            when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));

            try (MockedStatic<FileStorageService> mockedFileStorage = mockStatic(FileStorageService.class)) {
                // TEST
                String result = categoryService.deleteCategory(categoryRequest);

                // VERIFY
                assertThat(result).isEqualTo("success");
                verify(categoryRepository).deleteById(1L);
                mockedFileStorage.verify(() ->
                        FileStorageService.deleteFile("Images/category-photos/", "pizza.jpg")
                );
            }
        }

        @Test
        @DisplayName("should throw exception when categoryId is null")
        void shouldThrowException_WhenCategoryIdIsNull() {
            // SET
            categoryRequest.setCategoryId(null);

            // TEST & VERIFY
            assertThatThrownBy(() -> categoryService.deleteCategory(categoryRequest))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("The given category Id cannot be null");

            verifyNoInteractions(categoryRepository);
        }

        @Test
        @DisplayName("should throw exception when category not found")
        void shouldThrowException_WhenCategoryNotFound() {
            // SET
            when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

            // TEST & VERIFY
            assertThatThrownBy(() -> categoryService.deleteCategory(categoryRequest))
                    .isInstanceOf(IdNotFoundException.class)
                    .hasMessage("Category Not found");

            verify(categoryRepository, never()).deleteById(any());
        }
    }
}