package dev.stanislavskyi.feedback_bot_vgr.telegram;

import dev.stanislavskyi.feedback_bot_vgr.dto.request.FeedbackRequest;
import dev.stanislavskyi.feedback_bot_vgr.model.RoleUser;
import dev.stanislavskyi.feedback_bot_vgr.service.FeedbackService;
import dev.stanislavskyi.feedback_bot_vgr.config.BranchConfig;
import dev.stanislavskyi.feedback_bot_vgr.telegram.state.UserSession;
import dev.stanislavskyi.feedback_bot_vgr.telegram.state.UserState;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Component
public class UpdateConsumer implements LongPollingSingleThreadUpdateConsumer {

    private final TelegramClient telegramClient;
    private final BranchConfig branchConfig;
    private final FeedbackService feedbackService;

    private final Map<Long, UserSession> userSessions = new ConcurrentHashMap<>();

    public UpdateConsumer(TelegramClient telegramClient, BranchConfig branchConfig, FeedbackService feedbackService) {
        this.telegramClient = telegramClient;
        this.branchConfig = branchConfig;
        this.feedbackService = feedbackService;
    }

    @SneakyThrows
    @Override
    public void consume(Update update) {
        if (update.hasMessage()) {
            handleTextMessage(update);
        } else if (update.hasCallbackQuery()) {
            handleCallback(update.getCallbackQuery());
        }
    }

    @SneakyThrows
    private void handleTextMessage(Update update) {
        String messageText = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();

        UserSession session = userSessions.computeIfAbsent(chatId, k -> new UserSession());
        //if (session.isCompleted()) return;

        if (messageText.equals("/start")) {
            session.setState(UserState.SELECTING_ROLE);
            askRole(chatId);
        } else if (session.getState() == UserState.FEEDBACK) {
            handleFeedback(chatId, messageText);
        }


    }

    private void handleCallback(CallbackQuery callback) throws Exception {
        Long chatId = callback.getMessage().getChat().getId();
        String callbackData = callback.getData();

        UserSession session = userSessions.get(chatId);
        if (session == null) {
            session = new UserSession();
            userSessions.put(chatId, session);
        }

        switch (session.getState()) {
            case SELECTING_ROLE -> {
                if (RoleTelegramUser.isValid(callbackData)) {
                    session.setSelectedRole(callbackData);
                    session.setState(UserState.SELECTING_BRANCH);


                    RoleTelegramUser roleEnum = RoleTelegramUser.fromString(callbackData);
                    String roleDisplayName = roleEnum != null ? roleEnum.getDisplayName() : "Невідома роль";


                    SendMessage confirm = SendMessage.builder()
                            .chatId(chatId)
                            .text("✅ Ви обрали роль: " + roleDisplayName)
                            .build();
                    telegramClient.execute(confirm);

                    askAutoServiceBranch(chatId);
                }
            }
            case SELECTING_BRANCH -> {
                if (branchConfig.getBranches().containsKey(callbackData)) {
                    session.setSelectedAutoServiceBranch(callbackData);
                    session.setState(UserState.FEEDBACK);

                    RoleTelegramUser roleEnum = RoleTelegramUser.fromString(session.getSelectedRole());
                    String roleDisplayName = roleEnum != null ? roleEnum.getDisplayName() : "Невідома роль";

                    String autoServiceBranchName = branchConfig.getBranches().get(callbackData);

                    SendMessage confirm = SendMessage.builder()
                            .chatId(chatId)
                            .text("👤 Роль: " + roleDisplayName + "\n" +
                                    "🏢 Філія: " + autoServiceBranchName + "\n\n" +
                                    "Тепер ви можете надсилати свої відгуки, скарги чи пропозиції. " +
                                    "Просто напишіть повідомлення, і воно буде опрацьовано.")
                            .build();

                    telegramClient.execute(confirm);
                }
            }
        }

        AnswerCallbackQuery answer = AnswerCallbackQuery.builder()
                .callbackQueryId(callback.getId())
                .build();
        telegramClient.execute(answer);
    }

    @SneakyThrows
    private void askRole(Long chatId) {

        SendMessage message = SendMessage.builder()
                .text("👋 Ласкаво просимо! Оберіть вашу посаду:")
                .chatId(chatId)
                .build();

        var button1 = InlineKeyboardButton.builder()
                .text("🔧 Механік")
                .callbackData("mechanic")
                .build();

        var button2 = InlineKeyboardButton.builder()
                .text("⚡ Електрик")
                .callbackData("electrician")
                .build();

        var button3 = InlineKeyboardButton.builder()
                .text("👔 Менеджер")
                .callbackData("manager")
                .build();

        List<InlineKeyboardRow> keyboardRows = List.of(
                new InlineKeyboardRow(button1),
                new InlineKeyboardRow(button2),
                new InlineKeyboardRow(button3)
        );

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(keyboardRows);
        message.setReplyMarkup(markup);

        telegramClient.execute(message);
    }

    @SneakyThrows
    private void askAutoServiceBranch(Long chatId) {
        SendMessage message = SendMessage.builder()
                .text("🏢 Оберіть філію автосервісу:")
                .chatId(chatId)
                .build();

        List<InlineKeyboardRow> keyboardRows = new ArrayList<>();

        for (Map.Entry<String, String> entry : branchConfig.getBranches().entrySet()) {  //BRANCHES.entrySet()
            var button = InlineKeyboardButton.builder()
                    .text(entry.getValue())
                    .callbackData(entry.getKey())
                    .build();
            keyboardRows.add(new InlineKeyboardRow(button));
        }

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(keyboardRows);
        message.setReplyMarkup(markup);

        telegramClient.execute(message);
    }

    private void handleFeedback(Long chatId, String feedbackText) throws Exception {
        UserSession session = userSessions.get(chatId);


        RoleTelegramUser telegramRole = RoleTelegramUser.fromString(session.getSelectedRole());

        RoleUser roleUser = switch (telegramRole) {
            case MECHANIC -> RoleUser.MECHANIC;
            case ELECTRICIAN -> RoleUser.ELECTRICIAN;
            case MANAGER -> RoleUser.MANAGER;
        };

        FeedbackRequest request = new FeedbackRequest();
        request.setRoleUser(roleUser);
        request.setAutoServiceBranch(session.getSelectedAutoServiceBranch());
        request.setFeedbackText(feedbackText);

        feedbackService.handleFeedback(request);

        SendMessage response = SendMessage.builder()
                .chatId(chatId)
                .text("📝 Дякуємо за ваш відгук! Він буде розглянутий та проаналізований")
                .build();


        telegramClient.execute(response);
        //session.setCompleted(true);
    }
}