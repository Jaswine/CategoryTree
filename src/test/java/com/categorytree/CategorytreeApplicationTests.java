package com.categorytree;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.categorytree.config.BotConfig;
import com.categorytree.models.Category;
import com.categorytree.models.CategoryRepository;
import com.categorytree.models.UserRepository;
import com.categorytree.service.TelegramBot;

@SpringBootTest
class CategorytreeApplicationTests {

    @Mock
    private CategoryRepository categoryRepository;

	@Mock
    private BotConfig botConfig;

    @InjectMocks
    private TelegramBot telegramBot;	

	@BeforeEach
    void setUp() {
        // Инициализация объекта конфигурации перед тестами
        MockitoAnnotations.openMocks(this);  // Initialize mocks

        when(botConfig.getBotToken()).thenReturn(botConfig.getBotToken());
        when(botConfig.getBotName()).thenReturn(botConfig.getBotName());
    }


    @Test
	void testCreateCategory() throws TelegramApiException {
		// Создаем мок Category
		Category parentCategory = new Category();
		
		parentCategory.setId(1L);
		parentCategory.setCategoryName("Дом и Сад");

		// Мокируем методы репозитория
		when(categoryRepository.findByCategoryName("Дом и Сад")).thenReturn(parentCategory);
		when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> {
            Category savedCategory = invocation.getArgument(0);
            savedCategory.setId(2L);
            return savedCategory;
        });

		// Вызываем метод для добавления новой категории
		telegramBot.addNewElement(123L, new String[]{"/addElement", "Дом и Сад"});
		// telegramBot.addNewElement(123L, new String[]{"/addElement", "Дом и Сад", "Лампы и освещение"});

		// Проверяем, что репозиторий был вызван с нужными параметрами
        verify(categoryRepository, times(1)).findByCategoryName("Дом и Сад");
        verify(categoryRepository, times(1)).save(any(Category.class));
	}


    // @Test
    // void testRemoveCategory() {
    //     // Your remove category test logic here
    // }
}
