**Feedback Bot VGR**

Телеграм-бот для збору відгуків співробітників автосервісу з автоматичним аналізом за допомогою Gemini AI, збереженням у PostgreSQL, експортом у Google Docs та створенням карток у Trello для критичних відгуків.
Адмін може переглядати відгуки через веб-адмінку (Spring + Thymeleaf) з фільтрацією по філії, ролі та рівню критичності.

username нашого бота у telegram: @vgr_anonym_feedback_bot

README-інструкція щодо запуску:

Для початку клонувати project.

Відкрити IntelliJ IDEA. Якщо IDEA вже відкрита з іншим проєктом → натисни File → New → Project from Version Control.
Вставити URL https://github.com/stanislavskyir/feedback-bot-vgr.git
Натиснути синю кнопку Clone

Далi натиснути злiва на iконку database

<img width="406" height="316" alt="image" src="https://github.com/user-attachments/assets/42c0540c-74d0-4b0b-bfac-27762a841bca" />

Підключитися до своєї postgres базі даних та створити нову Query Console

<img width="691" height="139" alt="image" src="https://github.com/user-attachments/assets/84507e26-cae9-4eba-a0fd-c07561d0eb90" />

Створити саму базу даних командой create database feedback_bot_db;

<img width="568" height="98" alt="image" src="https://github.com/user-attachments/assets/29f1ec57-c7b6-4b51-ab99-3a1bd89b5be5" />

Далi перейти до конфігурації

<img width="330" height="230" alt="image" src="https://github.com/user-attachments/assets/305e1a68-5c22-4be9-86e6-6616706f9e79" />

Перейти сюди до Environment variables, але якщо нема його, то вибрати Environment variables в Modify Options

<img width="869" height="84" alt="image" src="https://github.com/user-attachments/assets/bd4bbb69-468e-4858-b6ec-93a43b0878de" />

Далi додавати 

<img width="590" height="423" alt="image" src="https://github.com/user-attachments/assets/19602afe-aded-4d97-afc5-3886ffd29f92" />

Для:

DB_URL, де jdbc:postgresql://localhost:5432/feedback_bot_db
DB_USERNAME, де postgres 
DB_PASSWORD, де root (в залежностi вiд вашого паролю в Postgers)

GOOGLE_DOCS_ID, де id номер вашого документу, в мене це https://docs.google.com/document/d/1jS6cWZG4ZQ7O3cvhHPzUfhARsrZncJ5ZJnALV978i-E/edit?tab=t.0   тобто 1jS6cWZG4ZQ7O3cvhHPzUfhARsrZncJ5ZJnALV978i-E
GEMINI_API_URL  GEMINI_API_URL=https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key\=
GEMINI_API_KEY   Тут потрiбно перейти по https://aistudio.google.com/apikey та получити свiй API KEY, або скористуватися моїм AIzaSyC5NNQ8Rn3ZWlQYeocKd_4P2Wz7NkHcOyc

TELEGRAM_BOT_USERNAME, де vgr_anonym_feedback_bot
TELEGRAM_BOT_TOKEN, де токен 8436225467:AAEhGRhjZH-k1lUfP_JBCfXdV8YmdzH-ioI

TRELLO_API_KEY   тут перейти сюди https://trello.com/power-ups/68b5d7f13bd469f3a5b21899/edit/api-key та получити свiй api key
TRELLO_API_TOKEN як отримати токен скрiн знизу



<img width="1203" height="825" alt="image" src="https://github.com/user-attachments/assets/90d3b648-5361-408d-a4ed-e4999eadb04f" />


Також, щоб отримати змогу добавляти текст в Google Docs, вам потрiбно перейти сюди   https://console.cloud.google.com/iam-admin/serviceaccounts/details/118220200092737942899/keys?project=gen-lang-client-0549716445

Та отримати свiй ключ. Натиснiть на add key

<img width="714" height="413" alt="image" src="https://github.com/user-attachments/assets/0e24eed5-10f0-4c1f-b6c6-2f6c724d54fd" />

Далi створiть новий у форматi JSON

<img width="687" height="425" alt="image" src="https://github.com/user-attachments/assets/931197a4-7a47-4df2-a97a-8f930454f9f3" />

Та файл, який завантажився, потрiбно назвати service-account.json та кинути сюди:

<img width="397" height="285" alt="image" src="https://github.com/user-attachments/assets/5088540c-4f78-4fd7-a29b-707bc988f231" />


Ось скрiни як вiзуально працює сервic

<img width="512" height="539" alt="1" src="https://github.com/user-attachments/assets/9a8577fd-7c72-4c22-890f-6cf380488390" />
<img width="626" height="293" alt="2" src="https://github.com/user-attachments/assets/58a4e3cb-ce21-4c0e-990e-f1bf2ffb86d5" />
<img width="1382" height="118" alt="3" src="https://github.com/user-attachments/assets/47d8b451-0dca-4b43-a192-facfd1bde037" />
<img width="1055" height="551" alt="4" src="https://github.com/user-attachments/assets/a3dd07a0-ca9f-4395-8e7e-21976dd6cc0a" />
<img width="1792" height="392" alt="5" src="https://github.com/user-attachments/assets/3b4658e0-db82-475d-8736-1c9b29f27d31" />
<img width="616" height="125" alt="6" src="https://github.com/user-attachments/assets/a0e6df1d-a89b-4e13-bb3f-218254a30365" />
<img width="1360" height="114" alt="7" src="https://github.com/user-attachments/assets/7e998f2e-3610-4fc9-b5d6-4857f1f28236" />
<img width="1101" height="961" alt="8" src="https://github.com/user-attachments/assets/e530e1a9-9464-42ed-a48d-045a7ece8eb4" />
<img width="1063" height="840" alt="9" src="https://github.com/user-attachments/assets/f4ca247a-a551-40e0-b15b-b20b6863d500" />
<img width="1822" height="444" alt="10" src="https://github.com/user-attachments/assets/c6edb933-8d54-45f6-a134-2ce97d10113b" />
<img width="1706" height="292" alt="11" src="https://github.com/user-attachments/assets/7832dfcd-fcb2-44da-bcf9-78e16730fd8d" />
<img width="739" height="856" alt="12" src="https://github.com/user-attachments/assets/d3a81ef2-53e1-4c70-a74f-3fa35a648da2" />
