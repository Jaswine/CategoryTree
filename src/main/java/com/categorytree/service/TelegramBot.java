package com.categorytree.service;
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
    public TelegramBot(UserRepository userRepository, CategoryRepository categoryRepository ,BotConfig config) {
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
                    System.out.println("/ADD ELEMENT" + userInput);
                    String[] elements = userInput.split("/");

                    System.out.println(elements.length);
                    if (elements.length > 0 ) {
                        addNewElement(chatId, elements);
                        sendMessage(chatId, "Add new element");
                    } else { 
                        sendMessage(chatId, "No element");
                    }
                } else if (messageText.startsWith("/removeElement")) {
                    String userInput = update.getMessage().getText().replace("/removeElement", "");
                    String[] elements = userInput.split("/");

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
                            break;
                        case "/upload":
                            break;
                        case "/help":
                            helpCommand(chatId);
                            break;
                        default: 
                            sendMessage(chatId, "Command was not recognized");
                    }
                }
            } catch (TelegramApiException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }

    // Show Start Message
    private void startCommandReceived(long chatId, String name) throws TelegramApiException {
        String answer = "Hi,  " + name + "!";

        sendMessage(chatId, answer);
    }

    // Show Help Commands
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

    // Send a message
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

    // Register user 
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

    // Add new element
    @Transactional
    private void addNewElement(long chatId, String[] elements) throws TelegramApiException {
        System.out.println("Matches between slashes:");

        for (String element : elements) {
            Category existingCategory = categoryRepository.findByCategoryName(element);

            if (existingCategory == null) {
                Category newCategory = new Category();
                newCategory.setCategoryName(element);
                categoryRepository.save(newCategory);
                System.out.println("Added new category: " + newCategory);
            } else {
                System.out.println("Category already exists: " + existingCategory);
                sendMessage(chatId, "Category already exists: " + element);
            }
        }

        System.out.println("Adding new element");
    }

    // Remove element
    private void removeElement(long chatId, String[] elements) throws TelegramApiException {
        for (String element : elements) {
            Category existingCategory = categoryRepository.findByCategoryName(element);

            if (existingCategory == null) {
                sendMessage(chatId, "Category not found");
            } else {
                categoryRepository.delete(existingCategory);
                sendMessage(chatId, "Category deleted");
            }
        }
    }

    // Show All categories
    private void showAllCategories(long chatId) throws TelegramApiException {
        Iterable<Category> allCategories = categoryRepository.findAll();
        sendMessage(chatId, "Дерево элементов:");
        
        for (Category element : allCategories) {
            sendMessage(chatId, element.getCategoryName());
        }  
    }

}
