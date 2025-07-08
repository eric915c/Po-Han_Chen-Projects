document.addEventListener("DOMContentLoaded", function() { //refer to ChatGPT and internet materials
    // Get all language radio buttons
    var languageOptions = document.querySelectorAll(".language-option input[type='radio']");

    // Add event listeners to language radio buttons
    languageOptions.forEach(function(option) {
        option.addEventListener("change", function() {
            updateLanguage(option.value);
        });
    });

    // Initial language update
    updateLanguage(getSelectedLanguage());
});

// Function to get the currently selected language
function getSelectedLanguage() {
    var selectedOption = document.querySelector(".language-option input[type='radio']:checked");
    return selectedOption ? selectedOption.value : "en"; // Default to English if none selected
}

// Function to update the language
function updateLanguage(language) {
    // Hide all elements with data-lang attribute
    var langElements = document.querySelectorAll("[data-lang]");
    langElements.forEach(function(element) {
        var key = element.getAttribute("data-lang");
        var translation = translations[key];
        if (translation) {
            element.textContent = translation[language] || translation["en"];
        }
    });
}

// Translations object
var translations = {
    "storefront": {
        "en": "Storefront",
        "zh-TW": "商店"
    },
    "cart": {
        "en": "Cart",
        "zh-TW": "購物車"
    },
    "account": {
        "en": "Account",
        "zh-TW": "帳戶"
    },
    "laguage": {
        "en": "Language",
        "zh-TW": "語言"
    },
    "settings": {
        "en": "Settings",
        "zh-TW": "設定"
    },
    "About": {
        "en": "About",
        "zh-TW": "關於"
    },
    "language-page": {
        "en": "Language Page",
        "zh-TW": "語言頁面"
    },
    "select-language": {
        "en": "Select Your Language",
        "zh-TW": "選擇您的語言"
    },
    "english": {
        "en": "English",
        "zh-TW": "英文"
    },
    "chinese-traditional": {
        "en": "繁體中文",
        "zh-TW": "繁體中文"
    }
};
