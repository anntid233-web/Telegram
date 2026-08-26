import os

# Путь к главному файлу настроек
file_path = 'TMessagesProj/src/main/java/org/telegram/ui/SettingsActivity.java'

with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

# 1. Добавляем поле aerogramRow
content = content.replace(
    'private int privacyRow;',
    'private int privacyRow;\n    private int aerogramRow;'
)

# 2. Регистрируем позицию в меню
content = content.replace(
    'privacyRow = rowCount++;',
    'privacyRow = rowCount++;\n        aerogramRow = rowCount++;'
)

# 3. Назначаем текст и иконку
content = content.replace(
    'if (position == privacyRow) {',
    'if (position == aerogramRow) {\n                textCell.setTextAndIcon("AeroGram", R.drawable.msg_settings, true);\n            } else if (position == privacyRow) {'
)

# 4. Обрабатываем клик по пункту
content = content.replace(
    'presentFragment(new PrivacyControlActivity());',
    'presentFragment(new PrivacyControlActivity());\n            } else if (position == aerogramRow) {\n                presentFragment(new AeroGramSettingsActivity());'
)
# На всякий случай дублируем для других версий Телеграма
content = content.replace(
    'presentFragment(new PrivacySettingsActivity());',
    'presentFragment(new PrivacySettingsActivity());\n            } else if (position == aerogramRow) {\n                presentFragment(new AeroGramSettingsActivity());'
)

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)

print("✅ Файл SettingsActivity.java успешно пропатчен!")
