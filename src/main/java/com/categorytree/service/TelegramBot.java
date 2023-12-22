package com.categorytree.service;
import java.io.Console;
import java.util.regex.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.categorytree.config.BotConfig;
import com.categorytree.models.Category;
import com.categorytree.models.CategoryRepository;
import com.categorytree.models.User;
import com.categorytree.models.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    private UserRepository userRepository;
    private CategoryRepository categoryRepository;
    final BotConfig config;

    @Autowired
    public TelegramBot(UserRepository userRepository, CategoryRepository categoryRepository , BotConfig config) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.config = config;
    }
    
    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getBotToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId  = update.getMessage().getChatId();

            try {
                if (messageText.startsWith("/addElement")) {
                    String userInput = update.getMessage().getText().replace("/addElement", "");
                    String[] elements = userInput.trim().split("/");

                    System.out.println(elements.length);
                    if (elements.length > 0 ) {
                        addNewElement(chatId, elements);
                    } else { 
                        sendMessage(chatId, "Элементы не найдены");
                    }
                } else if (messageText.startsWith("/removeElement")) {
                    String userInput = update.getMessage().getText().replace("/removeElement", "");
                    String[] elements = userInput.trim().split("/");

                    removeElement(chatId, elements);
                } else {
                    switch (messageText) {
                        case "/start":
                            registerUser(chatId, update.getMessage().getChat().getUserName());
                            startCommandReceived(chatId, update.getMessage().getChat().getFirstName());
                            break;
                        case "/viewTree":
                            showAllCategories(chatId);
                            break;
                        case "/download":
                            downloadCategories(chatId);
                            break;
                        case "/upload":
                            break;
                        case "/help":
                            helpCommand(chatId);
                            break;
                        default: 
                            sendMessage(chatId, "Команда не найдена");
                    }
                }
            } catch (TelegramApiException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }

    // TODO: Show Start Message
    private void startCommandReceived(long chatId, String name) throws TelegramApiException {
        String answer = "Hi,  " + name + "!";

        sendMessage(chatId, answer);
    }

    // TODO: Send a message
    private void sendMessage(long chatId, String textToSend)  throws TelegramApiException{
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error occurred sending" + e.getMessage());
        }
    }

    // TODO: Register user 
    private void registerUser(long chatId, String username) {
        if (userRepository.findById(chatId).isEmpty()) {
            long ChatId = chatId;
            
            User user = new User();

            user.seChatId(ChatId);
            user.setUsername(username);
            user.setIsAdmin(false);

            userRepository.save(user);
            System.out.println(user);
        } else {
            var user = userRepository.findById(chatId);
            System.out.println(user);
        }
    }

     // TODO: Show Help Commands
    private void helpCommand(long chatId)  throws TelegramApiException{
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("""
            /viewTree - отображает дерево;
            /addElement/<родительский элемент>/<дочерний элемент> - добавление дочернего;
            /removeElement/<название элемента> - удаление элемента;
            /help - Выводит список всех доступных команд и краткое их описание.
            /download - Скачивает Excel документ с деревом категорий.
            /upload - Принимает Excel документ с деревом категорий и сохраняет все элементы в базе данных
        """);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error occurred sending" + e.getMessage());
        }
    }

    // TODO: Add new element
    @Transactional
    private void addNewElement(long chatId, String[] elements) throws TelegramApiException {
        System.out.println("Matches between slashes:");

        Category existingCategory = categoryRepository.findByCategoryName(elements[1]);
        System.out.println("Колличество слов" + elements.length);
        System.out.println("Существует ли уже категория" + existingCategory);

        for (String elem : elements) {
            System.out.println("Элемент: " + elem);
        }

        if (existingCategory == null && elements.length == 2) {
            Category newCategory = new Category();
            newCategory.setCategoryName(elements[1]);
            categoryRepository.save(newCategory);
            sendMessage(chatId, "Категория создана успешно"); 
        } else if (existingCategory != null && elements.length == 2) {
            sendMessage(chatId, "Категория уже была создана");
        } else if (existingCategory != null && elements.length == 3) {
            Category newCategory1 = new Category();
            newCategory1.setCategoryName(elements[2]);
            newCategory1.setParent(existingCategory);
            existingCategory.getChildren().add(newCategory1);
            categoryRepository.save(newCategory1);

            categoryRepository.save(existingCategory);
            categoryRepository.save(newCategory1);
            sendMessage(chatId, "Категория добавлена успешно"); 
        } else {
            sendMessage(chatId, "Категория не найдена"); 
        }
    }

    // TODO: Remove element
    private void removeElement(long chatId, String[] elements) throws TelegramApiException {
        Category existingCategory = categoryRepository.findByCategoryName(elements[0]);

        if (existingCategory == null) {
            sendMessage(chatId, "Категория не найдена");
        } else {
            deleteCategoryAndChildren(existingCategory);
            sendMessage(chatId, "Категория удалена");
        }
    }

    // TODO: Delete category's children
    private void deleteCategoryAndChildren(Category category) {
        if (category.getChildren() != null) {
            for (Category child : category.getChildren()) {
                deleteCategoryAndChildren(child);
            }
        }

        categoryRepository.delete(category);
    }

    // // TODO: Show All categories
    // private void showAllCategories(long chatId) throws TelegramApiException {
    //     Iterable<Category> allCategories = categoryRepository.findAll();
    //     sendMessage(chatId, "Дерево элементов:");
        
    //     for (Category element : allCategories) {
    //         sendMessage(chatId, element.getCategoryName());
    //     }  
    // }

    // TODO: Show All categories
    private void showAllCategories(long chatId) throws TelegramApiException {
        Iterable<Category> allCategories = categoryRepository.findAll();
        sendMessage(chatId, "Дерево элементов:");

        for (Category category : allCategories) {
            if (category.getChildren() != null && !category.getChildren().isEmpty()) {
                StringBuilder categoryInfo = new StringBuilder();
                categoryInfo.append(category.getCategoryName());
                showCategoryChildren(chatId, category, 1, categoryInfo.toString());   
            } else {
                if (category.getParent() == null) {
                    sendMessage(chatId, category.getCategoryName());
                }
            }
        }
    }

    // TODO: Render Category and their children
    private void showCategoryChildren(long chatId, Category category, int level, String categoryInfo) throws TelegramApiException {
        if (category.getChildren() != null && !category.getChildren().isEmpty()) {
            for (Category child : category.getChildren()) {
                StringBuilder childInfo = new StringBuilder(categoryInfo);
                childInfo.append("\n").append(" - ".repeat(level)).append(child.getCategoryName());
                sendMessage(chatId, childInfo.toString());
                showCategoryChildren(chatId, child, level + 1, childInfo.toString());
            }
        }
    }


    // TODO: Download Excel File
    private void downloadCategories(long chatId) throws TelegramApiException {

    }

}
